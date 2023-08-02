package com.mazaiting;

/**
 * 属性类型
 * 
 * @author mazaiting
 */
public class AttributeType {
	/**
	 * 属性类型
	 */
	public static final int ATTR_NULL = 0;
	public static final int ATTR_REFERENCE = 1;
	public static final int ATTR_ATTRIBUTE = 2;
	public static final int ATTR_STRING = 3;
	public static final int ATTR_FLOAT = 4;
	public static final int ATTR_DIMENSION = 5;
	public static final int ATTR_FRACTION = 6;
	public static final int ATTR_FIRST_INT = 16;
	public static final int ATTR_HEX = 17;
	public static final int ATTR_BOOLEAN = 18;
	public static final int ATTR_FIRST_COLOR = 28;
	public static final int ATTR_RGB8 = 29;
	public static final int ATTR_ARGB4 = 30;
	public static final int ATTR_RGB4 = 31;
	public static final int ATTR_LAST_COLOR = 31;
	public static final int ATTR_LAST_INT = 31;

	/**
	 * 单位
	 */
	public static final int COMPLEX_UNIT_PX = 0, COMPLEX_UNIT_DIP = 1, COMPLEX_UNIT_SP = 2, COMPLEX_UNIT_PT = 3,
			COMPLEX_UNIT_IN = 4, COMPLEX_UNIT_MM = 5, COMPLEX_UNIT_SHIFT = 0, COMPLEX_UNIT_MASK = 15,
			COMPLEX_UNIT_FRACTION = 0, COMPLEX_UNIT_FRACTION_PARENT = 1, COMPLEX_RADIX_23p0 = 0, COMPLEX_RADIX_16p7 = 1,
			COMPLEX_RADIX_8p15 = 2, COMPLEX_RADIX_0p23 = 3, COMPLEX_RADIX_SHIFT = 4, COMPLEX_RADIX_MASK = 3,
			COMPLEX_MANTISSA_SHIFT = 8, COMPLEX_MANTISSA_MASK = 0xFFFFFF;

	private static final float RADIX_MULTS[] = { 0.00390625F, 3.051758E-005F, 1.192093E-007F, 4.656613E-010F };

	private static final String DIMENSION_UNITS[] = { "px", "dip", "sp", "pt", "in", "mm", "", "" };

	private static final String FRACTION_UNITS[] = { "%", "%p", "", "", "", "", "", "" };

	/**
	 * 获取属性值
	 * 
	 * @param data 属性
	 * @return
	 */
	public static String getAttributeData(AttributeData data) {
		if (ATTR_STRING == data.type)
			return ParseChunkUtil.getStringContent(data.data);
		if (ATTR_ATTRIBUTE == data.type)
			return String.format("?%s%08X", getPackage(data.data), data.data);
		if (ATTR_REFERENCE == data.type)
			return String.format("@%s%08X", getPackage(data.data), data.data);
		if (ATTR_FLOAT == data.type)
			return String.valueOf(Float.intBitsToFloat(data.data));
		if (ATTR_HEX == data.type)
			return String.format("0x%08X", data.data);
		if (ATTR_BOOLEAN == data.type)
			return data.data != 0 ? "true" : "false";
		if (ATTR_DIMENSION == data.type)
			return Float.toString(complexToFloat(data.data)) + DIMENSION_UNITS[data.data & COMPLEX_UNIT_MASK];
		if (ATTR_FRACTION == data.type)
			return Float.toString(complexToFloat(data.data)) + FRACTION_UNITS[data.data & COMPLEX_UNIT_MASK];
		if (ATTR_FIRST_COLOR <= data.type && data.type <= ATTR_LAST_COLOR)
			return String.format("#%08X", data.data);
		if (ATTR_FIRST_INT <= data.type && data.data <= ATTR_LAST_INT)
			return String.valueOf(data.data);
		return String.format("<0x%X, type 0x%02X>", data.data, data.type);
	}

	/**
	 * 获取前缀
	 * 
	 * @param id
	 *            标识
	 * @return
	 */
	private static String getPackage(int id) {
		if (id >>> 24 == 1) {
			return "android:";
		}
		return "";
	}

	/**
	 * 整型转为浮点型
	 * 
	 * @param data
	 *            整型
	 * @return
	 */
	private static float complexToFloat(int data) {
		return (data & 0xFFFFFF00) * RADIX_MULTS[(data >> 4) & 3];
	}

	/**
	 * 获取类型
	 * 
	 * @param type
	 *            类型
	 * @return
	 */
	public static String getAttrType(int type) {
		switch (type) {
		case ATTR_NULL:
			return "ATTR_NULL";
		case ATTR_REFERENCE:
			return "ATTR_REFERENCE";
		case ATTR_ATTRIBUTE:
			return "ATTR_ATTRIBUTE";
		case ATTR_STRING:
			return "ATTR_STRING";
		case ATTR_FLOAT:
			return "ATTR_FLOAT";
		case ATTR_DIMENSION:
			return "ATTR_DIMENSION";
		case ATTR_FRACTION:
			return "ATTR_FRACTION";
		case ATTR_FIRST_INT:
			return "ATTR_FIRSTINT";
		case ATTR_HEX:
			return "ATTR_HEX";
		case ATTR_BOOLEAN:
			return "ATTR_BOOLEAN";
		case ATTR_FIRST_COLOR:
			return "ATTR_FIRSTCOLOR";
		case ATTR_RGB8:
			return "ATTR_RGB8";
		case ATTR_ARGB4:
			return "ATTR_ARGB4";
		case ATTR_RGB4:
			return "ATTR_RGB4";
		}
		return "";
	}
}
