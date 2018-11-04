package com.testjenkins;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;


public class InterKeyWords {
	// 成员变量
	public sendUrl inputUrl;
	public jsonPase json;
	public HashMap<String, String> tmpResult;

	// 构造函数，初始化关键字类时，将写excel的类和sendURL以及json解析类初识化
	public InterKeyWords() {
		inputUrl = new sendUrl();
		json = new jsonPase();
		tmpResult = new HashMap<String, String>();
	}

	// 添加头域
	public void addHeader(String key, String value) {
		try {
			inputUrl.insertHeader(key, value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// post方法实现
	public void testPost(String url, String input) {
		String param= toParam(input);
		System.out.println("param:"+param);
		String response = inputUrl.sendPost(url, param);
		System.out.println("post方法返回值" + response);
		//如果返回值为空，说明post方法发送异常
		if (response == null || response.length() <= 2) { 
			System.out.println("报错信息：" + inputUrl.getExp());
		} else {
			json.Pase(response, 1, false);
		}
		System.out.println("jsonlist:" + json.jsonList);
	}

	// get方法实现
	@Test
	public void testGet(String url, String input) {
		String param= toParam(input);
		System.out.println("param:"+param);
		String response = inputUrl.sendGet(url, param);
		System.out.println(response);
		if (response == null || response.length() <= 2) {
		} else {
			json.Pase(response, 1, false);
		}
		System.out.println(json.jsonList);
		Assert.assertTrue(json.jsonList.contains("successed"));
		
	}

	// 设置保存请求cookie
	public void savecookie() {
		try {
			inputUrl.saveCookie();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

	// 清除请求的cookie
	public void clearcookie() {
		try {
			inputUrl.clearCookie();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}

	}

	// 断言校验字段的结果是否与预期校验值一致，传入参数分别为校验字段，校验值，以及json层，对应excel中的G/H/I列
	public void assertSame(String key, String value, String index) {
		int i = 0;
		try {
			i = Integer.parseInt(index);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (json.jsonList.size() > 0 && json.jsonList.get(i).get(key) != null
				&& json.jsonList.get(i).get(key).toString().equals(value)) {
			System.out.println("测试用例执行成功！\r\n");
			// 将执行结果写到excel文件中
		} else {
			System.out.println("测试用例执行失败！\r\n");
			// 将执行结果写到excel文件中
		}
	}

	// 断言校验字段结果中是否包含预期校验值，传入参数分别为校验字段，校验值，以及json层，对应excel中的G/H/I列
	public void assertContain(String key, String value, String index) {
		int i = 0;
		try {
			i = Integer.parseInt(index);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(json.jsonList.get(i));
		if (json.jsonList != null && json.jsonList.get(i).get(key) != null
				&& json.jsonList.get(i).get(key).toString().contains(value)) {
			System.out.println("测试通过\r\n");
			// 将执行结果写到excel文件中
		} else {
			System.out.println("用例执行失败\r\n");
			// 将执行结果写到excel文件中
		}

	}
	
	//保存接口的返回值中的参数到字典中。
	public void savingParam(String key, String index) {
		int i = 0;
		try {
			i = Integer.parseInt(index);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			tmpResult.put(key, json.jsonList.get(i).get(key).toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//解析输入内容中的参数，替换参数列表
	public String toParam(String input) {
		String keyname = "";
		String param = "";
		//找到第一个{#在字符串中的位置
		int l = input.indexOf("{#");
		System.out.println(l);
		//当{#位置在字符串中时
		while (l >= 0) {
			keyname = input.substring(l + 2, input.indexOf("#}"));
			System.out.println("log::info：参数是：" + keyname);
			if (l > 0) {
				param += input.substring(0, l);
			}
			try {
				param += tmpResult.get(keyname);
			} catch (Exception e) {
				param += "null";
			}
			input = input.substring(input.indexOf("#}") + 2, input.length());
			l = input.indexOf("{#");
		}
		param += input;
		return param;
	}
}
