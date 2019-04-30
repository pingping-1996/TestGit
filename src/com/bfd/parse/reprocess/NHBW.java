package com.bfd.parse.reprocess;

import com.bfd.crawler.utils.DataUtil;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParseResult.ParseData;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class NHBW implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NHBW.class);

    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace)
    {
        System.out.println("��������unit��������");

        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        List<Map<String, Object>> comments = new ArrayList();
        System.out.println("****************************922data is *************************************");
        try
        {
            if (!unit.getAjaxdata().isEmpty())
            {
                Map<String, Object> jsonmap = (Map)unit.getAjaxdata().get(0);
                byte[] valuebyte = DataUtil.unzipAndDecode((String)jsonmap.get("data"));
                String news = new String(valuebyte);

                Document doc = Jsoup.parse(news);

                String url = unit.getUrl();
                String[] num = url.split("#");
                String url2 = num[0];
                Integer num2 = Integer.valueOf(num[2]);
                int num3 = num2.intValue() + 1;
                String url3 = num[0] + "#" + num[1] + "#" + num3;

                String regEx = "name=\"pageCount\" id=\"pageCount_\\d+\" value=\"(\\d+)\"";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(news);
                Integer page4 = Integer.valueOf(0);
                if (m.find())
                {
                    String page = m.group();
                    System.out.println("haibaowangyeshu20190222=================" + page);
                    String page2 = page.substring(page.indexOf("value=") + 6);
                    String regEx2 = "\\d+";
                    Pattern p2 = Pattern.compile(regEx2);
                    Matcher m2 = p2.matcher(page2);
                    if (m2.find())
                    {
                        String page3 = m2.group();
                        page4 = Integer.valueOf(page3);
                    }
                    System.out.println("haibaowangyeshu201902221828" + page4);
                }
                if (num2.intValue() < page4.intValue())
                {
                    List<Map<String, Object>> tasks = (List)resultData.get("tasks");
                    Map map = new HashMap();
                    map.put("link", url3);
                    map.put("rawlink", url3);
                    map.put("linktype", "newscomment");
                    tasks.add(map);
                    resultData.put("nextpage", url3);
                }
                Elements elements = doc.select(".comment-item");
                for (Element element : elements)
                {
                    String name = element.select(".name a").text();
                    String name2 = element.select(".name").text();
                    name2 = name2.substring(0, name2.indexOf(" "));
                    if (name.isEmpty()) {
                        name = name2;
                    }
                    String conntent="";
                    String up_cnt ="";
                    if(!("".equals(element.tagName(".contxt")))){
                        conntent = element.select(".contxt").get(0).text();
                    }

                    String time = element.select(".from").text();
                    if(("".equals(element.tagName(".returnL3")))){
                        up_cnt = element.select(".returnL3").get(0).text();
                    }

                    Elements elements2 = element.select(".screen1 .maincon");
                    List<Map<String, Object>> list = new ArrayList();
                    if (!elements2.isEmpty()) {
                        for (Element element2 : elements2)
                        {
                            String rep_name = element2.select(".people").text();
                            String rep_time = element2.select(".time").text();
                            String rep_content = element2.select(".write .contxt").text();

                            String rep_up_cnt = element2.select(".returnL3").text();

                            Map map = new HashMap();
                            map.put("username", rep_name);
                            map.put("comment_time", rep_time);
                            map.put("comment_content", rep_content);
                            map.put("up_cnt", rep_up_cnt);
                            list.add(map);
                        }
                    }
                    Map map = new HashMap();
                    map.put("news_id", num[1]);
                    map.put("username", name);
                    map.put("comment_content", conntent);
                    map.put("comment_time", time);
                    map.put("up_cnt", up_cnt);
                    map.put("answers", list);
                    System.out.println("������201902211411");
                    comments.add(map);
                }
                resultData.put("comments", comments);
                resultData.put("url", url2);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}
