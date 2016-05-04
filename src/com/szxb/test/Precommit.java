package com.szxb.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.xzxb.servlet.GetWxOrderno;
import com.xzxb.servlet.RequestHandler;
import com.xzxb.servlet.TenpayUtil;

/**
 * @author TangRen
 * 
 * @date 2016-04-22 Servlet implementation class Precommit
 */
@WebServlet("/Precommit")
public class Precommit extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Precommit() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		String amount = request.getParameter("amount");
		// String body = request.getParameter("body");
		String body = "����";
		// String

		//// //��ȡ���˶�ά���ַ
		String code_url = weiXinSaoMaPay(request, response, amount, body);
		System.out.println(code_url);
		System.out.println(code_url);
		System.out.println(code_url);

		out.println(code_url);
	}

	public String weiXinSaoMaPay(HttpServletRequest request, HttpServletResponse response, String money, String body)
			throws IOException {
		// Khzl khzl = (Khzl) request.getSession().getAttribute("KHZL");
		// ��ȡ���źͽ��
		String hunhe = "" + TenpayUtil.OrderNo() + "," + money + "";// �����źͽ��

		 String appid = "wx812954dc9708e485";//���ں�id
		 String appsecret = "71ba8d2cb8f8c234c888fc7e8f3acde7";//���ں���Կ
		 String mch_id = "1242511202";//�̻���
		 String partnerkey = "8982s7z18373x21207bf599cf6b4898a";//�̻���Կ
		 String wangzhi = "http://1m49l86048.imwork.net:21102/luckyDome";//�Լ�����ַ

//		String appid = "wx40cf1a2fa351ab20";// ���ں�id
//		String appsecret = "960f1f093acaeccd228a423c599507d0";// ���ں���Կ
//		String mch_id = "1324640301";// �̻���
//		String partnerkey = "581392283FunXinKeJiYouXianGongSi";// �̻���Կ
//		String wangzhi = "http://1m49l86048.imwork.net:21102/luckyDome";// �Լ�����ַ

		// ��ȡopenId�����ͳһ֧���ӿ�https://api.mch.weixin.qq.com/pay/unifiedorder
		String currTime = TenpayUtil.getCurrTime();
		// 8λ����
		String strTime = currTime.substring(8, currTime.length());
		// ��λ�����
		String strRandom = TenpayUtil.buildRandom(4) + "";
		// 10λ���к�,�������е�����
		String strReq = strTime + strRandom;
		// �����
		String nonce_str = strReq;
		// ��Ʒ������������޸�
		// String body = "����";
		String[] shunzu = hunhe.split(",");
		// �̻�������
		String out_trade_no = shunzu[0];
		// ���ת��Ϊ��Ϊ��λ
		float sessionmoney = Float.parseFloat(shunzu[1]);
		String finalmoney = String.format("%.2f", sessionmoney);
		finalmoney = finalmoney.replace(".", "");
		int intMoney = Integer.parseInt(finalmoney);
		
		System.out.println("sessionmoney:"+sessionmoney);
		System.out.println("finalmoney:"+finalmoney);
		System.out.println("intMoney:"+intMoney);

		// �ܽ���Է�Ϊ��λ������С����
		int total_fee = intMoney;

		// �������ɵĻ��� IP
		// String spbill_create_ip = request.getRemoteAddr();
		String spbill_create_ip = localIp();
		// ����notify_url�� ֧����ɺ�΢�ŷ�����������Ϣ�������жϻ�Ա�Ƿ�֧���ɹ����ı䶩��״̬�ȡ�
		String notify_url = wangzhi + "/NourlServlet";

		String trade_type = "NATIVE";
		SortedMap<String, Object> packageParams = new TreeMap<String, Object>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("out_trade_no", out_trade_no);
		// ����д�Ľ��Ϊ1 �ֵ�ʱ�޸�
		packageParams.put("total_fee", total_fee+"");
		packageParams.put("spbill_create_ip", spbill_create_ip);
		packageParams.put("notify_url", notify_url);
		packageParams.put("trade_type", trade_type);

		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init(appid, appsecret, partnerkey);

		String sign = reqHandler.createSign(packageParams);
		String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>" + mch_id + "</mch_id>" + "<nonce_str>"
				+ nonce_str + "</nonce_str>" + "<sign>" + sign + "</sign>" + "<body><![CDATA[" + body + "]]></body>"
				+ "<out_trade_no>" + out_trade_no + "</out_trade_no>" +
				// ������д��1 �ֵ�ʱ�޸�
				"<total_fee>" + total_fee + "</total_fee>" + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
				+ "<notify_url>" + notify_url + "</notify_url>" + "<trade_type>" + trade_type + "</trade_type>"
				+ "</xml>";

		GetWxOrderno gd = new GetWxOrderno();
		// ���صĶ�ά���������
		JSONObject code_url = gd.getPayNo2("https://api.mch.weixin.qq.com/pay/unifiedorder", xml, "code_url");
		System.out.println("����json���ݣ�" + code_url.toString());
		return code_url.toString();
	}

	/**
	 * ��ȡ����Ip
	 * 
	 * ͨ�� ��ȡϵͳ���е�networkInterface����ӿ� Ȼ����� ÿ�������µ�InterfaceAddress�顣 ��÷���
	 * <code>InetAddress instanceof Inet4Address</code> ������һ��IpV4��ַ
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String localIp() {
		String ip = null;
		Enumeration allNetInterfaces;
		try {
			allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				List<InterfaceAddress> InterfaceAddress = netInterface.getInterfaceAddresses();
				for (InterfaceAddress add : InterfaceAddress) {
					InetAddress Ip = add.getAddress();
					if (Ip != null && Ip instanceof Inet4Address) {
						ip = Ip.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			System.out.println("��ȡ����Ipʧ��:�쳣��Ϣ:" + e.getMessage());
		}
		return ip;
	}

}
