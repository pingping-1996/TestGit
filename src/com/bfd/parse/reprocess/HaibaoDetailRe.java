package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.reprocess.ReProcessResult;
import com.bfd.parse.reprocess.ReProcessor;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HaibaoDetailRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(HaibaoDetailRe.class);
    private static final Pattern patternpage = Pattern.compile("上一页");
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult, ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        String html = unit.getPageData();
        Document doc = Jsoup.parse(html);
        try {
            processdata = new HashMap();
            Map<String, Object> resultData = parseResult.getParsedata().getData();
            Matcher matcherm = patternpage.matcher(html);

        } catch (Exception e) {
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }
        ParseUtils.getIid(unit, parseResult);
        // 解析结果返回值 0代表成功
        return new ReProcessResult(processcode, processdata);
    }

    public static void main(String[] args) throws IOException {
        Document doc=Jsoup.connect("https://www.guokr.com/article/443236/").get();
        String str=doc.select(".content-th>a").text();
        System.out.println(str);
    }

}