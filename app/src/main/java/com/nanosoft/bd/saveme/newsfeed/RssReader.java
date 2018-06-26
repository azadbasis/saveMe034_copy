package com.nanosoft.bd.saveme.newsfeed;

import com.nanosoft.bd.saveme.newsfeed.prothomAlo.ProthomAlo;
import com.nanosoft.bd.saveme.newsfeed.bhorerKagoj.Bhorerkagoj;

import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class RssReader {

    private String rssUrl;

    public RssReader(String rssUrl) {
        this.rssUrl = rssUrl;
    }


    public List<RssItem> getItems() throws Exception {
        // SAX parse RSS data
        if (rssUrl.equals("http://www.prothom-alo.com/"))
            return ProthomAlo.GetLinks();
        if (rssUrl.equals("http://www.bhorerkagoj.net/online/"))
            return Bhorerkagoj.GetLinks();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        RssParseHandler handler = new RssParseHandler();

        saxParser.parse(rssUrl, handler);


        return handler.getItems();

    }

}
