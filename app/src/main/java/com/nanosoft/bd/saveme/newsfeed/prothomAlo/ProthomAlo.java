package com.nanosoft.bd.saveme.newsfeed.prothomAlo;

import com.nanosoft.bd.saveme.newsfeed.RssItem;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Nanosoft-Android on 11/5/2016.
 */

public class ProthomAlo {
    public static List<RssItem> GetLinks() {
        String content = null;
        URLConnection connection = null;
        try {
            connection = new URL("http://www.prothom-alo.com/").openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            while (scanner.hasNext())
             content += scanner.next();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ArrayList<RssItem> list1=new ArrayList<>();

        Pattern p = Pattern.compile("<a href='.*</a>");

        Matcher m = p.matcher(content);
        while (m.find()) {
            String parsedLink = m.group().replaceAll("(<a href=')(.*/article/.*</a>)", "$1http://www.prothom-alo.com/$2");

            RssItem item=new RssItem();
            item.setTitle(parsedLink.replaceAll("(<a href='.*/article/.*>)(.*)(</a>)","$2"));
            item.setLink(parsedLink.replaceAll("(<a href=')(.*/article/.*)('>.*</a>)","$2"));
            list1.add(item);
        }
        return list1;
    }
}
