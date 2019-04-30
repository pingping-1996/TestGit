package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

import static org.jsoup.Jsoup.parse;

public class NhaibaowangcommentRe2 implements ReProcessor{
    private static final Log LOG = LogFactory.getLog(BpcbabyContentRe.class);
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        System.out.println("=======海报入口=======");
        int processcode = 0;
        Map<String, Object> processdata = new HashMap();
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        try{
            System.out.println("海报网啊海报网");
            String html = unit.getPageData();
            Document doc = parse(html);
            System.out.println("20190218海报网！！！！！！"+doc);
            String str =  doc.select(".contxt p").get(0).text();
            resultData.put("str",str);

        }catch (Exception e){
            e.printStackTrace();
            LOG.info(this.getClass().getName() + "reprocess exception...");
            processcode = 500011;
        }



        return new ReProcessResult(processcode, processdata);
    }



}
