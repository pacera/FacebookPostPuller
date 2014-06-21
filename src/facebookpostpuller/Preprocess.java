/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facebookpostpuller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RJ
 */
public final class Preprocess {

    private final ArrayList<String> emoticons;

    public Preprocess() {
        emoticons = new ArrayList<>();
        loadEmoticons();

    }

    private void loadEmoticons() {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get("emoticons.txt")));
        } catch (IOException ex) {
            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
        }
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                emoticons.add(s.toString().trim());
                s = new StringBuilder();
            } else {
                s = s.append(content.charAt(i));
            }
        }

    }
    
    private String replaceEmoticon(String message, String emoticon) {
        String temp = "";
        StringTokenizer st = new StringTokenizer(message);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (emoticon.equals(token)) {
                temp += "%emoticon% ";
            } else {
                temp += token + " ";
            }
        }
        
        return temp.trim();
    }

    public String emoticons(String message) {
        for (String emoticon : emoticons) {
            message = replaceEmoticon(message, emoticon);
            for (int i = 0; i < 50; i++) {
                emoticon = emoticon.concat(String.valueOf(emoticon.charAt(emoticon.length() - 1)));
                message = replaceEmoticon(message, emoticon);
            }
        }
        return message;
    }

    public String url(String message) {
        //String regex = "_^(?:(?:https?|ftp)://)(?:\\S+(?::\\S*)?@)?(?:(?!10(?:\\.\\d{1,3}){3})(?!127(?:\\.\\d{1,3}){3})(?!169\\.254(?:\\.\\d{1,3}){2})(?!192\\.168(?:\\.\\d{1,3}){2})(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}0-9]+-?)*[a-z\\x{00a1}-\\x{ffff}0-9]+)*(?:\\.(?:[a-z\\x{00a1}-\\x{ffff}]{2,})))(?::\\d{2,5})?(?:/[^\\s]*)?$_iu";        
        String regex2 = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        message = message.replaceAll(regex2, "%url%");
        return message;
    }
    
    public String emoji(String message) {
        try {
            String regex = "[^\\x00-\\x7F]";
            byte[] utf8Bytes = message.getBytes("UTF-8");
            String utf8message = new String(utf8Bytes, "UTF-8");
            return utf8message.replaceAll(regex, " %emoticon% ").trim(  );
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
