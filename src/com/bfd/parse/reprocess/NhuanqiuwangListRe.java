package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NhuanqiuwangListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NhuanqiuwangListRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String,Object>> tasks=(List)resultData.get("tasks");



//http://world.huanqiu.com/article/2.html?agt=25210
        String url = unit.getUrl();
        String index = "/(\\d+).html";
        Pattern r2 = Pattern.compile(index);
        Matcher m2 = r2.matcher(url);
        String yema = "";
        String nextpage = "";
        String url2 = "";
        int i = 0;
        if(m2.find()){
            url2 = url.replace(m2.group(),"");
            yema = m2.group(1);
            i = Integer.valueOf(yema);
            i++;
            nextpage = url2+"/"+i+".html";
            resultData.put("nextpage",nextpage);
            Map map = new HashMap();
            map.put("link",nextpage);
            map.put("linktype","newslist");
            map.put("rawlink",nextpage);
            tasks.add(0,map);
        }else{
            nextpage = url + "2.html";
            resultData.put("nextpage",nextpage);
            Map map = new HashMap();
            map.put("link",nextpage);
            map.put("linktype","newslist");
            map.put("rawlink",nextpage);
            tasks.add(0,map);

        }



        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}
