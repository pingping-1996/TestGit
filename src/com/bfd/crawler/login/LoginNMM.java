package com.bfd.crawler.login;

import com.bfd.crawler.loginutil.JsonUtils;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    int accnum = 750;



    @Override
    public String loginPlugin() {

        getIplist();//获取本地ip列表
        int num = (int) (Math.random() * ipList.size());//随机获取ip列表中某个ip
        String ip = ipList.get(num);
        String jsonStr = "";
//        LOG.info("mmbip" + ip);
        try {
            Map<String, Object> json = new HashMap<>();

            Date date=new Date();
            long d=new Date().getTime()+9000000*24*60*60*1000;
            date.setTime(d);
            String cookie = "";
            org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
            GetMethod get = new GetMethod("http://www.xcar.com.cn/bbs/viewthread.php?tid=34129412&page=2");
            client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            client.executeMethod(get);
            org.apache.commons.httpclient.Cookie[] cookies = client.getState().getCookies();

            if (cookies.length == 0) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.length; i++) {
                    String a = cookies[i].toString();
                    if(i==cookies.length-1){
                        cookie += a;
                    }else{
                        cookie += a;
                        cookie += ";";
                    }
                }
                System.out.println("20190104==================="+cookie);
            }
            String result = cookie;

            LOG.info("dddddddddd++++1" + result);
            System.out.println("dddddddddd+++++2" + result);
            accnum = accnum - 1;
            //向json中添加数据
            Map<String, String> a = new HashMap<String, String>();
            a.put("ipflag", ip);
            a.put("ua", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
            a.put("cookieType", "tourist");
            json.put("otherdata", a);
//				json.put("ipflag", ip);
            json.put("code", 0);
            json.put("accnum", accnum);
            json.put("userid", "Bxcar" + accnum);
            json.put("cookie", result);
            //__9XRwuHhHTjQiC8wXCnCj7wsEBU9x=wy; expires=Sat, 21 Apr 26660 02:33:33 GMT; path=/; domain=.mmbang.com
            jsonStr = JsonUtils.toJSONString(json);


        } catch (Exception e) {
            System.out.println(e);
        }

        return jsonStr;
    }


    @Override
    public String loginPlugin(String userId) {
        getIplist();//获取本地ip列表
        int num = (int) (Math.random() * ipList.size());//随机获取ip列表中某个ip
        String ip = ipList.get(num);
        String jsonStr = "";

        try {
            Map<String, Object> json = new HashMap<>();
            String cookie = "";
            org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
            GetMethod get = new GetMethod("http://www.xcar.com.cn/bbs/viewthread.php?tid=34129412&page=2");
            client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            client.executeMethod(get);
            org.apache.commons.httpclient.Cookie[] cookies = client.getState().getCookies();
            if (cookies.length == 0) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.length; i++) {
                    String a = cookies[i].toString();
                    if(i==cookies.length-1){
                        cookie += a;
                    }else{
                        cookie += a;
                        cookie += ";";
                    }
                }
                System.out.println(cookie);
            }
            Date date=new Date();
            long d=new Date().getTime()+9000000*24*60*60*1000;
            date.setTime(d);
            String result = cookie;
            accnum = accnum - 1;

//            str = "__9XRwuHhHTjQiC8wXCnCj7wsEBU9x";
//            html = createCookie(str, "wy", 9000000 + "", ".mmbang.com");
            LOG.info("ffffffff++++" + result);
            //向json中添加数据
            Map<String, String> a = new HashMap<String, String>();
            a.put("ipflag", ip);
            a.put("ua", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
            a.put("cookieType", "tourist");
            json.put("otherdata", a);
            json.put("code", 0);
            json.put("accnum", accnum);
            json.put("userid", "Bxcar" + accnum);
            json.put("cookie", result);
            jsonStr = JsonUtils.toJSONString(json);
//			else{
//				String cookie = chinaPosrSloginnew2(ip);
//				System.out.println(cookie+"ddd2");
//				Map<String,String> a = new HashMap<String,String> ();
//				a.put("ipflag",ip);
//				a.put("ua","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
//				a.put("cookieType", "tourist");
//				json.put("otherdata", a);
////				json.put("ipflag", ip);
//				json.put("code", 0);
//				json.put("accnum", accnum);
//				json.put("userid", "Nmamabang"+accnum);
//				json.put("cookie", cookie);
//				//__9XRwuHhHTjQiC8wXCnCj7wsEBU9x=wy; expires=Sat, 21 Apr 26660 02:33:33 GMT; path=/; domain=.mmbang.com
//				jsonStr = JsonUtils.toJSONString(json);
//			}

        } catch (Exception e) {
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
                            if (ip.startsWith("211")) {
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
            ipnum = ipList.size() - 1;
        }
    }


    public static void main(String[] args) throws Exception {
        Date date=new Date();
        long d=new Date().getTime()+9000000*24*60*60*1000;
        date.setTime(d);
        String str = "__9XRwuHhHTjQiC8wXCnCj7wsEBU9x=wy; expires="+date.toGMTString()+"; path=/; domain=.mmbang.com";
        System.out.println(str);




//        engine.eval("\tfunction createCookie(name,value,days, domain) {\n" +
//                "\t\tif(name == 'http_referrer'){\n" +
//                "\t\t\tvar exp = new Date();\n" +
//                "\t\t\texp.setTime(exp.getTime() + 10000);\n" +
//                "\t\t\treturn name+\"=\"+value+\";expires=\"+exp.toGMTString();\n" +
//                "\t\t}\n" +
//                "\t\tif (days) {\n" +
//                "\t\t\tvar date = new Date();\n" +
//                "\t\t\tdate.setTime(date.getTime()+(days*24*60*60*1000));\n" +
//                "\t\t\tvar expires = \"; expires=\"+date.toGMTString()+\"; path=/; domain=\"+domain;\n" +
//                "\t\t}\n" +
//                "\t\telse var expires = \"\";\n" +
//                "\t\treturn name+\"=\"+value+expires;\n" +
//                "\t}");//D:\workdoc\aes.js
        //date.setTime(date.getTime()+(days*24*60*60*1000));\n" +
        //                "\t\t\tvar expires = \"; expires=\"+date.toGMTString()+\"; path=/; domain=\"+domain;\n" +
     }

    public static String chinaPosrSloginnew1(String ip) throws Exception {
        CookieStore cookieStroe = new BasicCookieStore();
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCookieStore(cookieStroe);
//		builder.setProxy(new HttpHost("127.0.0.1", 1080));
        HttpClient httpClient = builder.build();
        HttpGet get = new HttpGet("https://www.mmbang.com/bang/436/28604212_1");
//		RequestConfig requestConfig = getRequestConfigByIp(ip);
//		get.setConfig(requestConfig);
        get.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        get.setHeader("accept-encoding", "gzip, deflate");
        get.setHeader("accept-Language", "zh-CN,zh;q=0.9");
        get.setHeader("cache-control", "max-age=0");
        get.setHeader("referer", "http://zhannei.baidu.com/cse/search?q=%E5%A4%87%E5%AD%95&p=2&s=16068099689117862280&entry=1");
        get.setHeader("upgrade-Insecure-Requests", "1");
        get.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
        HttpResponse response = httpClient.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity, "utf-8");
        return html;

    }

    public static String chinaPosrSloginnew2(String ip) throws Exception {
        CookieStore cookieStroe = new BasicCookieStore();
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCookieStore(cookieStroe);
//		builder.setProxy(new HttpHost("127.0.0.1", 1080));
        HttpClient httpClient = builder.build();
        HttpGet get = new HttpGet("https://www.mmbang.com/bang/436/28604212_1");
//		RequestConfig requestConfig = getRequestConfigByIp(ip);
//		get.setConfig(requestConfig);
        get.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        get.setHeader("accept-encoding", "gzip, deflate");
        get.setHeader("accept-Language", "zh-CN,zh;q=0.9");
        get.setHeader("cache-control", "max-age=0");
        get.setHeader("referer", "http://zhannei.baidu.com/cse/search?q=%E5%A4%87%E5%AD%95&p=2&s=16068099689117862280&entry=1");
        get.setHeader("upgrade-Insecure-Requests", "1");
        get.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
        HttpResponse response = httpClient.execute(get);
        int statusCode = response.getStatusLine().getStatusCode();
        StringBuffer tmpcookies = new StringBuffer();
        if (statusCode == 302 || statusCode == 200 || statusCode == 301) {
            List cookies = cookieStroe.getCookies();

            String domain = "";
            String path = "";
            String expirDate = "";
            for (Iterator iterator1 = cookies.iterator(); iterator1.hasNext(); ) {
                Cookie c = (Cookie) iterator1.next();
                String name = c.getName();
                String value = c.getValue();
                c.getExpiryDate();
                path = c.getPath();
                domain = c.getDomain();

                if (c.getExpiryDate() != null) {
                    expirDate = c.getExpiryDate().toString();
                }

                //referer=http%3A%2F%2Fzhannei.baidu.com%2Fcse%2Fsearch%3Fq%3D%25E5%25A4%2587%25E5%25AD%2595%26p%3D2%26s%3D16068099689117862280%26entry%3D1; path=/ domain=www.mmbang.comuid=0;xss_key=502507cc18b384171ca079890c3af58d; expires=Sat Mar 02 14:19:59 CST 2019;ddd


                tmpcookies.append(name).append("=").append(value).append(";");
                if (StringUtils.isNotEmpty(path) && !tmpcookies.toString().contains(" path=")) {
                    tmpcookies.append(" path=").append(path).append(";");
                }

                if (StringUtils.isNotEmpty(domain) && !tmpcookies.toString().contains(" domain=")) {
                    tmpcookies.append(" domain=").append(domain).append(";");
                }

                if (StringUtils.isNotEmpty(expirDate) && !tmpcookies.toString().contains(" expires=")) {
                    tmpcookies.append(" expires=").append(expirDate).append(";");
                }
            }
            return tmpcookies.toString();
        } else {
            return "";
        }

    }

    public static RequestConfig getRequestConfigByIp(String ip) {
        RequestConfig requestConfig = null;
        RequestConfig.Builder config_builder = RequestConfig.custom();
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        config_builder.setLocalAddress(inetAddress);
        requestConfig = config_builder.build();
        return requestConfig;
    }



}
