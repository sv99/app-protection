package com.mazaiting;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParseChunkUtil {
	/** XML文件内容 */
	private static StringBuilder builder = new StringBuilder();
	/** String Chunk 偏移量 */
	private static int STRING_CHUNK_OFFSET = 8;
	/** 字符串列表 */
	private static ArrayList<String> stringContentList;
	/** Resource Chunk 偏移量，动态计算 */
	private static int resourceChunkOffset;
	/** XMLContent Chunk 偏移量，动态计算 */
	private static int nextChunkOffset;
	/**KEY为uri,VALUE为prefix的map*/
	private static Map<String,String> uriPrefixMap = new HashMap<>();
	/**KEY为prefix,VALUE为uri的map*/
	private static Map<String, String> prefixUriMap = new HashMap<>();

	/**
	 * 解析xml的头部信息 
	 * 	1. Magic Number: 文件魔数，4个字节 
	 * 	2. File Size: 文件大小，4个字节
	 * 
	 * @param byteSrc
	 */
	public static void parseXmlHeader(byte[] byteSrc) {
		// 1. Magic Number: 文件魔数，4个字节
		byte[] magic = Util.copyByte(byteSrc, 0, 4);
		System.out.println("magic number:" + Util.byteToHexString(magic));
		// 2. File Size: 文件大小，4个字节
		byte[] size = Util.copyByte(byteSrc, 4, 4);
		System.out.println("xml size:" + Util.byteToHexString(size));

		// 拼接文件内容
		builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		builder.append("\n");
	}

	/**
	 * 解析String Chunk 
	 * 	1. ChunkType：StringChunk的类型，固定四个字节：0x001C0001 
	 * 	2. ChunkSize：StringChunk的大小，四个字节 
	 * 	3. StringCount：StringChunk中字符串的个数，四个字节 
	 * 	4. StyleCount：StringChunk中样式的个数，四个字节，但是在实际解析过程中，这个值一直是0x00000000 
	 * 	5. Unknown：位置区域，四个字节，在解析的过程中，这里需要略过四个字节 
	 * 	6. StringPoolOffset：字符串池的偏移值，四个字节，这个偏移值是相对于StringChunk的头部位置 
	 * 	7. StylePoolOffset：样式池的偏移值，四个字节，这里没有Style,所以这个字段可忽略 
	 * 	8. StringOffsets：每个字符串的偏移值，所以他的大小应该是：StringCount*4个字节 
	 * 	9. SytleOffsets：每个样式的偏移值，所以他的大小应该是SytleCount*4个字节 
	 * 	10. String Pool 
	 * 	11. Style Pool
	 * 
	 * @param byteSrc 二进制数据
	 */
	public static void parseStringChunk(byte[] byteSrc) {
		// 1. Chunk Type：String Chunk的类型，固定四个字节：0x001C0001
		byte[] type = Util.copyByte(byteSrc, STRING_CHUNK_OFFSET, 4);
		System.out.println("string chunktag:" + Util.byteToHexString(type));

		// 2. Chunk Size：String Chunk的大小，四个字节
		byte[] sizeByte = Util.copyByte(byteSrc, STRING_CHUNK_OFFSET + 4, 4);
		// 获取String Chunk大小
		int chunkSize = Util.byte2Int(sizeByte);
		System.out.println("chunk size:" + chunkSize);

		// 3. String Count：StringChunk中字符串的个数，四个字节
		byte[] stringCountByte = Util.copyByte(byteSrc, STRING_CHUNK_OFFSET + 8, 4);
		// 获取字符串个数
		int chunkStringCount = Util.byte2Int(stringCountByte);
		System.out.println("string count:" + chunkStringCount);

		// 4. Style Count：StringChunk中样式的个数，四个字节，但是在实际解析过程中，这个值一直是0x00000000
		// 5. Unknown：位置区域，四个字节，在解析的过程中，这里需要略过四个字节
		// 6. String Pool Offset：字符串池的偏移值，四个字节，这个偏移值是相对于StringChunk的头部位置
		// 创建指定字符串个数的列表
		stringContentList = new ArrayList<String>(chunkStringCount);
		// 7. Style Pool Offset：样式池的偏移值，四个字节，这里没有Style,所以这个字段可忽略
		// 8. String Offsets：每个字符串的偏移值，所以他的大小应该是：StringCount*4个字节
		byte[] stringOffsetByte = Util.copyByte(byteSrc, 28, 4);
		// 获取字符串内容开始位置
		int stringContentStart = 8 + Util.byte2Int(stringOffsetByte);
		System.out.println("start:" + stringContentStart);
		// 9. Sytle Offsets：每个样式的偏移值，所以他的大小应该是SytleCount*4个字节
		// 10. String Pool
		// 获取String Content
		byte[] stringContentByte = Util.copyByte(byteSrc, stringContentStart, chunkSize);
		// 获取内容开始位置, 字符串大小
		int start = 0, stringSize;
		// 字符串
		String text = "";
		// 循环结束条件，字符串列表中的数据个数等于字符串个数
		while (stringContentList.size() < chunkStringCount) {
			// 解析字符串时问题，编码：UTF-8和UTF-16,如果是UTF-8的话是以00结尾的，如果是UTF-16的话以00 00结尾的
			// 格式是：偏移值开始的两个字节是字符串的长度，接着是字符串的内容，后面跟着两个字符串的结束符00
			// 一个字符对应两个字节，所以要乘以2
			stringSize = Util.byte2Short(Util.copyByte(stringContentByte, start, 2)) * 2;
			System.out.println("string size : " + stringSize);
			// 获取字符串文本
			text = new String(Util.copyByte(stringContentByte, start + 2, stringSize + 2));
			System.out.println("text : " + Util.filterStringNull(text));
			// 字符串文本
			stringContentList.add(Util.filterStringNull(text));
			start += (2 + stringSize + 2);
		}
		// 11. Style Pool

		// 此处的代码是用来解析资源文件xml的
		/*int index = 0; 
		while(index < chunkStringCount){
			byte[] stringSizeByte = Util.copyByte(stringContentByte, start, 2);
			stringSize = (stringSizeByte[1] & 0x7F);
			System.out.println("string size:"+Util.byteToHexString(Util.int2Byte(stringSize)));
			if(stringSize != 0){ //这里注意是UTF-8编码的 String
				text = ""; 
				try{ 
					text = new String(Util.copyByte(stringContentByte, start + 2,stringSize), "utf-8"); 
				}catch(Exception e){
					System.out.println("string encode error:"+e.toString());
				}
				stringContentList.add(text); 
			} else { 
				stringContentList.add("");
			}
			start += (stringSize+3);
			index++;
		}
		 */
		// 拼接Resource Chunk 偏移位置
		resourceChunkOffset = STRING_CHUNK_OFFSET + Util.byte2Int(sizeByte);
	}

	/**
	 * 解析Resource Chunk 
	 * 	1. Chunk Type: Resource Chunk 类型，4个字节，0x00080108 
	 * 	2. Chunk Size: Resource Chunk 大小，4个字节 
	 * 	3. ResourceIds: 资源ID, 大小是ResourceChunk大小除以4，减去头部的大小8个字节(ChunkType和ChunkSize)==(Chunk Size / 4 - 2) * 4个字节
	 * 
	 * @param byteSrc 二进制数据
	 */
	public static void parseResourceChunk(byte[] byteSrc) {
		// 1. Chunk Type: Resource Chunk 类型，4个字节，0x00080108
		byte[] typeByte = Util.copyByte(byteSrc, resourceChunkOffset, 4);
		System.out.println("type: " + Util.byteToHexString(typeByte));
		// 2. Chunk Size: Resource Chunk 大小，4个字节
		byte[] sizeByte = Util.copyByte(byteSrc, resourceChunkOffset + 4, 4);
		// 文件大小
		int size = Util.byte2Int(sizeByte);
		System.out.println("size: " + size);
		// 3. ResourceIds: 资源ID,
		// 大小是ResourceChunk大小除以4，减去头部的大小8个字节(ChunkType和ChunkSize)==(Chunk Size /
		// 4 - 2) * 4个字节
		// chunk size包含chunk type 与 chunk size两个数组的字节，所以要剔除
		byte[] resourceIdByte = Util.copyByte(byteSrc, resourceChunkOffset + 8, size - 8);
		// 创建资源列表
		ArrayList<Integer> resourceIdList = new ArrayList<>(resourceIdByte.length / 4);
		// 遍历获取资源ID
		for (int i = 0; i < resourceIdByte.length; i += 4) {
			// 获取资源ID
			int resId = Util.byte2Int(Util.copyByte(resourceIdByte, i, 4));
			System.out.println("id: " + resId + ", hex: " + Util.byteToHexString(Util.copyByte(resourceIdByte, i, 4)));
			// 将资源ID添加到列表
			resourceIdList.add(resId);
		}
		// 计算XMLContent Chunk 偏移量
		nextChunkOffset = resourceChunkOffset + size;
	}

	/**
	 * 开始解析XML的正文内容 
	 * 	1. type: 类型，4个字节 
	 * 	2. size: 文件大小，4个字节 
	 * 	3. start namaspace 
	 * 	4. start tag 
	 * 	5. end tag 
	 * 	6. end namespace
	 * 
	 * @param byteSrc 二进制数据
	 */
	public static void parseXmlContent(byte[] byteSrc) {
		// 判断是否到结尾处了
		while (!isEnd(byteSrc.length)) {
			// 获取类型
			byte[] typeByte = Util.copyByte(byteSrc, nextChunkOffset, 4);
			// 获取节点类型
			int type = Util.byte2Int(typeByte);
			System.out.println("chunk type: " + Util.byteToHexString(typeByte));
			// 文件大小
			byte[] sizeByte = Util.copyByte(byteSrc, nextChunkOffset + 4, 4);
			// 获取文件大小
			int size = Util.byte2Int(sizeByte);
			System.out.println("size: " + size);
			switch (type) {
				case ChunkMagicNumber.CHUNK_START_NS:
					System.out.println("parse start namespace");
					parseStartNameSpaceChunk(Util.copyByte(byteSrc, nextChunkOffset, size));
					break;
				case ChunkMagicNumber.CHUNK_START_TAG:
					System.out.println("parse start tag");
					parseStartTagChunk(Util.copyByte(byteSrc, nextChunkOffset, size));
					break;
				case ChunkMagicNumber.CHUNK_END_TAG:
					System.out.println("parse end tag");
					parseEndTagChunk(Util.copyByte(byteSrc, nextChunkOffset, size));
					break;
				case ChunkMagicNumber.CHUNK_END_NS:
					System.out.println("parse end namespace");
					parseEndNameSpaceChunk(Util.copyByte(byteSrc, nextChunkOffset, size));
					break;
				default:
					break;
			}
			// 赋值
			nextChunkOffset += size;
		}
		System.out.println("parse xml: " + builder.toString());
	}

	/**
	 * 解析命名空间 开始
	 * 	1. Chunk Type：类型，4个字节
	 * 	2. Chunk Size: 大小，4个字节
	 * 	3. Line Number: 在AndroidManifest.xml文件中的行号，4个字节
	 * 	4. Unkonwn(0xFFFFFFFF): 未知区域，4个字节
	 * 	5. Prefix: 命名空间前缀，4个字节。如：android
	 * 	6. Uri: 命令空间的Uri。如：http://schemas.android.com/apk/res/android
	 * @param byteSrc 字节数组
	 */
	private static void parseStartNameSpaceChunk(byte[] byteSrc) {
		// 1. Chunk Type：类型，4个字节
		byte[] typeByte = Util.copyByte(byteSrc, 0, 4);
		System.out.println("type: " + Util.byteToHexString(typeByte));
		// 2. Chunk Size: 大小，4个字节
		byte[] sizeByte = Util.copyByte(byteSrc, 4, 4);
		// 获取大小
		int size = Util.byte2Int(sizeByte);
		System.out.println("size: " + size);
		// 3. Line Number: 在AndroidManifest.xml文件中的行号，4个字节
		byte[] lineNumberByte = Util.copyByte(byteSrc, 8, 4);
		// 获取行号
		int lineNumber = Util.byte2Int(lineNumberByte);
		System.out.println("line number: " + lineNumber);
		// 4. Unkonwn(0xFFFFFFFF): 未知区域，4个字节
		// 5. Prefix: 命名空间前缀，4个字节。如：android
		byte[] prefixByte = Util.copyByte(byteSrc, 16, 4);
		// 获取前缀标识
		int prefixIndex = Util.byte2Int(prefixByte);
		// 获取前缀
		String prefix = stringContentList.get(prefixIndex);
		System.out.println("prefix: " + prefixIndex + ", prefix str: " + prefix);
		// 6. Uri: 命令空间的Uri。如：http://schemas.android.com/apk/res/android
		byte[] uriByte = Util.copyByte(byteSrc, 20, 4);
		// 获取uri标识
		int uriIndex = Util.byte2Int(uriByte);
		// 获取uri
		String uri = stringContentList.get(uriIndex);
		System.out.println("uri: " + uriIndex + ", uri str: " + uri);
		
		// 存入Map
		uriPrefixMap.put(uri, prefix);
		prefixUriMap.put(prefix, uri);		
	}

	/**
	 * 解析TAG 开始
	 * 	1. Chunk Type: 类型，4个字节：0x00100102
	 * 	2. Chunk Size: 大小，4个字节
	 * 	3. Line Number: AndroidManifesta.xml文件中的行号，4个字节
	 * 	4. Unknown: 未知区域，4个字节
	 * 	5. NamespaceUri: 标签用到的命名空间的Uri,比如用到了android这个前缀，那么就需要用http://schemas.android.com/apk/res/android这个Uri去获取，四个字节
	 * 	6. Name: 标签名称（字符串中的索引值），4个字节
	 * 	7. Flags: 标签的类型，4个字节，开始/结束
	 * 	8. AttributeCount: 标签包含的属性个数，4个字节
	 * 	9. ClassAttribute: 标签包含的属性，4个字节
	 * 	10. Attributes: 属性内容，每个属性算是一个Entry，固定大小为5的字节数组
	 * 	[Namespace，Uri，Name，ValueString，Data]，我们在解析的时候需要注意第四个值，要做一次处理：需要右移24位。所以这个字段的大小是：属性个数*5*4个字节
	 * @param byteSrc 字节数组
	 */
	private static void parseStartTagChunk(byte[] byteSrc) {
		// 1. Chunk Type: 类型，4个字节：0x00100102
		byte[] typeByte = Util.copyByte(byteSrc, 0, 4);
		System.out.println("type: " + Util.byteToHexString(typeByte));
		// 2. Chunk Size: 大小，4个字节
		byte[] sizeByte = Util.copyByte(byteSrc, 4, 4);
		// 获取大小
		int size = Util.byte2Int(sizeByte);
		System.out.println("size: " + size);
		// 3. Line Number: AndroidManifesta.xml文件中的行号，4个字节
		byte[] lineNumberByte = Util.copyByte(byteSrc, 8, 4);
		// 获取行号
		int lineNumber = Util.byte2Int(lineNumberByte);
		System.out.println("line number: " + lineNumber);
		// 4. Unknown: 未知区域，4个字节
		byte[] prefixByte = Util.copyByte(byteSrc, 8, 4);
		// 获取前缀标识
		int prefixIndex = Util.byte2Int(prefixByte);
		// 这里可能会返回-1, 如果返回-1的话，那就说明没有前缀
		if (-1 != prefixIndex && prefixIndex < stringContentList.size()) {
			System.out.println("prefix: " + prefixIndex);
			System.out.println("prefix str: " + stringContentList.get(prefixIndex));
		} else {
			System.out.println("prefix null");
		}
		// 5. NamespaceUri: 标签用到的命名空间的Uri,比如用到了android这个前缀，那么就需要用http://schemas.android.com/apk/res/android这个Uri去获取，四个字节
		byte[] uriByte = Util.copyByte(byteSrc, 16, 4);
		// 获取uri标识
		int uriIndex = Util.byte2Int(uriByte);
		// 如果前缀大的话，说明uri不存在
		if (-1 != uriIndex && prefixIndex < stringContentList.size()) {
			System.out.println("uri: " + uriIndex);
			System.out.println("uri str: " + stringContentList.get(uriIndex));
		} else {
			System.out.println("uri null");
		}
		// 6. Name: 标签名称（字符串中的索引值），4个字节
		byte[] tagNameByte = Util.copyByte(byteSrc, 20, 4);
		System.out.println(Util.byteToHexString(tagNameByte));
		// 获取标签名称标识
		int nameIndex = Util.byte2Int(tagNameByte);
		// 获取标签名称
		String name = stringContentList.get(nameIndex);
		if (-1 != nameIndex) {
			System.out.println("tag name index: " + nameIndex);
			System.out.println("tag name str: " + name);
		} else {
			System.out.println("tag name null");
		}
		// 7. Flags: 标签的类型，4个字节，开始/结束
		// 8. AttributeCount: 标签包含的属性个数，4个字节
		byte[] attrCountByte = Util.copyByte(byteSrc, 28, 4);
		// 获取属性个数
		int attrCount = Util.byte2Int(attrCountByte);
		System.out.println("attr count:" + attrCount);
		// 9. ClassAttribute: 标签包含的属性，4个字节
		// 10. Attributes: 属性内容，每个属性算是一个Entry，固定大小为5的字节数组
		// 创建指定长度的属性列表
		ArrayList<AttributeData> attrList = new ArrayList<>(attrCount);
		// 遍历
		for (int i = 0; i < attrCount; i++) {
			// 五个属性
			Integer[] values = new Integer[5];
			// 创建属性对象
			AttributeData attData = new AttributeData();
			// 遍历赋值
			for (int j = 0; j < 5; j++) {
				// 5个属性，每个属性占4个字节，所以是i*5*4
				int value = Util.byte2Int(Util.copyByte(byteSrc, 36 + i * 5 * 4 + j * 4, 4));
				switch (j) {
				case 0:
					attData.nameSpaceUri = value;
					break;
				case 1:
					attData.name = value;
					break;
				case 2:
					attData.valueString = value;
					break;
				case 3:
					// 获取到的type要右移24位
					attData.type = (value >> 24);					
					break;
				case 4:
					attData.data = value;
					break;
				default:
					break;
				}
				values[j] = value;
			}
			// 添加到属性列表
			attrList.add(attData);
		}
		// 构造XML结构
		builder.append(createStartTagXml(name, attrList));
	}
	
	/**
	 * 创建一个xml的TAG
	 * @param name tag名称
	 * @param attrList 属性列表
	 * @return
	 */
	private static String createStartTagXml(String name, ArrayList<AttributeData> attrList) {
		StringBuilder builder = new StringBuilder();
		// 入口
		if ("manifest".equals(name)) {
			builder.append("<manifest xmls:");
			StringBuilder prefixSb = new StringBuilder();
			for (String key : prefixUriMap.keySet()) {
				prefixSb.append(key + ":\"" + prefixUriMap.get(key) + "\"");
				prefixSb.append("\n");
			}
			builder.append(prefixSb.toString());
		} else {
			builder.append("<" + name);
		}
		
		// 构建属性值
		if (0 == attrList.size()) {
			builder.append(">\n");
		} else {
			builder.append("\n");
			for (int i = 0; i < attrList.size(); i++) {
				// 获取属性
				AttributeData attrData = attrList.get(i);
				// 获取命名空间
				String prefixName = uriPrefixMap.get(attrData.getNameSpaceUri());
				// 有的地方没有前缀
				if (null == prefixName) {
					prefixName = "";
				}
				builder.append("    ");
				builder.append(prefixName + (prefixName.length() > 0 ? ":" : "") + attrData.getName() + "=");
				builder.append("\"" + AttributeType.getAttributeData(attrData) + "\"");
				if (i == (attrList.size() - 1)) {
					builder.append(">");
				}
				builder.append("\n");
			}
		}
		return builder.toString();
	}

	/**
	 * 解析TAG 结束
	 * 	1. Chunk Type(0x00100101): 4个字节
	 * 	2. Chunk Size: 4个字节
	 * 	3. Line Number: 4个字节
	 * 	4. Unknown(0xFFFFFFFF):4个字节
	 * 	5. Name: 4个字节
	 * 	6. Unknown: 4个字节
	 * 	7. Unknown: 4个字节
	 * @param byteSrc 字节数组
	 */
	private static void parseEndTagChunk(byte[] byteSrc) {
		// 1. Chunk Type(0x00100101): 4个字节
		byte[] tyepByte = Util.copyByte(byteSrc, 0, 4);
		System.out.println("type: " + Util.byteToHexString(tyepByte));
		// 2. Chunk Size: 4个字节
		byte[] sizeByte = Util.copyByte(byteSrc, 4, 4);
		// 获取长度
		int size = Util.byte2Int(sizeByte);
		System.out.println("size: " + size);
		// 3. Line Number: 4个字节
		byte[] lineNumberByte = Util.copyByte(byteSrc, 8, 4);
		// 获取行号
		int lineNumber = Util.byte2Int(lineNumberByte);
		System.out.println("line number:" + lineNumber);
		
		// 解析prefix
		byte[] prefixByte = Util.copyByte(byteSrc, 8, 4);
		// 获取前缀标识
		int prefixIndex = Util.byte2Int(prefixByte);
		// 可能返回-1，如果返回-1，则说明没有prefix
		if (-1 != prefixIndex && prefixIndex < stringContentList.size()) {
			System.out.println("prefix: " + prefixIndex);
			System.out.println("prefix str: " + stringContentList.get(prefixIndex));
		} else {
			System.out.println("prefix null.");
		}
		// 解析Uri
		byte[] uriByte = Util.copyByte(byteSrc, 16, 4);
		// 获取uri标识
		int uriIndex = Util.byte2Int(uriByte);
		// 如果前缀大的话，说明uri不存在
		if (-1 != uriIndex && prefixIndex < stringContentList.size()) {
			System.out.println("uri: " + uriIndex);
			System.out.println("uri str: " + stringContentList.get(uriIndex));
		} else {
			System.out.println("uri null");
		}
		
		// 4. Unknown(0xFFFFFFFF):4个字节
		// 5. Name: 4个字节
		byte[] tagNameByte = Util.copyByte(byteSrc, 20, 4);
		System.out.println("name: " + Util.byteToHexString(tagNameByte));
		// 获取tag名标识
		int tagNameIndex = Util.byte2Int(tagNameByte);
		// 获取tag名
		String tagName = stringContentList.get(tagNameIndex);
		if (-1 != tagNameIndex) {
			System.out.println("tag name index: " + tagNameIndex);
			System.out.println("tag name str: " + tagName);
		} else {
			System.out.println("tag name null");
		}
			
		builder.append(createEndTagXml(tagName));
	}

	/**
	 * 创建Tag结束结点
	 * @param tagName tag名
	 * @return
	 */
	private static String createEndTagXml(String tagName) {
		return "</" + tagName + ">\n";
	}

	/**
	 * 解析命名空间 结束
	 * @param byteSrc 字节数组
	 */
	private static void parseEndNameSpaceChunk(byte[] byteSrc) {
		
	}
	
	/**
	 * 判断是否到结尾处了
	 * 
	 * @param length 长度
	 * @return
	 */
	private static boolean isEnd(int length) {
		return nextChunkOffset >= length;
	}

	/**
	 * 把构造好的XML写入文件中
	 */
	public static void writeFormatXmlToFile() {
		FileWriter fw = null;
		try {
			fw = new FileWriter("xml/AndroidManifest_format.xml");
			fw.write(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取字符串内容
	 * @param index 标识
	 * @return
	 */
	public static String getStringContent(int index) {
		return stringContentList.get(index);
	}

}
