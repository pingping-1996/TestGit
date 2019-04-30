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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NtengxunCommentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NtengxunCommentRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        System.out.println("===20190306腾讯新闻===");
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        List<Map<String, Object>> comments = new ArrayList<>();
        List<Map<String,Object>> tasks = (List)resultData.get("tasks");

        try {
            String arry = unit.getPageData();
            String regex = "_article(\\d+)commentv2\\(";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(arry);
            String xxx = "";
            if (matcher.find()) {
                xxx = matcher.group(0);
            }
            String html = arry.replace(xxx, "");
            String html2 = html.substring(0, html.length() - 1);
            JSONObject jsonObject = JSON.parseObject(html2);
            JSONObject jsonObject2 = jsonObject.getJSONObject("data");
            System.out.println(jsonObject2);
            JSONArray jsonArray = jsonObject2.getJSONArray("oriCommList");
            System.out.println(jsonArray);
            String id = "";
            String userid = "";
            String name = "";
            for (int i = 0; i < jsonArray.size(); i++) {
                List list = new ArrayList();
                //System.out.println("进入");
                JSONObject jsonArrays = jsonArray.getJSONObject(i);
                String content = jsonArrays.getString("content");//评论内容
                //System.out.println(content);

                String up = jsonArrays.getString("up");//点赞数
                //System.out.println(up);
                String time = jsonArrays.getString("time");
                String tiem2 = stampToDate(time);
                id = jsonArrays.getString("id");
                userid = jsonArrays.getString("userid");

                System.out.println("id=======" + id);
                String rep = jsonObject2.getString("repCommList");
                if (!("[]".equals(rep))){
                JSONObject object = jsonObject2.getJSONObject("repCommList");
                if (object!=null) {
                    JSONArray arryy = object.getJSONArray(id);
                    if (arryy != null) {
                        for (int j = 0; j < arryy.size(); j++) {
                            JSONObject reviewObj = new JSONObject();
                            JSONObject arryys = arryy.getJSONObject(j);
                            String content2 = arryys.getString("content");
                            String up2 = arryys.getString("up");


                            String time3 = arryys.getString("time");
                            String time4 = stampToDate(time3);
                            JSONObject object2 = jsonObject2.getJSONObject("userList");
                            String userid2 = arryys.getString("userid");
                            if(!object2.isEmpty()){
                                JSONObject object3 =  object2.getJSONObject(userid2);
                                name = object3.getString("nick");
                            }
                            System.out.println("二级content======" + content2);
                            reviewObj.put("comment_content", content2);
                            reviewObj.put("up_cnt", Integer.valueOf(up2));
                            reviewObj.put("comment_time",time4);
                            reviewObj.put("username", name);
                            list.add(reviewObj);
                        }
                    }
                }
                }
                JSONObject object2 = jsonObject2.getJSONObject("userList");
                if(object2!=null){
                    JSONObject object3 =  object2.getJSONObject(userid);
                    name = object3.getString("nick");
                    //JSONArray arryy = object.getJSONArray(id);
                    System.out.println("hjklklmm===="+name);

                }
                Map map = new HashMap();
                map.put("comment_content", content);
                map.put("up_cnt", Integer.valueOf(up));
                map.put("comment_time",tiem2);
                map.put("answers", list);
                map.put("username",name);
                comments.add(map);
            }
            //resultData.put("replys", comments);

            resultData.put("comments", comments);
            String cid = jsonObject2.getString("last");
            String targetid = jsonObject2.getString("targetid");//http://coral.qq.com/article/3764469063/comment/v2?callback=_article3764469063commentv2&orinum=10&oriorder=o&pageflag=1&cursor=6509213987263871496&scorecursor=0&orirepnum=2&reporder=o&reppageflag=1&source=1
            System.out.println("腾讯新闻cid==="+cid);
            if(!("false".equals(cid))){
                String next = "http://coral.qq.com/article/" + targetid + "/comment/v2?callback=_article" + targetid + "commentv2&orinum=10&oriorder=o&pageflag=1&cursor=" + cid + "&scorecursor=0&orirepnum=2&reporder=o&reppageflag=1&source=1";

                Map map2 = new HashMap();
                map2.put("link", next);
                map2.put("rawlink", next);
                map2.put("linktype", "newscomment");
                tasks.add(map2);
                // "last":false,
                resultData.put("nextpage", next);
                resultData.put("tasks", tasks);
            }


        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("有错误！！！！！！！！！！！！！！！！！");
        }
            ParseUtils.getIid(unit, result);
            return new ReProcessResult(processcode, processdata);
    }

    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt*1000);
        res = simpleDateFormat.format(date);
        return res;
    }
}
