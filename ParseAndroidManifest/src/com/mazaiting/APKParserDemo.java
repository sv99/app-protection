package com.mazaiting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.xmlpull.v1.XmlPullParser;

import android.content.res.AXmlResourceParser;
import android.util.TypedValue;

public class APKParserDemo {
	/**指定要解析的文件*/
	private static final String DEFAULT_XML = "AndroidManifest.xml";
	public static void main(String[] args) {
		String apkPath = "xml/source.apk";
		String content = getManifestXMLFromAPK(apkPath);
		writeFormatXmlToFile(content);
	}
	
	/**
	 * 获取AndroidManifest.xml文件
	 * @param apkPath apk路径
	 */
	private static String getManifestXMLFromAPK(String apkPath) {
		ZipFile zipFile = null;
		StringBuilder xmlBuilder = new StringBuilder();
		try {
			// apk文件
			File apkFile = new File(apkPath);
			// 获取压缩文件
			zipFile = new ZipFile(apkFile, ZipFile.OPEN_READ);
			// 获取指定文件
			ZipEntry entry = zipFile.getEntry(DEFAULT_XML);
			
			// 创建XML文件资源解析器
			AXmlResourceParser parser = new AXmlResourceParser();
			// 打开文件
			parser.open(zipFile.getInputStream(entry));
			
			StringBuilder sb = new StringBuilder();
			final String indentStep = "	";
			int type;
			while ((type = parser.next()) != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					log(xmlBuilder, "<?xml version=\"1.0\" encoding=\"utf-8\"?>");
					break;
				case XmlPullParser.START_TAG:
					log(false, xmlBuilder, "%s<%s%s", sb, getNamespacePrefix(parser.getPrefix()), parser.getName());
					sb.append(indentStep);
					
					int nameSpaceCountBefore = parser.getNamespaceCount(parser.getDepth() - 1);
					int nameSpaceCount = parser.getNamespaceCount(parser.getDepth());
					
					for (int i = nameSpaceCountBefore; i != nameSpaceCount; i++) {
						log(xmlBuilder, "%sxmlns:%s=\"%s\"", 
								i == nameSpaceCountBefore ? "  " : sb, 
										parser.getNamespacePrefix(i), parser.getNamespaceUri(i));
					}
					
					for (int i = 0, size = parser.getAttributeCount(); i != size; i++) {
						log(false, xmlBuilder, "%s%s%s=\"%s\"", " ",
								getNamespacePrefix(parser.getAttributePrefix(i)),
								parser.getAttributeName(i),
								getAttributeValue(parser, i));
					}
					log(xmlBuilder, ">");
					
					break;
				case XmlPullParser.END_TAG:
					sb.setLength(sb.length() - indentStep.length());
					log(xmlBuilder, "%s</%s%s>", sb,
							getNamespacePrefix(parser.getPrefix()),
							parser.getName());
					break;
				case XmlPullParser.TEXT:
					log(xmlBuilder, "%s%s", sb, parser.getText());
					break;
				default:
					break;
				}
				
			}
			parser.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xmlBuilder.toString();
	}
	
	/**
	 * 把构造好的XML写入文件中
	 */
	public static void writeFormatXmlToFile(String content) {
		FileWriter fw = null;
		try {
			fw = new FileWriter("xml/ApkParser_format.xml");
			fw.write(content);
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
	 * 获取属性
	 * @param parser 解析器
	 * @param index 标识
	 * @return
	 */
	private static String getAttributeValue(AXmlResourceParser parser,int index) {
		int type=parser.getAttributeValueType(index);
		int data=parser.getAttributeValueData(index);
		if (type==TypedValue.TYPE_STRING) {
			return parser.getAttributeValue(index);
		}
		if (type==TypedValue.TYPE_ATTRIBUTE) {
			return String.format("?%s%08X",getPackage(data),data);
		}
		if (type==TypedValue.TYPE_REFERENCE) {
			return String.format("@%s%08X",getPackage(data),data);
		}
		if (type==TypedValue.TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (type==TypedValue.TYPE_INT_HEX) {
			return String.format("0x%08X",data);
		}
		if (type==TypedValue.TYPE_INT_BOOLEAN) {
			return data!=0?"true":"false";
		}
		if (type==TypedValue.TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data))+
				DIMENSION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type==TypedValue.TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))+
				FRACTION_UNITS[data & TypedValue.COMPLEX_UNIT_MASK];
		}
		if (type>=TypedValue.TYPE_FIRST_COLOR_INT && type<=TypedValue.TYPE_LAST_COLOR_INT) {
			return String.format("#%08X",data);
		}
		if (type>=TypedValue.TYPE_FIRST_INT && type<=TypedValue.TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>",data,type);
	}
	
	private static String getPackage(int id) {
		if (id>>>24==1) {
			return "android:";
		}
		return "";
	}
	
	/**
	 * 获取前缀
	 * @param prefix
	 * @return
	 */
	private static String getNamespacePrefix(String prefix) {
		if (prefix==null || prefix.length()==0) {
			return "";
		}
		return prefix+":";
	}
	
	/**
	 * 拼接字符串
	 * @param xmlSb 字符串拼接器
	 * @param format 格式化
	 * @param arguments 参数
	 */
	private static void log(StringBuilder xmlSb,String format,Object...arguments) {
		log(true,xmlSb, format, arguments);
	}
	
	/**
	 * 拼接字符串
	 * @param newLine 是否为新行
	 * @param xmlSb 字符串拼接器
	 * @param format 格式化
	 * @param arguments 参数
	 */
	private static void log(boolean newLine,StringBuilder xmlSb,String format,Object...arguments) {
		xmlSb.append(String.format(format, arguments));
		if(newLine) xmlSb.append("\n");
	}
	
	// ILLEGAL STUFF, DONT LOOK :)
	
	public static float complexToFloat(int complex) {
		return (float)(complex & 0xFFFFFF00)*RADIX_MULTS[(complex>>4) & 3];
	}
	
	private static final float RADIX_MULTS[]={
		0.00390625F,3.051758E-005F,1.192093E-007F,4.656613E-010F
	};
	private static final String DIMENSION_UNITS[]={
		"px","dip","sp","pt","in","mm","",""
	};
	private static final String FRACTION_UNITS[]={
		"%","%p","","","","","",""
	};
}


















