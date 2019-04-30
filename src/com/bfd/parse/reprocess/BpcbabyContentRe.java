package com.bfd.parse.reprocess;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import com.sun.imageio.plugins.common.I18N;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bfd.crawler.utils.DataUtil;
import scala.runtime.Int;
import sun.org.mozilla.javascript.internal.ObjArray;

import static org.jsoup.Jsoup.parse;

public class BpcbabyContentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(BpcbabyContentRe.class);
        private static final Pattern patternpage = Pattern.compile("<span>1</span>");
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String, Object>> replys = new ArrayList<>();
        List<Map<String, Object>> contentimgs = new ArrayList<>();
        List<Map<String,Object>> tasks = (List)resultData.get("tasks");
        String html = unit.getPageData();
        Document doc = parse(html);
        try{
            if(!unit.getPageData().isEmpty()){
                 if(unit.getAjaxdata().size()>0){
                    Map<String, Object> jsonmap = unit.getAjaxdata().get(0);
                    System.out.println("pcbaby动态map"+jsonmap);
                    byte[] valuebyte = DataUtil.unzipAndDecode((String)jsonmap.get("data"));
                    String news = new String(valuebyte);
                    System.out.println("pcbaby解密后的ajax返回的data值为===" + news);
                    JSONObject jsonObject = JSON.parseObject(news);
                    Integer views_cnt = -1024;
                    if(jsonObject!=null){
                        String views = jsonObject.getString("views");
                        views_cnt = Integer.valueOf(views);
                    }
                     resultData.put("view_cnt",views_cnt);

                }

            Matcher matcherm = patternpage.matcher(html);
            if (resultData != null && resultData.size() > 0) {
                String url = unit.getUrl();
                //https://bbs.pcbaby.com.cn/topic-3942663.html
                //https://bbs.pcbaby.com.cn/topic-3942663-5.html
                String post_id = "";
                if (matcherm.find()) {
                    String regEx = "-(\\d+).";
                    Pattern p = Pattern.compile(regEx);
                    Matcher flag = p.matcher(url);
                    if (flag.find()) {
                         post_id = flag.group(1);
                    }
                    //post_id = url.substring(url.indexOf("topic-") + 6, url.length() - 5);
                } else {
                    String regEx = "-(\\d+)-";
                    Pattern p = Pattern.compile(regEx);
                    Matcher flag = p.matcher(url);
                    if (flag.find()) {
                        post_id = flag.group(1);
                    }
                }
                //post_id = post_id.substring(1,post_id.length()-1);
                resultData.put("post_id", post_id);//主题



                //每一页的第一层需要单抓取，并判断是不是第一页
                String regEx = "topic-\\d*-";//匹配不是第一页
                Pattern p = Pattern.compile(regEx);
                Matcher flag = p.matcher(url);
                String lll = "";
                if (flag.find()) {
                    lll = flag.group();//   不为空不是第一页
                }
                if (lll.equals("")) {//第一页
                    //contents
                    String contents111 = doc.select(".post_first .replyBody").text();
                    String name111 = doc.select(".post_wrap_first .fb a").text();
                    String time111 = doc.select(".post_wrap_first .post_time").text()
                            .substring(6);
                    String jifen111 = doc.select(".post_wrap_first .user_title nobr").text();
                    String jifen222 = jifen111.substring(jifen111.indexOf("积分") + 2, jifen111.indexOf(","));
                    String cate = doc.select(".header_crumb").text();
                    String author_avatar = doc.select(".avatar a img").get(0).attr("src");
                    System.out.println("楼主积分值：" + jifen222);
                    String title = doc.select(".post_r_tit h1").text();

                    //楼主的图片集合
                    Elements elements = doc.select(".post_main-pic");
                    for (Element element : elements) {
                        String pic = element.select(".img_link").attr("src");
                        //System.out.println("图片集合" + pic);
                        Map reply = new HashMap();
                        reply.put("img", pic);
                        reply.put("rawimg", pic);
                        contentimgs.add(reply);
                    }
                    resultData.put("contentimgs", contentimgs);//主贴图片集合
                    resultData.put("title", title);//主题
                    resultData.put("contents", contents111);//主贴内容
                    resultData.put("cate", cate);//论坛名称
                    resultData.put("newstime", time111);//发表时间
                    resultData.put("authorname", name111);//楼主名字
                    resultData.put("experience_cnt", jifen222);//楼主经验值
                    resultData.put("author_avatar", author_avatar);//楼主头像链接
                } else {
                    String con = doc.select(".replyBody .cite").text();
                    String contents211 = doc.select(".replyBody").get(0).text()
                            .replace(con,"");

                    String name211 = doc.select(".post_wrap_first .fb a").text();
                    String time211 = doc.select(".post_wrap_first .post_time").text()
                            .substring(6);
                    String jifen211 = doc.select(".post_wrap_first .user_title nobr").text();
                    String jifen311 = jifen211.substring(jifen211.indexOf("积分") + 2, jifen211.indexOf(","));
                    //String link211 = doc.select(".avatar a").attr("href");
                    //System.out.println(jifen211);
                    System.out.println("pcbaby的第一层积分值：" + jifen311);
                    String floor = doc.select(".post_floor em").get(0).text()
                            .replace("楼","");//楼层
                    Integer floor2 = Integer.valueOf(floor);
                    String rep = "";
                    String reply_id = doc.select(".post_wrap_first").attr("data-pid");
                    String answer_id = "";

                /*Map task = new HashMap();
                task.put("link",link211 );
                task.put("rawlink", link211);
                task.put("linktype","bbsuserinfo");
                tasks.add(task);*/
                    System.out.println("post_id===================="+post_id);
                    Map reply = new HashMap();
                    if(!doc.select(".cite").isEmpty()){
                        rep = doc.select(".cite a").get(0).attr("href");
                        String[] str =  rep.split("_");
                        answer_id =  str[1].replace(".html","");
                        reply.put("answer_id", answer_id);
                    }
                    reply.put("reply_id", reply_id);
                    reply.put("replydate", time211);
                    reply.put("replycontent", contents211);
                    reply.put("replyusername", name211);
                    //reply.put("replylink", task);
                    reply.put("replyfloor", floor2);//楼层
                    reply.put("post_id", post_id);//回复人链接
                    replys.add(reply);

                }

                //循环回复楼层
                //post_80111517
                    Elements elements = doc.select(".post_wrap");
                System.out.println("进入");
                for (Element element : elements) {
                    String rep_name = element.select(".fb a").text();//回复人用户名
                    String rep_time = element.select(".post_info .post_time").text()
                            .substring(6);//回复时间
                    //String rep_link = element.select(".fb a").attr("href");//回复人链接
                    String rep_jingyan = element.select(".user_title nobr").text();//回复人论坛积分
                    String rep_jingyan2 = rep_jingyan.substring(rep_jingyan.indexOf("积分") + 2, rep_jingyan.indexOf(","));
                    System.out.println(rep_jingyan);
                    System.out.println("积分值：" + rep_jingyan2);
                    String con = element.select(".replyBody .cite").text();
                    String rep_connect = element.select(".replyBody").text()
                            .replace(con,"");
                    String reply_id = element.attr("data-pid");



                    String floor = element.select(".post_floor em").text();//楼层
                    String floorr = "";
                    if(floor.isEmpty()){
                        continue;

                    }
                    String rep = "";
                    String answer_id = "";

                    System.out.println(rep_connect+"初始楼层=========="+ floor);
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
                    System.out.println("201903051714pcbaby楼================"+floorr);
                    Integer floor2 = Integer.valueOf(floorr);
                    /*Map task = new HashMap();
                    task.put("link",rep_link);
                    task.put("rawlink",rep_link);
                    task.put("linktype","bbsuserinfo");
                    tasks.add(task);*/
                    Map reply = new HashMap();
                    if(!doc.select(".cite").isEmpty()){
                        rep = doc.select(".cite a").get(0).attr("href");
                        String[] str =  rep.split("_");
                        answer_id =  str[1].replace(".html","");
                        reply.put("answer_id", answer_id);
                    }
                    reply.put("reply_id",reply_id);
                    reply.put("replydate", rep_time);
                    reply.put("replycontent", rep_connect);
                    reply.put("replyusername", rep_name);
                    reply.put("replyfloor", floor2);//楼层
                    reply.put("post_id", post_id);
                    //reply.put("replylink", task);
                    replys.add(reply);
                }
                resultData.put("replys", replys);//所有回复已写入

                //String liulan = doc.select("#views").text();//得不到浏览数需要js引擎完成
                String huifu = doc.select(".overView .yh").get(1).text();
                //System.out.println("浏览：" + liulan + "回复：" + huifu);
                resultData.put("reply_cnt", huifu);

                //获取最后一页信息
                if (!doc.select(".iNum").text().isEmpty()) {
                    String lastpage = doc.select(".iNum").get(0).text();
                    String lastpage2 = lastpage.substring(1, lastpage.length() - 1);
                    System.out.println("总页数：" + lastpage2);
                    Integer lastpage3 = Integer.valueOf(lastpage2);
                    System.out.println("pcbaby主题帖一共回复页数是201902192227：" + lastpage3);
                    //截取url
                    String url2 = "";
                    String nextPage = "";
                    if (lll.equals("")) {
                        System.out.println("2306是第一页啊！");
                    } else {
                        System.out.println("2306不是第一页啊！");
                    }
                    if (!lll.equals("")) {
                        System.out.println("不是pcbabay主题帖的第一页");
                        url2 = url.substring(0, url.indexOf("-"));
                        String url3 = url.substring(url.indexOf("-") + 1);//573767-2.html
                        String url4 = url3.substring(0, url3.indexOf("-"));//573767
                        String url5 = url3.substring(url3.indexOf("-") + 1, url3.indexOf("."));//页码
                        System.out.println("测试:" + url5);
                        Integer page = Integer.valueOf(url5);
                        int page2 = page + 1;
                        nextPage = url2 + "-" + url4 + "-" + page2 + ".html";
                        //resultData.put("nextPage", nextPage);
                        //生成下一页tasks
                        System.out.println("20190219pcbaby" + page);
                        System.out.println("20190219pcbaby总页数总页数：" + lastpage3);
                        if (page < lastpage3) {
                            Map task = new HashMap();
                            task.put("link", nextPage);
                            task.put("rawlink", nextPage);
                            task.put("linktype", "bbspost");
                            tasks.add(task);
                            resultData.put("nextPage", nextPage);
                            System.out.println("不是最后一页pcbaby20190219");
                        } else {
                            System.out.println("最后一页pcbaby20190219");
                            //resultData.put("nextPage","没有了！！！");
                        }
                        resultData.put("tasks", tasks);


                    } else {
                        System.out.println("201902192242");
                        //判断是否有下一页
                        if (lastpage3 >= 2) {
                            url2 = url.substring(0, url.indexOf(".html"));
                            System.out.println("如果是首页2230:" + url2);
                            nextPage = url2 + "-" + 2 + ".html";
                            System.out.println(nextPage);
                            //生成第二页的tasks
                            Map task = new HashMap();
                            task.put("link", nextPage);
                            task.put("rawlink", nextPage);
                            task.put("linktype", "bbspost");
                            tasks.add(task);
                            resultData.put("tasks", tasks);
                            resultData.put("nextPage", nextPage);
                        }
                    }
                }

            }
        }
    }catch (Exception e){
            e.printStackTrace();
    }
        ParseUtils.getIid(unit, parseResult);
        return new ReProcessResult(processcode, processdata);
    }
}
