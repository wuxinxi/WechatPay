package com.xzxb.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.json.XML;

import com.alibaba.fastjson.JSONObject;
import com.szxb.utils.http.HttpClientConnectionManager;


public class GetWxOrderno {
	public static DefaultHttpClient httpclient;

	static {
		httpclient = new DefaultHttpClient();
		httpclient = (DefaultHttpClient) HttpClientConnectionManager
				.getSSLInstance(httpclient);
	}

	/**
	 * ��ȡprepay_id
	 * @author TangRen
	 * @param url
	 * @param xmlParam
	 * @param param
	 *            ȡ�����ز����ĸ���Ӧ��ֵ
	 * @return
	 */
	public static String getPayNo(String url, String xmlParam, String param) {
		System.out.println("xml��:" + xmlParam);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
				true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		String prepay_id = "";
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils
					.toString(response.getEntity(), "UTF-8");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			System.out.println("json��:" + jsonStr);

			if (jsonStr.indexOf("FAIL") != -1) {
				return prepay_id;
			}
			Map map = doXMLParse(jsonStr);
			String return_code = (String) map.get("return_code");
			prepay_id = (String) map.get(param);// "prepay_id" "code_url"
		} catch (Exception e) {
			System.out.println("getPayNo:" + e.getMessage());
		}
		return prepay_id;
	}
	public static JSONObject getPayNo2(String url, String xmlParam, String param) {
		System.out.println("xml��:" + xmlParam);
		JSONObject jsonObject=new JSONObject();
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
				true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		String prepay_id = "";
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils
					.toString(response.getEntity(), "UTF-8");
			
			Map<String, Object> dataMap = new HashMap<String, Object>();
			System.out.println("json���ؽ��:" + jsonStr);

			if (jsonStr.indexOf("FAIL") != -1) {
				return jsonObject;
			}
			Map map = doXMLParse(jsonStr);
			String return_code = (String) map.get("return_code");
			prepay_id = (String) map.get(param);// "prepay_id" "code_url"
			jsonObject.put("prepay_id", prepay_id);
			jsonObject.put("json_result", XML.toJSONObject(jsonStr).toString());
		} catch (Exception e) {
			System.out.println("getPayNo:" + e.getMessage());
		}
		return jsonObject;
	}
	
	
	/**
	 * ��ȡ��ѯ��������
	 * @param url
	 * @param xmlParam
	 * @param param
	 *            ȡ�����ز����ĸ���Ӧ��ֵ
	 * @return
	 */
	public static Map getPayNoRtMap(String url, String xmlParam) {
		System.out.println("xml��:" + xmlParam);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
				true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils
					.toString(response.getEntity(), "UTF-8");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			System.out.println("json��:" + jsonStr);

			Map map = doXMLParse(jsonStr);
			return map;
		} catch (Exception e) {
			System.out.println("getPayNoRtMap:" + e.getMessage());
			return null;
		}
	}

	/**
	 * �˿�
	 * 
	 * @param url
	 * @param xmlParam
	 * @return
	 */
	public static Map getRetund(String url, String xmlParam) {
		System.out.println("getRetund xml��:" + xmlParam);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
				true);
		HttpPost httpost = HttpClientConnectionManager.getPostMethod(url);

		Map<String, String> map = new HashMap<String, String>();
		try {
			httpost.setEntity(new StringEntity(xmlParam, "UTF-8"));
			HttpResponse response = httpclient.execute(httpost);
			String jsonStr = EntityUtils
					.toString(response.getEntity(), "UTF-8");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			System.out.println("json��:" + jsonStr);

			map = doXMLParse(jsonStr);
		} catch (Exception e) {
			System.out.println("getRetund:" + e.getMessage());
		}
		return map;
	}

	/**
	 * ����xml,���ص�һ��Ԫ�ؼ�ֵ�ԡ������һ��Ԫ�����ӽڵ㣬��˽ڵ��ֵ���ӽڵ��xml���ݡ�
	 * 
	 * @param strxml
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Map doXMLParse(String strxml) throws Exception {
		if (null == strxml || "".equals(strxml)) {
			return null;
		}
		Map m = new HashMap();
		InputStream in = String2Inputstream(strxml);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		Element root = doc.getRootElement();
		List list = root.getChildren();
		Iterator it = list.iterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String k = e.getName();
			String v = "";
			List children = e.getChildren();
			if (children.isEmpty()) {
				v = e.getTextNormalize();
			} else {
				v = getChildrenText(children);
			}
			m.put(k, v);
		}
		// �ر���
		in.close();
		return m;
	}

	/**
	 * ��ȡ�ӽ���xml
	 * 
	 * @param children
	 * @return String
	 */
	public static String getChildrenText(List children) {
		StringBuffer sb = new StringBuffer();
		if (!children.isEmpty()) {
			Iterator it = children.iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				String name = e.getName();
				String value = e.getTextNormalize();
				List list = e.getChildren();
				sb.append("<" + name + ">");
				if (!list.isEmpty()) {
					sb.append(getChildrenText(list));
				}
				sb.append(value);
				sb.append("</" + name + ">");
			}
		}

		return sb.toString();
	}

	public static InputStream String2Inputstream(String str) {
		return new ByteArrayInputStream(str.getBytes());
	}
}