package com.bfd.parse.reprocess;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bfd.crawler.utils.DataUtil;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VqqContentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(VqqContentRe.class);

    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        int processcode = 0;
        HashMap processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        try {
                String html = unit.getPageData();
                Document doc = Jsoup.parse(html);
                String url = unit.getUrl();

                String comment_url="";
                String comment_id="";
                if(!url.contains("comment")){
                    resultData.put("view_cnt",doc.select(".action_item.action_count .num").text());//播放量
                    resultData.put("reply_cnt",doc.select(".txp_btn_text").attr("title").replace("热评",""));
                    resultData.put("title",doc.select(".video_title._video_title").text());
                    String newsTime=doc.select("span.date._date").text().replace("发布","").
                            replace("年","-").replace("月","-").replace("日","");
                    resultData.put("newstime",newsTime);

                    comment_id=getStrByPrePost(html,"comment_id\":\"","\"");

                    if(StringUtils.isBlank(comment_id)){
                        String pageId="";
                        String pattern="/page/(\\w+)\\.html";
                        Pattern r=Pattern.compile(pattern);
//                         现在创建 matcher 对象
                        Matcher m = r.matcher(url);
                        if (m.find( )) {
                            pageId = m.group(1);
                        }
                        String video_comment_url="https://ncgi.video.qq.com/fcgi-bin/video_comment_id?otype=json&callback=&op=3&vid="+pageId+"&_="+new Date().getTime();
                        String video_comment_idHTML=video_comment_id(video_comment_url);
//                        String video_comment_idHTML="";
//                        for (int i = 0; i < unit.getAjaxdata().size(); i++) {
//                            Map<String, Object> jsonmap = unit.getAjaxdata().get(i);
//                            System.out.println("jsonmap==="+jsonmap);
//                            String burl=jsonmap.get("url").toString();
//                            if(burl.contains("video_comment_id")){
//                                byte[] valuebyte = DataUtil.unzipAndDecode((String)jsonmap.get("data"));
//                                String news = new String(valuebyte);
//                                System.out.println("解密后的ajax返回的data值为qq====" + news);
//                                video_comment_idHTML=news;
//                            }
//
//                        }

                        video_comment_idHTML=video_comment_idHTML.replace("QZOutputJson=","");
                        video_comment_idHTML=video_comment_idHTML.substring(0,video_comment_idHTML.length()-1);
                        JSONObject video_comment_id_json=JSONObject.parseObject(video_comment_idHTML);
                        comment_id=video_comment_id_json.getString("comment_id");
                    }

                    comment_url="https://video.coral.qq.com/varticle/"+comment_id+"/comment/v2?" +
                            "orinum=10&oriorder=o&pageflag=1&scorecursor=0&orirepnum=2&reporder=o&" +
                            "reppageflag=1&source=9&_="+new Date().getTime();

                }else{
                    comment_url=url;

                    String pattern="/varticle/(\\d+)/comment";
                    Pattern r=Pattern.compile(pattern);
                    // 现在创建 matcher 对象
                    Matcher m = r.matcher(url);
                    if (m.find( )) {
                        comment_id=m.group(1);
                    } else {
                        System.out.println("没有匹配");
                    }
                }

                resultData.put("author",doc.select("span.user_name").text());

                String commentStr=comment(comment_url);
                JSONObject commentJson=JSONObject.parseObject(commentStr);

                Integer errCode=commentJson.getInteger("errCode");
                if(errCode==0) {
                    JSONObject dataJSON = commentJson.getJSONObject("data");

                    boolean hasnext = dataJSON.getBoolean("hasnext");
                    String last = dataJSON.getString("last");

                    if(hasnext){
                        String nextPage=comment_url+"&cursor="+last;
                        resultData.put("nextpage",nextPage);

                        List<Map<String, String>> tasks = new ArrayList<>();
                        Map task=new HashMap();
                        task.put("link",nextPage);
                        task.put("rawlink",nextPage);
                        task.put("linktype","bbspost");
                        tasks.add(task);
                        resultData.put("tasks",tasks);
                    }

                    JSONArray oriCommList = dataJSON.getJSONArray("oriCommList");
                    JSONObject userList = dataJSON.getJSONObject("userList");

                    List<Map<String, Object>> replys = new ArrayList<>();
                    if (oriCommList.size() > 0) {
                        for (int i = 0; i < oriCommList.size(); i++) {
                            Map<String, Object> reply=new HashMap<>();
                            JSONObject oriComm = (JSONObject) oriCommList.get(i);
                            String userid = oriComm.getString("userid");
                            JSONObject userObj=userList.getJSONObject(userid);
                            if(userObj.size()>0){
                                reply.put("replyusername",userObj.getString("nick"));
                            }


                            String content = oriComm.getString("content");
                            String id = oriComm.getString("id");
                            String up = oriComm.getString("up");//赞
//                    String pokenum = oriComm.getString("pokenum");//踩

                            String time=oriComm.getString("time");
                            String orireplynum = oriComm.getString("orireplynum");//全部回复数

                            List<Map> answers=new ArrayList<>();


                            Integer pageTotalCnt=(Integer.parseInt(orireplynum)+10-1)/10;
                            String answer_cursor="";
                            for(int j=0;j<pageTotalCnt;j++){

                                if(j==0){
                                    answer_cursor="0";
                                }

                                String comment_answer = "https://video.coral.qq.com/vcomment/" + id + "/reply/v2?targetid="+comment_id+"&reqnum=10&pageflag=2&source=9&cursor=" + answer_cursor + "&_=" + new Date().getTime();

                                System.out.println(comment_answer);

                                JSONObject commentAnswerJSON=JSONObject.parseObject(comment(comment_answer));
                                JSONObject commentAnswerJSONData=commentAnswerJSON.getJSONObject("data");
                                JSONArray repCommList=commentAnswerJSONData.getJSONArray("repCommList");

                                JSONObject repCommUserList=commentAnswerJSONData.getJSONObject("userList");
                                String commentAnswer_first=commentAnswerJSONData.getString("first");
                                answer_cursor=commentAnswer_first;
                                for(int k=0;k<repCommList.size();k++){
                                    Map answer=new HashMap();
                                    JSONObject repComm=(JSONObject)repCommList.get(k);
                                    String answer_content=repComm.getString("content");
                                    String answers_time=repComm.getString("time");
                                    String answer_up_cnt=repComm.getString("up");

                                    JSONObject userRepObj=repCommUserList.getJSONObject(repComm.getString("userid"));
                                    if(userRepObj.size()>0){
                                        answer.put("replyusername",userRepObj.getString("nick"));
                                    }


                                    answer.put("up_cnt",answer_up_cnt);
                                    answer.put("content",answer_content);
                                    answer.put("answers_time",timeStamp2Date(answers_time,null));
                                    answers.add(answer);
                                }

                            }

                            reply.put("replycontent",content);
                            reply.put("replydate",timeStamp2Date(time,null));
                            reply.put("post_id",comment_id);
                            reply.put("up_cnt",up);
                            reply.put("answers",answers);
                            replys.add(reply);
                        }
                    }


                    resultData.put("replys",replys);
                }

                LOG.info("result Data is ->" + JsonUtils.toJSONString(resultData));

        } catch (Exception var13) {
            processcode = 500011;
            var13.printStackTrace();
            LOG.info("reprocess exception..." + var13.getClass().getName() + var13.getMessage());
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
    }

    public static void main(String[] args) throws IOException {
        Map resultData=new HashMap();
        String url = "https://v.qq.com/x/cover/xc2yyp2tejg0zf0/q002638gf1s.html";

        url="https://v.qq.com/x/page/x0808h6tzyc.html";
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3610.2 Safari/537.36").get();

//        url="https://video.coral.qq.com/varticle/2747881035/comment/v2?orinum=10&oriorder=o&pageflag=1&cursor=6411913899638505362&scorecursor=0&orirepnum=2&reporder=o&reppageflag=1&source=9&_=1545792907587";

        String comment_url="";
        String comment_id="";
        if(!url.contains("comment")){
            resultData.put("view_cnt",doc.select(".action_item.action_count .num").text());//播放量
            resultData.put("reply_cnt",doc.select(".txp_btn_text").attr("title").replace("热评",""));
            resultData.put("title",doc.select(".video_title._video_title").text());
            String newsTime=doc.select("span.date._date").text().replace("发布","").
                    replace("年","-").replace("月","-").replace("日","");
            resultData.put("newstime",newsTime);

            String html=doc.html();
            comment_id=getStrByPrePost(html,"comment_id\":\"","\"");



            if(StringUtils.isBlank(comment_id)){
                String pageId="";
                String pattern="/page/(\\w+)\\.html";
                Pattern r=Pattern.compile(pattern);
                // 现在创建 matcher 对象
                Matcher m = r.matcher(url);
                if (m.find( )) {
                    pageId = m.group(1);
                }
                String video_comment_url="https://ncgi.video.qq.com/fcgi-bin/video_comment_id?otype=json&callback=&op=3&vid="+pageId+"&_="+new Date().getTime();
                String video_comment_idHTML=video_comment_id(video_comment_url);
                video_comment_idHTML=video_comment_idHTML.replace("QZOutputJson=","");
                video_comment_idHTML=video_comment_idHTML.substring(0,video_comment_idHTML.length()-1);
                JSONObject video_comment_id_json=JSONObject.parseObject(video_comment_idHTML);
                comment_id=video_comment_id_json.getString("comment_id");
            }

            comment_url="https://video.coral.qq.com/varticle/"+comment_id+"/comment/v2?" +
                    "orinum=10&oriorder=o&pageflag=1&scorecursor=0&orirepnum=2&reporder=o&" +
                    "reppageflag=1&source=9&_="+new Date().getTime();
        }else{
            comment_url=url;

            String pattern="/varticle/(\\d+)/comment";
            Pattern r=Pattern.compile(pattern);
            // 现在创建 matcher 对象
            Matcher m = r.matcher(url);
            if (m.find( )) {
                comment_id=m.group(1);
            } else {
                System.out.println("没有匹配");
            }
        }

        String commentStr=comment(comment_url);
        JSONObject commentJson=JSONObject.parseObject(commentStr);

        Integer errCode=commentJson.getInteger("errCode");
        if(errCode==0) {
            JSONObject dataJSON = commentJson.getJSONObject("data");

            boolean hasnext = dataJSON.getBoolean("hasnext");
            String last = dataJSON.getString("last");

            if(hasnext){
                String nextPage=comment_url+"&cursor="+last;
                resultData.put("nextpage",nextPage);

                List<Map<String, String>> tasks = new ArrayList<>();
                Map task=new HashMap();
                task.put("link",nextPage);
                task.put("rawlink",nextPage);
                task.put("linktype","bbspost");
                tasks.add(task);
                resultData.put("tasks",tasks);
            }

            JSONArray oriCommList = dataJSON.getJSONArray("oriCommList");
            JSONObject userList = dataJSON.getJSONObject("userList");

            List<Map<String, Object>> replys = new ArrayList<>();
            if (oriCommList.size() > 0) {
                for (int i = 0; i < oriCommList.size(); i++) {
                    Map<String, Object> reply=new HashMap<>();
                    JSONObject oriComm = (JSONObject) oriCommList.get(i);
                    String userid = oriComm.getString("userid");
                    JSONObject userObj=userList.getJSONObject(userid);
                    if(userObj.size()>0){
                        reply.put("replyusername",userObj.getString("nick"));
                    }


                    String content = oriComm.getString("content");
                    String id = oriComm.getString("id");
                    String up = oriComm.getString("up");//赞
//                    String pokenum = oriComm.getString("pokenum");//踩

                    String time=oriComm.getString("time");
                    String orireplynum = oriComm.getString("orireplynum");//全部回复数

                    List<Map> answers=new ArrayList<>();


                    Integer pageTotalCnt=(Integer.parseInt(orireplynum)+10-1)/10;
                    System.out.println("pageTotalCnt"+pageTotalCnt);
                    String answer_cursor="";
                    for(int j=0;j<pageTotalCnt;j++){

                        if(j==0){
                            answer_cursor="0";
                        }

                        String comment_answer = "https://video.coral.qq.com/vcomment/" + id + "/reply/v2?targetid="+comment_id+"&reqnum=10&pageflag=2&source=9&cursor=" + answer_cursor + "&_=" + new Date().getTime();

                        System.out.println(comment_answer);

                        JSONObject commentAnswerJSON=JSONObject.parseObject(comment(comment_answer));
                        JSONObject commentAnswerJSONData=commentAnswerJSON.getJSONObject("data");
                        JSONArray repCommList=commentAnswerJSONData.getJSONArray("repCommList");

                        JSONObject repCommUserList=commentAnswerJSONData.getJSONObject("userList");
                        String commentAnswer_first=commentAnswerJSONData.getString("first");
                        answer_cursor=commentAnswer_first;
                        for(int k=0;k<repCommList.size();k++){
                            Map answer=new HashMap();
                            JSONObject repComm=(JSONObject)repCommList.get(k);
                            String answer_content=repComm.getString("content");
                            String answers_time=repComm.getString("time");
                            String answer_up_cnt=repComm.getString("up");

                            JSONObject userRepObj=repCommUserList.getJSONObject(repComm.getString("userid"));
                            if(userRepObj.size()>0){
                                answer.put("replyusername",userRepObj.getString("nick"));
                            }


                            answer.put("up_cnt",answer_up_cnt);
                            answer.put("content",answer_content);
                            answer.put("answers_time",timeStamp2Date(answers_time,null));
                            answers.add(answer);
                        }

                    }
                    reply.put("replycontent",content);
                    reply.put("replydate",timeStamp2Date(time,null));
                    reply.put("post_id",comment_id);
                    reply.put("up_cnt",up);
                    reply.put("answers",answers);
                    replys.add(reply);

                }
            }


            resultData.put("replys",replys);
        }

        System.out.println(JSONObject.toJSONString(resultData));
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

    public static String video_comment_id(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get =new HttpGet(url);
        get.addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        get.addHeader("Accept-Encoding","gzip, deflate");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.9");

        get.addHeader("Cache-Control","max-age=0");

        get.addHeader("Connection","keep-alive");
        get.addHeader("Host","ncgi.video.qq.com");
        get.addHeader("Referer","https://page.coral.qq.com/coralpage/comment/video.html");
        get.addHeader("Upgrade-Insecure-Requests","1");
        get.addHeader("Cookie","pgv_pvi=7670181888; pgv_pvid=1095659872; pt2gguin=o0412181346; RK=aQjI0JhROa; ptcz=561484f3a040ca05c6d3d97b0cb757edb1914518c11cbe1f7ec3ba602ed511b8; tvfe_boss_uuid=6a910104f6a71cb4; pgv_info=ssid=s3440696312; o_cookie=412181346; mobileUV=1_167e3226b71_dc820; pgv_si=s846907392; _qpsvr_localtk=0.934454356004272; ptisp=ctc");
        get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3610.2 Safari/537.36");

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

    public static String comment(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get =new HttpGet(url);
        get.addHeader("Accept","*/*");
        get.addHeader("Accept-Encoding","gzip, deflate");
        get.addHeader("Accept-Language","zh-CN,zh;q=0.9");
        get.addHeader("Connection","keep-alive");
        get.addHeader("Host","video.coral.qq.com");
        get.addHeader("Referer","https://page.coral.qq.com/coralpage/comment/video.html");
        get.addHeader("Cookie","tvfe_boss_uuid=6270b7f3235cc2ed; pgv_pvid=6615459840; pgv_info=ssid=s9614788416; g_tk=6ace119902fab872118ba47d76bb4d9de50286d9");
        get.addHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3610.2 Safari/537.36");

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

}
