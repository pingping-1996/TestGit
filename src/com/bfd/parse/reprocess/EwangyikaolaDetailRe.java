package com.bfd.parse.reprocess;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.crawler.utils.DataUtil;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;

public class EwangyikaolaDetailRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(EwangyikaolaDetailRe.class);
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        try {
    	    for (Map<String, Object> data : unit.getAjaxdata()) {
    	    	if(data.containsKey("data")){
		            byte[] valuebyte= DataUtil.unzipAndDecode((String) data.get("data"));
		            String news = new String(valuebyte);
		            System.out.println("json news值是："+news);
    	    	}
    	    }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ReProcessResult(processcode, processdata);
    }
}