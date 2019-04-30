package com.bfd.parse.reprocess;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;

public class NdagongwangListRe implements ReProcessor{
    private static final Log LOG = LogFactory.getLog(NdagongwangListRe.class);
    private static final Pattern PATTERN = Pattern.compile("\\d+");
    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
        int processcode = 0;
        System.out.println("大公网后处理插件启动");
        Map<String, Object> processdata = new HashMap<String, Object>();
        // 得到模板解析结果
        Map<String, Object> resultData = result.getParsedata().getData();
        try {
            if (resultData != null && resultData.size() > 0) {
                // 修改items值
                if (resultData.containsKey("items")) {
                    // 遍历 items追加type字段
                    List<Map<String,Object>> items = (List<Map<String, Object>>) resultData.get("items");
                    for (Map<String,Object> item : items) {
                        String posttime = item.get("posttime").toString();
                        if(posttime.endsWith("分钟前")){
                            int endIndex =  posttime.length()-3;
                            Matcher matcher = PATTERN.matcher(posttime);
                            String str = "";
                            while (matcher.find()) {
                                str =  matcher.group(0);
                                break;
                            }
                            int stratIndex =  posttime.indexOf(str);
                            String stringIndex = posttime.substring(stratIndex, endIndex);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            cal.add(Calendar.MINUTE, -Integer.parseInt(stringIndex));
                            posttime = sdf.format(cal.getTime());
                        }else if(posttime.endsWith("小时前")){
                            int endIndex =  posttime.length()-3;
                            Matcher matcher = PATTERN.matcher(posttime);
                            String str = "";
                            while (matcher.find()) {
                                str =  matcher.group(0);
                                break;
                            }
                            int stratIndex =  posttime.indexOf(str);
                            String stringIndex = posttime.substring(stratIndex, endIndex);
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(date);
                            cal.add(Calendar.MINUTE, -Integer.parseInt(stringIndex)*60);
                            posttime = sdf.format(cal.getTime());
                        }else if(posttime.contains("昨天")){
                            int stratIndex =  posttime.indexOf("昨天");
                            String stringIndex = posttime.substring(stratIndex+2);
                            Calendar cal = Calendar.getInstance();
                            stringIndex = sdf1.format(date)+" "+stringIndex+":00";
                            try {
                                date = sdf.parse(stringIndex);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            cal.setTime(date);
                            cal.add(Calendar.MINUTE, -24*60);// 24小时制
                            posttime = sdf.format(cal.getTime());
                        }else if(posttime.contains("前天")){
                            int stratIndex =  posttime.indexOf("前天");
                            String stringIndex = posttime.substring(stratIndex+2);
                            Calendar cal = Calendar.getInstance();
                            stringIndex = sdf1.format(date)+" "+stringIndex+":00";
                            try {
                                date = sdf.parse(stringIndex);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            cal.setTime(date);
                            cal.add(Calendar.MINUTE, -24*60*2);// 24小时制
                            posttime = sdf.format(cal.getTime());
                        }else {
                            Matcher matcher = PATTERN.matcher(posttime);
                            String str = "";
                            while (matcher.find()) {
                                str =  matcher.group(0);
                                break;
                            }
                            int stratIndex =  posttime.indexOf(str);
                            String stringIndex = posttime.substring(stratIndex);
                            stringIndex = "2019-"+stringIndex+":00";
                            try {
                                date = sdf.parse(stringIndex);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            posttime = sdf.format(date);
                        }
                        item.put("posttime", posttime);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.info( this.getClass().getName() + "reprocess exception...");
            processcode = 1;
        }
        // 解析结果返回值 0代表成功
        return new ReProcessResult(processcode, processdata);
    }
}
