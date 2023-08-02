package com.mazaiting.type;

/**
 * The head of the resource index table is the value string resource pool of the resource item, which contains all the value strings of the resource items defined in the resource package
 * A string can correspond to multiple ResStringPool_span and one ResStringPool_ref
 * struct ResStringPool_header
 {
 struct ResChunk_header header;

 // Number of strings in this pool (number of uint32_t indices that follow
 // in the data).
 uint32_t stringCount;

 // Number of style span arrays in the pool (number of uint32_t indices
 // follow the string indices).
 uint32_t styleCount;

 // Flags.
 enum {
 // If set, the string index is sorted by the string values (based
 // on strcmp16()).
 SORTED_FLAG = 1<<0,

 // String pool is encoded in UTF-8
 UTF8_FLAG = 1<<8
 };
 uint32_t flags;

 // Index from header of the string data.
 uint32_t stringsStart;

 // Index from header of the style data.
 uint32_t stylesStart;
 };
 *
 * @author mazaiting
 */
public class ResStringPoolHeader {
	/**
	 * Sort marker
	 */
	public final static int SORTED_FLAG = 1;
	/**
	 * UTF-8 encoding identification
	 */
	public final static int UTF8_FLAG = (1 << 8);
	/**
	 * Standard Chunk header information structure
	 */
	public  ResChunkHeader  header ;
	/**
	 * the number of strings
	 */
	public int stringCount;
	/**
	 * The number of string styles
	 */
	public int styleCount;
	/**
	 * The attribute of the string, the possible values ​​include 0x000 (UTF-16), 0x001 (the string is sorted), 0x100 (UTF-8) and their combined values
	 */
	public int flags;
	/**
	 * The distance of the string content block relative to its head
	 */
	public  int  stringsStart ;
	/**
	 * The distance of the string style block relative to its head
	 */
	public int stylesStart;

	public ResStringPoolHeader() {
		header  =  new  ResChunkHeader ();
	}

	/**
	 * Get the number of bytes occupied by the current String Pool Header
	 * @return
	 */
	public int getHeaderSize() {
		return header.getHeaderSize() + 4 + 4 + 4 + 4 + 4;
	}

	@Override
	public String toString(){
		return "header: " + header.toString() + "\n" + "stringCount: " + stringCount + ",styleCount: " + styleCount
				+ ",flags: " + flags + ",stringStart: " + stringsStart + ",stylesStart: " + stylesStart;
	}
}