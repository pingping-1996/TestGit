package com.bfd.parse.reprocess;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bfd.crawler.utils.JsonUtils;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.reprocess.ReProcessResult;
import com.bfd.parse.reprocess.ReProcessor;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DZYTBContentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(DZYTBContentRe.class);

    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        int processcode = 0;
        HashMap processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        List<Map<String, Object>> replys = new ArrayList<>();
        try {
            if (resultData != null && resultData.size() > 0) {
                String html = unit.getPageData();

                Document doc = Jsoup.parse(html);
                    if(doc.select("#errorText").isEmpty()){
                        String url = unit.getUrl();

//                String firstUrl="";
                        String tid ="";
                        String pattern = "baidu.com/p/(\\d+)";

                        // 创建 Pattern 对象
                        Pattern r = Pattern.compile(pattern);

                        // 现在创建 matcher 对象
                        Matcher m = r.matcher(url);
                        if (m.find( )) {
                            tid=m.group(1);
                        } else {
                            System.out.println("没有匹配");
                        }
                        //是否包含pid，如果包含去掉
                        if(url.contains("?pid=")){
                            url=url.substring(0,url.indexOf("?pid="));
                        }else if(url.contains("?fid=")){
                            url=url.substring(0,url.indexOf("?fid="));
                        }
                        //是否包含pn，如不包含，加上，算是首页
//                if(!url.contains("?pn=")){
//                    url+="?pn=1";
//                    firstUrl=url;
//                }else{
//                    firstUrl=url.substring(0,url.indexOf("?pn=")+4)+1;
//                }

                        String nextPage="";
                        Elements pb_list_pagers=doc.select(".l_pager.pager_theme_5.pb_list_pager a");

                        for(Element pb_list_pager:pb_list_pagers){
                            if(pb_list_pager.text().contains("下一页")){
                                nextPage="https://tieba.baidu.com"+pb_list_pager.attr("href");
                            }
                        }
                        Integer page=1;
                        if(!doc.select(".pb_footer .l_posts_num .tP").isEmpty()){
                            page=Integer.parseInt(doc.select(".pb_footer .l_posts_num .tP").text());
                        }

//                String tid = dzytbContentRe.getStrByPrePost(url,"/p/","?pn=");
//                        String forum_id = getStrByPrePost(html, "forum_id: \"", "\"");

                        String forum_id = getStrByPrePost(html, "forum_id\":", ",");
                        String totalComment = totalComment(url, tid, forum_id,page+"");
                        JSONObject jsonObject = JSONObject.parseObject(totalComment);
                        JSONObject data = jsonObject.getJSONObject("data");



                        JSONObject comment_list =null;
                        if(data.get("comment_list") instanceof JSONArray){

                        }else{
                            comment_list = data.getJSONObject("comment_list");
                        }

                        resultData.put("reply_cnt",doc.select(".pb_footer .l_reply_num span.red:eq(0)").text());
                        resultData.put("cate",doc.select(".card_title_fname").text().trim());
                        String title=doc.select(".core_title_txt").text();
                        resultData.put("title",title);
                        /**
                         * 首页开始
                         */
                        if(page==1){
                                resultData.put("authorname",doc.select(".p_postlist>div").first().select(".d_author .p_author .d_name .p_author_name.j_user_card").text());
    //
                                String author_level=doc.select(".p_postlist>div").first().select(".d_author .p_author .l_badge .d_badge_lv").text();
                //
                //
                                if(StringUtils.isEmpty(author_level)){
                                    String field=doc.select(".l_post.j_l_post.l_post_bright.noborder").attr("data-field");
                                    if(StringUtils.isNotEmpty(field)){
                                        JSONObject fieldJSON=JSONObject.parseObject(field);
                                        author_level=fieldJSON.getJSONObject("author").getString("level_id");
                                    }
                                }
                                resultData.put("author_level",author_level);

                                String userIdStr=doc.select(".p_postlist>div").first().select(".d_author .p_author .d_name").attr("data-field");
                                JSONObject jsonObject_userId=JSONObject.parseObject(userIdStr);
                                resultData.put("authorid",jsonObject_userId.getString("user_id"));

                                String contents="";
                                contents+=doc.select(".p_postlist>div").first().select(".d_post_content.j_d_post_content").text();

                                Elements imgs = doc.select(".p_postlist>div").first().select(".d_post_content.j_d_post_content img");

                                for(int i=0;i<imgs.size();i++){
                                    contents+=imgs.get(i).attr("src");
                                    if(i!=imgs.size()-1){
                                        contents+=",";
                                    }
                                }
                                resultData.put("contents",contents);

                //        String newstime = firstDoc.select(".p_postlist>div").get(0).select(".post-tail-wrap .tail-info").text();
                //        newstime = newstime.substring(newstime.indexOf("楼") + 1, newstime.length());


                                String newstime = doc.select(".p_postlist>div").first().select(".post-tail-wrap").text();
                                if(newstime.contains("楼")){
                                    newstime = newstime.substring(newstime.indexOf("楼") + 1, newstime.length());
                                }
                                if(StringUtils.isEmpty(newstime)){
                                    String field=doc.select(".l_post.j_l_post.l_post_bright.noborder").attr("data-field");
                                    if(StringUtils.isNotEmpty(field)){
                                        JSONObject fieldJSON=JSONObject.parseObject(field);
                                        newstime=fieldJSON.getJSONObject("content").getString("date");
                                    }
                                }

                                resultData.put("newstime",newstime);

                                if(resultData.get("author_avatar")==null){
                                    resultData.put("author_avatar",doc.select(".p_postlist>div").first().select(".p_author_face img").attr("src"));
                                }
                        }


//

//
//
////                String main_post_content_id = firstDoc.select(".p_postlist>div").get(0).select(".d_post_content.j_d_post_content").attr("id");
////                main_post_content_id = main_post_content_id.replace("post_content_", "");
//

                        /**
                         * 首页结束
                         */

                        Elements elements =null;
//                String currentPage=url.substring(0,url.indexOf("?pn=")+4)+page;
//                doc=Jsoup.connect(currentPage).get();
                if(page>1){
                        elements = doc.select(".p_postlist>div");
                }else{
                    elements = doc.select(".p_postlist>div:gt(0)");
                }
                        resultData.put("post_id",tid);

                        for (int i=0;i<elements.size();i++) {
                            Map<String, Object> reply=new HashMap<>();
                            String post_content_id = elements.get(i).select(".d_post_content.j_d_post_content").attr("id");
                            post_content_id = post_content_id.replace("post_content_", "");
                            String post_content_text = elements.get(i).select(".d_post_content.j_d_post_content").text();

                            String replydate = elements.get(i).select(".post-tail-wrap").text();

                            if(StringUtils.isNotEmpty(replydate)){
                                replydate = replydate.substring(replydate.indexOf("楼") + 1, replydate.length());
                            }else{
                                String field=elements.get(i).attr("data-field");
                                if(StringUtils.isNotEmpty(field)){
                                    JSONObject fieldJSON=JSONObject.parseObject(field);
                                    replydate=fieldJSON.getJSONObject("content").getString("date");
                                }
                            }


                            Elements post_tail_wrap=elements.get(i).select(".d_post_content_main  .core_reply.j_lzl_wrapper " +
                                    ".core_reply_tail.clearfix .post-tail-wrap .tail-info");
//                            for(Element element1:post_tail_wrap){
//                                if(element1.text().contains("楼")){
//                                    reply.put("replyfloor",element1.text().replace("楼",""));
//                                }
//                            }

//                            if(reply.get("replyfloor")==null){
                                String field=elements.get(i).attr("data-field");
                                if(StringUtils.isNotEmpty(field)){
                                    JSONObject fieldJSON=JSONObject.parseObject(field);
                                    reply.put("replyfloor",fieldJSON.getJSONObject("content").getString("post_no"));
                                }
//                            }

                            String d_authorEle = elements.get(i).select(".d_author .p_author .d_name .p_author_name.j_user_card").text();

                            if(StringUtils.isNotEmpty(d_authorEle)){
                                reply.put("replyusername",d_authorEle);
                                reply.put("replydate",replydate);
                                reply.put("replycontent",post_content_text);
                                reply.put("post_id",tid);
                            }

                            if(comment_list!=null){
                                JSONObject post_contentJSON = comment_list.getJSONObject(post_content_id);
                                if (post_contentJSON != null) {
                                    JSONArray comment_info = comment_list.getJSONObject(post_content_id).getJSONArray("comment_info");
                                    List<Map> answers=new ArrayList<>();
                                    for (Object o : comment_info) {
                                        JSONObject info = (JSONObject) o;
                                        String username = info.getString("username");
                                        String content = info.getString("content");
                                        content=Jsoup.parseBodyFragment(content).text();
                                        String answers_time = info.getString("now_time");
                                        Map map=new HashMap();
                                        map.put("username",username);
                                        map.put("content",content);
                                        map.put("answers_time",timeStamp2Date(answers_time,null));
                                        answers.add(map);

                                    }
                                    reply.put("answers",answers);

                                }
                            }

                            if(!reply.isEmpty()){
                                replys.add(reply);
                            }


                        }
                        resultData.put("replys",replys);


                        if(StringUtils.isNotEmpty(nextPage)){
                            List<Map<String, String>> tasks = new ArrayList<>();
                            Map task=new HashMap();
                            task.put("link",nextPage);
                            task.put("rawlink",nextPage);
                            task.put("linktype","bbspost");
                            tasks.add(task);
                            resultData.put("tasks",tasks);

                            resultData.put("nextpage",nextPage);
                        }



                        LOG.info("result Data is ->" + JsonUtils.toJSONString(resultData));
                    }

            }
        } catch (Exception var13) {
            processcode = 500011;
            var13.printStackTrace();
            LOG.info("reprocess exception..." + var13.getClass().getName() + var13.getMessage());
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
    }


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

    public static String totalComment(String url, String tid, String fid, String pn) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get =new HttpGet("https://tieba.baidu.com/p/totalComment?t="+new Date().getTime()+"&tid="+tid+"&fid="+fid+"&pn="+pn+"&see_lz=0");
        get.addHeader("Accept","application/json, text/javascript, */*; q=0.01");
        get.addHeader("Accept-Encoding","gzip, deflate, br");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.9");
        get.addHeader("Connection","keep-alive");
        get.addHeader("Referer",url);
        get.addHeader("Cookie","BAIDUID=B8460E15796BA7CBCA64434A78F7956C:FG=1; BIDUPSID=B8460E15796BA7CBCA64434A78F7956C; PSTM=1521095564; TIEBA_USERTYPE=d5dbceecc68091eb1eccd52b; bdshare_firstime=1522050771950; BDUSS=nhrNjJCWkFSY1ZqUUJTNkFseDNkd0oyWVRlemN5NVZqdmdQMWs2MjNEbHhTeEpiQVFBQUFBJCQAAAAAAAAAAAEAAACSpR0HMTMxbGxsb3ZlAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHG-6lpxvupaN; TIEBAUID=a4634cd4e3dc11670f1e0642; STOKEN=d239f91d24d7b421dced853ce54b400ee5588bffc2a9e0c6952b72e2b59ca051; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; MCITY=-%3A; 119383442_FRSVideoUploadTip=1; wise_device=0; H_PS_PSSID=1428_21082_26350_22074; PSINO=1; pgv_pvi=9495717888; pgv_si=s619693056; Hm_lvt_98b9d8c2fd6608d564bf2ac2ae642948=1537942208,1537945617,1537945663,1537945834; Hm_lpvt_98b9d8c2fd6608d564bf2ac2ae642948=1537945834");
        get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000).setConnectionRequestTimeout(1000)
                .setSocketTimeout(5000).build();
        get.setConfig(requestConfig);

        CloseableHttpResponse response =httpClient.execute(get);
        int statusCode =response.getStatusLine().getStatusCode();
        HttpEntity entity =response.getEntity();
        String html = EntityUtils.toString(entity,"utf-8");
        response.close();
        httpClient.close();
        return html;
    }

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @return
     */
    public static String timeStamp2Date(String seconds, String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }


    public static void main9(String[] args) throws IOException {
        Map<String, Object> resultData = new HashMap<>();
        List<Map<String, Object>> replys = new ArrayList<>();
//                String html = unit.getPageData();
                String url = "http://tieba.baidu.com/p/5964083283?pn=8&red_tag=g1521798552";
                Document doc = Jsoup.connect(url).get();
                String html=doc.html();


                String firstUrl="";
                String tid ="";
                String pattern = "baidu.com/p/(\\d+)";

                // 创建 Pattern 对象
                Pattern r = Pattern.compile(pattern);

                // 现在创建 matcher 对象
                Matcher m = r.matcher(url);
                if (m.find( )) {
                    tid=m.group(1);
                } else {
                    System.out.println("没有匹配");
                }
                //是否包含pid，如果包含去掉
                if(url.contains("?pid=")){
                    url=url.substring(0,url.indexOf("?pid="));
                }else if(url.contains("?fid=")){
                    url=url.substring(0,url.indexOf("?fid="));
                }
                //是否包含pn，如不包含，加上，算是首页
                if(!url.contains("?pn=")){
                    url+="?pn=1";
                    firstUrl=url;
                }else{
                    firstUrl=url.substring(0,url.indexOf("?pn=")+4)+1;
                }

                String nextPage="";
                Elements pb_list_pagers=doc.select(".l_pager.pager_theme_5.pb_list_pager a");

                for(Element pb_list_pager:pb_list_pagers){
                    if(pb_list_pager.text().contains("下一页")){
                        nextPage="https://tieba.baidu.com"+pb_list_pager.attr("href");
                    }
                }
                Integer page=1;
                if(StringUtils.isNotEmpty(nextPage)){
                    page=Integer.parseInt(doc.select(".pb_footer .l_posts_num .tP").text());
                }

//                String tid = dzytbContentRe.getStrByPrePost(url,"/p/","?pn=");
                String forum_id = getStrByPrePost(html, "forum_id: \"", "\"");
                String totalComment = totalComment(url, tid, forum_id,page+"");
                JSONObject jsonObject = JSONObject.parseObject(totalComment);
                JSONObject data = jsonObject.getJSONObject("data");



                JSONObject comment_list =null;
                if(data.get("comment_list") instanceof JSONArray){

                }else{
                    comment_list = data.getJSONObject("comment_list");
                }


                /**
                 * 首页开始
                 */
                Document firstDoc=Jsoup.connect(firstUrl).get();

                resultData.put("authorname",firstDoc.select(".p_postlist>div").get(0).select(".d_author .p_author .d_name .p_author_name.j_user_card").text());

                String author_level=firstDoc.select(".p_postlist>div").get(0).select(".d_author .p_author .l_badge .d_badge_lv").text();


                if(StringUtils.isEmpty(author_level)){
                    String field=firstDoc.select(".l_post.j_l_post.l_post_bright.noborder").attr("data-field");
                    if(StringUtils.isNotEmpty(field)){
                        JSONObject fieldJSON=JSONObject.parseObject(field);
                        author_level=fieldJSON.getJSONObject("author").getString("level_id");
                    }
                }
                resultData.put("author_level",author_level);

                String userIdStr=firstDoc.select(".p_postlist>div").get(0).select(".d_author .p_author .d_name").attr("data-field");
                JSONObject jsonObject_userId=JSONObject.parseObject(userIdStr);
                resultData.put("authorid",jsonObject_userId.getString("user_id"));

                String contents="";
                contents+=firstDoc.select(".p_postlist>div").get(0).select(".d_post_content.j_d_post_content").text();

                Elements imgs = firstDoc.select(".p_postlist>div").get(0).select(".d_post_content.j_d_post_content img");

                for(int i=0;i<imgs.size();i++){
                    contents+=imgs.get(i).attr("src");
                    if(i!=imgs.size()-1){
                        contents+=",";
                    }
                }
                resultData.put("contents",contents);

//        String newstime = firstDoc.select(".p_postlist>div").get(0).select(".post-tail-wrap .tail-info").text();
//        newstime = newstime.substring(newstime.indexOf("楼") + 1, newstime.length());


                String newstime = firstDoc.select(".p_postlist>div").get(0).select(".post-tail-wrap").text();
                if(newstime.contains("楼")){
                    newstime = newstime.substring(newstime.indexOf("楼") + 1, newstime.length());
                }
                if(StringUtils.isEmpty(newstime)){
                    String field=firstDoc.select(".l_post.j_l_post.l_post_bright.noborder").attr("data-field");
                    if(StringUtils.isNotEmpty(field)){
                        JSONObject fieldJSON=JSONObject.parseObject(field);
                        newstime=fieldJSON.getJSONObject("content").getString("date");
                    }
                }

                resultData.put("newstime",newstime);
                if(resultData.get("reply_cnt")==null){
                    resultData.put("reply_cnt",firstDoc.select(".pb_footer .l_reply_num span.red:eq(0)").text());
                }
                if(resultData.get("cate")==null){
                    resultData.put("cate",firstDoc.select(".card_title_fname").text().trim());
                }
                if(resultData.get("author_avatar")==null){
                    resultData.put("author_avatar",firstDoc.select(".p_postlist>div").get(0).select(".p_author_face img").attr("src"));
                }
                //card_title_fname


//                String main_post_content_id = firstDoc.select(".p_postlist>div").get(0).select(".d_post_content.j_d_post_content").attr("id");
//                main_post_content_id = main_post_content_id.replace("post_content_", "");

                String title=firstDoc.select(".core_title_txt").text();
                resultData.put("title",title);
                /**
                 * 首页结束
                 */

                Elements elements =null;
                String currentPage=url.substring(0,url.indexOf("?pn=")+4)+page;
                doc=Jsoup.connect(currentPage).get();
                if(page>1){
                    elements = doc.select(".p_postlist>div");
                }else{
                    elements = doc.select(".p_postlist>div:gt(0)");
                }
                resultData.put("post_id",tid);

                for (int i=0;i<elements.size();i++) {
                    Map<String, Object> reply=new HashMap<>();
                    String post_content_id = elements.get(i).select(".d_post_content.j_d_post_content").attr("id");
                    post_content_id = post_content_id.replace("post_content_", "");
                    String post_content_text = elements.get(i).select(".d_post_content.j_d_post_content").text();

                    String replydate = elements.get(i).select(".post-tail-wrap").text();

                    if(StringUtils.isNotEmpty(replydate)){
                        replydate = replydate.substring(replydate.indexOf("楼") + 1, replydate.length());
                    }else{
                        String field=elements.get(i).attr("data-field");
                        if(StringUtils.isNotEmpty(field)){
                            JSONObject fieldJSON=JSONObject.parseObject(field);
                            replydate=fieldJSON.getJSONObject("content").getString("date");
                        }
                    }


                    Elements post_tail_wrap=elements.get(i).select(".d_post_content_main  .core_reply.j_lzl_wrapper " +
                            ".core_reply_tail.clearfix .post-tail-wrap .tail-info");
                    for(Element element1:post_tail_wrap){
                        if(element1.text().contains("楼")){
                            reply.put("replyfloor",element1.text().replace("楼",""));
                        }
                    }


                    String d_authorEle = elements.get(i).select(".d_author .p_author .d_name .p_author_name.j_user_card").text();

                    if(StringUtils.isNotEmpty(d_authorEle)){
                        reply.put("replyusername",d_authorEle);
                        reply.put("replydate",replydate);
                        reply.put("replycontent",post_content_text);
                        reply.put("post_id",tid);
                    }

                    if(comment_list!=null){
                        JSONObject post_contentJSON = comment_list.getJSONObject(post_content_id);
                        if (post_contentJSON != null) {
                            JSONArray comment_info = comment_list.getJSONObject(post_content_id).getJSONArray("comment_info");
                            List<Map> answers=new ArrayList<>();
                            for (Object o : comment_info) {
                                JSONObject info = (JSONObject) o;
                                String username = info.getString("username");
                                String content = info.getString("content");
                                content=Jsoup.parseBodyFragment(content).text();
                                String answers_time = info.getString("now_time");
                                Map map=new HashMap();
                                map.put("username",username);
                                map.put("content",content);
                                map.put("answers_time",timeStamp2Date(answers_time,null));
                                answers.add(map);

                            }
                            reply.put("answers",answers);

                        }
                    }

                    if(!reply.isEmpty()){
                        replys.add(reply);
                    }


                }
                resultData.put("replys",replys);


                if(StringUtils.isNotEmpty(nextPage)){
                    List<Map<String, String>> tasks = new ArrayList<>();
                    Map task=new HashMap();
                    task.put("link",nextPage);
                    task.put("rawlink",nextPage);
                    task.put("linktype","bbspost");
                    tasks.add(task);
                    resultData.put("tasks",tasks);

                    resultData.put("nextpage",nextPage);
                }


                System.out.println(JsonUtils.toJSONString(resultData));
                LOG.info("result Data is ->" + JsonUtils.toJSONString(resultData));
    }



}
