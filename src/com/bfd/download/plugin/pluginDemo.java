package com.bfd.download.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bfd.download.util.OkHttpUtil;

public class pluginDemo implements DownPlugin{
	
	private static Log logger = LogFactory.getLog(pluginDemo.class);
	/**
	 * 实现各个特定下载任务，param组装，cookie, header定制化
	 */
	public PluginResultEntity doDown(HashMap<String, Object> task, String ip) {
		//逻辑代码
		HashMap<String, Object> taskData = (HashMap<String, Object>) task.get("taskdata");
		PluginResultEntity response = new PluginResultEntity(200,"http://www.baidu.com","哈哈哈，测试在测试呀");
		String url = "";
		Map<String, Object> header = null;
		String  download_ip = ip;
		//httpClinet.download();
		//实现具体页面下载逻辑，
		//将数据源码content,
		System.out.println("task is "+ task.toString());
		System.out.println("ip is "+ ip);
		HashMap<String, Object> res = OkHttpUtil.doDownLoad(taskData, ip);
		if((boolean) res.get("isOk")){
			response.setContent(res.get("content").toString());
			response.setCode(200);
			response.setRequestUrl(taskData.get("url").toString());
		}
		
		return response;
	}

	@Override
	public AjaxRequestEntity getRequest(HashMap<String, Object> task) {
		
		AjaxRequestEntity ajaxRequestResult = new AjaxRequestEntity();
		List<Map<String,Object>> ajax = new ArrayList<Map<String, Object>>();
		Map<String,Object> request = new HashMap<String, Object>();
		//当前动态请求的url
		request.put("url", "http:\\\\localhost\\ajaxPluginTest");
		request.put("method", "GET");
		request.put("ajaxext", null);
		request.put("header", null); //可指定特殊header
		ajax.add(request);
		ajaxRequestResult.setAjax(ajax);
		ajaxRequestResult.setCode(200);
		ajaxRequestResult.setStatus(true);
		ajaxRequestResult.setNum(0);  //指定有几条动态请求 此时1
		return ajaxRequestResult;
	}
}
