package com.mazaiting;

import  java.net.URI ;
import java.util.ArrayList;
import  java.util.zip.ZipFile ;

import com.mazaiting.type.ResChunkHeader;
import com.mazaiting.type.ResStringPoolHeader;
import com.mazaiting.type.ResStringPoolRef;
import com.mazaiting.type.ResTableConfig;
import com.mazaiting.type.ResTableEntry;
import com.mazaiting.type.ResTableHeader;
import com.mazaiting.type.ResTableMap;
import com.mazaiting.type.ResTableMapEntry;
import com.mazaiting.type.ResTablePackage;
import com.mazaiting.type.ResTableRef;
import com.mazaiting.type.ResTableType;
import com.mazaiting.type.ResTableTypeSpec;
import com.mazaiting.type.ResValue;

public class ParseResourceUtil {
	/** String offset value */
	private static int resStringPoolChunkOffset;
	/** The offset value of the package content */
	private static int packageChunkOffset;
	/**The offset value of the key string pool*/
	private  static  int  keyStringPoolChunkOffset ;
	/** The offset value of the type string pool */
	private  static  int  typeStringPoolChunkOffset ;
	/** Parse the offset value of the resource type */
	private static int resTypeOffset;
	/** Resource bundle ID and type ID */
	private static int packId;
	private static int resTypeId;

	/**All string pools*/
	private static ArrayList<String> resStringList = new ArrayList<>();
	/**The pool of all resource key values*/
	private static ArrayList<String> keyStringList = new ArrayList<>();
	/** A pool of values ​​of all types */
	private static ArrayList<String> typeStringList = new ArrayList<>();

	/**
	 * Parse the header information
	 * @param arscArray arsc binary data
	 */
	public static void parseResTableHeaderChunk(byte[] arscArray) {
		ResTableHeader resTableHeader = new ResTableHeader();
		resTableHeader.header = parseResChunkHeader(arscArray, 0);
		resStringPoolChunkOffset = resTableHeader.header.headerSize;
		// Parse the number of PackageCount (an apk may contain multiple Package resources)
		byte[] packageCountByte = Util.copyByte(arscArray, resTableHeader.header.getHeaderSize(), 4);
		resTableHeader.packageCount = Util.byte2int(packageCountByte);

//		System.out.println(resTableHeader.toString());
	}

	/**
	 * Parse all string content in the Resource.arsc file
	 * @param arscArray binary data
	 */
	public static void parseResStringPoolChunk(byte[] arscArray) {
		ResStringPoolHeader stringPoolHeader = parseStringPoolChunk(arscArray, resStringList, resStringPoolChunkOffset);
		packageChunkOffset = resStringPoolChunkOffset + stringPoolHeader.header.size;
//		System.out.println(stringPoolHeader.toString());
	}

	/**
	 * Parsing package information
	 * @param arscArray arsc binary data
	 */
	public static void parsePackage(byte[] arscArray) {
		ResTablePackage resTablePackage = new ResTablePackage();

		// parse the header information
		resTablePackage.header = parseResChunkHeader(arscArray, packageChunkOffset);

		int offset = packageChunkOffset + resTablePackage.header.getHeaderSize();

		// analysis package ID
		byte[] idByte = Util.copyByte(arscArray, offset, 4);
		resTablePackage.id = Util.byte2int(idByte);
		packId = resTablePackage.id;

		// parse the package name
		// 128 here is the size of this field, you can check the type description, it is of char type, so multiply it by 2
		byte[] nameByte = Util.copyByte(arscArray, offset + 4, 128 * 2);
		String packageName = new String(nameByte);
		packageName = Util.filterStringNull(packageName);
//		System.out.println("packageName: " + packageName);

		// parse the offset value of the type string
		byte[] typeStringByte = Util.copyByte(arscArray, offset + 4 + 128 * 2, 4);
		resTablePackage.typeStrings = Util.byte2int(typeStringByte);

		// parse the lastPublicType field
		byte[] lastPublicType = Util.copyByte(arscArray, offset + 8 + 128 * 2, 4);
		resTablePackage.lastPublicType = Util.byte2int(lastPublicType);

		// Parse the offset value of the keyString string
		byte[] keyStrings = Util.copyByte(arscArray, offset + 12 + 128 * 2, 4);
		resTablePackage.keyStrings = Util.byte2int(keyStrings);

		// parse lastPublicKey
		byte[] lastPublicKey = Util.copyByte(arscArray, offset + 16 + 128 * 2, 4);
		resTablePackage.lastPublicKey = Util.byte2int(lastPublicKey);

		// Get the offset value of the type string and the offset value of the type string here
		keyStringPoolChunkOffset = packageChunkOffset + resTablePackage.keyStrings;
		typeStringPoolChunkOffset = packageChunkOffset + resTablePackage.typeStrings;

//		System.out.println(resTablePackage.toString());
	}

	/**
	 * Parse the string content of the resource type
	 * @param arscArray binary data
	 */
	public static void parseTypeStringPoolChunk(byte[] arscArray) {
		ResStringPoolHeader resStringPoolHeader = parseStringPoolChunk(arscArray, typeStringList, typeStringPoolChunkOffset);
//		System.out.println(resStringPoolHeader.toString());
	}

	/**
	 * Parse resource string content
	 * @param arscArray binary data
	 */
	public static void parseKeyStringPoolChunk(byte[] arscArray) {
		ResStringPoolHeader stringPoolHeader = parseStringPoolChunk(arscArray, keyStringList, keyStringPoolChunkOffset);
		// After parsing the key string, you need to assign the offset value to resType, and you need to continue parsing later
		resTypeOffset = keyStringPoolChunkOffset + stringPoolHeader.header.size;
	}

	/**
	 * Parse ResTypeSpec type description content
	 * @param arscArray binary data
	 */
	public static void parseResTypeSpec(byte[] arscArray) {
		ResTableTypeSpec typeSpec = new ResTableTypeSpec();
		// parse the header information
		typeSpec.header = parseResChunkHeader(arscArray, resTypeOffset);

		int offset = resTypeOffset + typeSpec.header.getHeaderSize();

		// parse id type
		byte[] idByte = Util.copyByte(arscArray, offset, 1);
		typeSpec.id = (byte) (idByte[0] & 0xFF);
		resTypeId = typeSpec.id;

		// Parsing the res0 field, this field is a spare, always 0
		byte[] res0Byte = Util.copyByte(arscArray, offset + 1, 1);
		typeSpec.res0 = (byte) (res0Byte[0] & 0xFF);

		// Parsing the res1 field, this field is a spare, always 0
		byte[] res1Byte = Util.copyByte(arscArray, offset + 2, 2);
		typeSpec.res1 = Util.byte2Short(res1Byte);

		byte []  entryCountByte  =  Util . copyByte ( arscArray ,  offset  +  4 ,  4 );
		typeSpec . entryCount  =  Util . byte2int ( entryCountByte );

		// Get EntryCount int array
		int []  intAry  =  new  int [ typeSpec . entryCount ];
		int intAryOffset = resTypeOffset + typeSpec.header.headerSize;
		for  ( int  i  =  0 ;  i  <  typeSpec . entryCount ;  i ++)  {
			int element = Util.byte2int(Util.copyByte(arscArray, intAryOffset + i * 4, 4));
			intAry[i] = element;
		}

		resTypeOffset += typeSpec.header.size;
	}

	/**
	 * Analysis ResType
	 * @param arscArray binary data
	 */
	public static void parseResTypeInfo(byte[] arscArray) {
		ResTableType type = new ResTableType();
		// parse the header information
		type.header = parseResChunkHeader(arscArray, resTypeOffset);

		int offset = resTypeOffset + type.header.getHeaderSize();

		// parse the id value of type
		byte[] idByte = Util.copyByte(arscArray, offset, 1);
		type.id = (byte) (idByte[0] & 0xFF);

		// Parse the value of the res0 field, a spare field, always 0
		byte[] res0 = Util.copyByte(arscArray, offset + 1, 1);
		type.res0 = (byte) (res0[0] & 0xFF);

		// Parse the value of the res1 field, a spare field, always 0
		byte[] res1 = Util.copyByte(arscArray, offset + 2, 2);
		type.res1 = Util.byte2Short(res1);

		byte []  entryCountByte  =  Util . copyByte ( arscArray ,  offset  +  4 ,  4 );
		type . entryCount  =  Util . byte2int ( entryCountByte );

		byte[] entriesStartByte = Util.copyByte(arscArray, offset + 8, 4);
		type.entriesStart = Util.byte2int(entriesStartByte);

		ResTableConfig resConfig = new ResTableConfig();
		resConfig = parseResTableConfig(Util.copyByte(arscArray, offset + 12, resConfig.getSize()));

//		System.out.println("config:"+resConfig);
//		System.out.println("res type info:"+type);
//		System.out.println("type_name:"+typeStringList.get(type.id-1));

		// Get entryCount int array
//		System.out.print("type int elements:");
		int []  intAry  =  new  int [ type . entryCount ];
		for  ( int  i  =  0 ;  i  <  type . entryCount ;  i ++)  {
			int element = Util.byte2int(Util.copyByte(arscArray, resTypeOffset + type.header.headerSize + i * 4, 4));
			intAry[i] = element;
//			System.out.print(element+",");
		}
//		System.out.println();

		// Start parsing the corresponding ResEntry and ResValue here
		int entryAryOffset = resTypeOffset + type.entriesStart;
		ResTableEntry []  tableEntries  =  new  ResTableEntry [ type . entryCount ];
		ResValue[] resValues = new ResValue[type.entryCount];
//		System.out.println("entry offset:"+Util.bytesToHexString(Util.int2Byte(entryAryOffset)));

		// If it is ResMapEntry, the offset value is different
		int bodySize = 0, valueOffset = entryAryOffset;
		for  ( int  i  =  0 ;  i  <  type . entryCount ;  i ++)  {
			int  resId  =  getResId ( i );
//			System.out.println("resId:"+Util.bytesToHexString(Util.int2Byte(resId)));
			ResTableEntry entry = new ResTableEntry();
			ResValue  value  =  new  ResValue ();
			valueOffset += bodySize;
//			System.out.println("valueOffset:"+Util.bytesToHexString(Util.int2Byte(valueOffset)));
			entry = parseResEntry(Util.copyByte(arscArray, valueOffset, entry.getSize()));

			// Determine whether the flag variable of entry is 1, if it is 1, it is ResTable_map_entry
			if (entry.flags == 1) {
				// value of complex type
				ResTableMapEntry mapEntry = new ResTableMapEntry();
				mapEntry = parseResMapEntry(Util.copyByte(arscArray, valueOffset, mapEntry.getSize()));
//				System.out.println("map entry:"+mapEntry);
				ResTableMap  resMap  =  new  ResTableMap ();
				for (int j = 0; j < mapEntry.count; j++) {
					int mapOffset = valueOffset + mapEntry.getSize() + resMap.getSize() * j;
					resMap = parseResTableMap(Util.copyByte(arscArray, mapOffset, resMap.getSize()));
//					System.out.println("map:" + resMap);
				}
				bodySize = mapEntry.getSize() + resMap.getSize() * mapEntry.count;
			} else {
//				System.out.println("entry: " + entry);
				// simple type value
				value = parseResValue(Util.copyByte(arscArray, valueOffset + entry.getSize(), value.getSize()));
//				System.out.println("value: " + value);
				bodySize = entry.getSize() + value.getSize();
			}

			tableEntries [ i ]  =  entry ;
			resValues[i] = value;
//			System.out.println("============================");
		}
		resTypeOffset += type.header.size;
	}

	/**
	 * Parse ResTableMap content
	 * @param srcByte binary data
	 * @return
	 */
	private static ResTableMap parseResTableMap(byte[] srcByte) {
		ResTableMap  tableMap  =  new  ResTableMap ();

		ResTableRef  ref  =  new  ResTableRef ();
		byte[] identByte = Util.copyByte(srcByte, 0, ref.getSize());
		if (null != identByte) {
			ref.ident = Util.byte2int(identByte);
			tableMap.name = ref;
		}

		ResValue  value  =  new  ResValue ();
		value = parseResValue(Util.copyByte(srcByte, ref.getSize(), value.getSize()));
		tableMap.value = value;

		return tableMap;
	}

	/**
	 * Parse ResValue content
	 * @param srcByte binary data
	 * @return
	 */
	private static ResValue parseResValue(byte[] srcByte) {
		ResValue  value  =  new  ResValue ();
		byte[] sizeByte = Util.copyByte(srcByte, 0, 2);
		if (null != sizeByte) {
			value.size = Util.byte2Short(sizeByte);
		}

		byte[] res0Byte = Util.copyByte(srcByte, 2, 1);
		if (null != res0Byte) {
			value.res0 = (byte) (res0Byte[0] & 0xFF);
		}

		byte[] dataType = Util.copyByte(srcByte, 3, 1);
		if (null != dataType) {
			value.dataType = (byte) (dataType[0] & 0xFF);
		}

		byte[] data = Util.copyByte(srcByte, 4, 4);
		if (null != data) {
			value.data = Util.byte2int(data);
		}

		return value;
	}

	/**
	 * Parse ResMapEntry content
	 * @param srcByte binary data
	 * @return
	 */
	private static ResTableMapEntry parseResMapEntry(byte[] srcByte) {
		ResTableMapEntry entry = new ResTableMapEntry();
		byte[] sizeByte = Util.copyByte(srcByte, 0, 2);
		entry.size = Util.byte2Short(sizeByte);

		byte[] flagByte = Util.copyByte(srcByte, 2, 2);
		entry.flags = Util.byte2Short(flagByte);

		ResStringPoolRef  key  =  new  ResStringPoolRef ();
		byte[] keyByte = Util.copyByte(srcByte, 4, 4);
		key.index = Util.byte2int(keyByte);
		entry.key = key;

		ResTableRef  ref  =  new  ResTableRef ();
		byte[] identByte = Util.copyByte(srcByte, 8, 4);
		ref.ident = Util.byte2int(identByte);
		entry.parent = ref;
		byte []  countByte  =  Util . copyByte ( srcByte ,  12 ,  4 );
		entry.count = Util.byte2int(countByte);

		return entry;
	}

	/**
	 * Parse ResEntry content
	 * @param srcByte binary data
	 * @return
	 */
	private static ResTableEntry parseResEntry(byte[] srcByte) {
		ResTableEntry entry = new ResTableEntry();
		byte[] sizeByte = Util.copyByte(srcByte, 0, 2);
		if (null != sizeByte) {
			entry.size = Util.byte2Short(sizeByte);
		}

		byte[] flagByte = Util.copyByte(srcByte, 2, 2);
		if (null != flagByte) {
			entry.flags = Util.byte2Short(flagByte);
		}

		ResStringPoolRef  key  =  new  ResStringPoolRef ();
		byte[] keyByte = Util.copyByte(srcByte, 4, 4);
		if (null != keyByte) {
			key.index = Util.byte2int(keyByte);
		}
		entry.key = key;

		return entry;
	}


	/**
	 * Get resource id
	 * Here the high position is packid, the middle position is restypeid, and the position is entryid
	 * @param entryid
	 * @return
	 */
	public static int getResId(int entryid){
		return (((packId)<<24) | (((resTypeId) & 0xFF)<<16) | (entryid & 0xFFFF));
	}

	/**
	 * Parse ResTablConfig configuration information
	 * @param srcByte binary data
	 * @return
	 */
	private static ResTableConfig parseResTableConfig(byte[] srcByte) {
		ResTableConfig config = new ResTableConfig();

		byte[] sizeByte = Util.copyByte(srcByte, 0, 4);
		config.size = Util.byte2int(sizeByte);

		// The following structure is a Union
		byte[] mccByte = Util.copyByte(srcByte, 4, 2);
		config.mcc = Util.byte2Short(mccByte);
		byte[] mncByte = Util.copyByte(srcByte, 6, 4);
		config.mnc = Util.byte2Short(mncByte);
		byte[] imsiByte = Util.copyByte(srcByte, 4, 4);
		config.imsi = Util.byte2int(imsiByte);

		// The following structure is a Union
		byte[] languageByte = Util.copyByte(srcByte, 8, 2);
		config.language = languageByte;
		byte[] countryByte = Util.copyByte(srcByte, 10, 2);
		config.country = countryByte;
		byte[] localeByte = Util.copyByte(srcByte, 8, 4);
		config.locale = Util.byte2int(localeByte);

		// The following structure is a Union
		byte[] orientationByte = Util.copyByte(srcByte, 12, 1);
		config.orientation = orientationByte[0];
		byte[] touchscreenByte = Util.copyByte(srcByte, 13, 1);
		config.touchscreen = touchscreenByte[0];
		byte[] densityByte = Util.copyByte(srcByte, 14, 2);
		config.density = Util.byte2Short(densityByte);
		byte[] screenTypeByte = Util.copyByte(srcByte, 12, 4);
		config.screenType = Util.byte2int(screenTypeByte);

		// The following structure is a Union
		byte[] keyboardByte = Util.copyByte(srcByte, 16, 1);
		config.keyboard = keyboardByte[0];
		byte[] navigationByte = Util.copyByte(srcByte, 17, 1);
		config.navigation = navigationByte[0];
		byte[] inputFlagsByte = Util.copyByte(srcByte, 18, 1);
		config.inputFlags = inputFlagsByte[0];
		byte[] inputPad0Byte = Util.copyByte(srcByte, 19, 1);
		config.inputPad0 = inputPad0Byte[0];
		byte []  inputByte  =  Util . copyByte ( srcByte ,  16 ,  4 );
		config.input = Util.byte2int(inputByte);

		// The following structure is a Union
		byte[] screenWidthByte = Util.copyByte(srcByte, 20, 2);
		config.screenWidth = Util.byte2Short(screenWidthByte);
		byte[] screenHeightByte = Util.copyByte(srcByte, 22, 2);
		config.screenHeight = Util.byte2Short(screenHeightByte);
		byte[] screenSizeByte = Util.copyByte(srcByte, 20, 4);
		config.screenSize = Util.byte2int(screenSizeByte);

		// The following structure is a Union
		byte[] sdVersionByte = Util.copyByte(srcByte, 24, 2);
		config.sdkVersion = Util.byte2Short(sdVersionByte);
		byte[] minorVersionByte = Util.copyByte(srcByte, 26, 2);
		config.minorVersion = Util.byte2Short(minorVersionByte);
		byte[] versionByte = Util.copyByte(srcByte, 24, 4);
		config.version = Util.byte2int(versionByte);

		// The following structure is a Union
		byte[] screenLayoutByte = Util.copyByte(srcByte, 28, 1);
		config.screenLayout = screenLayoutByte[0];
		byte[] uiModeByte = Util.copyByte(srcByte, 29, 1);
		config.uiMode = uiModeByte[0];
		byte[] smallestScreenWidthDpByte = Util.copyByte(srcByte, 30, 2);
		config.smallestScreenWidthDp = Util.byte2Short(smallestScreenWidthDpByte);
		byte[] screenConfigByte = Util.copyByte(srcByte, 28, 4);
		config.screenConfig = Util.byte2int(screenConfigByte);

		// The following structure is a Union
		byte[] screenWidthDpByte = Util.copyByte(srcByte, 32, 2);
		config.screenWidthDp = Util.byte2Short(screenWidthDpByte);
		byte[] screenHeightDpByte = Util.copyByte(srcByte, 34, 2);
		config.screenHeightDp = Util.byte2Short(screenHeightDpByte);
		byte[] screenSizeDpByte = Util.copyByte(srcByte, 32, 4);
		config.screenSizeDp = Util.byte2int(screenSizeDpByte);

		byte[] localeScriptByte = Util.copyByte(srcByte, 36, 4);
		config.localeScript = localeScriptByte;

		byte[] localeVariantByte = Util.copyByte(srcByte, 40, 8);
		config.localeVariant = localeVariantByte;

		return config;
	}

	/**
	 * Parse resource header information
	 * All Chunk public header information
	 * @param arscArray array
	 * @param start start position
	 * @return
	 */
	private  static  ResChunkHeader  parseResChunkHeader ( byte []  arscArray ,  int  start )  {
		ResChunkHeader  header  =  new  ResChunkHeader ();

		// parse header type
		byte[] typeByte = Util.copyByte(arscArray, start, 2);
		header.type = Util.byte2Short(typeByte);

		// parse header size
		byte[] headerSizeByte = Util.copyByte(arscArray, start + 2, 2);
		header.headerSize = Util.byte2Short(headerSizeByte);

		// Parse the size of the entire Chunk
		byte[] tableSizeByte = Util.copyByte(arscArray, start + 4, 4);
		header.size = Util.byte2int(tableSizeByte);

		return header;
	}

	/**
	 * The same parsing string content
	 * @param arscArray binary array
	 * @param stringList string list
	 * @param stringOffset string offset value
	 * @return
	 */
	private static ResStringPoolHeader parseStringPoolChunk(byte[] arscArray, ArrayList<String> stringList, int stringOffset) {
		ResStringPoolHeader stringPoolHeader = new ResStringPoolHeader();
		// parse the header information
		stringPoolHeader.header = parseResChunkHeader(arscArray, stringOffset);

		int offset = stringOffset + stringPoolHeader.header.getHeaderSize();

		// Get the number of strings
		byte[] stringCountByte = Util.copyByte(arscArray, offset, 4);
		stringPoolHeader.stringCount = Util.byte2int(stringCountByte);

		// Number of parsing styles
		byte []  styleCountByte  =  Util . copyByte ( arscArray ,  offset  +  4 ,  4 );
		stringPoolHeader . styleCount  =  Util . byte2int ( styleCountByte );

		// Here is the format of the string: UTF-8/UTF-16
		byte[] flagByte = Util.copyByte(arscArray, offset + 8, 4);
		stringPoolHeader.flags = Util.byte2int(flagByte);

		// from the beginning of the string content
		byte[] stringStartByte = Util.copyByte(arscArray, offset + 12, 4);
		stringPoolHeader.stringsStart = Util.byte2int(stringStartByte);

		// position where style content starts
		byte[] styleStartByte = Util.copyByte(arscArray, offset + 16, 4);
		stringPoolHeader.stylesStart = Util.byte2int(styleStartByte);

		// Get the index array of string content and the index array of style content
		int[] stringIndexAry = new int[stringPoolHeader.stringCount];
		int []  styleIndexAry  =  new  int [ stringPoolHeader . styleCount ];

		int  stringIndex = offset + 20;
		for (int i = 0; i < stringIndexAry.length; i++) {
			stringIndexAry[i] = Util.byte2int(Util.copyByte(arscArray, stringIndex + i * 4, 4));
		}

		int  styleIndex  =  stringIndex  +  4  *  styleIndexAry . length ;
		for (int i = 0; i < styleIndexAry.length; i++) {
			styleIndexAry[i] = Util.byte2int(Util.copyByte(arscArray, styleIndex + i * 4, 4));
		}

		// The last byte of the first two bytes of each string is the length of the string
		int  stringContentIndex  =  styleIndex  +  stringPoolHeader . styleCount  *  4 ;
		int index = 0;
		while (index < stringPoolHeader.stringCount) {
			byte[] stringSizeByte = Util.copyByte(arscArray, stringContentIndex, 2);
			int stringSize = (stringSizeByte[1] & 0x7F);
			if (0 != stringSize) {
				String val = "";
				try {
					val = new String(Util.copyByte(arscArray, stringContentIndex + 2, stringSize), "UTF-8");
				}  catch  ( Exception  e )  {
					e.printStackTrace();
				}
				stringList.add(val);
			} else {
				stringList.add("");
			}
			stringContentIndex += (stringSize + 3);
			index++;
		}

		for (String str : stringList) {
			System.out.println("str: " + str);
		}

		return stringPoolHeader;
	}

	/**
	 * Determine if the end of the file is reached
	 * @param length length
	 * @return
	 */
	public  static  boolean  isEnd ( int  length )  {
		return resTypeOffset >= length ? true : false;
	}

	/**
	 * Determine whether it is a type descriptor
	 * @param arscArray binary data
	 * @return
	 */
	public static boolean isTypeSpec(byte[] arscArray) {
		ResChunkHeader header = parseResChunkHeader(arscArray, resTypeOffset);

		return header.type == 0x0202 ? true : false;
	}

	public static String getKeyString(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String getResString(int data) {
		// TODO Auto-generated method stub
		return null;
	}

}