package com.mazaiting;

public class ParseMain {
	/**
	 * 应用主入口
	 */
	public static void main(String[] args) {
		// 读取AndroidManifest.xml文件为二进制数据
		byte[] byteSrc = Util.readXML("xml/AndroidManifest.xml");
		// 解析XML 头
		System.out.println("Parse XML Header -----------");
		ParseChunkUtil.parseXmlHeader(byteSrc);
		System.out.println();
		// 解析String Chunk
		System.out.println("Parse String Chunk -----------");
		ParseChunkUtil.parseStringChunk(byteSrc);
		System.out.println();
		// 解析Resource Chunk
		System.out.println("Parse Resource Chunk -----------");
		ParseChunkUtil.parseResourceChunk(byteSrc);
		System.out.println();
		// 解析XML内容
		System.out.println("Parse XML Content -----------");
		ParseChunkUtil.parseXmlContent(byteSrc);
		System.out.println();
		// 输出XML文件
		ParseChunkUtil.writeFormatXmlToFile();
	}

}














