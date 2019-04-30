package com.bfd.parse.haibao;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.reprocess.ReProcessResult;
import com.bfd.parse.reprocess.ReProcessor;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HaibaoCommentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(HaibaoCommentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String,Object>> tasks=(List<Map<String,Object>>)resultData.get("tasks");
        String html = unit.getPageData();
        String url =unit.getUrl();
        Document doc = Jsoup.parse(html);
        try {
            Integer currentPage=1;
            int totalPageNum =1;
            Integer totalRecord=0;
            if(url.contains("page=")){
                Pattern r = Pattern.compile("page=(\\d+)");
                Matcher m = r.matcher(url);
                if (m.find()) {
                    currentPage=Integer.parseInt(m.group(1));
                }
            }else if(currentPage==1){
                String str=doc.select(".cmts-title.cmts-hide .gfl").text().replace("全部评论(","")
                        .replace(")","");
                totalRecord=Integer.parseInt(str);
                totalPageNum = (totalRecord  +  30  - 1) / 30;
            }


                if(currentPage<totalPageNum){
                    if(totalRecord>0){
                        String commenturl="";
                        commenturl="https://www.guokr.com/article/442577/?page=2#comments";

                        resultData.put("comment_url", commenturl);
                        Map task=new HashMap();
                        task.put("link",commenturl);
                        task.put("rawlink",commenturl);
                        task.put("linktype","newscomment");
                        tasks.add(task);
                        resultData.put("tasks",tasks);
                    }else{
                        resultData.put("comment_url", "");
                        resultData.put("tasks",tasks);
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