package com.bfd.parse.reprocess;

import com.bfd.crawler.utils.DataUtil;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParseResult.ParseData;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.json.JsonData;
import com.bfd.parse.util.JsonUtil;
import com.bfd.parse.util.ParseUtils;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NiqiyiContent
        implements ReProcessor
{
    private static final Log LOG = LogFactory.getLog(NiqiyiContent.class);

    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace)
    {
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        try
        {
            if ((resultData != null) && (resultData.size() > 0))
            {
                String html = unit.getPageData();
                System.out.println("######today20190130ajaxdata######");
                System.out.println("######today20190129ajaxdata######" + unit.getAjaxdata().toString());
                Boolean flag = Boolean.valueOf(true);
                JsonData jsondata;
                for (int i = 0; i < unit.getAjaxdata().size(); i++)
                {
                    Map<String, Object> jsonmap = (Map)unit.getAjaxdata().get(i);
                    jsondata = new JsonData();
                    for (Map.Entry<String, Object> entry : jsonmap.entrySet()) {
                        if (entry.getKey() == "data")
                        {
                            byte[] valuebyte = DataUtil.unzipAndDecode(entry.getValue().toString());

                            String strdata = new String(valuebyte);
                            if ((strdata.contains("{")) && (strdata.contains("}")))
                            {
                                strdata = strdata.substring(strdata.indexOf("{"), strdata.lastIndexOf("}") + 1);
                                Map<String, Object> one = (Map)JsonUtil.parseObject(strdata);
                                if (one.containsKey("data"))
                                {
                                    if ((one.get("data") instanceof ArrayList))
                                    {
                                        List<Map<String, Object>> data = (List)one.get("data");
                                        int hot = ((Integer)((Map)data.get(0)).get("hot")).intValue();
                                        resultData.put("recommend_cnt", Integer.valueOf(hot));
                                    }
                                    else if ((one.get("data") instanceof String))
                                    {
                                        int like_cnt = Integer.parseInt(one.get("data").toString());
                                        resultData.put("like_cnt", Integer.valueOf(like_cnt));
                                    }
                                    else if ((one.get("data") instanceof Map))
                                    {
                                        Map<String, Object> data = (Map)one.get("data");
                                        int reply_cnt = ((Integer)data.get("totalCount")).intValue();
                                        resultData.put("reply_cnt", Integer.valueOf(reply_cnt));
                                        System.out.println("#########reply_cnt:" + reply_cnt);
                                        if (reply_cnt < 1) {
                                            flag = Boolean.valueOf(false);
                                        }
                                    }
                                    System.out.println("######todayWJ+++" + strdata);
                                }
                                else if (entry.getKey() == "url")
                                {
                                    jsondata.setUrl(entry.getValue().toString());
                                }
                                else if (entry.getKey() == "charset")
                                {
                                    jsondata.setCharset(entry.getValue().toString());
                                }
                                else if (entry.getKey() == "code")
                                {
                                    jsondata.setHttpcode(entry.getValue().toString());
                                }
                            }
                        }
                    }
                }
                String author = "";

                Pattern p1 = Pattern.compile("\",\"name\":\"(.*?)\",\"");
                Matcher m1 = p1.matcher(html);
                if (m1.find()) {
                    author = m1.group(1);
                }
                resultData.put("author", author);
                String content_id = "";
                Pattern p2 = Pattern.compile("param\\['tvid'\\] = \"(.*?)\";");
                Matcher m2 = p2.matcher(html);
                if (m2.find()) {
                    content_id = m2.group(1);
                }
                resultData.put("news_id", content_id);

                int upload_cnt = 0;
                Pattern p3 = Pattern.compile("\"vcount\":\"(\\d+)\",");
                Matcher m3 = p3.matcher(html);
                if (m3.find()) {
                    upload_cnt = Integer.parseInt(m3.group(1));
                }
                resultData.put("upload_cnt", Integer.valueOf(upload_cnt));
                String commentUrl = "";
                List<Object> tasks = (List)resultData.get("tasks");
                Map<String, String> map = new HashMap();
                if (flag.booleanValue())
                {
                    commentUrl = "https://sns-comment.iqiyi.com/v3/comment/get_comments.action?agent_type=118&agent_version=9.11.5&authcookie=null&business_type=17&content_id=" + content_id + "&hot_size=10&last_id=&page=1&page_size=10&types=time";

                    String link = commentUrl;
                    map.put("link", link);
                    map.put("rawlink", link);
                    map.put("linktype", "newscomment");
                    tasks.add(map);
                }
                resultData.put("comment_url", commentUrl);
                resultData.put("tasks", tasks);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LOG.info(getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
    }
}
