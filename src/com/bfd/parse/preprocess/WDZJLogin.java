package com.bfd.parse.preprocess;

import com.bfd.crawler.login.LoginPluginInterface;
import com.bfd.crawler.loginutil.JsonUtils;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;

import com.bfd.jdbc.DBShardingManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WDZJLogin implements LoginPluginInterface
{
    private static final Log LOG = LogFactory.getLog(WDZJLogin.class);
    static List<String> ipList = new ArrayList();
    static int ipnum = 0;
    int accnum = 0;

    public String loginPlugin()
    {

        int num = (int)(Math.random() * ipList.size());
        String ip = (String)ipList.get(num);
        String jsonStr = "";
        Properties prop = new Properties();
        try
        {
            File f = new File("../etc/wdzj.properties");
            InputStream in =  new FileInputStream(f);
            prop.load(in);
            String username = (String) prop.get("username");
            String password = (String) prop.get("password");
            /*LOG.info("password,username" + username+"=="+password);*/
            /*System.out.println("password,username" + username+"=="+password);*/
            in.close();
            String[] aryname = username.split(",");
            String[] arypwd = password.split(",");
            Map<String,String> usm_pwd = new HashMap<String,String>();
            usm_pwd.put(username, password);
            List<String> userIdlist = getUserid();
            /*LOG.info("userIdlist" + userIdlist.size());*/
            /*System.out.println("userIdlist" + userIdlist.size());*/
            Iterator<Entry<String, String>> ituserid = usm_pwd.entrySet().iterator();
            while(ituserid.hasNext()){
                Entry<String, String> entry=ituserid.next();
                String key=entry.getKey();
                if(userIdlist.contains(key)){
                    ituserid.remove(); //OK
                    accnum = accnum-1;
                }
            }

            Map<String, Object> json = new HashMap();
            String cookie = chinaPosrSloginnew(getFirstOrNull(usm_pwd).toString().substring(0, getFirstOrNull(usm_pwd).toString().indexOf(",")),
                    getFirstOrNull(usm_pwd).toString().substring(getFirstOrNull(usm_pwd).toString().indexOf(",")+1,getFirstOrNull(usm_pwd).toString().length()),ip);
            System.out.println("liwl++++++网贷之家 cookie is" + cookie);
            LOG.info("liwl++++++网贷之家 cookie is" + cookie);
            int code = 0;
            //判断cookie是否失效
//            if ((cookie != null) && (cookie.contains("domain=wdzj.com")))
//            {
//                code = 0;
//                this.accnum -= 1;
//            }
//            else
//            {
//                code = 1;
//            }
            Map<String, String> a = new HashMap();
            a.put("ipflag", ip);
            a.put("ua", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
            a.put("cookieType", "tourist");
            json.put("otherdata", a);
            System.out.println("code is:"+code);
            json.put("code", Integer.valueOf(0));
            json.put("accnum", Integer.valueOf(0));
            json.put("userid", "Nwangdaizhijia");
            json.put("cookie", cookie);
            jsonStr = JsonUtils.toJSONString(json);
            LOG.info("网贷之家jsonStr" + jsonStr);
            System.out.println("网贷之家jsonStr" + jsonStr);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return jsonStr;
    }

    public String loginPlugin(String userId)
    {
        LOG.info("******2019年2月1日13:54 网贷之家start again******");
        System.out.println("******2019年2月1日13:54 网贷之家start again******");
        getIplist();
        int num = (int)(Math.random() * ipList.size());
        String ip = (String)ipList.get(num);
        String jsonStr = "";
        LOG.info("liwl2++++++getIplist 网贷之家" + ipList.size());
        Properties prop = new Properties();
        try
        {
            File f = new File("../etc/wdzj.properties");
            InputStream in =  new FileInputStream(f);
            LOG.info("in:" + in);
            prop.load(in);
            String username = (String) prop.get("username");
            String password = (String) prop.get("password");
            LOG.info("password,username" + username+"=="+password);
            System.out.println("password,username" + username+"=="+password);
            in.close();
            String[] aryname = username.split(",");
            String[] arypwd = password.split(",");
            Map<String,String> usm_pwd = new HashMap<String,String>();
            for(int i = 0; i < aryname.length; i++){
                usm_pwd.put(aryname[i], arypwd[i]);
            }
            System.out.println("username and password" + aryname+"===="+arypwd);
            LOG.info("username and password" + aryname+"===="+arypwd);
            List<String> userIdlist = getUserid();
            LOG.info("userIdlist" + userIdlist.size());
            System.out.println("userIdlist" + userIdlist.size());
            Iterator<Entry<String, String>> ituserid = usm_pwd.entrySet().iterator();
            while(ituserid.hasNext()){
                Entry<String, String> entry=ituserid.next();
                String key=entry.getKey();
                if(userIdlist.contains(key)){
                    ituserid.remove(); //OK
                    accnum = accnum-1;
                }
            }
            String cookie = chinaPosrSloginnew(getFirstOrNull(usm_pwd).toString().substring(0, getFirstOrNull(usm_pwd).toString().indexOf(",")),
                    getFirstOrNull(usm_pwd).toString().substring(getFirstOrNull(usm_pwd).toString().indexOf(",")+1,getFirstOrNull(usm_pwd).toString().length()),ip);
            LOG.info("liwl2++++++网贷之家 cookie is" + cookie);

            Map<String, Object> json = new HashMap();
            int code;
//            if ((cookie != "") && (cookie.contains("login_token"))) {
//                code = 0;
//            } else {
//                code = 1;
//            }
            try
            {
                Map<String, String> a = new HashMap();
                a.put("ipflag", ip);
                a.put("ua", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
                a.put("cookieType", "tourist");

                json.put("otherdata", a);
                json.put("code", Integer.valueOf(0));
                json.put("accnum", Integer.valueOf(0));
                json.put("userid", userId);
                json.put("cookie", cookie);

                jsonStr = JsonUtils.toJSONString(json);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
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

    public static <K, V> V getFirstOrNull(Map<K, V> map) {
        V obj = null;
        for (Entry<K, V> entry : map.entrySet()) {
            obj = (V) (entry.getKey()+","+entry.getValue());
            if (obj != null) {
                break;
            }
        }
        return obj;
    }

    public static String chinaPosrSloginnew(String username,String password,String ip)
            throws Exception
    {
        CookieStore cookieStroe = new BasicCookieStore();
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultCookieStore(cookieStroe);

        HttpClient httpClient = builder.build();
        Date times = new Date();
        long time = times.getTime();
        String url = "https://passport.wdzj.com/userInterface/login?username="+username+"&password="+password+"&auto_login=1";
        System.out.println("2019年2月11日10:47 登录url是："+url);
        HttpGet get = new HttpGet(url);
//        RequestConfig requestConfig = getRequestConfigByIp(ip);
//        get.setConfig(requestConfig);
        get.setHeader("Accept", "*/*");
        get.setHeader("Accept-Encoding", "gzip, deflate");
        get.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        get.setHeader("Cache-Control", "no-cache");
        get.setHeader("Connection", "keep-alive");
        get.setHeader("Host", "passport.wdzj.com");
//        get.setHeader("Referer", "https://bbs.wdzj.com/");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
        HttpResponse response = httpClient.execute(get);
        Header[] header = response.getAllHeaders();

        String location = "";
        for (int i = 0; i < header.length; i++)
        {
            if (header[i].getName().equals("Location")) {
                location = header[i].getValue();
            }
            System.out.println("本地header is："+header[i].getName() + "---" + header[i].getValue());
        }
        System.out.println("location is :" + location);
        LOG.info("网贷之家响应状态码----" + response.getStatusLine().getStatusCode());
        if ((response.getStatusLine().getStatusCode() == 302) || (response.getStatusLine().getStatusCode() == 200) || (response.getStatusLine().getStatusCode() == 301))
        {
            List cookies = cookieStroe.getCookies();
            String cookie = "";
            for (Iterator iterator1 = cookies.iterator(); iterator1.hasNext();)
            {
                Cookie c = (Cookie)iterator1.next();
                String name = c.getName();
                String value = c.getValue();
                String path = c.getPath();
                String domain = c.getDomain();
                System.out.println(name + "=" + value + "; path=" + path + "; domain=" + domain + ";");
//                cookie = cookie + name + "=" + value + "; path=" + path + "; domain=" + domain + ";";
            }
            url = "https://www.wdzj.com/front/login";
            HttpGet get_session = new HttpGet(url);
            get_session.setHeader("Accept", "*/*");
            get_session.setHeader("Accept-Encoding", "gzip, deflate");
            get_session.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            get_session.setHeader("Cache-Control", "no-cache");
            get_session.setHeader("Connection", "keep-alive");
            get_session.setHeader("X-Requested-With", "XMLHttpRequest");
            get_session.setHeader("Host", "www.wdzj.com");
            get_session.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.10 Safari/537.36");
            HttpResponse response_session = httpClient.execute(get_session);
            Header[] sheader = response_session.getAllHeaders();
            String slocation = "";
            for (int i = 0; i < sheader.length; i++)
            {
                if (sheader[i].getName().equals("Location")) {
                    slocation = sheader[i].getValue();
                }
                System.out.println(sheader[i].getName() + "---" + sheader[i].getValue());
            }
            String session = "";
            System.out.println("网贷之家获取session响应状态码是：" + response.getStatusLine().getStatusCode());
            if ((response.getStatusLine().getStatusCode() == 302) || (response.getStatusLine().getStatusCode() == 200) || (response.getStatusLine().getStatusCode() == 301)){
                CookieStore sessionStroe = new BasicCookieStore();
                List sessions = cookieStroe.getCookies();
                for (Iterator iterator1 = sessions.iterator(); iterator1.hasNext();)
                {
                    Cookie s = (Cookie)iterator1.next();
                    String name = s.getName();
                    String value = s.getValue();
                    String path = s.getPath();
                    String domain = s.getDomain();
                    if(name.equals("WDZJ_FRONT_SESSION_ID")){
                        session = name + "=" + value + ";";
                    }
                    System.out.println(name + "=" + value + "; path=" + path + "; domain=" + domain + ";");
                }
            }
            System.out.println("20190202 9:17 THE SESSION IS:"+session);
            return session;
        }
        return "";
    }

//    public static void main(String[] args)
//            throws Exception
//    {
//        String cookie = chinaPosrSloginnew("18540271844","wangjun123","127.0.0.1");
//        System.out.println("cookie is:"+cookie);
//        String url = "https://www.wdzj.com/front/search/index?key=%25E8%25B4%25B7%25E6%25AC%25BE&referer=https://www.wdzj.com/wdzj/front/search/index&type=2&originalType=2";
//        System.out.println(getHtml(url,cookie));
//
//    }

    public static RequestConfig getRequestConfigByIp(String ip)
    {
        RequestConfig requestConfig = null;
        RequestConfig.Builder config_builder = RequestConfig.custom();
        InetAddress inetAddress = null;
        try
        {
            inetAddress = InetAddress.getByName(ip);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
        config_builder.setLocalAddress(inetAddress);
        requestConfig = config_builder.build();
        return requestConfig;
    }

    public static String getHtml(String url,String cookie) throws FileNotFoundException {
        String htmlContent = "";
        HttpClientBuilder builder = HttpClientBuilder.create();
        HttpClient client = builder.build();

        builder.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");// UA[(int)
        // (Math.random()*UA.length)]
        HttpGet request = new HttpGet(url);
        request.setHeader(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        request.setHeader("Cache-Control", "no-cache");
        request.setHeader("Host", "www.wdzj.com");
        request.setHeader("Upgrade-Insecure-Requests", "1");
        request.setHeader("Cookie", cookie);
        try {
            HttpResponse response = client.execute(request);
            htmlContent = EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        return htmlContent;
    }

    public static List<String> getUserid(){
        Statement st;
        ResultSet rs;
        Connection conn = getConnection("config");
        List<String> list = new ArrayList<String>();
        try {
            st = conn.createStatement();
            String sql = "select userId from crawl_login where siteID =(select siteID from website where cid ='Nwangdaizhijia');";
            rs=st.executeQuery(sql);
            while(rs.next())
            {
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        freeConnection("config", conn);
        return list;
    }
    public static Connection getConnection(String connname){
        return DBShardingManager.getInstance().getConnection(connname);
    }
    public static void freeConnection(String conName,Connection conn){
        DBShardingManager.getInstance().freeConnection(conName,  conn) ;
    }
}