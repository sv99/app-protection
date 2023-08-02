package com.mazaiting.type;

/**
 * The type resource item data block is used to describe the specific information
 * of the resource item, and the name, value and configuration of each resource
 * item can be known.
 * Type resource item data is also organized according to type and configuration,
 * that is, a type with n configurations corresponds to a total of n types
 * Resource item data block.
 *
 * struct ResTable_type
 {
 struct ResChunk_header header;

 enum {
 NO_ENTRY = 0xFFFFFFFF
 };

 // The type identifier this chunk is holding.  Type IDs start
 // at 1 (corresponding to the value of the type bits in a
 // resource identifier).  0 is invalid.
 uint8_t id;

 // Must be 0.
 uint8_t res0;
 // Must be 0.
 uint16_t res1;

 // Number of uint32_t entry indices that follow.
 uint32_t entryCount;

 // Offset from header where ResTable_entry data starts.
 uint32_t entriesStart;

 // Configuration this collection of entries is designed for.
 ResTable_config config;
 };
 *
 * @author mazaiting
 */
public class ResTableType {
	/**
	 * NO_ENTRY constant
	 */
	public final static int NO_ENTRY = 0xFFFFFFFF;
	/**
	 * Chunk header information structure
	 */
	public  ResChunkHeader  header ;
	/**
	 * Type ID that identifies the resource
	 */
	public byte id;
	/**
	 * Reserved, always 0
	 */
	public byte res0;
	/**
	 * Reserved, always 0
	 */
	public short res1;
	/**
	 * The number of resource items of this type refers to the number of resource items with the same name
	 */
	public int entryCount;
	/**
	 * The offset value of the resource item array block relative to the header
	 */
	public int entriesStart;
	/**
	 * Point to a ResTable_config, used to describe configuration information, region, language, resolution, etc.
	 */
	public ResTableConfig resConfig;

	public ResTableType() {
		header  =  new  ResChunkHeader ();
		resConfig  =  new  ResTableConfig ();
	}

	/**
	 * Get the number of bytes occupied by the current resource type
	 * @return
	 */
	public int getSize() {
		return header.getHeaderSize() + 1 + 1 + 2 + 4 + 4;
	}

	@Override
	public String toString(){
		return "header: " + header.toString() + ",id: " + id + ",res0: " + res0 + ",res1: " + res1 +
				",entryCount: " + entryCount + ",entriesStart: " + entriesStart;
	}

}