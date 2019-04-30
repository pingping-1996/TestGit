package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import scala.util.matching.Regex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NchinaeconomyListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NchinaeconomyListRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String,Object>> tasks=(List)resultData.get("tasks");

        String html = unit.getPageData();
        Document doc = Jsoup.parse(html);

        String pattern = "createPageHTML\\((\\d+)\\,";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(html);
        String last = "";
        if (m.find()){
            last = m.group(1);
        }
        Integer pagelast = Integer.valueOf(last);


        String url = unit.getUrl();
//如果截取到、或者截取到为首页
        String index = "index_(\\d+).shtml";
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
            if(i<pagelast){
                nextpage = url2+"index_"+i+".shtml";
                resultData.put("nextpage",nextpage);
                /* {
      "iid": "05480749cb94c2256cf61afd0e94e82f",
      "link": "http://mil.huanqiu.com/strategysituation/2.html",
      "linktype": "newslist",
      "rawlink": "http://mil.huanqiu.com/strategysituation/2.html"

    },*/
                Map map = new HashMap();
                map.put("link",nextpage);
                map.put("linktype","newslist");
                map.put("rawlink",nextpage);
                tasks.add(0,map);
            }else{
                 resultData.put("nextpage","");
            }
        }else{

            nextpage = url + "index_1.shtml";
            resultData.put("nextpage",nextpage);
            Map map = new HashMap();
            map.put("link",nextpage);
            map.put("linktype","newslist");
            map.put("rawlink",nextpage);
            tasks.add(0,map);

        }


        //resultData.put("",);



        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}
