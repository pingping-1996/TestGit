package com.bfd.parse.haibao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bfd.parse.reprocess.ReProcessResult;
import com.bfd.parse.reprocess.ReProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;

public class BonlyladyCommentRe implements ReProcessor {
    private static final Log LOG = LogFactory.getLog(BonlyladyCommentRe.class);
    private static final Pattern patternpage = Pattern.compile("<strong>1</strong>");
    @Override
    public ReProcessResult process(ParseUnit unit, ParseResult result,
                                   ParserFace parseFace) {
        int processcode = 0;
        Map<String, Object> processdata = null;
        try {
            processdata = new HashMap<String, Object>();
            Map<String, Object> resultData = result.getParsedata().getData();

            String html = unit.getPageData();
            Matcher matcherm = patternpage.matcher(html);
            if (matcherm.find()){
                List<Map<String, Object>> replys = (List<Map<String, Object>>) resultData.get("replys");
                replys.remove(0);
                resultData.put("replys", replys);
            }else{
                resultData.remove("author_avatar");
                resultData.remove("contents");
                resultData.remove("newstime");
                resultData.remove("authorname");
                resultData.remove("forum_score");
                resultData.remove("author");
                resultData.remove("author_level");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ReProcessResult(processcode, processdata);
    }
}
