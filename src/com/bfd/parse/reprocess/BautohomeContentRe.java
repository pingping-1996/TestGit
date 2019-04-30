package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
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

public class BautohomeContentRe implements ReProcessor{
    private static final Log LOG = LogFactory.getLog(BautohomeContentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        System.out.println("20190218汽车之家13251342");
        int processcode = 0;
        Map<String, Object> processdata = null;

        try {
            Map<String, Object> resultData = parseResult.getParsedata().getData();
            List<Map<String, Object>> tasks = (List) resultData.get("tasks");
            List<Map<String, Object>> replys = new ArrayList<>();
            String html = unit.getPageData();
            List<Map<String, Object>> ceshi = new ArrayList<>();
            System.out.println("20190212汽车之家源码1548==================="+html);
            Document doc = Jsoup.parse(html);
            //            System.out.println("20190212汽车之家==================="+doc);
            String url = unit.getUrl();
            resultData.put("url",url);
            //判断是不是第一页
            String first = doc.select(".pages .cur").text();
            System.out.println("这是第一页===========================");
            System.out.println(first);
            if (first.equals("1 1")) {//是第一页
                //获取楼主信息
                String name = doc.select(".txtcenter .c01439a").get(0).text();
                String dengji = doc.select(".txtcenter .crade").get(0).attr("title");
                String time = doc.select(".plr26 span").get(1).text();
                String louceng = doc.select(".fr button.rightbutlz").get(0).text();
                //String link = doc.select(".txtcenter a.c01439a").get(0).attr("href");
                //移除循环里面的楼主信息

                resultData.put("name",name);
                resultData.put("dengji",dengji);
                resultData.put("time",time);
                resultData.put("louceng",louceng);
                //resultData.put("link",link);

            } else {
                //////////////////////////////////////////////////////////////

            }
            //直接在返回信息获取总页数===============yeshu3

            String yeshu = doc.select(".gopage .fs").text();
            System.out.println("总页数截取前1554："+yeshu);
            String yeshu2 = yeshu.substring(yeshu.indexOf("/ ") + 2, yeshu.indexOf(" 页"));
            System.out.println(yeshu2);
            Integer yeshu3 = Integer.valueOf(yeshu2);
            //在返回信息中获取下一页的url
            String nextpage = doc.select("#x-pages1 .afpage").attr("href");
            //生成下一页任务
            if(yeshu3.equals(first)){
                ///////////////////
            }else{
                Map task = new HashMap();
                task.put("link",nextpage);
                task.put("rawlink",nextpage);
                task.put("linktype","bbsuserinfo");
                tasks.add(task);
             }
            resultData.put("tasks",tasks);

            //浏览数
            String liulan = doc.select(".consnav #x-views").text();
            //回复数
            String huifu = doc.select(".consnav #x-replys").text();
            System.out.println(liulan + huifu);
            //循环
            String louceng111 = "";
            Elements elements = doc.select(".contstxt");
            for (Element element : elements) {
                String name = element.select(".txtcenter  .c01439a").text();
                String dengji = element.select(".txtcenter  .crade").attr("title");
                String time = element.select(".plr26").text();
                Pattern pattern = Pattern.compile("\\d*-\\d*-\\d* \\d*:\\d*:\\d*");
                Matcher time2 = pattern.matcher(time);
                String time3 = "";
                if (time2.find()) {
                    time3 = String.valueOf(time2.group());
                    System.out.println("匹配的发表时间" + time3);
                }
                louceng111 = element.select(".fr button.rightbutlz").text();
                String link = element.select(".txtcenter a.c01439a").attr("href");

                String content = element.select(".w740").text();
                //System.out.println("发表时间："+time3+"发表内容："+content+"楼层："+louceng111+"链接："+link
                // +"等级："+dengji+"名字："+name);
                //if第一页则删除

                Map taskk = new HashMap();
                taskk.put("link", link);
                taskk.put("rawlink", link);
                taskk.put("linktype", "bbsuserinfo");
                tasks.add(taskk);

                Map mapp = new HashMap<>();
                mapp.put("replyusername", name);
                mapp.put("aaa", dengji);
                mapp.put("replydate", time);
                mapp.put("bbb", louceng111);
                mapp.put("replylink", taskk);
                mapp.put("replycontent", content);


                ceshi.add(mapp);
                /*楼主循环内容被移除*/
                if (louceng111.equals("楼主")) {
                    ceshi.remove(0);
                }
            }

            System.out.println("测试还有没有楼主了111：" + ceshi);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
            System.out.println("汽车之家后处理插件有问题");
        }
        return new ReProcessResult(processcode, processdata);

    }


}
