package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BpcautoConnectRe implements ReProcessor{
    private static final Log LOG = LogFactory.getLog(BpcautoConnectRe.class);
    private static final Pattern patternpage = Pattern.compile("<span>1</span>");
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result,
                                   ParserFace parseFace) {
        //加上post_id
        String url = unit.getUrl();
        //https://bbs.pcauto.com.cn/topic-11854908-2.html
        String regEx = "topic-\\d*-";
        Pattern p =Pattern.compile(regEx);
        Matcher m = p.matcher(url);
        if(m.matches()){
            System.out.println("什么鬼201902201118"+m.matches());
        }else{
            System.out.println("没毛病11130");
        }

        int processcode = 0;
        Map<String, Object> processdata = null;
        try {
            processdata = new HashMap<String, Object>();
            Map<String, Object> resultData = result.getParsedata().getData();

            //String html = unit.getPageData();
            if(m.matches()){
                resultData.remove("author_avatar");
                resultData.remove("contents");
                resultData.remove("newstime");
                resultData.remove("authorname");
                resultData.remove("author");
                resultData.remove("author_level");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ReProcessResult(processcode, processdata);
    }
}
