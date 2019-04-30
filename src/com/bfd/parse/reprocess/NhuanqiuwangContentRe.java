package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NhuanqiuwangContentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NhuanqiuwangContentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String,Object>> tasks=(List)resultData.get("tasks");

        String url = unit.getUrl();
//http://world.huanqiu.com/exclusive/2019-04/14779472.html?agt=25210
        //匹配页码
        String pattern = "/(\\d+).html";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        String news_id = "";
        if(m.find()){
            news_id = m.group(1);
        }

        resultData.put("news_id",news_id);
        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}
