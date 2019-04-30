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

import javax.xml.bind.SchemaOutputResolver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BxcarListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(BxcarListRe.class);
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        System.out.println("20190130爱卡汽车");
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = result.getParsedata().getData();
        List<Map<String, Object>> items = new ArrayList<>();
        List<Map<String,Object>> tasks=(List)resultData.get("tasks");
        String arry = unit.getPageData();// String html = unit.getPageData();
        System.out.println("20190212爱卡汽车爱卡汽车爱卡汽车============="+arry);
        try{
            /*
             * 获取动态数据返回信息
             *
             * */
            String arry2 = arry.substring(4,arry.length()-1);
            JSONObject jsondata = JSON.parseObject(arry2);//转为obj格式
            JSONObject bbsModelList = jsondata.getJSONObject("bbsModelList");
            String num = bbsModelList.getString("totalPageNums");//修改前为社区
            //String num = bbsModelList.getString("totalPage");
            Integer num1 = Integer.valueOf(num);
            JSONArray bbsList = bbsModelList.getJSONArray("bbsList");//得到list,改前为社区
            //JSONArray bbsList = bbsModelList.getJSONArray("togetherViewList");
            System.out.println("20190212卡汽车列表有问题20190219: ==="+num1);
            /*
             *将内容详情链接添加到tasks集合中
             *
             * 把所有相关信息写入item集合中
             *
             * */
            for(int i=0;i<bbsList.size();i++){
                JSONObject reviewObj = new JSONObject();
                JSONObject revuewObj2 = new JSONObject();
                //System.out.println("20190131爱卡汽车的列表数"+bbsList.size());
                JSONObject bbsLists=bbsList.getJSONObject(i);
                //获取当前帖子链接
                revuewObj2.put("link",bbsLists.getString("post_url"));//帖子链接
                revuewObj2.put("rawlink",bbsLists.getString("post_url"));//修改前
                //revuewObj2.put("rawlink",bbsLists.getString("info_url"));
                revuewObj2.put("linktype","bbspost");//链接类型
                tasks.add(revuewObj2);
                //获取帖子列表相关信息
                reviewObj.put("posttime",bbsLists.getString("publish_time"));//发表日期
                //reviewObj.put("itemname",bbsLists.getString("title"));//帖子标题
                String regex = "<[^>]*>";
                reviewObj.put("itemname",bbsLists.getString("title").replaceAll(regex, ""));
                //reviewObj.put("itemname",bbsLists.getString("title_info"));

                reviewObj.put("reply_cnt",bbsLists.getString("replies"));//回复数

                reviewObj.put("view_cnt",bbsLists.getString("views"));//浏览数

                reviewObj.put("cate",bbsLists.getString("fourm_name"));//论坛来源

                reviewObj.put("itemlink", revuewObj2);

                items.add(reviewObj);

            }
            resultData.put("items", items);
            resultData.put("tasks", tasks);

            /*
             * 生成下一页任务并添加到tasks中
             * */
            String url =unit.getUrl();
            //截取url中的页码
            //http://sou.xcar.com.cn/XcarSearch/searchresult/together/%E9%A3%9E%E5%88%A9%E6%B5%A6/4
            //http://sou.xcar.com.cn/XcarSearch/infobbssearchresult/bbs/%E6%AC%A7%E8%8E%B1%E9%9B%85/none/none/none/none/1
            String s= url;
            String s1=s.replaceAll(".*[^\\d](?=(\\d+))","");
            System.out.println("去掉最后一组数字=============="+s1);

            String page = url.substring(url.indexOf("/none/none/none/none/")+21);//
            //http://sou.xcar.com.cn/XcarSearch/infobbssearchresult/bbs/%E6%AC%A7%E8%8E%B1%E9%9B%85/none/none/none/none/1

            //http://sou.xcar.com.cn/XcarSearch/infobbssearchresult/bbs/%E6%88%B4%E6%A3%AE/none/none/none/none/240
            //http://sou.xcar.com.cn/XcarSearch/infobbssearchresult/bbs/%E6%B4//
            //http://sou.xcar.com.cn/XcarSearch/infobbssearchresult/bbs/加湿器/none/none/none/none/4
            //System.out.println("20190219爱卡汽车2359================xcar:"+page);
            //去掉最后一个数字

            String url2 = url.substring(0,url.indexOf("/none/none/none/none/")+21);

            Integer i = Integer.valueOf(page);
            System.out.println("20190304======================"+i);

            int ij = i+1;
            String nextPage = url2+ij;

            if(i < num1 ){
                if(i < 100){
                    resultData.put("nextpage",nextPage);
                    Map nextpage_ = new HashMap();
                    nextpage_.put("link", nextPage);
                    nextpage_.put("rawlink", nextPage);
                    nextpage_.put("linktype", "bbspostlist");
                    //resultData.put("nextpage_", nextpage_);
                    tasks.add(nextpage_);
                    resultData.put("tasks", tasks);

                    //resultData.put("pageidx",page);//本页
                    resultData.put("url",url);//本页url
                    //resultData.put("type","bbslist");//类型
                    resultData.put("nextpage",nextPage);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }
        ParseUtils.getIid(unit, result);
        return new ReProcessResult(processcode, processdata);
    }
}
