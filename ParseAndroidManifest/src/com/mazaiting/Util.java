package com.mazaiting;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Util {
	/**
	 * 将AndroidManifest.xml文件读取为二进制数据
	 * @param path 文件路径
	 * @return
	 */
	static byte[] readXML(String path) {
		byte[] bytes = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(path);
			baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);				
			}
			bytes = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return bytes;
	}
	
	/**
	 * 4位字节数组转为换整型
	 * @param res 字节数组
	 * @return
	 */
	public static int byte2Int(byte[] res) {
		return (res[0] & 0xff) | ((res[1] << 8) & 0xff00) |
				((res[2] << 24) >>> 8) | (res[3] << 24);
	}
	
	/**
	 * 整型转换为字节数组
	 * @return
	 */
	public static byte[] int2Byte(final int num) {
		int byteNum = (40 - Integer.numberOfLeadingZeros(num));
		byte[] byteArray = new byte[4];
		for (int i = 0; i < byteNum; i++) {
			byteArray[3 - i] = (byte) (num >>> (i * 8));
		}
		return byteArray;
	}
	
	/**
	 * 2位字节数组转换为short类型
	 * @param b 字节数组
	 * @return
	 */
	public static short byte2Short(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}
	
	/**
	 * 字节数组转为16进制字符串
	 * @param src 字节数组
	 * @return
	 */
	public static String byteToHexString(byte[] src) {
		byte[] bytes = reverseBytes(src);
		StringBuilder builder = new StringBuilder();
		if (null == bytes || bytes.length <= 0) {
			return null;
		}
		for (int i = 0; i < bytes.length; i++) {
			int value = bytes[i] & 0xFF;
			String hv = Integer.toHexString(value);
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv + " ");
		}
		return builder.toString();
	}

	/**
	 * 反转字节数组
	 * @param src 字节数组
	 * @return
	 */
	private static byte[] reverseBytes(byte[] src) {
		byte[] bytes = new byte[src.length];
		for (int i = 0; i < src.length; i++) {
			bytes[i] = src[i];
		}
		if (null == bytes || (bytes.length % 2) != 0) {
			return bytes;
		}
		int i = 0, len = bytes.length;
		while (i < len / 2) {
			byte tmp = bytes[i];
			bytes[i] = bytes[len - i - 1];
			bytes[len - i - 1] = tmp;
			i++;			
		}
		return bytes;
	}
	
	/**
	 * 字节数组转换为字符数组
	 * @param bytes 字节数组
	 * @return
	 */
	public static char[] getChars(byte[] bytes) {
		Charset cs = Charset.forName("UTF-8");
		ByteBuffer bb = ByteBuffer.allocate(bytes.length);
		bb.put(bytes);
		bb.flip();
		CharBuffer cb = cs.decode(bb);
		return cb.array();
	}
	
	/**
	 * 从指定位置复制指定长度的字节数组
	 * @param src 字节数组
	 * @param start 开始位置
	 * @param len 长度
	 * @return
	 */
	public static byte[] copyByte(byte[] src, int start, int len) {
		if (null == src) return null;
		if (start > src.length) return null;
		if (start + len > src.length) return null;
		if (start < 0) return null;
		if (len <= 0) return null;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = src[i+start];
		}		
		return result;		
	}
	
	/**
	 * 过滤字符串
	 * @param str 待过滤的字符串
	 * @return
	 */
	public static String filterStringNull(String str) {
		if (null == str || str.length() == 0) {
			return str;
		}
		byte[] strByte = str.getBytes();
		ArrayList<Byte> newByte = new ArrayList<>();
		for (int i = 0; i < strByte.length; i++) {
			if (strByte[i] != 0) {
				newByte.add(strByte[i]);
			}
		}
		byte[] newByteArr = new byte[newByte.size()];
		for (int i = 0; i < newByteArr.length; i++) {
			newByteArr[i] = newByte.get(i);
		}
		return new String(newByteArr);
	}
	
	/**
	 * 从字节数组中获取字符串
	 * @param srcByte 字节数组
	 * @param start 开始位置
	 * @return
	 */
	public static String getStringFromByteAry(byte[] srcByte, int start) {
		if (null == srcByte) return "";
		if (start < 0) return "";
		if (start >= srcByte.length) return "";
		byte val = srcByte[start];
		int i = 1;
		ArrayList<Byte> byteList = new ArrayList<>();
		while (val != 0) {
			byteList.add(srcByte[start+i]);
			val = srcByte[start+i];
			i++;			
		}
		byte[] valAry = new byte[byteList.size()];
		for (int j = 0; j < byteList.size(); j++) {
			valAry[j] = byteList.get(j);
		}
		try {
			return new String(valAry, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
}