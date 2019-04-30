package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.collections.map.HashedMap;
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

public class BxcarContentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(BxcarContentRe.class);

    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace) {
        System.out.println("201902251758");
        int processcode = 0;
        HashMap processdata = new HashMap();
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        String html = unit.getPageData();
        Document doc = Jsoup.parse(html);
        try{

            if (html.contains("please open cookie!")) {
                System.out.println("cookie失效了请更新！");
                processcode = 500019;
            }else{

            if (resultData != null && resultData.size() > 0) {
                List<Map<String, Object>> replys = new ArrayList<>();
                List<Map<String, Object>> tasks = (List) resultData.get("tasks");
                /*帖子詳情主題*/
                String title = doc.select(".maintop .title").text();
                String title2 = title.substring(title.indexOf(">")).replace(">  ", "");
                resultData.put("title",title2);
                System.out.println("title2================"+title2);
                /*当前url*/
                String url = unit.getUrl();
                //循环
                Elements elements = doc.select(".main .item");
                String post_id2 = "";
                String regEx = "tid=\\d+";
                Pattern p =Pattern.compile(regEx);
                Matcher m = p.matcher(url);
                if(m.find()){
                    String post_id = m.group();
                    post_id2 = post_id.replace("tid=","");
                }

                for (Element element : elements) {
                    //地址
                    String dizhi = "";
                    if(element.select(".ursr_info span").size()>1){
                        dizhi = element.select(".ursr_info span").get(1).text()
                            .replace("来自 ","");
                    }
                    //post_id
                    String reply_id = "";
                    if(doc.select(".main .item a").size()>0){
                        reply_id = element.select("a").get(0).attr("name")
                                .replace("pid","");
                    }

                    //answer_id
                    String answer_id2 = "";
                    if(element.select(".msgbody .msgheader a").size()>1){
                        String answer_id = element.select(".msgbody .msgheader a").get(1)
                                .attr("href");
                        String regex = "pid=(\\d+)";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(answer_id);
                        if(matcher.find()){
                            answer_id2 = matcher.group(1);
                        }
                    }
                    //各层等级
                    String dengji = element.select(".starname span").text();
                    //各层经验值 需要判断是否为空 取不到alt属性
                    String jingyan = element.select(".otherinfo .star .img").attr("alt");
                    System.out.println(jingyan);
                    //楼层
                    String louceng = element.select(".t_number").text();
                    //发表时间  判断 截取  正则匹配不上
                    String time = element.select(".mainboxTop p").text().replace("发表于 ", "");
                    String regex = "\\d+-\\d+-\\d+ \\d+:\\d+";
                    Pattern pattern4 = Pattern.compile(regex);
                    Matcher matcher4 = pattern4.matcher(time);
                    if (matcher4.find()) {
                         System.out.println("时间是：" + matcher4.group());
                    }
                    String name = element.select(".name a").text();//回复人
                    //回复内容  replys   有问题
                    element.select(".msgbody .msgheader").empty();
                    String connect = element.select(".t_msgfont1").text();
                    //回复人链接
                    String huifulink = element.select(".avatar a").attr("href");
                    //发帖数
                    String reply_post_cnt = element.select(".tznum a em")
                            .text();
                    //http://www.xcar.com.cn/bbs/viewthread.php?tid=34174524
                    //http://www.xcar.com.cn/bbs/viewthread.php?tid=34174524&page=2

                    String floorr = "";
                    if(louceng.equals("沙发")){
                        floorr = "2";
                    }else if(louceng.equals("板凳")){
                        floorr = "3";
                    }else if(louceng.equals("地板")){
                        floorr = "4";
                    }else if(louceng.equals("地下室")){
                        floorr = "5";
                    }else{
                        floorr = louceng.replace("楼","");
                    }
                    System.out.println("201903051714pcbaby楼================"+floorr);
                    Integer floor2 = Integer.valueOf(floorr);
                    Map map = new HashedMap();
                    map.put("reply_id",reply_id);
                    map.put("contents", connect);
                    map.put("replydate", matcher4.group());
                    map.put("replyusername", name);
                    map.put("replyfloor", floor2);
                    map.put("post_id",post_id2);
                    map.put("reply_user_city", dizhi);
                    map.put("reply_level",dengji);
                    if(!answer_id2.isEmpty()){
                        map.put("answer_id",answer_id2);
                    }
                    map.put("reply_post_cnt",reply_post_cnt);
                    replys.add(map);
                    }
                    String num11 = doc.select(".fn_0209 .active").text();
                    if(num11.equals("1 1")){
                        System.out.println("第一页呀");
                        replys.remove(0);
                }
                resultData.put("replys", replys);//放入回复内容集合

                //判断是否为首页 首页取楼主信息
                String chakan2 = "";
                String huifu = "";
                String huifuu = "";
                String huifuuu = "";
                String num1 = doc.select(".fn_0209 .active").text();
                System.out.println(num1);
                if (num1.equals("1 1")) {
                    //查看数      √ （只有第一页有浏览数）
                    String chakan = doc.select(".mainboxTop span").text();
                    chakan2 = chakan.substring(3, chakan.indexOf(" |"));
                    System.out.println(chakan2);
                    //主贴回复数  √ (只有第一页有回复数)
                    huifu = doc.select(".mainboxTop span").text();
                    huifuu = huifu.substring(huifu.indexOf("|"));
                    System.out.println("wqewqedadsdsdcfds-------------------------------"+huifuu);
                    huifuuu = huifuu.replace("| 回复 ","");
                    System.out.println("wqewqedadsdsdcfds-------------------------------"+huifuuu);
                    Pattern pattern = Pattern.compile("\\d+$");
                    Matcher matcher = pattern.matcher(huifu);
                    if (matcher.find()) {
                        System.out.println("字符串sa是以数字结尾的，结尾的数字是：" + matcher.group());
                    }
                    //String huifu2 = String.valueOf(matcher.group());
                    System.out.println("yyy");
                    String dengji111 = doc.select(".starname span").get(0).text();//等级
                    //String jingyan111 = doc.select(".otherinfo .star .img").get(0).text();//经验
                    String time111 = doc.select(".mainboxTop p").get(0).text().replace("发表于 ", "");
                    String regex = "\\d+-\\d+-\\d+ \\d+:\\d+";
                    Pattern pattern4 = Pattern.compile(regex);
                    Matcher matcher4 = pattern4.matcher(time111);
                    if (matcher4.find()) {
                        System.out.println("时间是：" + matcher4.group());
                    }

                    String connect111 = doc.select(".t_msgfont1").get(0).text();
                    String name111 = doc.select(".name a").get(0).text();//作者

                    //只有第一页显示这些内容
                    resultData.put("contents", connect111);//主贴内容
                    resultData.put("author_level", dengji111);//类型
                    resultData.put("authorname", name111);
                    resultData.put("newstime", matcher4.group());
                    //resultData.put("author_avatar",jingyan111);
                }
                String luntan = doc.select(".titleStyle1 h3 a").get(2).text();
                double chakan3 = 0.00;
                if(chakan2.contains("W")){
                    chakan2 = chakan2.replace("W","");
                    System.out.println("wan2:"+chakan2);
                    chakan3  = Double.parseDouble(chakan2);
                    chakan3 = chakan3*10000;
                }
                resultData.put("view_cnt", chakan3);
                resultData.put("reply_cnt", huifuuu);
                resultData.put("cate", luntan);//论坛名称
                resultData.put("post_id", post_id2);
                //resultData.put("type", "bbspost");

                //doc.select().text();
                //判断是不是第一页
                if (num1.equals("1 1")) {
                    //这是第一页，   怎么判断有没有第二页？？？？？？？？
                    String num2 = doc.select(".fn_0209 .page").text();
                    System.out.println(num2);
                    if(num2.equals("")){
                        System.out.println("爱卡汽车评论就此一页");
                    }else{
                        String nextPage = url + "&page="+ 2;
                        Map task = new HashMap();
                        task.put("link", nextPage);
                        task.put("rawlink", nextPage);
                        task.put("linktype", "bbspost");
                        tasks.add(task);
                        resultData.put("nextpage", nextPage);
                    }
                }else{
                    //已经排除第一页（只有第一页和最后一页有page_no）
                    String last = doc.select("a.page_no").attr("class");
                    System.out.println("爱卡汽车last=========================="+last);
                    if(last.equals("page_no")){
                        System.out.println("最后一页");//不用生成下一页任务
                    }else{
                        String page = url.substring(url.length() - 1);//
                        System.out.println(page);
                        Integer page222 = Integer.valueOf(page);
                        int i = page222 + 1;
                        String url222 = url.substring(0, url.indexOf("&page="));
                        String nextPage = url222 + "&page=" + i;
                            Map task = new HashMap();
                            task.put("link", nextPage);
                            task.put("rawlink", nextPage);
                            task.put("linktype", "bbspost");
                            tasks.add(task);
                        System.out.println("不是爱卡汽车评论的最后一页");
                        resultData.put("nextpage", nextPage);


                    }
                }
                resultData.put("tasks", tasks);
            }
        }
            ///////////////////////差图片集合！！！！！！

        }catch(Exception e) {
            processcode = 500011;
            e.printStackTrace();
            System.out.println("Bxcar......==========================20190225");
        }
        ParseUtils.getIid(unit, parseResult);
        return new ReProcessResult(processcode, processdata);
    }
}
