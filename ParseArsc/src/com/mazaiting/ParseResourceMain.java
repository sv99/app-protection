package com.mazaiting;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream ;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile ;

public class ParseResourceMain {
	//	private final static String FILE_PATH = "res/source.apk";
	private final static String FILE_PATH = "res/resources.arsc";
	public static void main(String[] args) {

//		byte[] arscArray = getArscFromApk(FILE_PATH);
		byte[] arscArray = getArscFromFile(FILE_PATH);

		System.out.println("parse restable header ...");
		ParseResourceUtil.parseResTableHeaderChunk(arscArray);
		System.out.println("===================================");
		System.out.println();

		System.out.println("parse resstring pool chunk  ...");
		ParseResourceUtil.parseResStringPoolChunk(arscArray);
		System.out.println("===================================");
		System.out.println();

		System.out.println("parse package chunk ...");
		ParseResourceUtil . parsePackage ( arscArray );
		System.out.println("===================================");
		System.out.println();

		System.out.println("parse typestring pool chunk ...");
		ParseResourceUtil.parseTypeStringPoolChunk(arscArray);
		System.out.println("===================================");
		System.out.println();

		System.out.println("parse keystring pool chunk ...");
		ParseResourceUtil.parseKeyStringPoolChunk(arscArray);
		System.out.println("===================================");
		System.out.println();

		/**
		 * Parse the body content
		 * The content of the text is the ResValue value, which is to start building the entry information in public.xml, and the xml files with different types of separation
		 */
		int resCount = 0;
		while (!ParseResourceUtil.isEnd(arscArray.length)) {
			resCount ++;
			boolean isSpec = ParseResourceUtil.isTypeSpec(arscArray);
			if (isSpec) {
				System.out.println("parse restype spec chunk ...");
				ParseResourceUtil.parseResTypeSpec(arscArray);
				System.out.println("===================================");
				System.out.println();
			} else {
				System.out.println("parse restype info chunk ...");
				ParseResourceUtil.parseResTypeInfo(arscArray);
				System.out.println("===================================");
				System.out.println();
			}
		}
		System.out.println("res count: " + resCount);

	}

	/**
	 * Get resouces.arsc from the file
	 * @param filePath file path
	 * @return
	 */
	private static byte[] getArscFromFile(String filePath) {
		byte[] srcByte = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(filePath);
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int  len  =  0 ;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			srcByte = baos.toByteArray();
		}  catch  ( Exception  e )  {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return srcByte;
	}

	/**
	 * Get the resources.arsc file from the APK
	 * @param filePath file path
	 * @return resources.arsc file binary data
	 */
	private static byte[] getArscFromApk(String filePath) {
		byte[] srcByte = null;
		ZipFile zipFile = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			zipFile = new ZipFile(filePath);
			ZipEntry entry = zipFile.getEntry("resources.arsc");
			is = zipFile.getInputStream(entry);
			baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int  len  =  0 ;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			srcByte = baos.toByteArray();
			zipFile.close();
		}  catch  ( Exception  e )  {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return srcByte;
	}

}