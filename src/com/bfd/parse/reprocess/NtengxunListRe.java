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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NtengxunListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(NtengxunListRe.class);
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        System.out.println("===20190306腾讯新闻===");
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        List<Map<String, Object>> items = new ArrayList<>();
        List<Map<String, Object>> tasks = new ArrayList<>();;
        String arry = unit.getPageData();
        JSONObject jsonObject = JSON.parseObject(arry);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        System.out.println("页面下载======================="+arry);
        try{
            if(!unit.getPageData().isEmpty()){
                if(jsonArray.isEmpty()){
                    System.out.println("腾讯新闻列表的最后一页");
                    resultData.put("tasks", tasks);
                    resultData.put("items", items);

                }else{
                    for(int i=0;i<jsonArray.size();i++){
                        JSONObject reviewObj = new JSONObject();
                        JSONObject revuewObj2 = new JSONObject();//tasks
                        JSONObject jsonArrays =jsonArray.getJSONObject(i);

                        revuewObj2.put("link",jsonArrays.getString("vurl"));//帖子链接
                        revuewObj2.put("rawlink",jsonArrays.getString("vurl"));//修改前
                        revuewObj2.put("linktype","newscontent");//链接类型
                        System.out.println("生成任务链接==="+jsonArrays.getString("vurl"));
                        if(!revuewObj2.isEmpty()){
                            tasks.add(revuewObj2);
                        }
                        jsonArrays.getString("title");
                        System.out.println((i+1)+"=========="+jsonArrays.getString("title"));
                        reviewObj.put("post_time",jsonArrays.getString("update_time"));//发表日期
                        reviewObj.put("title",jsonArrays.getString("title"));//标题
                        reviewObj.put("source",jsonArrays.getString("source"));//来源
                        reviewObj.put("reply_cnt",jsonArrays.getString("comment_num"));//来源//评论数
                        items.add(reviewObj);
                    }
                    resultData.put("items", items);
                    resultData.put("tasks", tasks);

                String url = unit.getUrl();
//        String url = "https://pacaio.match.qq.com/irs/rcd?cid=146&token=49cbb2154853ef1a74ff4e53723372ce&ext=msh&page=1&expIds=20190306A0E6BV";
                    String regEx = "page=(\\d+)";//匹配不是第一页
                    Pattern p =Pattern.compile(regEx);
                    Matcher flag = p.matcher(url);
                    String url2="";
                    if(flag.find()){
                        url2 = flag.group();
                    }
                    String url22 = url2.substring(5);
                    Integer url3  = Integer.valueOf(url22);
                    url3 ++;
                    String nextpage="https://pacaio.match.qq.com/irs/rcd?cid=146&token=49cbb2154853ef1a74ff4e53723372ce&ext=msh&page=" + url3;

                    resultData.put("nextpage",nextpage);

                    Map nextpage_ = new HashMap();
                    nextpage_.put("link", nextpage);
                    nextpage_.put("rawlink", nextpage);
                    nextpage_.put("linktype", "newslist");
                    resultData.put("nextpage_", nextpage_);
                    tasks.add(nextpage_);
                    resultData.put("tasks", tasks);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("有错误！！！！！！！！！！！！！！！！！");
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
    }
}
