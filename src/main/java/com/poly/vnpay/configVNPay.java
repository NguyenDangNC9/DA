package com.poly.vnpay;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class configVNPay {

	public static String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
	public static String vnp_ReturnUrl = "http://localhost:8080/order/detail/vnpay";
	public static String vnp_TmnCode = "EI78HZOZ";
	public static String secretKey = "EFTJODQSJSNPJDZYLNQZMEFYSIVRHYWT";
	public static String vnp_Version = "2.0";
	public static String vnp_Command = "pay";
	public static String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

	public String md5(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(message.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : array) {
				sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			
			return "";
		}
	}

	public static String hmacSHA512(final String key, final String data) {
		try {
			if (key == null || data == null) {
				throw new NullPointerException();
			}
			final Mac hmac512 = Mac.getInstance("HmacSHA512");
			byte[] hmacKeyBytes = key.getBytes();
			final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
			hmac512.init(secretKey);
			byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
			byte[] result = hmac512.doFinal(dataBytes);
			StringBuilder sb = new StringBuilder(2 * result.length);
			for (byte b : result) {
				sb.append(String.format("%02x", b & 0xff));
			}
			
			return sb.toString();
		} catch (Exception ex) {
			
			return "";
		}
	}

	public static String getIpAddress(HttpServletRequest request) {
		String ipAdress;
		try {
			ipAdress = request.getHeader("X-FORWARDED-FOR");
			if (ipAdress == null) {
				ipAdress = request.getRemoteAddr();
				
			}
		} catch (Exception e) {
			ipAdress = "Invalid IP:" + e.getMessage();
			
		}
		return ipAdress;
	}

	public static String getRandomNumber(int len) {
		Random rnd = new Random();
		String chars = "0123456789";
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		
		return sb.toString();
	}

	public static String hashAllFields(Map<String, String> fields) {
		List<String> fieldNames = new ArrayList<>(fields.keySet());
		Collections.sort(fieldNames);
		StringBuilder sb = new StringBuilder();
		Iterator<String> itr = fieldNames.iterator();
		while (itr.hasNext()) {
			String fieldName = itr.next();
			String fieldValue = fields.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				sb.append(fieldName);
				sb.append("=");
				sb.append(fieldValue);
				if (itr.hasNext()) {
					sb.append("&");
				}
			}
		}
		
		return hmacSHA512(secretKey, sb.toString());
	}
}
