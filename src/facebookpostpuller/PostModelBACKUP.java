/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package facebookpostpuller;

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Post;
import com.restfb.types.User;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

/**
 *
 * @author RJ
 */
public class PostModelBACKUP extends Observable implements Runnable {
    private final String threadName;
    
    private final FacebookClient facebookClient;
    private final Connection<User> myFriends;
    
    private final int startIndex;
    private final int endIndex;
    
    private static HashMap<Post, User> posts;
    private static HashMap<String, String> metadata; // Name and Age
    
    private String outputString;
    private boolean stopped;
    
    public PostModelBACKUP(String threadName, FacebookClient facebookClient, Connection<User> myFriends, int startIndex, int endIndex) {
        this.threadName = threadName;
        
        this.facebookClient = facebookClient;
        this.myFriends = myFriends;
        
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        
        PostModelBACKUP.posts = new HashMap<>();
        PostModelBACKUP.metadata = new HashMap<>();
        
        this.outputString = "";
        this.stopped = true;
    }

    public HashMap<Post, User> getPosts() {
        return posts;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

    public String getOutputString() {
        return outputString;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    
    public boolean hasPostsInMemory() {
        return !posts.isEmpty();
    }
    
    public static int calculateAge(Date inputBirthdate) {
        DateTime birthdate = new DateTime(inputBirthdate);
        DateTime now = new DateTime();
        Years age = Years.yearsBetween(birthdate, now);
        return age.getYears();
    }
    
    public static void convertToArff(File file) throws Exception {

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
            //StringFilter filter = new StringFilter(message);
            vals[0] = data.attribute(0).addStringValue(name); // 5/27/2014
            vals[1] = data.attribute(1).addStringValue(message);

            int age = calculateAge(user.getBirthdayAsDate());
            if (age >= 13 && age <= 17) {
                vals[2] = attVals.indexOf("13-17");
            } else if (age >= 18 && age <= 24) {
                vals[2] = attVals.indexOf("18-24");
            } else if (age >= 25 && age <= 34) {
                vals[2] = attVals.indexOf("25-34");
            } else if (age >= 35 && age <= 44) {
                vals[2] = attVals.indexOf("35-44");
            } else if (age >= 45 && age <= 54) {
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
    
    public static void saveUserInformationAsCSV(String path) {
        File file = new File(path.concat("_UserData.csv"));
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                try (BufferedWriter bw = new BufferedWriter(fw)) {
                    Iterator it = metadata.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry)it.next();
                        System.out.println(pairs.getKey() + " " + pairs.getValue());
                        String line = pairs.getKey().toString()
                                .concat(",")
                                .concat(pairs.getValue().toString())
                                .concat("\r\n");
                        
                        bw.write(line);
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                }
                System.out.println(".txt file saved at ".concat(file.getAbsolutePath()));
            } catch (IOException ex) {
                Logger.getLogger(PostModelBACKUP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void run() {
        User friend;
        User evaluatedFriend;
        DateTime birthdate;
        List<User> friends = myFriends.getData();
        for (int i = this.startIndex; i < this.endIndex; i++) {
            if (stopped) {
                break;
            }
            friend = friends.get(i);
            evaluatedFriend = facebookClient.fetchObject(friend.getId(), User.class,
                    Parameter.with("fields", "id, name, birthday"));
            
            birthdate = new DateTime(evaluatedFriend.getBirthdayAsDate());
            
            // This line checks if the user's profile has his/her birthdate set and made public
            // Skips users who has no bday set and made public on their profile 
            if (birthdate.getYear() == DateTime.now().getYear()) {
                continue;
            }
               
            // Puts the name and age of the friend
            metadata.put(String.valueOf(evaluatedFriend.getName()),
                    String.valueOf(calculateAge(evaluatedFriend.getBirthdayAsDate())));
            
            outputString = "Thread " + threadName + "\n"
                    + "Name: "
                    + evaluatedFriend.getName()
                    + "\nAge: "
                    + calculateAge(evaluatedFriend.getBirthdayAsDate()) + "\n\n";
            setChanged();
            notifyObservers();
            outputString = "";
            
            DateTime minusOneYear = DateTime.parse(evaluatedFriend.getBirthday(),
                    DateTimeFormat.forPattern("M/d/y"))
                    .withYear(DateTime.now().getYear())
                    .minusYears(1);

            Long targetTime = minusOneYear.getMillis() / 1000;

            Long now = DateTime.now().getMillis() / 1000;
            System.out.println("Thread " + threadName + ": " + evaluatedFriend.getName());
            Connection<Post> feed = facebookClient.fetchConnection(evaluatedFriend.getId().concat("/posts"),
                    Post.class,
                    Parameter.with("fields", "message,from"),
                    Parameter.with("until", now),
                    Parameter.with("since", targetTime),
                    Parameter.with("limit", 200)); // limited to x posts
            // Dataset range (first x posts since the user's birthdate last year until today)
            
            String recentMessage = "";
            for (Post post : feed.getData()) {
                if (stopped) {
                    break;
                }

                if (post.getMessage() == null) {
                    continue;
                }
                
                // Prevents duplicate posts. Idk kung bug ba ng RestFB o kung ano man
                if(recentMessage.equals(post.getMessage())) {
                    recentMessage = post.getMessage();
                    continue;
                }
                
                recentMessage = post.getMessage();
                // End of duplicate checker
                
                //outputString = post.getMessage() + "\n";
                //setChanged();
                //notifyObservers();
                //outputString = "";

                posts.put(post, evaluatedFriend);
            }
        }
        stopped = true;
        
        System.out.println("Thread " + threadName + " finished execution\n" + posts.size());
        System.out.println(metadata.size());
    }
}
