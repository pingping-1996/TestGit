package com.bfd.parse.reprocess;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NtengxunContentRe2 implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NtengxunContentRe2.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        System.out.println("===20190306腾讯新闻===");
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        List<Map<String, Object>> tasks = new ArrayList<>();;
        String arry = unit.getPageData();
        String html = arry.replace("getNewsContentOnlyOutput(","");
        String html2 = html.substring(0,html.length()-1);
        JSONObject jsonObject = JSON.parseObject(html2);
        JSONArray jsonArray = jsonObject.getJSONArray("content");
        try {
            String value = "";
            List list = new ArrayList();
            for(int i=0;i<jsonArray.size();i++){
                JSONObject jsonArrays=jsonArray.getJSONObject(i);
                value = jsonArrays.getString("value");
                //System.out.println(i+ value);
                list.add(value);
            }
            System.out.println(list);
            String strResult = "";
            for(int i=0;i<list.size();i++){
                strResult+=list.get(i);
            }
            //url
            //https://openapi.inews.qq.com/getQQNewsNormalContent?id=20190306A0M77B00&chlid=news_rss&refer=mobilewwwqqcom&otype=jsonp&ext_data=all&srcfrom=newsapp&callback=getNewsContentOnlyOutput
            //https://openapi.inews.qq.com/getQQNewsNormalContent?id=20190306A0VPLI00&chlid=news_rss&refer=mobilewwwqqcom&otype=jsonp&ext_data=all&srcfrom=newsapp&callback=getNewsContentOnlyOutput
            //https://openapi.inews.qq.com/getQQNewsNormalContent?id=20190306d00&chlid=news_rss&refer=mobilewwwqqcom&otype=jsonp&ext_data=all&srcfrom=newsapp&callback=getNewsContentOnlyOutput
            //String strResult2 = strResult.substring(0,strResult.length());
            System.out.println("文章内容==="+strResult);
            resultData.put("content",strResult);

            String cid = jsonObject.getString("cid");
                               //http://coral.qq.com/article/3761370170/comment/v2?callback=_article3761370170commentv2&orinum=10&oriorder=o&pageflag=1&cursor=0&scorecursor=0&orirepnum=2&reporder=o&reppageflag=1&source=1
            String commenturl = "http://coral.qq.com/article/"+cid+"/comment/v2?callback=_article"+cid+"commentv2&orinum=10&oriorder=o&pageflag=1&cursor=0&scorecursor=0&orirepnum=2&reporder=o&reppageflag=1&source=1";
            resultData.put("comment_url",commenturl);
            Map map = new HashMap();
            map.put("link", commenturl);//帖子链接
            map.put("rawlink", commenturl);//修改前
            map.put("linktype","newscomment");//链接类型
            tasks.add(map);
            resultData.put("comment_url",commenturl);
            resultData.put("tasks",tasks);
//评论入口url
// http://coral.qq.com/article/3764028740/comment/v2?callback=_article3764028740commentv2&orinum=10&oriorder=o&pageflag=1

        } catch (
        Exception e) {
        e.printStackTrace();
        System.out.println("有错误！！！！！！！！！！！！！！！！！");
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
        }
}

