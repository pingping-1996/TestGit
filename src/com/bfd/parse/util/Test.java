package com.bfd.parse.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.bfd.crawler.utils.FileUtil;

//import com.bfd.parse.data.Website;
//import com.bfd.parse.data.WebsiteMap;

public class Test {

//	public static void main(String[] args) {
//		PropertyConfigurator.configure("log4j.properties");
//		Website website = WebsiteMap.INSTANCE.get("testifeng");
//		System.out.println(JsonUtil.toJSONString(website));
//	}

	private static final Log LOG = LogFactory.getLog(Test.class);
	private static final Pattern numberRegex = Pattern.compile("^[0-9].*?");
	private static final Pattern regex = Pattern.compile("BFD\\.BFD_(?:ITEM_)?INFO\\s*=\\s*\\{(.*?)\\};",
			Pattern.DOTALL);

	public static void main2(String[] args) throws Exception {

		String test = FileUtil.readFromFile2Str("/home/ian/test/test");
		System.out.println(JsonUtil.parseObject(test));
		// testGetItemInfo();
		// ScriptEngineManager factory = new ScriptEngineManager();
		// ScriptEngine engine = factory.getEngineByName("jav8");
		// try {
		// engine.eval("print('Hello, world!')");
		// } catch (ScriptException ex) {
		// ex.printStackTrace();
		// }
	}

	private static void testGetItemInfo() {
		getBfdItemInfo(FileUtil.readFromFile2Str("data/test_data/test_manzuo.html"));
		getBfdItemInfo(FileUtil.readFromFile2Str("data/test_data/test_manzuo2.html"));
		getBfdItemInfo(FileUtil.readFromFile2Str("data/test_data/test_58tuan.html"));
		getBfdItemInfo(FileUtil.readFromFile2Str("data/test_data/test_dida.html"));
	}

	private static void getBfdItemInfo(String data) {
		Matcher matcher = regex.matcher(data);
		if (matcher.find()) {
			String string = matcher.group(1);
			Map<String, Object> itemInfo = getItemInfo(string);
		} else {
			System.out.println("not found");
		}
	}

	private static Map<String, Object> getItemInfo(String string) {
		Map<String, Object> result = new HashMap<String, Object>();
		int posCode = 0;
		StringBuilder key = new StringBuilder();
		StringBuilder value = new StringBuilder();
		char curChar = 0;
		char befChar = 0;
		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if ('\n' == ch) {
				continue;
			}
			if (posCode == 0 && ch == ':') {
				posCode = 1;
				continue;
			}
			if (posCode == 0) {
				if (',' == ch) {
					befChar = ch;
					continue;
				}
				if ('/' == ch) {
					befChar = ch;
					int ii = string.substring(i).indexOf('\n');
					i += ii;
					befChar = 0;
					continue;
				}
				key.append(ch);
				befChar = ch;
			}
			if (posCode == 1) {
				if (' ' == ch) {
					continue;
				}
				if ('\"' == ch || '\'' == ch) {
					curChar = ch;
					posCode = 3; //
				} else if ('[' == ch) {
					posCode = 4;
				} else if ('{' == ch) {
					posCode = 5;
				} else {
					posCode = 2;
					i--;
				}
				continue;
			}
			if (posCode == 2) {
				if (',' == ch) {
					posCode = 0;
					String val = value.toString().trim();
					if (val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false")) {
						result.put(key.toString().trim(), Boolean.valueOf(val));
					} else if (numberRegex.matcher(val).find()) {
						if (val.contains("."))
							result.put(key.toString().trim(), Double.valueOf(val));
						else
							result.put(key.toString().trim(), Long.valueOf(val));
					} else {
						System.out.println("invalid data, key=" + key.toString().trim() + ", value=" + val);
					}
					key = new StringBuilder();
					value = new StringBuilder();
					posCode = 0;
					befChar = 0;
					continue;
				} else {
					value.append(ch);
				}
			}
			if (posCode == 3) {
				if (ch == curChar && befChar == '\\') {
					value.setCharAt(value.length() - 1, ch);
					befChar = ch;
				} else if (ch == curChar) {
					result.put(key.toString().trim(), value.toString());
					key = new StringBuilder();
					value = new StringBuilder();
					posCode = 0;
					befChar = 0;
				} else {
					value.append(ch);
					befChar = ch;
				}
			}
			if (posCode == 4) {
				String subStr = string.substring(i);
				int idx = subStr.indexOf(']');
				subStr = subStr.substring(0, idx - 1);
				result.put(key.toString().trim(), getListValue(subStr));
				key = new StringBuilder();
				value = new StringBuilder();
				befChar = 0;
				posCode = 0;
				i += idx;
			}
			if (posCode == 5) {
				String substr = string.substring(i);
				int idx = substr.indexOf('}');
				substr = substr.substring(0, idx);
				result.put(key.toString().trim(), getItemInfo(substr));
				key = new StringBuilder();
				value = new StringBuilder();
				posCode = 0;
				befChar = 0;
				i += idx;
			}
		}
		return result;
	}

	private static List<Object> getListValue(String string) {
		List<Object> result = new ArrayList<Object>();
		string = string.trim();
		char c = string.charAt(0);
		if ('\"' != c && '\'' != c) {
			String[] array = string.split(",");
			result.addAll(Arrays.asList(array));
		} else {
			String[] array = string.split(c + "," + c);
			for (int i = 0; i < array.length; i++) {
				if (i == 0) {
					result.add(array[i].substring(1));
				} else if (i == array.length - 1) {
					result.add(array[i].substring(0, array[i].length() - 1));
				} else {
					result.add(array[i]);
				}
			}
		}
		return result;
	}

	public static void testlog(String[] args) {
		PropertyConfigurator.configure("/home/ian/dev/parser/log4j.properties");
		LOG.debug("dddd");
		LOG.debug("dddd");
		LOG.debug("dddd");
		test3();
	}

	private static void test3() {
		LOG.debug("test");
	}

	public static void test2(String[] args) {
		// test();
		String ss = "background:url(http://img01.taobaocdn.com/bao/uploaded/i1/325718097/T2BJ02XmFcXXXXXXXX_!!325718097.jpg_30x30.jpg) center no-repeat;";
		Pattern compile = Pattern.compile("background:url\\(([^\"]+)\\)\\s+");
		Matcher matcher = compile.matcher(ss);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		}
	}

	private static void test() {
		try {

			String str = new String("ssss");
			String str2 = str;
			System.out.println(str == str2);
			URL url = new File("/home/ian/dev/PageParser/plugins/meilishuoParser/meilishuoParser.jar").toURL();

		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
