package com.nanosoft.bd.saveme.newsfeed.bhorerKagoj;

import com.nanosoft.bd.saveme.newsfeed.RssItem;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NanoSoft on 11/10/2016.
 */

public class Bhorerkagoj {

    public static List<RssItem> GetLinks() {
        String content = null;
        URLConnection connection = null;
        try {
            connection = new URL("http://www.bhorerkagoj.net/online/").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            while (scanner.hasNext())
                content += scanner.next();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int start=content.indexOf("<div id=\"home\"");
        int end=content.indexOf("<div id=\"menu1\"");
        content=content.substring(start,end);

        ArrayList<RssItem> list1=new ArrayList<>();

        Pattern p = Pattern.compile("<a href=\".*</a>");

        Matcher m = p.matcher(content);
        while (m.find()) {
            String parsedLink = m.group();

            RssItem item=new RssItem();
            item.setTitle(parsedLink.replaceAll("(<a href=\".*>)(.*)(</a>)","$2"));
            item.setLink(parsedLink.replaceAll("(<a href=\")(.*)(\">.*</a>)","$2"));
            list1.add(item);
        }
        return list1;
    }


}


