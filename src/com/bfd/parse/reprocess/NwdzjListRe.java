package com.bfd.parse.reprocess;

import com.alibaba.fastjson.JSONObject;
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

public class NwdzjListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NwdzjListRe.class);
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace){
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        List<Map<String, Object>> items = new ArrayList<>();
        List<Map<String,Object>> tasks=(List)resultData.get("tasks");
        try{
            /*
             * 获取动态数据返回信息
             * */
            String html = unit.getPageData();
            //System.out.println(html);
            if(html.indexOf("登录查看更多") != -1){
                System.out.println("cookie失效了");
                processcode = 500019;
            }else{
                Document doc = Jsoup.parse(html);
                Elements listItems = doc.select(".so-tzbox li");
                String regex = "\\d{4}-\\d{1,2}-\\d{1,2} \\d{2}:\\d{2}";
                for(Element listItem : listItems){
                    JSONObject jsonitem = new JSONObject();
                    JSONObject jsonlink = new JSONObject();
                    //获取当前帖子链接
                    jsonlink.put("link","https:"+listItem.select("a.tit").attr("href"));//帖子链接
                    jsonlink.put("rawlink","https:"+listItem.select("a.tit").attr("href"));//
                    jsonlink.put("linktype","newscontent");//链接类型
                    tasks.add(jsonlink);
                    //获取帖子列表相关信息
                    jsonitem.put("title",listItem.select(".tit").text());
                    String time = listItem.select(".ts").text();
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(time);
                    if (matcher.find()) {
                        jsonitem.put("posttime",matcher.group());
                    }

                    jsonitem.put("link",jsonlink);
                    items.add(jsonitem);
                }
                resultData.put("items", items);
                resultData.put("tasks", tasks);
                resultData.put("category","1");

                String url =unit.getUrl();

                int new_page_num = 1;
                if(url.indexOf("currentPage") > -1){
                    new_page_num = Integer.parseInt(url.substring(url.indexOf("&currentPage=")+13));
                }
                //String copage_url = getStrByPrePost(url, "", "&currentPage=");
                String copage_url = "";
                int next_page_num = new_page_num + 1;
                int total_page_num = new_page_num;
                Elements page_infos = doc.select(".pageList .page");
                for(Element page_info:page_infos){
                    System.out.println(page_info.text());
                    if(page_info.text().equals("尾页")){
                        total_page_num = Integer.parseInt(page_info.attr("currentnum"));
                    }
                }



                System.out.println("总页数："+total_page_num);
                //String  next_page_url = copage_url+"&currentPage="+next_page_num;
                String  next_page_url = "";
                System.out.println("total_page_num:"+total_page_num);
                if(next_page_num <= total_page_num){
                    if(url.indexOf("currentPage") > -1){
                        next_page_url = url.substring(0,url.indexOf("&currentPage=")+13) + next_page_num;
                    }else{
                        next_page_url = url+ "&currentPage=2";
                    }
                    resultData.put("nextpage",next_page_url);
                    Map nextpage_ = new HashMap();
                    nextpage_.put("link", next_page_url);
                    nextpage_.put("rawlink", next_page_url);
                    nextpage_.put("linktype", "newslist");
                    resultData.put("nextpage_", nextpage_);
                    tasks.add(nextpage_);
                    resultData.put("type","newslist");
                    resultData.put("cid","Nwangdaizhijia");
                    resultData.put("url",url);
                }else if(next_page_num == total_page_num){
                    resultData.put("nextpage","");
                    resultData.put("type","newscontent");
                    resultData.put("cid","Nwangdaizhijia");
                    resultData.put("url",url);
                }
            }


        }catch (Exception e){
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
    }

//    public static void main(String[] args) {
//        String url = "https://www.wdzj.com/wdzj/front/search/index?referer=//www.wdzj.com/wdzj/front/search/index&key=%25E5%258D%2597%25E4%25BA%25AC";
//        String str = url.substring(url.indexOf("&currentPage=")+13);
//        System.out.println("str:"+str);
//    }

    public static String getStrByPrePost(String str, String pre, String post) {
        if (str != null) {
            if (pre != null) {
                int s = str.indexOf(pre);
                if (s > -1) {
                    str = str.substring(s + pre.length(), str.length());
                } else {
                    return null;
                }

            }
            if (post != null) {
                int e = str.indexOf(post);
                if (e > -1) {
                    str = str.substring(0, e);
                } else {
                    return null;
                }

            }
        }
        return str;
    }

}
