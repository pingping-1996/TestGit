package com.bfd.parse.reprocess;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bfd.crawler.utils.DataUtil;
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

public class BpcautoContentRe implements ReProcessor{
    private static final Log LOG = LogFactory.getLog(BpcautoContentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        System.out.println("太平洋汽车网201902210718");
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String,Object>> replys = (List)resultData.get("replys");
        try {
            Document doc=Jsoup.parse(unit.getPageData());
            /*String html = unit.getPageData();
            Document doc = Jsoup.parse(html);*/
            Elements replysEles=doc.select("#post_list>.post_wrap.postids");
            String floorr = "";

            for(Element replyEle:replysEles){
                String floor=replyEle.select(".post_floor").text();
                if(floor.equals("沙发")){
                    floorr = "2";
                }else if(floor.equals("板凳")){
                    floorr = "3";
                }else if(floor.equals("地板")){
                    floorr = "4";
                }else if(floor.equals("地下室")){
                    floorr = "5";
                }else{
                    floorr = floor.replace("楼","");
                }
                //String post_floor=replyEle.select(".post_floor").text().replace("楼","");
                b:for(Map<String,Object> map:replys){

                    if(map.containsKey("replyfloor")){
                        System.out.println("201903131049太平洋汽车");
                        String replyfloor=map.get("replyfloor").toString();
                        System.out.println("shisha==="+map.get("replyfloor").toString());
                        if(floorr.equals(replyfloor)){
                            String rep = "";
                            if(!replyEle.select(".cite").isEmpty()){
                                System.out.println("201903131030太平洋汽车");
                                rep = replyEle.select(".cite font a").get(1).attr("href");
                                String[] str =  rep.split("_");
                                map.put("answer_id",str[1].replace(".html",""));
                            }
                            String[] numid = replyEle.attr("id").split("_");
                            map.put("reply_id",numid[1]);
                            replyEle.select("td.post_msg_wrap .cite").empty();
                            map.put("replycontent",replyEle.select("td.post_msg_wrap .post_msg.replyBody").text());

                            break b;
                        }
                    }

                }
            }

//            for(Map<String,Object> map:replys){
//                String replyfloor=map.get("replyfloor").toString();


//                if(map.get("replycontent")!=null&&!map.get("replycontent").equals("")){
//                    String replycontent=map.get("replycontent").toString();
//
//                }
            //}

            System.out.println("太平洋汽车网201902210714");

            //String single = doc.select(".post_r_tit a").attr("class");
            //System.out.println("single========"+single);
            //判断是不是第一页
            String url = unit.getUrl();
            //https://bbs.pcauto.com.cn/topic-11854908-2.html
            String regEx = "topic-\\d*-";//匹配不是第一页
            Pattern p =Pattern.compile(regEx);
            Matcher flag = p.matcher(url);
            String lll="";
            if(flag.find()){
                lll = flag.group();
            }
            String urll = url.replace(".html","");
            String[] urlll = urll.split("-");
            String urllll = urlll[1];

            if(resultData.containsKey("replys")){
                List<Map<String,Object>> comments = (List<Map<String,Object>>) resultData.get("replys");
                for(Map<String,Object> map : comments){
                    if(!map.containsKey("post_id")){
                            map.put("post_id",urllll);
                    }
                }
            }

            if (resultData.containsKey("replys")) {
                List<Map<String, Object>> comments = (List<Map<String, Object>>) resultData.get("replys");
                for (Map<String, Object> map : comments) {
                    if (map.size()>0) {
                        if(map.get("replyfloor")!=null){
                            String replyfloor = map.get("replyfloor").toString();
                            map.put("replyfloor", Integer.valueOf(replyfloor));
                        }

                    }
                }
            }

                 resultData.put("post_id",urllll);

                if(!lll.equals("")){
                    System.out.println("这不是第一页");
                    String title = doc.select("#subjectTitle").text();//帖子主题
                    Elements elements  = doc.select(".post_wrap");
                    List<Map<String,Object>> tasks = (List)resultData.get("tasks");
                    resultData.remove("author_avatar");
                    resultData.remove("contents");
                    resultData.remove("newstime");
                    resultData.remove("authorname");
                    resultData.remove("author");
                    resultData.remove("author_level");
                    resultData.remove("uesr_city");
                    resultData.remove("fans_cnt");
                    resultData.remove("post_cnt");
                    resultData.remove("essence_cnt");

                    for(Element element:elements){
                        String reply_con = element.select(".replyBody").text();//回复内容
                        String posttime = element.select(".post_time").text()
                                .replace("发表于 ","");//发表时间
                        if(posttime.isEmpty()){
                            continue;
                        }
//                        String floor = element.select(".post_floor em").text();//楼层
//                        String use_name = element.select(".ofw a").get(0).text();//名字
                    }
                 }else{
                    System.out.println("这是第一页");
                    String content = doc.select(".post_main .post_msg").get(0).text();
                    resultData.put("contents",content);
                    replys.remove(0);


                }
                /*Elements elements  = doc.select(".post_wrap");
                for(Element element:elements) {
                    String reply_con2 = element.select(".cite").text();
                    String reply_con = element.select(".replyBody").text()
                            .replace(reply_con2, "");//回复内容
                    System.out.println("==========" + reply_con);
                } */



        } catch (Exception e) {
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
            System.out.println("太平洋汽车网后处理插件有问题");
        }
        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}
