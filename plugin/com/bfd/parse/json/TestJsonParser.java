package com.bfd.parse.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.parse.client.URLNormalizerClient;
import com.bfd.parse.facade.parseunit.ParseUnit;
import com.bfd.parse.util.TextUtil;

public class TestJsonParser  implements JsonParser {

	private static final Log LOG = LogFactory.getLog(TestJsonParser.class);
	@Override
	public JsonParserResult parse(Map<String, Object> taskdata,
			List<JsonData> dataList, URLNormalizerClient normalizerClient,
			ParseUnit unit) {

		int parsecode = 0;
		Map<String, Object> parsedata = new HashMap<String, Object>();
		// 对每一条数据进行处理
		for (JsonData data : dataList) {
			if (!data.downloadSuccess()) {
				continue;
			}
			String json = TextUtil.getUnzipJson(data, unit);
			try {
				// 执行解析程序
				executeParse(parsedata, json, data.getUrl(), unit);
			} catch (Exception e) {
				parsecode = 500012;
			}
		}
		JsonParserResult result = new JsonParserResult();
		try {
			result.setData(parsedata);
			// 0代表成功
			result.setParsecode(parsecode);
		} catch (Exception e) {
		}
		return result;
	}

	public void executeParse(Map<String, Object> parsedata ,String json,String url,ParseUnit unit) {
		// 保存动态数据源码到解析结果中
		parsedata.put("html", json.replaceAll("<[^>]*?>|\\s+", "").replaceAll("\\w+", "").replaceAll("[!@#$%^&*()_+-=<>?,./';\":}{\\[\\]]", "").substring(0, 10));
	}
}
