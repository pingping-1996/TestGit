package com.bfd.parse.preprocess;

import com.bfd.crawler.login.LoginPluginInterface;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import org.apache.http.client.config.RequestConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class Bxcar implements PreProcessor {

    public boolean process(ParseUnit unit, ParserFace parseFace){

        unit.getUrl();
        Map<String, String> a = new HashMap();
        return false;
    }


}
