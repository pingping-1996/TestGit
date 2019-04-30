package com.bfd.parse.reprocess;

import com.bfd.crawler.utils.DataUtil;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
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

public class NhaibaowangContentRe implements ReProcessor{
    private static final Log LOG = LogFactory.getLog(NhaibaowangContentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
         try {
             String urll="";
             if (!unit.getPageData().isEmpty()) {
                 System.out.println("20190222=========1856========================");
                 Integer num2 = 0;
                 if (!unit.getAjaxdata().isEmpty()) {
                     Map<String, Object> jsonmap = (Map) unit.getAjaxdata().get(0);
                     byte[] valuebyte = DataUtil.unzipAndDecode((String) jsonmap.get("data"));
                     String news = new String(valuebyte);
                     //System.out.println("海报网的评论数请求201902222029：" + news);
                     Document doc = Jsoup.parse(news);
                     String num = doc.select(".discuss3 span").text();
                     num2 = Integer.valueOf(num);
                     resultData.put("reply_cnt ", num2);
                     System.out.println("得到的评论数===" + num2);
                 }
                 /*
                  * 判断第一页
                  *
                  * */
                 String url = unit.getUrl();
                 //http://beauty.haibao.com/article/2483224.htm
                 String regEx3 = "/article/(\\d+)";
                 Pattern p3 =Pattern.compile(regEx3);
                 Matcher m3 = p3.matcher(url);
                 String id = "";
                 if(m3.find()){
                     id = m3.group(1);
                 }
                 resultData.put("news_id",id);

                 System.out.println("yyyyyyyyyyyyyyyy"+url);
                 //http://star.haibao.com/article/2471565_2.htm
                 //http://star.haibao.com/article/2471565.htm
                 String regEx = "_";//匹配shi不是第一页
                 Pattern p = Pattern.compile(regEx);
                 Matcher flag = p.matcher(url);
                 String lll = "";
                 if (flag.find()) {
                     lll = flag.group();
                 }
                 String html = unit.getPageData();
                 Document doc = Jsoup.parse(html);
            /* String num = doc.select(".discuss3").text();
             if(!num.isEmpty()){
                 resultData.put("reply_cnt",num);
             }*/
                 if (lll.equals("")) {//是第一页，只有第一页需要生成评论的链接
                     if (resultData.containsKey("tasks")) {
                         System.out.println("进入海报网内容页");
                         List<Map<String, Object>> tasks = (List) resultData.get("tasks");
                         //http://comments.haibao.com/comments/2464117/ARTICLE/
                         String regEx2 = "\\d+";//匹配不是第一页
                         Pattern p2 = Pattern.compile(regEx2);
                         Matcher flag2 = p2.matcher(url);
                         String lll2 = "";
                         if (flag2.find()) {
                             lll2 = flag2.group();
                         }
                         urll = "http://comments.haibao.com/ajax/comment:loadCommentMore.html" + "#" + id + "#" + 1;

                         Map map = new HashMap();
                         //map.put("iid", lll2);
                         map.put("link", urll);
                         map.put("rawlink", urll);
                         map.put("linktype", "newscomment");
                         if (num2 != 0) {
                             System.out.println("海报网内容+++++++2207");
                             tasks.add(map);
                         }

                     }
                   /* "iid":"cf862b9e8405a1eecec70ae2f19a1ec5",
                    "link":"http://star.haibao.com/article/2464117_2.htm",
                    "rawlink":"/article/2464117_2.htm",
                    "linktype":"newscontent"*/
                 }

             }
             resultData.put("comment_url",urll);
             }catch(Exception e){
                 e.printStackTrace();
             }
             ParseUtils.getIid(unit, parseResult);
             return new ReProcessResult(processcode, processdata);
         }


}
