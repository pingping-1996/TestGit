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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhaibaowangListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NhaibaowangListRe.class);

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
            System.out.println("20190219海报网："+frist);
            String nextpage2 = "";
            if(StringUtils.isNotEmpty(nextPage)){
                if(frist.equals("1")){
                    //String url2 = url.substring(0,url.indexOf("&p"));
                    nextpage2 = url + "&page=" + nextPage;
                //http://www.haibao.com/search/index.html?name=%E6%AC%A7%E8%8E%B1%E9%9B%85&type=2&page=1&kw=&orderType=1&isHighLight=1
                //http://www.haibao.com/search/index.html?name=欧莱雅&type=2&page=2
                }else{
                    String url2 = url.substring(0,url.indexOf("&p"));
                    nextpage2 = url2+"&page="+nextPage;
                }
                Map nextpage_ = new HashMap();
                nextpage_.put("link", nextpage2);
                nextpage_.put("rawlink", nextpage2);
                nextpage_.put("linktype", "newslist");

                String nums = doc.select(".searchBarNum span").text();
                System.out.println("共搜索到*******" + nums);

                String nextpage3 = doc.select(".pages a:contains(上一页)").attr("number");
                System.out.println(nextpage3);
                if(nums.equals("999+")){
                    if(!nextpage3.equals("99")){
                        tasks.add(nextpage_);
                        System.out.println("不是最后一页=============");
                        resultData.put("nextpage_", nextpage_);
                        resultData.put("nextpage",nextpage2);
                        resultData.put("tasks", tasks);
                    }
                }else{
                    Integer in = Integer.valueOf(nums);
                    Integer nextpagee = Integer.valueOf(nextPage);
                    int in2 = in/10;
                    if(nextpagee<=in2){
                        tasks.add(nextpage_);
                        System.out.println("不是最后一页=============");
                        resultData.put("nextpage_", nextpage_);
                        resultData.put("nextpage",nextpage2);
                        resultData.put("tasks", tasks);
                    }

                }

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