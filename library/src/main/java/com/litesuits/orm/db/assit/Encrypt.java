package com.litesuits.orm.db.assit;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 支持SHA-1,SHA-2(SHA-256,SHA-384,SHA-512),MD2,MD5加密
 * @author MaTianyu
 * 2014-3-8下午10:58:20
 */
public final class Encrypt {
	public static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	public static String getMD2EncString(String txt) {
		return getEncodeString(txt, "MD2");
	}

	public static String getMD5EncString(String txt) {
		return getEncodeString(txt, "MD5");
	}

	public static String getSHA1EncString(String txt) {
		return getEncodeString(txt, "SHA-1");
	}

	public static String getSHA256EncString(String txt) {
		return getEncodeString(txt, "SHA-256");
	}

	public static String getSHA384EncString(String txt) {
		return getEncodeString(txt, "SHA-384");
	}

	public static String getSHA512EncString(String txt) {
		return getEncodeString(txt, "SHA-512");
	}

	public static String getEncodeString(String src, String algorithm) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(src.getBytes());
			byte[] md = digest.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getEncodeBytes(String src, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(src.getBytes());
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
}
