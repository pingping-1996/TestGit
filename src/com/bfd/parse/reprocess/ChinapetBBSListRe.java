package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.reprocess.ReProcessResult;
import com.bfd.parse.reprocess.ReProcessor;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class ChinapetBBSListRe  implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(ChinapetBBSListRe.class);

    public ChinapetBBSListRe() {
    }

    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        String html = unit.getPageData();
        Document doc = Jsoup.parse(html);
        try {
            Element elements=doc.select(".forumline").get(1);
            List<Map<String, Object>> items = new ArrayList<>();
            List<Map<String,Object>> tasks=(List)resultData.get("tasks");

            for(Element element:elements.select("tbody tr:gt(0)")){
                if(element.select(".catBottom").size()==0){
                    String itemname=element.select(".row1 span.topictitle a.topictitle").text();
                    String reply_cnt=element.select(".row2 .postdetails").get(0).text();
                    String view_cnt=element.select(".row2 .postdetails").get(1).text();
                    String posttime=element.select(".row3Right .postdetails").html().split("<br")[0].toString();
                    String href=element.select(".row1 span.topictitle a.topictitle").attr("href");
                    href="http://www.chinapet.net/bbs/"+href;
                    Map map = new HashedMap();
                    map.put("link",href);
                    map.put("rawlink", href);
                    map.put("linktype", "bbspostlist");

                    tasks.add(map);

                    Map mapP1 = new HashedMap();
                    mapP1.put("itemlink", map);
                    mapP1.put("itemname", itemname);
                    mapP1.put("reply_cnt",reply_cnt);
                    mapP1.put("posttime",posttime);
                    mapP1.put("view_cnt",view_cnt);

                    items.add(mapP1);
                }

            }
            Elements pages=doc.select("form").get(0).select("table[align=center]").select("td[align=right]").select("span.nav a");
            for(Element page:pages){
                if(page.text().contains("下一个")){
                    resultData.put("nextpage","http://www.chinapet.net/bbs/"+page.attr("href"));
                    Map task=new HashMap();
                    task.put("link","http://www.chinapet.net/bbs/"+page.attr("href"));
                    task.put("rawlink","http://www.chinapet.net/bbs/"+page.attr("href"));
                    task.put("linktype","bbspostlist");
                    tasks.add(task);
                }
            }

            resultData.put("items",items);
            resultData.put("tasks",tasks);
            resultData.put("cate",doc.select("form").get(0).select("tr:eq(2)").get(0).select("td:eq(1)").text());
        } catch (Exception e) {
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }
        ParseUtils.getIid(unit, parseResult);
        return new ReProcessResult(processcode, processdata);
    }

}
