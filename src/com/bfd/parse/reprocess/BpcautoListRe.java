package com.bfd.parse.reprocess;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.ParseUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

public class BpcautoListRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(BpcautoListRe.class);

    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult parseResult,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        Map<String, Object> resultData = parseResult.getParsedata().getData();
        /*
        * 列表页没有回复数要加上-1024
        * */
        int reply_cnt = -1024;
        if (resultData.containsKey("items")) {
            List<Map<String, Object>> comments = (List<Map<String, Object>>) resultData.get("items");
            for (Map<String, Object> map : comments) {
                if (!map.containsKey("reply_cnt")) {
                    map.put("reply_cnt", reply_cnt);
                }
            }

        }


        ParseUtils.getIid(unit, parseResult);

        return new ReProcessResult(processcode, processdata);
    }
}
