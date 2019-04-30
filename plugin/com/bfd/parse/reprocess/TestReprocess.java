package com.bfd.parse.reprocess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.parse.ParseResult;
import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;

public class TestReprocess implements ReProcessor{
	private static final Log LOG = LogFactory.getLog(TestReprocess.class);

	@Override
	public ReProcessResult process(ParseUnit unit, ParseResult result, ParserFace parseFace) {
		int processcode = 0;
		String html = unit.getPageData();
		String url = unit.getUrl();
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
						item.put("type", "企业国有 ");
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
