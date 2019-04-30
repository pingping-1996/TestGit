package com.bfd.crawler.login;

import com.bfd.crawler.loginutil.JsonUtils;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public class LoginBxcar
        implements LoginPluginInterface
{
    private static final Log LOG = LogFactory.getLog(LoginBxcar.class);
    static List<String> ipList = new ArrayList();
    static int ipnum = 0;
    int accnum = 750;

    public String loginPlugin()
    {
        getIplist();
        int num = (int)(Math.random() * ipList.size());
        String ip = (String)ipList.get(num);
        String jsonStr = "";
        LOG.info("������IP" + ip);
        try
        {
            Map<String, Object> json = new HashMap();
            String cookie = "";
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod("http://www.xcar.com.cn/bbs/viewthread.php?tid=34129412&page=2");
            client.getParams().setCookiePolicy("compatibility");
            client.executeMethod(get);
            Cookie[] cookies = client.getState().getCookies();
            if (cookies.length == 0)
            {
                System.out.println("None");
            }
            else
            {
                for (int i = 0; i < cookies.length; i++)
                {
                    String a = cookies[i].toString();
                    if (i == cookies.length - 1)
                    {
                        cookie = cookie + a;
                    }
                    else
                    {
                        cookie = cookie + a;
                        cookie = cookie + ";";
                    }
                }
            }
            this.accnum -= 1;

            Map<String, String> a = new HashMap();
            a.put("ipflag", ip);
            a.put("ua", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
            a.put("cookieType", "tourist");
            json.put("otherdata", a);

            json.put("code", Integer.valueOf(0));
            json.put("accnum", Integer.valueOf(this.accnum));
            json.put("userid", "Bxcar" + this.accnum);
            json.put("cookie", cookie);

            jsonStr = JsonUtils.toJSONString(json);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return jsonStr;
    }

    public String loginPlugin(String userId)
    {
        getIplist();
        int num = (int)(Math.random() * ipList.size());
        String ip = (String)ipList.get(num);
        String jsonStr = "";
        try
        {
            Map<String, Object> json = new HashMap();
            String cookie = "";
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod("http://www.xcar.com.cn/bbs/viewthread.php?tid=34129412&page=2");
            client.getParams().setCookiePolicy("compatibility");
            client.executeMethod(get);
            Cookie[] cookies = client.getState().getCookies();
            if (cookies.length == 0)
            {
                System.out.println("None");
            }
            else
            {
                for (int i = 0; i < cookies.length; i++)
                {
                    String a = cookies[i].toString();
                    if (i == cookies.length - 1)
                    {
                        cookie = cookie + a;
                    }
                    else
                    {
                        cookie = cookie + a;
                        cookie = cookie + ";";
                    }
                }
            }
            this.accnum -= 1;

            Map<String, String> a = new HashMap();
            a.put("ipflag", ip);
            a.put("ua", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
            a.put("cookieType", "tourist");
            json.put("otherdata", a);
            json.put("code", Integer.valueOf(0));
            json.put("accnum", Integer.valueOf(this.accnum));
            json.put("userid", "Bxcar" + this.accnum);
            json.put("cookie", cookie);
            jsonStr = JsonUtils.toJSONString(json);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return jsonStr;
    }

    private static void getIplist()
    {
        if (ipList.size() == 0)
        {
            try
            {
                Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
                while (networkInterfaces.hasMoreElements())
                {
                    NetworkInterface networkInterface = (NetworkInterface)networkInterfaces.nextElement();
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements())
                    {
                        InetAddress inetAddress = (InetAddress)inetAddresses.nextElement();
                        if ((inetAddress != null) && ((inetAddress instanceof Inet4Address)))
                        {
                            String ip = inetAddress.getHostAddress();
                            if (ip.startsWith("211")) {
                                ipList.add(ip);
                            }
                            LOG.info("ben ji ip " + ip);
                            System.out.println("ben ji ip " + ip);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                LOG.error("ben ji ip error");
                System.out.println("ben ji ip error");
            }
            ipnum = ipList.size() - 1;
        }
    }

    public static void main(String[] args)
            throws Exception
    {
        String cookie = "";
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod("http://www.xcar.com.cn/bbs/viewthread.php?tid=34129412&page=2");
        client.getParams().setCookiePolicy("compatibility");
        client.executeMethod(get);
        Cookie[] cookies = client.getState().getCookies();
        if (cookies.length == 0)
        {
            System.out.println("None");
        }
        else
        {
            for (int i = 0; i < cookies.length; i++)
            {
                String a = cookies[i].toString();
                if (i == cookies.length - 1)
                {
                    cookie = cookie + a;
                }
                else
                {
                    cookie = cookie + a;
                    cookie = cookie + ";";
                }
            }
        }
        System.out.println(cookie);
    }
}
