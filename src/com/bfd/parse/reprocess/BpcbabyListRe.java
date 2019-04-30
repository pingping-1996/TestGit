package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.jsoup.Jsoup.*;

public class BpcbabyListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(BpcbabyListRe.class);

    public BpcbabyListRe() {
    }

    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace) {
		int processcode = 0;
        Map<String, Object> processdata = new HashMap();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String time = df.format(new Date());// new Date()为获取当前系统时间
		//System.out.println("pcbaby列表页======"+doc);
		try{
			Map<String, Object> resultData = parseResult.getParsedata().getData();
 			List<Map<String,Object>> tasks= (List)resultData.get("tasks");
			String html = unit.getPageData();
			Document doc = parse(html);
			String url = unit.getUrl();
			Elements elements=doc.select(".aListDl");
			List<Map<String, Object>> items = new ArrayList<>();
			for(Element element:elements){
 				String title = element.select(".oh .aList-title a").attr("title");//帖子主题
				String reply = element.select(".lt-num .red").text();//回复数
				String href  = element.select(".aList-title a").attr("href");
				System.out.println("20190219pcbaby的所有信息："+"帖子主题："+title+"回复："+reply+"链接："+href);
  				Map map = new HashedMap();

					map.put("link", "https:"+href);
					map.put("rawlink", "https:"+href);
					map.put("linktype", "bbspost");
 					System.out.println(map);

					tasks.add(map);
				
				Map mapP1 = new HashedMap();
                    mapP1.put("itemlink", map);
                    mapP1.put("itemname", title);
                    mapP1.put("reply_cnt",reply);
                    mapP1.put("posttime",time);
                    //mapP1.put("view_cnt",view_cnt);

					items.add(mapP1);
			//论坛名称（没有）
			//浏览数（没有）
			//发表日期（没有）

				}
				String urll = "";
				System.out.println("pcbaby20190202=======================================20190202");
			    String nextpagee = doc.select(".pcbaby_page .next").attr("class");
				System.out.println("201902131056判断条件1800："+nextpagee+"  nextpagee");
			    if(nextpagee.equals("")){
 				}else{
					System.out.println("pcbaby不是最后一页");
					urll = doc.select(".l-box .pcbaby_page .next").attr("href");
					urll = "https:"+urll;
 					Map task=new HashMap();
					task.put("link",urll);
					task.put("rawlink",urll);
					task.put("linktype","bbspostlist");

					tasks.add(task);
				}
				/*本页页码*/
			    String pageidx = doc.select(".pcbaby_page span").text();
			    resultData.put("pageidx",pageidx);
 			    resultData.put("items",items);
                resultData.put("tasks",tasks);
				resultData.put("nextpage",urll);


		}catch (Exception e){
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }
        ParseUtils.getIid(unit, parseResult);
        return new ReProcessResult(processcode, processdata);
    }
		
}
	