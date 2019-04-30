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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NchinadailyListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NchinadailyListRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String,Object>> tasks=(List)resultData.get("tasks");

        String url = unit.getUrl();

        String html = unit.getPageData();
        Document doc = Jsoup.parse(html);

        //循环查找
        //String text = doc.select("#div_currpage span").text();
        Boolean text = false;
        Elements elements = doc.select("#div_currpage a");
        for (Element element :elements) {
            String page = element.text();
            if(page.equals("下一页")){
                text = true;//19页
            }
        }

        String pattern = "page_(\\d+).html";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(url);
        String next = "";
        String nextpage="";

        if(m.find()){
            next = m.group(1);
            Integer nextint = Integer.valueOf(next);
            nextint ++;
            String url2 = url.replace(m.group(),"");
            nextpage = url2+"page_"+nextint+".html";
            resultData.put("nextpage", nextpage);
            Map map = new HashMap();
            map.put("link",nextpage);
            map.put("linktype","newslist");
            map.put("rawlink",nextpage);

            tasks.add(0,map);
            resultData.put("tasks", tasks);

        }else{
            nextpage = unit.getUrl()+"/page_2.html";
            resultData.put("nextpage", nextpage);
            Map map = new HashMap();
            map.put("link",nextpage);
            map.put("linktype","newslist");
            map.put("rawlink",nextpage);

            tasks.add(0,map);
            resultData.put("tasks", tasks);

        }
        if(text==false){
            tasks.remove(0);
            resultData.put("nextpage", "");
        }





//https://china.chinadaily.com.cn/5bd5639ca3101a87ca8ff636/page_2.html


        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}
