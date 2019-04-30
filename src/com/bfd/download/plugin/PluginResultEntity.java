package com.bfd.download.plugin;

import java.util.List;

public class PluginResultEntity {
	
	//下载状态码
	private int code;
	//静态数据结果
	private String content;
	//动态请求链接集
	private List<String> ajaxRequest;
	//字符集
	private String charset;
	//目前页面地址
	private String requestUrl;
	//
	private String Location;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getLocation() {
		return Location;
	}
	public void setLocation(String location) {
		Location = location;
	}
	
	public List<String> getAjaxRequest() {
		return ajaxRequest;
	}
	public void setAjaxRequest(List<String> ajaxRequest) {
		this.ajaxRequest = ajaxRequest;
	}
	/**
	 * 完整流下载插件，静态资源下载结果构造
	 * @param code
	 * @param requestUrl
	 * @param content
	 */
	public PluginResultEntity(int code, String requestUrl, String content){
		this.code = code;
		this.content = content;
		this.requestUrl = requestUrl;
		
	}
	/**
	 * 动态资源下载
	 * @param code
	 * @param requestUrl
	 * @param ajaxContent
	 */
	public PluginResultEntity(int code, String requestUrl, List<String> ajaxRequest){
		this.code = code;
		this.requestUrl = requestUrl;
		this.ajaxRequest = ajaxRequest;
	}
	
	/**
	 * 完整流下载插件：静态资源和动态数据下载结果封装
	 * @param code
	 * @param requestUrl
	 * @param content
	 * @param ajaxContent
	 */
	public PluginResultEntity(int code, String requestUrl, String content, List<String> ajaxRequest){
		this.code = code;
		this.requestUrl = requestUrl;
		this.content = content;
		this.ajaxRequest = ajaxRequest;
	}
	
	
	public static void main(String[] args) {
	}
	
}
