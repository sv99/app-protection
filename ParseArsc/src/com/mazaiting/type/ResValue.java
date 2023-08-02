package com.mazaiting.type;

import com.mazaiting.ParseResourceUtil;

/**
 * struct Res_value
 {
 // Number of bytes in this structure.
 uint16_t size;

 // Always set to 0.
 uint8_t res0;

 // Type of the data value.
 enum {
 // The 'data' is either 0 or 1, specifying this resource is either
 // undefined or empty, respectively.
 TYPE_NULL = 0x00,
 // The 'data' holds a ResTable_ref, a reference to another resource
 // table entry.
 TYPE_REFERENCE = 0x01,
 // The 'data' holds an attribute resource identifier.
 TYPE_ATTRIBUTE = 0x02,
 // The 'data' holds an index into the containing resource table's
 // global value string pool.
 TYPE_STRING = 0x03,
 // The 'data' holds a single-precision floating point number.
 TYPE_FLOAT = 0x04,
 // The 'data' holds a complex number encoding a dimension value,
 // such as "100in".
 TYPE_DIMENSION = 0x05,
 // The 'data' holds a complex number encoding a fraction of a
 // container.
 TYPE_FRACTION = 0x06,
 // The 'data' holds a dynamic ResTable_ref, which needs to be
 // resolved before it can be used like a TYPE_REFERENCE.
 TYPE_DYNAMIC_REFERENCE = 0x07,

 // Beginning of integer flavors...
 TYPE_FIRST_INT = 0x10,

 // The 'data' is a raw integer value of the form n..n.
 TYPE_INT_DEC = 0x10,
 // The 'data' is a raw integer value of the form 0xn..n.
 TYPE_INT_HEX = 0x11,
 // The 'data' is either 0 or 1, for input "false" or "true" respectively.
 TYPE_INT_BOOLEAN = 0x12,

 // Beginning of color integer flavors...
 TYPE_FIRST_COLOR_INT = 0x1c,

 // The 'data' is a raw integer value of the form #aarrggbb.
 TYPE_INT_COLOR_ARGB8 = 0x1c,
 // The 'data' is a raw integer value of the form #rrggbb.
 TYPE_INT_COLOR_RGB8 = 0x1d,
 // The 'data' is a raw integer value of the form #argb.
 TYPE_INT_COLOR_ARGB4 = 0x1e,
 // The 'data' is a raw integer value of the form #rgb.
 TYPE_INT_COLOR_RGB4 = 0x1f,

 // ...end of integer flavors.
 TYPE_LAST_COLOR_INT = 0x1f,

 // ...end of integer flavors.
 TYPE_LAST_INT = 0x1f
 };
 uint8_t dataType;

 // Structure of complex data values (TYPE_UNIT and TYPE_FRACTION)
 enum {
 // Where the unit type information is.  This gives us 16 possible
 // types, as defined below.
 COMPLEX_UNIT_SHIFT = 0,
 COMPLEX_UNIT_MASK = 0xf,

 // TYPE_DIMENSION: Value is raw pixels.
 COMPLEX_UNIT_PX = 0,
 // TYPE_DIMENSION: Value is Device Independent Pixels.
 COMPLEX_UNIT_DIP = 1,
 // TYPE_DIMENSION: Value is a Scaled device independent Pixels.
 COMPLEX_UNIT_SP = 2,
 // TYPE_DIMENSION: Value is in points.
 COMPLEX_UNIT_PT = 3,
 // TYPE_DIMENSION: Value is in inches.
 COMPLEX_UNIT_IN = 4,
 // TYPE_DIMENSION: Value is in millimeters.
 COMPLEX_UNIT_MM = 5,

 // TYPE_FRACTION: A basic fraction of the overall size.
 COMPLEX_UNIT_FRACTION = 0,
 // TYPE_FRACTION: A fraction of the parent size.
 COMPLEX_UNIT_FRACTION_PARENT = 1,

 // Where the radix information is, telling where the decimal place
 // appears in the mantissa.  This give us 4 possible fixed point
 // representations as defined below.
 COMPLEX_RADIX_SHIFT = 4,
 COMPLEX_RADIX_MASK = 0x3,

 // The mantissa is an integral number -- i.e., 0xnnnnnn.0
 COMPLEX_RADIX_23p0 = 0,
 // The mantissa magnitude is 16 bits -- ie, 0xnnnn.nn
 COMPLEX_RADIX_16p7 = 1,
 // The mantissa magnitude is 8 bits -- ie, 0xnn.nnnn
 COMPLEX_RADIX_8p15 = 2,
 // The mantissa magnitude is 0 bits -- ie, 0x0.nnnnnn
 COMPLEX_RADIX_0p23 = 3,

 // Where the actual value is.  This gives us 23 bits of
 // precision.  The top bit is the sign.
 COMPLEX_MANTISSA_SHIFT = 8,
 COMPLEX_MANTISSA_MASK = 0xffffff
 };

 // Possible data values for TYPE_NULL.
 enum {
 // The value is not defined.
 DATA_NULL_UNDEFINED = 0,
 // The value is explicitly defined as empty.
 DATA_NULL_EMPTY = 1
 };

 // The data for this item, as interpreted according to dataType.
 uint32_t data;

 void copyFrom_dtoh(const Res_value& src);
 };
 *
 * @author mazaiting
 */
public class ResValue {
	/**
	 * Constants used by the dataType field
	 */
	public final static int TYPE_NULL = 0x00;
	public final static int TYPE_REFERENCE = 0x01;
	public final static int TYPE_ATTRIBUTE = 0x02;
	public final static int TYPE_STRING = 0x03;
	public final static int TYPE_FLOAT = 0x04;
	public final static int TYPE_DIMENSION = 0x05;
	public final static int TYPE_FRACTION = 0x06;
	public final static int TYPE_FIRST_INT = 0x10;
	public final static int TYPE_INT_DEC = 0x10;
	public final static int TYPE_INT_HEX = 0x11;
	public final static int TYPE_INT_BOOLEAN = 0x12;
	public final static int TYPE_FIRST_COLOR_INT = 0x1c;
	public final static int TYPE_INT_COLOR_ARGB8 = 0x1c;
	public final static int TYPE_INT_COLOR_RGB8 = 0x1d;
	public final static int TYPE_INT_COLOR_ARGB4 = 0x1e;
	public final static int TYPE_INT_COLOR_RGB4 = 0x1f;
	public final static int TYPE_LAST_COLOR_INT = 0x1f;
	public final static int TYPE_LAST_INT = 0x1f;

	public static final int
			COMPLEX_UNIT_PX			=0,
			COMPLEX_UNIT_DIP		=1,
			COMPLEX_UNIT_SP			=2,
			COMPLEX_UNIT_PT			=3,
			COMPLEX_UNIT_IN			=4,
			COMPLEX_UNIT_MM			=5,
			COMPLEX_UNIT_SHIFT		=0,
			COMPLEX_UNIT_MASK		=15,
			COMPLEX_UNIT_FRACTION	=0,
			COMPLEX_UNIT_FRACTION_PARENT=1,
			COMPLEX_RADIX_23p0		=0,
			COMPLEX_RADIX_16p7		=1,
			COMPLEX_RADIX_8p15		=2,
			COMPLEX_RADIX_0p23		=3,
			COMPLEX_RADIX_SHIFT		=4,
			COMPLEX_RADIX_MASK		=3,
			COMPLEX_MANTISSA_SHIFT	=8,
			COMPLEX_MANTISSA_MASK	=0xFFFFFF;

	/**
	 * Head size of ResValue
	 */
	public short size;
	/**
	 * Reserved, always 0
	 */
	public byte res0;
	/**
	 * The type of data, obtained from the enumeration
	 */
	public byte dataType;
	/**
	 * The index corresponding to the data
	 */
	public  int  data ;

	/**
	 * Get the number of bytes occupied by the current class
	 * @return
	 */
	public int getSize() {
		return 2 + 1 + 1 + 4;
	}

	public String getTypeStr(){
		switch(dataType){
			case TYPE_NULL:
				return "TYPE_NULL";
			case TYPE_REFERENCE:
				return "TYPE_REFERENCE";
			case TYPE_ATTRIBUTE:
				return "TYPE_ATTRIBUTE";
			case TYPE_STRING:
				return "TYPE_STRING";
			case TYPE_FLOAT:
				return "TYPE_FLOAT";
			case TYPE_DIMENSION:
				return "TYPE_DIMENSION";
			case TYPE_FRACTION:
				return "TYPE_FRACTION";
			case TYPE_FIRST_INT:
				return "TYPE_FIRST_INT";
			case TYPE_INT_HEX:
				return "TYPE_INT_HEX";
			case TYPE_INT_BOOLEAN:
				return "TYPE_INT_BOOLEAN";
			case TYPE_FIRST_COLOR_INT:
				return "TYPE_FIRST_COLOR_INT";
			case TYPE_INT_COLOR_RGB8:
				return "TYPE_INT_COLOR_RGB8";
			case TYPE_INT_COLOR_ARGB4:
				return "TYPE_INT_COLOR_ARGB4";
			case TYPE_INT_COLOR_RGB4:
				return "TYPE_INT_COLOR_RGB4";
		}
		return "";
	}

	public String getDataStr() {
		if (dataType == TYPE_STRING) {
			return ParseResourceUtil.getResString(data);
		}
		if (dataType == TYPE_ATTRIBUTE) {
			return String.format("?%s%08X",getPackage(data),data);
		}
		if (dataType == TYPE_REFERENCE) {
			return String.format("@%s%08X",getPackage(data),data);
		}
		if (dataType == TYPE_FLOAT) {
			return String.valueOf(Float.intBitsToFloat(data));
		}
		if (dataType == TYPE_INT_HEX) {
			return String.format("0x%08X",data);
		}
		if (dataType == TYPE_INT_BOOLEAN) {
			return data!=0?"true":"false";
		}
		if (dataType == TYPE_DIMENSION) {
			return Float.toString(complexToFloat(data))+
					DIMENSION_UNITS[data & COMPLEX_UNIT_MASK];
		}
		if (dataType == TYPE_FRACTION) {
			return Float.toString(complexToFloat(data))+
					FRACTION_UNITS[data & COMPLEX_UNIT_MASK];
		}
		if (dataType >= TYPE_FIRST_COLOR_INT && dataType <= TYPE_LAST_COLOR_INT) {
			return String.format("#%08X",data);
		}
		if (dataType >= TYPE_FIRST_INT && dataType <= TYPE_LAST_INT) {
			return String.valueOf(data);
		}
		return String.format("<0x%X, type 0x%02X>",data, dataType);
	}

	private static String getPackage(int id) {
		if (id>>>24==1) {
			return "android:";
		}
		return "";
	}

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

	@Override
	public String toString(){
		return "size: " + size + ",res0: " + res0 + ",dataType: " + getTypeStr() + ",data: " + getDataStr();
	}

}