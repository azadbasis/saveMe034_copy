package com.nanosoft.bd.saveme.newsfeed.prothomAlo;

/**
 * Created by Nanosoft-Android on 11/5/2016.
 */

public class ParsedLink {
    public ParsedLink(String text){
        Text=text;
    }
    public String GetTitle(){
        return Text.replaceAll("(<a href='.*/article/.*>)(.*)(</a>)","$2");
    }
    public String GetUrl(){
        return Text.replaceAll("(<a href=')(.*/article/.*)('>.*</a>)","$2");
    }
    public String Text;

    @Override
    public String toString() {
        return GetTitle();
    }
}
