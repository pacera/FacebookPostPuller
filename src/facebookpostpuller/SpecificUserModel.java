/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facebookpostpuller;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;
import com.restfb.types.User;
import static facebookpostpuller.PostModel.calculateAge;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 *
 * @author RJ
 */
public class SpecificUserModel {

    private HashMap<Post, User> posts;
    private String accessToken;
    private String username;
    private String ageGroup;

    public SpecificUserModel(String accessToken, String username, String ageGroup) {
        this.accessToken = accessToken;
        this.username = username;
        this.ageGroup = ageGroup;
        
        this.posts = new HashMap<>();
    }

    public void get() {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
        User friend = facebookClient.fetchObject(username, User.class,
                            Parameter.with("fields", "id, name, birthday"));
        Connection<Post> feed = facebookClient.fetchConnection(username.concat("/posts"),
                Post.class,
                Parameter.with("fields", "message,from"),
                Parameter.with("limit", 500)); // limited to x posts
        for (Post post : feed.getData()) {
            if (post.getMessage() == null) continue;
            posts.put(post, friend);
            System.out.println(post.getMessage());
        }
        
    }

    public void convertToArff(File file) throws Exception {

        FastVector atts;
        FastVector attVals;
        Instances data;
        double[] vals;

        file = new File(file + ".arff");

        atts = new FastVector();
        atts.addElement(new Attribute(("name"), (FastVector) null)); // 5/27/2014
        atts.addElement(new Attribute(("message"), (FastVector) null));

        attVals = new FastVector();
        attVals.addElement("13-17");
        attVals.addElement("18-24");
        attVals.addElement("25-34");
        attVals.addElement("35-44");
        attVals.addElement("45-54");
        atts.addElement(new Attribute("age-group", attVals));

        data = new Instances("predict_age", atts, 0);

        Iterator it = posts.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            vals = new double[data.numAttributes()];
            User user = (User) pairs.getValue();
            String name = user.getName(); // 5/27/2014
            String message = ((Post) (pairs.getKey())).getMessage();

            Preprocess pre = new Preprocess();
            message = pre.emoticons(message);
            message = pre.emoji(message);
            message = pre.url(message);

            //StringFilter filter = new StringFilter(message);
            vals[0] = data.attribute(0).addStringValue(name); // 5/27/2014
            vals[1] = data.attribute(1).addStringValue(message);

            if (ageGroup.equals("13-17")) {
                vals[2] = attVals.indexOf("13-17");
            } else if (ageGroup.equals("18-24")) {
                vals[2] = attVals.indexOf("18-24");
            } else if (ageGroup.equals("25-34")) {
                vals[2] = attVals.indexOf("25-34");
            } else if (ageGroup.equals("35-44")) {
                vals[2] = attVals.indexOf("35-44");
            } else if (ageGroup.equals("45-54")) { // Modified 6/11/2014 
                vals[2] = attVals.indexOf("45-54");
            }

            data.add(new Instance(1.0, vals));

            it.remove();
        }

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(file);
        saver.writeBatch();
    }
}
