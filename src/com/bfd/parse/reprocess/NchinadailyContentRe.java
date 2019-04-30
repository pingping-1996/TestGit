package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NchinadailyContentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NchinadailyContentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String,Object>> tasks=(List)resultData.get("tasks");
//https://china.chinadaily.com.cn/a/201904/22/WS5cbd7c4ea310e7f8b157832f.html
        //匹配页码

        String str = unit.getUrl();
        str = str.split("/")[str.split("/").length-1].replace(".html","");



        resultData.put("news_id",str);
        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}