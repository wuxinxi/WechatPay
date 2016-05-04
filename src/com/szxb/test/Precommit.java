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
		String body = "测试";
		// String

		//// //获取到了二维码地址
		String code_url = weiXinSaoMaPay(request, response, amount, body);
		System.out.println(code_url);
		System.out.println(code_url);
		System.out.println(code_url);

		out.println(code_url);
	}

	public String weiXinSaoMaPay(HttpServletRequest request, HttpServletResponse response, String money, String body)
			throws IOException {
		// Khzl khzl = (Khzl) request.getSession().getAttribute("KHZL");
		// 获取单号和金额
		String hunhe = "" + TenpayUtil.OrderNo() + "," + money + "";// 订单号和金额

		 String appid = "wx812954dc9708e485";//公众号id
		 String appsecret = "71ba8d2cb8f8c234c888fc7e8f3acde7";//公众号密钥
		 String mch_id = "1242511202";//商户号
		 String partnerkey = "8982s7z18373x21207bf599cf6b4898a";//商户密钥
		 String wangzhi = "http://1m49l86048.imwork.net:21102/luckyDome";//自己的网址

//		String appid = "wx40cf1a2fa351ab20";// 公众号id
//		String appsecret = "960f1f093acaeccd228a423c599507d0";// 公众号密钥
//		String mch_id = "1324640301";// 商户号
//		String partnerkey = "581392283FunXinKeJiYouXianGongSi";// 商户密钥
//		String wangzhi = "http://1m49l86048.imwork.net:21102/luckyDome";// 自己的网址

		// 获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
		String currTime = TenpayUtil.getCurrTime();
		// 8位日期
		String strTime = currTime.substring(8, currTime.length());
		// 四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		String strReq = strTime + strRandom;
		// 随机数
		String nonce_str = strReq;
		// 商品描述根据情况修改
		// String body = "测试";
		String[] shunzu = hunhe.split(",");
		// 商户订单号
		String out_trade_no = shunzu[0];
		// 金额转化为分为单位
		float sessionmoney = Float.parseFloat(shunzu[1]);
		String finalmoney = String.format("%.2f", sessionmoney);
		finalmoney = finalmoney.replace(".", "");
		int intMoney = Integer.parseInt(finalmoney);
		
		System.out.println("sessionmoney:"+sessionmoney);
		System.out.println("finalmoney:"+finalmoney);
		System.out.println("intMoney:"+intMoney);

		// 总金额以分为单位，不带小数点
		int total_fee = intMoney;

		// 订单生成的机器 IP
		// String spbill_create_ip = request.getRemoteAddr();
		String spbill_create_ip = localIp();
		// 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
		String notify_url = wangzhi + "/NourlServlet";

		String trade_type = "NATIVE";
		SortedMap<String, Object> packageParams = new TreeMap<String, Object>();
		packageParams.put("appid", appid);
		packageParams.put("mch_id", mch_id);
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", body);
		packageParams.put("out_trade_no", out_trade_no);
		// 这里写的金额为1 分到时修改
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
				// 金额，这里写的1 分到时修改
				"<total_fee>" + total_fee + "</total_fee>" + "<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>"
				+ "<notify_url>" + notify_url + "</notify_url>" + "<trade_type>" + trade_type + "</trade_type>"
				+ "</xml>";

		GetWxOrderno gd = new GetWxOrderno();
		// 返回的二维码调用链接
		JSONObject code_url = gd.getPayNo2("https://api.mch.weixin.qq.com/pay/unifiedorder", xml, "code_url");
		System.out.println("返回json数据：" + code_url.toString());
		return code_url.toString();
	}

	/**
	 * 获取本机Ip
	 * 
	 * 通过 获取系统所有的networkInterface网络接口 然后遍历 每个网络下的InterfaceAddress组。 获得符合
	 * <code>InetAddress instanceof Inet4Address</code> 条件的一个IpV4地址
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
			System.out.println("获取本机Ip失败:异常信息:" + e.getMessage());
		}
		return ip;
	}

}
