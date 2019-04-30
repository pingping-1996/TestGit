package com.bfd.parse.reprocess;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NtengxunContentRe implements ReProcessor {

    private static final Log LOG = LogFactory.getLog(NtengxunContentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        System.out.println("腾讯新闻201903111350");
        System.out.println("===20190306腾讯新闻===");
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        List<Map<String,Object>> tasks = (List)resultData.get("tasks");
        try {
            if(unit.getPageData()!=null) {
                Map<String, Object> jsonmap = (Map) unit.getAjaxdata().get(0);
                byte[] valuebyte = DataUtil.unzipAndDecode((String) jsonmap.get("data"));
                String news = new String(valuebyte);
                System.out.println("腾讯新闻解密后的ajax返回的data值为" + news);
                System.out.println("news==="+news);


                JSONObject jsonObject = JSON.parseObject(news);
                JSONObject jjj = jsonObject.getJSONObject("data");
                String commentnum = jjj.getString("commentnum");
                resultData.put("reply_cnt",commentnum);//评论数

                String arry = unit.getPageData();
                Document doc = Jsoup.parse(arry);
                String text = doc.select(".content-article p").text();
                System.out.println("文章内容=============" + text);
                resultData.put("content", text);//内容
                String regex2 = "\"title\": \"(.*?)\"";
                Pattern pattern2 = Pattern.compile(regex2);
                Matcher matcher2 = pattern2.matcher(arry);
                String title = "";
                if (matcher2.find()) {
                    title = matcher2.group(1);
                }
                resultData.put("title", title);//标题

                String regex3 = "name=\"apub:time\" content=\"(.*?)\"";
                Pattern pattern3 = Pattern.compile(regex3);
                Matcher matcher3 = pattern3.matcher(arry);
                String time = "";
                if (matcher3.find()) {
                    time = matcher3.group(1);
                }
                resultData.put("post_time", time);//发表时间
                Integer commentnum2 = Integer.valueOf(commentnum);
                if(commentnum2!=0){
                    String regex = "\"comment_id\": \"(.*?)\"";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(arry);
                    String cid = "";
                    if (matcher.find()) {
                        cid = matcher.group(1);
                    }
                    String commenturl = "http://coral.qq.com/article/" + cid + "/comment/v2?callback=_article" + cid + "commentv2&orinum=10&oriorder=o&pageflag=1&cursor=0&scorecursor=0&orirepnum=2&reporder=o&reppageflag=1&source=1";
                    resultData.put("comment_url", commenturl);
                    Map map = new HashMap();
                    map.put("link", commenturl);//帖子链接
                    map.put("rawlink", commenturl);//修改前
                    map.put("linktype", "newscomment");//链接类型
                    tasks.add(map);
                    resultData.put("tasks", tasks);
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            System.out.println("有错误！！！！！！！！！！！！！！！！！");
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
    }
}
