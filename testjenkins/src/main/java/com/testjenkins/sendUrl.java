package com.testjenkins;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

//http接口请求类
public class sendUrl {
	// private static ArrayList<List<Map<String, Object>>>[] listret = new
	// ArrayList[6];
	private MyX509TrustManager cacert = null;
	public String cookie = "";
	private String exp = "";
	private boolean addcookie;
	private boolean addtoken;
	private String token;
	private boolean addHeader;
	private String Header;
	private String HeaderValue;

	// 构造函数，构造时，创建证书。
	public sendUrl() {
		cacert = new MyX509TrustManager();
	}

	// 获取异常信息
	public String getExp() {
		return exp;
	}

	// 参数编码
	public String EnCode(String u) {
		return u.replaceAll(" ", "%20");
	}

	// unicode解码
	static final Pattern reUnicode = Pattern.compile("\\\\u([0-9a-zA-Z]{4})");

	public String DeCode(String u) {
		try {
			Matcher m = reUnicode.matcher(u);
			StringBuffer sb = new StringBuffer(u.length());
			while (m.find()) {
				m.appendReplacement(sb, Character.toString((char) Integer.parseInt(m.group(1), 16)));
			}
			m.appendTail(sb);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return u;
		}
	}

	// 实现get方法调用
	public String sendGet(String url, String param) {
		// System.out.println(url + "?" + param);
		String result = "";
		InputStream in = null;
		try {
			// 实现HTTPS
			TrustManager[] tm = { this.cacert };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			// 创建URL对象\
			URL realUrl;
			if (param.length() > 0)
				// 拼接接口传递参数
				realUrl = new URL(url + "?" + EnCode(param));
			else
				realUrl = new URL(url);
			// 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
			HttpURLConnection conn = null;
			// 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
			// 设置同时调用http和https协议
			// 如果发送HTTPS失败，再用http请求
			try {
				conn = (HttpURLConnection) realUrl.openConnection();
				((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
			} catch (Exception e) {
				conn = (HttpURLConnection) realUrl.openConnection();
			}
			// 固定头域设置
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Cookie", this.cookie);
			// 自定义添加头域
			if (this.addHeader) {
				conn.setRequestProperty(Header, HeaderValue);
				addHeader = false;
			}
			// 添加token
			// if(this.addtoken){
			// conn.setRequestProperty("token", this.token);
			// this.addtoken = false;
			// }
			// 初始化请求头
			conn.setDoInput(true);
			conn.setDoOutput(true);
			// 设置连接超时时间
			conn.setConnectTimeout(25000);
			// 设置返回超时时间
			conn.setReadTimeout(25000);
			conn.connect();

			// 获取cookie并添加到头域
			String key = null;
			if (this.addcookie) {
				for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
					// System.out.println(conn.getHeaderField(i));
					if (key.equalsIgnoreCase("Set-Cookie")) {
						this.cookie += conn.getHeaderField(i) + ";";
					}
				}
				this.addcookie = false;
			}
			// System.out.println(this.cookie);
			// 获取回包
			in = conn.getInputStream();
			if (in != null) {
				result = convertStreamToString(in);
				// System.out.println("???" + result);
				// System.out.println(DeCode(result));
				return DeCode(result);
			}
			exp = "scc";
		} catch (Exception e) {
			System.out.println("发送get请求失败！" + e);
			e.printStackTrace();
			if (e.getMessage().length() > 500)
				exp = e.getMessage().substring(0, 500);
			else
				exp = e.getMessage();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				// e2.printStackTrace();
			}
		}
		// System.out.println(result);
		// jsonPase(result, 1, false);

		return null;
	}

	// 实现post请求发包
	public String sendPost(String url, String param) {
		PrintWriter out = null;
		// System.out.println(url + "?" + param);
		String result = "";
		try {
			TrustManager[] tm = { this.cacert };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			// 创建URL对象
			URL realUrl = new URL(url);
			// 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
			HttpURLConnection conn = null;
			// 创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
			// 设置同时调用http和https协议
			try {
				conn = (HttpURLConnection) realUrl.openConnection();
				((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
			} catch (Exception e) {
				conn = (HttpURLConnection) realUrl.openConnection();
			}
			// 设置通用的请求属性
			conn.setRequestMethod("POST");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Cookie", this.cookie);
			if (this.addHeader) {
				conn.setRequestProperty(Header, HeaderValue);
				this.addHeader = false;
			}
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(25000);
			conn.setReadTimeout(25000);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(EnCode(param));
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应

			// 获取回包和保存cookie
			InputStream reader = conn.getInputStream();
			if (reader != null) {
				result = convertStreamToString(reader);
			}
			exp = "scc";
			// 添加cookie
			String key = null;
			if (this.addcookie) {
				for (int i = 1; (key = conn.getHeaderFieldKey(i)) != null; i++) {
					// System.out.println(conn.getHeaderField(i));
					if (key.equalsIgnoreCase("Set-Cookie")) {
						this.cookie += conn.getHeaderField(i) + ";";
					}
				}
				this.addcookie = false;
			}
//			System.out.println(this.cookie);
			return DeCode(result);
		} catch (Exception e) {
			System.out.println("发送post请求失败！\n" + e);
			e.printStackTrace();
			if (e.getMessage().length() > 500)
				exp = e.getMessage().substring(0, 500);
			else
				exp = e.getMessage();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		// System.out.println(result);
		// jsonPase(result, 1, false);

		return null;
	}

	// 编码转为utf-8
	public static String convertStreamToString(InputStream is) {
		StringBuilder sb1 = new StringBuilder();
		byte[] bytes = new byte[4096];
		int size = 0;

		try {
			while ((size = is.read(bytes)) > 0) {
				String str = new String(bytes, 0, size, "UTF-8");
				sb1.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb1.toString();
	}

	// 单个文件上传
	public String Upload(String urlStr, String param) {
		String res = "";
		HttpURLConnection conn = null;
		String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符
		try {
			// 获取键值
			String[] p = param.split("&");
			String name = p[0].split("=")[0];
			String value = p[0].split("=")[1];
			String filePath = p[1].split("=")[1];

			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Cookie", this.cookie);
			System.out.println(this.cookie);
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());

			// 文件二进制流上传
			File file = new File(filePath);
			String filename = file.getName();
			// MagicMatch match = Magic.getMagicMatch(file, false, true);
			// String contentType = match.getMimeType();
			String contentType = "multipart/form-data;";

			StringBuffer strBuf = new StringBuffer();
			strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
			strBuf.append("Content-Disposition: form-data; " + name + "=\"" + value + "\"; filename=\"" + filename
					+ "\"\r\n");
			strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

			out.write(strBuf.toString().getBytes());
			// 真正传文件的代码
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}

			in.close();
			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// 读取返回数据
			InputStream reader = conn.getInputStream();
			if (this.addcookie) {
				this.cookie += conn.getHeaderField("Set-Cookie");
				this.addcookie = false;
			}

			if (reader != null) {
				res = convertStreamToString(reader);
				// System.out.println("???");
				// System.out.println(result);
				// System.out.println(result);
				reader.close();
				return DeCode(res);
			}
			reader = null;
			exp = "scc";
		} catch (Exception e) {
			System.out.println("发送POST请求出错。" + urlStr);
			e.printStackTrace();
			if (e.getMessage().length() > 500)
				exp = e.getMessage().substring(0, 500);
			else
				exp = e.getMessage();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return res;
	}

	// 批量文件上传
	public String MultiUpload(String urlStr, Map<String, String> fileMap) {
		String res = "";
		HttpURLConnection conn = null;
		String BOUNDARY = "---------------------------123821742118716"; // boundary就是request头和上传文件内容的分隔符
		try {
			URL url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			conn.setRequestProperty("Cookie", this.cookie);
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			OutputStream out = new DataOutputStream(conn.getOutputStream());

			// file
			if (fileMap != null) {
				Iterator<Map.Entry<String, String>> iter = fileMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = iter.next();
					String inputName = (String) entry.getKey();
					String inputValue = (String) entry.getValue();
					if (inputValue == null) {
						continue;
					}
					File file = new File(inputValue);
					String filename = file.getName();
					MagicMatch match = Magic.getMagicMatch(file, false, true);
					String contentType = match.getMimeType();

					StringBuffer strBuf = new StringBuffer();
					strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
					strBuf.append("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename
							+ "\"\r\n");
					strBuf.append("Content-Type:" + contentType + "\r\n\r\n");

					out.write(strBuf.toString().getBytes());

					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					in.close();
				}
			}

			byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
			out.write(endData);
			out.flush();
			out.close();

			// 读取返回数据
			StringBuffer strBuf = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				strBuf.append(line).append("\n");
			}
			res = strBuf.toString();
			reader.close();
			reader = null;
			exp = "scc";
		} catch (Exception e) {
			System.out.println("发送POST请求出错。" + urlStr);
			e.printStackTrace();
			if (e.getMessage().length() > 500)
				exp = e.getMessage().substring(0, 500);
			else
				exp = e.getMessage();
		} finally {
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return DeCode(res);
	}

	/**
	 * @param cookie
	 *            the cookie to set
	 */

	// 保存下一步请求的cookie
	public void saveCookie() {
		this.addcookie = true;
	}

	// 清空cookie
	public void clearCookie() {
		this.cookie = "";
	}

	public void insertHeader(String key, String value) {
		this.addHeader = true;
		Header = key;
		HeaderValue = value;
	}
}