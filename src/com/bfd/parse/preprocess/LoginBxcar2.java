package com.bfd.parse.preprocess;

import com.bfd.crawler.login.LoginPluginInterface;
import com.bfd.crawler.loginutil.JsonUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;



public class LoginBxcar2 implements LoginPluginInterface {
    private static final Log LOG = LogFactory.getLog(LoginBxcar2.class);
    static List<String> ipList = new ArrayList<>();
    static int ipnum = 0;
    int accnum=200;
    public String loginPlugin() {
        getIplist();//获取本地ip列表
        int num=(int)(Math.random()*ipList.size());//随机获取ip列表中某个ip
        String ip = ipList.get(num);
        String jsonStr = "";
        try{
            Map<String, Object> json = new HashMap<>() ;

            String cookie="TY_SESSION_ID=b21ca37f-3b35-4fef-92aa-1d6927bf528a; _Xdwuv=5c48251ebef16; _Xdwnewuv=1; _PVXuv=5c48a80d413fc; _fwck_www=8467c18e04e9cfa191c9e89ef7c68786; _appuv_www=4e25489e00ce39894493853d4ec9411b; _fwck_my=e4b214cdf4d2e271930b8237cd9b6826; _appuv_my=47bb852d5a5829288037166484125771; bbs_cookietime=31536000; _locationInfo_=%7Burl%3A%22http%3A%2F%2Fshenyang.xcar.com.cn%2F%22%2Ccity_id%3A%2263%22%2Cprovince_id%3A%226%22%2C%20city_name%3A%22%25E6%25B2%2588%25E9%2598%25B3%22%7D; ";
            //向json中添加数据
            Map<String,String> a = new HashMap<String,String> ();
            a.put("ipflag",ip);
            a.put("ua","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
            a.put("cookieType", "tourist");
            json.put("otherdata", a);
//				json.put("ipflag", ip);
            json.put("code", 0);
            json.put("accnum", accnum);
            json.put("userid", "Bxcar");
            json.put("cookie", cookie);
            //__9XRwuHhHTjQiC8wXCnCj7wsEBU9x=wy; expires=Sat, 21 Apr 26660 02:33:33 GMT; path=/; domain=.mmbang.com
            jsonStr = JsonUtils.toJSONString(json);
        }catch(Exception e){
            e.printStackTrace();
         }

        return jsonStr;

    }
    public String loginPlugin(String userId){
        getIplist();//获取本地ip列表
        int num=(int)(Math.random()*ipList.size());//随机获取ip列表中某个ip
        String ip = ipList.get(num);
        String jsonStr = "";

        try{
            Map<String, Object> json = new HashMap<> () ;

            String cookie="TY_SESSION_ID=b21ca37f-3b35-4fef-92aa-1d6927bf528a; _Xdwuv=5c48251ebef16; _Xdwnewuv=1; _PVXuv=5c48a80d413fc; _fwck_www=8467c18e04e9cfa191c9e89ef7c68786; _appuv_www=4e25489e00ce39894493853d4ec9411b; _fwck_my=e4b214cdf4d2e271930b8237cd9b6826; _appuv_my=47bb852d5a5829288037166484125771; bbs_cookietime=31536000; _locationInfo_=%7Burl%3A%22http%3A%2F%2Fshenyang.xcar.com.cn%2F%22%2Ccity_id%3A%2263%22%2Cprovince_id%3A%226%22%2C%20city_name%3A%22%25E6%25B2%2588%25E9%2598%25B3%22%7D; ";
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
            json.put("userid", "Bxcar");
            json.put("cookie", cookie);
            jsonStr = JsonUtils.toJSONString(json);
        }catch(Exception e){
                e.printStackTrace();
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

}
