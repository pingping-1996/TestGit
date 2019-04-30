package com.bfd.parse.preprocess;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.parse.ParserFace;
import com.bfd.parse.facade.parseunit.ParseUnit;

public class TestPreprocess implements PreProcessor{
	private static final Log LOG = LogFactory.getLog(TestPreprocess.class);

	@Override
	public boolean process(ParseUnit unit, ParserFace parseFace) {
		// 得到页面源码
		String pageData = unit.getPageData();
		// 进行页面源码替换
		pageData = pageData.replace("<dd>","<div>").replace("</dd>", "</div>");
		LOG.info("tag <dd> has change to <div>");
		// 将修改后的页面源码放入unit中供模板解析使用
		unit.setPageData(pageData);
		unit.setPageBytes(pageData.getBytes());
		unit.setPageEncode("utf8");
		LOG.info("preprocess done!");
		// 返回成功
		return true;
	}

}
