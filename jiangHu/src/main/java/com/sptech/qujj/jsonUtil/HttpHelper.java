package com.sptech.qujj.jsonUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.sptech.qujj.App;
import com.sptech.qujj.exception.HttpException;

public class HttpHelper {

	private static DefaultHttpClient httpClient = null;

	private static HttpClient getHttpClient() throws Exception {
		if (httpClient != null) {
			return httpClient;
		}
		MySSLSocketFactory sslSocketFactory = new MySSLSocketFactory(App
				.getApplication().getKeyStore());
		sslSocketFactory
				.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 1000 * 20);
		HttpConnectionParams.setSoTimeout(params, 1000 * 20);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https", sslSocketFactory, 443));
		ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);
		httpClient = new DefaultHttpClient(conMgr, params);
		return httpClient;
	}

	public static String doPost(Map<String, Object> jsonInfo, String url)
			throws HttpException {

		HttpClient client = null;
		try {
			client = getHttpClient();
		} catch (Exception e1) {
			e1.printStackTrace();
			return "";
		}
		try {
			// 创建HttpPost对象。
			System.out.println("接口传参数11---======" + url);
			HttpPost httpRequest = new HttpPost(url);
			// 如果传递参数个数比较多的话可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : jsonInfo.keySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(key, (String) jsonInfo
						.get(key)));
			}
			// 设置请求参数
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送POST请求
			HttpResponse httpResponse = client.execute(httpRequest);
			System.out.println("成功====="
					+ httpResponse.getStatusLine().getStatusCode());
			// 如果服务器成功地返回响应
			/*
			 * if (httpResponse.getStatusLine().getStatusCode() == 200){ //
			 * 获取服务器响应字符串 String result =
			 * EntityUtils.toString(httpResponse.getEntity()); return result; }
			 */
			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				httpRequest.abort();
			}
			HttpEntity entity = httpResponse.getEntity();

			return EntityUtils.toString(entity, "utf-8").trim();
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			System.out.println("SocketException error");
			e.getStackTrace();
			throw new HttpException(ErrorMsg.TIMEOUT);
		} catch (Exception e) {
			e.getStackTrace();
			throw new HttpException(ErrorMsg.UNKNOWN);
		} finally {
			if (client != null) {
				// client.getConnectionManager().closeExpiredConnections();
				// client.getConnectionManager().shutdown();
			}
		}
	}
	
	public static String doGet(String url)
			throws HttpException {

		HttpClient client = null;
		try {
			client = getHttpClient();
		} catch (Exception e1) {
			e1.printStackTrace();
			return "";
		}
		try {
			System.out.println("============doGet请求地址：" + url + "============");
			// 创建HttpPost对象。
			HttpGet httpRequest = new HttpGet(url);
			// 发送POST请求
			HttpResponse httpResponse = client.execute(httpRequest);
			//
			return ResponseResult(httpResponse);
		} catch (SocketException e) {
			System.out.println(e.getMessage());
			System.out.println("SocketException error");
			e.getStackTrace();
			throw new HttpException(ErrorMsg.TIMEOUT);
		} catch (Exception e) {
			e.getStackTrace();
			throw new HttpException(ErrorMsg.UNKNOWN);
		} finally {
			if (client != null) {
				// client.getConnectionManager().closeExpiredConnections();
				// client.getConnectionManager().shutdown();
			}
		}
	}
	
	/**
     * @param response
     */
    public static String ResponseResult(HttpResponse response)
    {
        if (null == response) {
            return "";
        }

        HttpEntity httpEntity = response.getEntity();
        try {
            InputStream inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String result = "";
            String line = "";
            while (null != (line = reader.readLine())) {
                result += line;
            }
            System.out.println("============返回结果:" + result + "============");
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("============返回错误============");
            return "";
        }
    }
}
