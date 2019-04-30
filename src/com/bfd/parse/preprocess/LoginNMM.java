package com.bfd.parse.preprocess;

import com.bfd.crawler.login.LoginPluginInterface;
import com.bfd.crawler.loginutil.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginNMM implements LoginPluginInterface {

	private static final Log LOG = LogFactory.getLog(LoginNMM.class);
	static List<String> ipList = new ArrayList<>();
	static int ipnum = 0;
	int accnum=200;
	@Override
	public String loginPlugin() {
		getIplist();//获取本地ip列表
		int num=(int)(Math.random()*ipList.size());//随机获取ip列表中某个ip
		String ip = ipList.get(num);
		String jsonStr = "";
		LOG.info("mmbip"+ip);
		try{
			System.out.println("111111111111111111111");
			LOG.info("mmbaction");
			Map<String, Object> json = new HashMap<> () ;
			String cookie = chinaPosrSloginnew(ip);
			LOG.info("mmbactioncookie"+cookie);
			String d= Jsoup.parse(cookie).select("script").html();
			LOG.info("mmbactioncookied++++"+d);
			Pattern mpattern = Pattern.compile("createCookie\\(\"(\\w+)\"");
			Matcher mmatcher = mpattern.matcher(d);
			String str = "";
			while (mmatcher.find()) {
				str = mmatcher.group(1);
			}
			LOG.info("cccccc++++"+cookie);
			System.out.println("cccccc+++++"+cookie);
			cookie=createCookie(str, "wy", 9000000+"", ".mmbang.com");
			LOG.info("dddddddddd++++"+cookie);
			System.out.println("dddddddddd+++++"+cookie);
			//向json中添加数据
				Map<String,String> a = new HashMap<String,String> ();
				a.put("ipflag",ip);
				a.put("ua","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
				a.put("cookieType", "tourist");
				json.put("otherdata", a);
//				json.put("ipflag", ip);
				json.put("code", 0);
				json.put("accnum", accnum);
				json.put("userid", "Nmamabang");
				json.put("cookie", cookie);
				//__9XRwuHhHTjQiC8wXCnCj7wsEBU9x=wy; expires=Sat, 21 Apr 26660 02:33:33 GMT; path=/; domain=.mmbang.com
				jsonStr = JsonUtils.toJSONString(json);
		}catch(Exception e){
			System.out.println(e);
		}

		return jsonStr;
	}

	@Override
	public String loginPlugin(String userId) {
		getIplist();//获取本地ip列表
		int num=(int)(Math.random()*ipList.size());//随机获取ip列表中某个ip
		String ip = ipList.get(num);
		String jsonStr = "";

		try{
			Map<String, Object> json = new HashMap<> () ;
			String cookie = chinaPosrSloginnew(ip);
			String d= Jsoup.parse(cookie).select("script").html();

			Pattern mpattern = Pattern.compile("createCookie\\(\"(\\w+)\"");
			Matcher mmatcher = mpattern.matcher(d);
			String str = "";
			while (mmatcher.find()) {
				str = mmatcher.group(1);
			}
			System.out.println("eeeeeee+++++"+cookie);
			cookie=createCookie(str, "wy", 9000000+"", ".mmbang.com");
			System.out.println("ffffffff+++++"+cookie);
			LOG.info("ffffffff++++"+cookie);
			//向json中添加数据
			Map<String,String> a = new HashMap<String,String> ();
			a.put("ipflag",ip);
			a.put("ua","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
			a.put("cookieType", "tourist");
			json.put("otherdata", a);
			json.put("code", 0);
			json.put("accnum", accnum);
			json.put("userid", "Nmamabang");
			json.put("cookie", cookie);
			jsonStr = JsonUtils.toJSONString(json);
		}catch(Exception e){
			System.out.println(e);
		}

		return jsonStr;
	}

	private static void getIplist() {
		if (ipList.size() == 0) {
			try {
				Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
				NetworkInterface networkInterface;
				Enumeration<InetAddress> inetAddresses;
				InetAddress inetAddress;
				String ip;
				while (networkInterfaces.hasMoreElements()) {
					networkInterface = networkInterfaces.nextElement();
					inetAddresses = networkInterface.getInetAddresses();
					while (inetAddresses.hasMoreElements()) {
						inetAddress = inetAddresses.nextElement();
						if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
							ip = inetAddress.getHostAddress();
//		 	 if (ip.startsWith("117")) {
							//过滤掉不是175开头的ip
							if(ip.startsWith("211")){
								ipList.add(ip);
							}
							LOG.info("ben ji ip " + ip);
							System.out.println("ben ji ip " + ip);
//		 	 }

						}
					}
				}
			} catch (Exception e) {
				LOG.error("ben ji ip error");
				System.out.println("ben ji ip error");
			}
			ipnum = ipList.size()-1;
		}
	}


	public static String chinaPosrSloginnew(String ip)throws Exception{
		CookieStore cookieStroe = new BasicCookieStore();
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setDefaultCookieStore(cookieStroe);
//		builder.setProxy(new HttpHost("127.0.0.1", 1080));
		HttpClient httpClient = builder.build();
		HttpGet get = new HttpGet("https://www.mmbang.com/bang/436/28604212_1");
		RequestConfig requestConfig = getRequestConfigByIp(ip);
		get.setConfig(requestConfig);
		get.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		get.setHeader("accept-encoding", "gzip, deflate");
		get.setHeader("accept-Language", "zh-CN,zh;q=0.9");
		get.setHeader("cache-control", "max-age=0");
		get.setHeader("referer","http://zhannei.baidu.com/cse/search?q=%E5%A4%87%E5%AD%95&p=2&s=16068099689117862280&entry=1");
		get.setHeader("upgrade-Insecure-Requests", "1");
		get.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
		HttpResponse response = httpClient.execute(get);
		int statusCode =response.getStatusLine().getStatusCode();
		HttpEntity entity =response.getEntity();
		String html = EntityUtils.toString(entity,"utf-8");
		return html;
	}

	public static RequestConfig getRequestConfigByIp(String ip)
	{
		RequestConfig requestConfig = null;
		RequestConfig.Builder config_builder = RequestConfig.custom();
		InetAddress inetAddress = null;
		try
		{
			inetAddress = InetAddress.getByName(ip);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
		}
		config_builder.setLocalAddress(inetAddress);
		requestConfig = config_builder.build();
		return requestConfig;
	}

	private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	//调用js方法解密key值
	private static String createCookie(String name, String value, String days, String domain) throws Exception{

		//engine.eval(script);
		engine.eval("\tfunction createCookie(name,value,days, domain) {\n" +
				"\t\tif(name == 'http_referrer'){\n" +
				"\t\t\tvar exp = new Date();\n" +
				"\t\t\texp.setTime(exp.getTime() + 10000);\n" +
				"\t\t\treturn name+\"=\"+value+\";expires=\"+exp.toGMTString();\n" +
				"\t\t}\n" +
				"\t\tif (days) {\n" +
				"\t\t\tvar date = new Date();\n" +
				"\t\t\tdate.setTime(date.getTime()+(days*24*60*60*1000));\n" +
				"\t\t\tvar expires = \"; expires=\"+date.toGMTString()+\"; path=/; domain=\"+domain;\n" +
				"\t\t}\n" +
				"\t\telse var expires = \"\";\n" +
				"\t\treturn name+\"=\"+value+expires;\n" +
				"\t}");//D:\workdoc\aes.js
		Invocable inv = (Invocable) engine;
		return (String) inv.invokeFunction("createCookie",name,value,days,domain);
	}
}
