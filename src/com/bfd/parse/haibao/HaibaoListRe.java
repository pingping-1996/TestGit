package com.bfd.parse.haibao;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.reprocess.ReProcessResult;
import com.bfd.parse.reprocess.ReProcessor;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HaibaoListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(HaibaoListRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        String html = unit.getPageData();
        Document doc = Jsoup.parse(html);
        String url =unit.getUrl();
        List<Map<String, Object>> tasks = (List<Map<String, Object>>) resultData.get("tasks");
        try {
            String nextPage = doc.select(".pages a:contains(下一页)").attr("number");
            String frist = doc.select(".pages .active").text();
            if(StringUtils.isNotEmpty(nextPage)){
                if(frist.equals("1")){
                    nextPage = url+"&page="+nextPage;///////////////需要改动////改完了
                //http://www.haibao.com/search/index.html?name=欧莱雅&type=1&page=2
                }else{
                    String url2 = url.substring(url.indexOf("&p"));
                    nextPage = url2+"&page="+frist;
                }
                resultData.put("nextpage",nextPage);

                Map nextpage_ = new HashMap();
                nextpage_.put("link", nextPage);
                nextpage_.put("rawlink", nextPage);
                nextpage_.put("linktype", "newslist");
                resultData.put("nextpage_", nextpage_);
                tasks.add(nextpage_);
                resultData.put("tasks", tasks);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }
        ParseUtils.getIid(unit, parseResult);
        // 解析结果返回值 0代表成功
        return new ReProcessResult(processcode, processdata);
    }

}