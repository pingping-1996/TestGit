package com.bfd.parse.haibao;

import IceUtilInternal.StringUtil;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.reprocess.ReProcessResult;
import com.bfd.parse.reprocess.ReProcessor;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test implements ReProcessor {
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace) {
        int processcode = 0;
        Map<String,Object> resultData = parseResult.getParsedata().getData();
        String html = unit.getPageData();
        Document doc = Jsoup.parse(html);
        String url = unit.getUrl();
        //获取任务
        List<Map<String,Object>> tasks = (List<Map<String,Object>>)resultData.get("tasks");
        try{
        String nextPage = doc.select(".pages a:contains(下一页)").attr("number");
        if(StringUtils.isNotEmpty(nextPage)){
                nextPage = url+"&page="+nextPage;
                resultData.put("nextpage",nextPage);
                Map<String,Object> nextpage_ = new HashMap<String ,Object>();
                nextpage_.put("link", nextPage);
                nextpage_.put("rawlink", nextPage);
                nextpage_.put("linktype", "newslist");
                resultData.put("nextpage_", nextpage_);
                tasks.add(nextpage_);
                resultData.put("tasks", tasks);
        }
        //添加一项任务


        }catch(Exception e){}

        doc.select(".pages a:contains(下一页)").attr("number");


        return null;
    }
}
