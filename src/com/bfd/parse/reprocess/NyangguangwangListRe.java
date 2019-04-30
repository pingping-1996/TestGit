package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NyangguangwangListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NyangguangwangListRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        //List<Map<String, Object>> items = (List)resultData.get("items");
        List<Map<String, Object>> items = new ArrayList<>();

        List<Map<String,Object>> tasks = (List)resultData.get("tasks");

        String html  = unit.getPageData();
        Document doc = Jsoup.parse(html);




        //取最后一页
        String lastpage = "";
        Integer lastpage2 = 0;
        String pattern = "createPageHTML\\((\\d+),";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(html);
        if(m.find()){
            lastpage = m.group(1);
            lastpage2 = Integer.valueOf(lastpage);
            System.out.println(lastpage2);

        }

        //取最后一页
        String pattern3 = "pageHtml\\((\\d+),";
        Pattern r3 = Pattern.compile(pattern3);
        Matcher m3 = r3.matcher(html);
        if(m3.find()){
            lastpage = m3.group(1);
            lastpage2 = Integer.valueOf(lastpage);
            System.out.println(lastpage2);

        }
        //pageHtml(100,

        //匹配列表页页码
        String pagenow =  "";
        //index_(\\d+).html
        String pattern2= "index_(\\d+).html";
        Pattern r2 =  Pattern.compile(pattern2);
        Matcher m2 =  r2.matcher(unit.getUrl());
        String url ="";
        if(m2.find()){
            pagenow = m2.group(1);
            Integer pagenow2 = Integer.valueOf(pagenow);
            pagenow2++;
            if(pagenow2<lastpage2){
                //下一页
                url = unit.getUrl().replace(m2.group(),"");
                String nextpage = url+"index_"+pagenow2+".html";
                Map map = new HashMap();
                map.put("link",nextpage);
                map.put("linktype","newslist");
                map.put("rawlink",nextpage);
                tasks.add(0,map);
                resultData.put("nextpage",nextpage);
            }else{
                url = unit.getUrl().replace(m2.group(),"");

                resultData.put("nextpage","");

            }
        }else{
            url = unit.getUrl();
            //第一页加上页码
            String nextpage = unit.getUrl()+"index_1.html";
            Map map = new HashMap();
            map.put("link",nextpage);
            map.put("linktype","newslist");
            map.put("rawlink",nextpage);
            tasks.add(0,map);
            resultData.put("nextpage",nextpage);
        }

        int i = 1;
//
        Elements elements = doc.select(".wh581.margin h3 a");

        for (Element element: elements) {
            System.out.println("==="+i);
            String title = element.text();
            String href2 = element.attr("href");
            String href = "";
            if(href2.contains("http")){
                href = href2;
            }else{
                href = url + element.attr("href").replaceFirst("./","");

            }

            Map map = new HashMap();
            Map map2 = new HashMap();
            Map map3 = new HashMap();

            map2.put("link",href);
            map2.put("linktype","newscontent");
            map2.put("rawlink",href);


            map.put("title",title);
            map.put("link",map2);

            map3.put("link",href);
            map3.put("linktype","newscontent");
            map3.put("rawlink",href);

            items.add(map);
            tasks.add(map3);

        }

        Elements elements2 = doc.select(".erji_left li span.bt");
        for (Element element: elements2) {
            String title = element.text();
            String href2 = element.attr("href");
            String href = "";
            if(href2.contains("http")){
                href = href2;
            }else{
                href = url + element.attr("href").replaceFirst("./","");

            }            Map map = new HashMap();
            Map map2 = new HashMap();
            Map map3 = new HashMap();

            map2.put("link",href);
            map2.put("linktype","newscontent");
            map2.put("rawlink",href);


            map.put("title",title);
            map.put("link",map2);

            map3.put("link",href);
            map3.put("linktype","newscontent");
            map3.put("rawlink",href);

            items.add(map);
            tasks.add(map3);

        }


        Elements elements3 = doc.select(".detail div h2 a");
         for (Element element: elements3) {
             String title = element.text();
             String href2 = element.attr("href");
             String href = "";
             if(href2.contains("http")){
                 href = href2;
             }else{
                 href = url + element.attr("href").replaceFirst("./","");

             }             Map map = new HashMap();
             Map map2 = new HashMap();
             Map map3 = new HashMap();

             map2.put("link",href);
             map2.put("linktype","newscontent");
             map2.put("rawlink",href);


             map.put("title",title);
             map.put("link",map2);

             map3.put("link",href);
             map3.put("linktype","newscontent");
             map3.put("rawlink",href);

             items.add(map);
             tasks.add(map3);

         }


//        .articleList  li .text  strong a
//        }

        Elements elements4 = doc.select(".articleList  li .text  strong a");
         for (Element element: elements4) {
             String title = element.text();
             String href2 = element.attr("href");
             String href = "";
             if(href2.contains("http")){
                 href = href2;
             }else{
                 href = url + element.attr("href").replaceFirst("./","");

             }             Map map = new HashMap();
             Map map2 = new HashMap();
             Map map3 = new HashMap();

             map2.put("link",href);
             map2.put("linktype","newscontent");
             map2.put("rawlink",href);


             map.put("title",title);
             map.put("link",map2);

             map3.put("link",href);
             map3.put("linktype","newscontent");
             map3.put("rawlink",href);

             items.add(map);
             tasks.add(map3);

//              createPageHTML(34
         }

        Elements elements5 = doc.select("#list .yahei a");
        for (Element element: elements5) {
            String title = element.text();
            String href2 = element.attr("href");
            String href = "";
            if(href2.contains("http")){
                href = href2;
            }else{
                href = url + element.attr("href").replaceFirst("./","");

            }            Map map = new HashMap();
            Map map2 = new HashMap();
            Map map3 = new HashMap();

            map2.put("link",href);
            map2.put("linktype","newscontent");
            map2.put("rawlink",href);

            map.put("title",title);
            map.put("link",map2);

            map3.put("link",href);
            map3.put("linktype","newscontent");
            map3.put("rawlink",href);

            items.add(map);
            tasks.add(map3);

            // createPageHTML(34
        }


        //地方：http://news.cnr.cn/local/    本模块不用关心页码，一页加载完


        /*String json = html.substring(html.indexOf("var collection ="),html.indexOf("];"));
        String json2 = json.replace("var collection = ","")+"]";
        System.out.println(json2.split("}").length);
        String[] arry = json2.split("}");
        for(int j = 0;j<arry.length-1;j++){
            String index = arry[j];
            String title2 = index.substring(index.indexOf("title: \""),index.indexOf("content: "));
            String title = title2.replace("title: \"","")
                    .replace("\",","");
            String url = index.substring(index.indexOf("url: \""),index.indexOf("shtml"));
            String href = url.replace("url: \"","")+"shtml";
            System.out.println("woshiwoshi==="+href);

            System.out.println("woshiwoshi"+title+href);
            Map map = new HashMap();
            Map map2 = new HashMap();
            Map map3 = new HashMap();

            map2.put("link",href);
            map2.put("linktype","newscontent");
            map2.put("rawlink",href);

            map.put("title",title);
            map.put("link",map2);

            map3.put("link",href);
            map3.put("linktype","newslist");
            map3.put("rawlink",href);

            System.out.println("woshiwoshi==="+map);
            items.add(map);
            tasks.add(map3);


        }
*/


        //#list .yahei a
        resultData.put("items", items);
        resultData.put("tasks", tasks);

        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}

