package com.mazaiting.type;

/**
 * The type specification data block is used to describe the configuration differences of resource items. Through this difference, we can know the configuration status of each resource item.
 * After knowing the configuration status of a resource item, after the Android resource management framework detects that the configuration information of the device has changed, it will
 * Can know whether the resource item needs to be reloaded. The type specification data block is organized by type, that is, each type corresponds to a
 * Type specification data block.
 *
 * struct ResTable_typeSpec
 {
 struct ResChunk_header header;

 // The type identifier this chunk is holding.  Type IDs start
 // at 1 (corresponding to the value of the type bits in a
 // resource identifier).  0 is invalid.
 uint8_t id;

 // Must be 0.
 uint8_t res0;
 // Must be 0.
 uint16_t res1;

 // Number of uint32_t entry configuration masks that follow.
 uint32_t entryCount;

 enum {
 // Additional flag indicating an entry is public.
 SPEC_PUBLIC = 0x40000000
 };
 };
 *
 * @author mazaiting
 */
public class ResTableTypeSpec {
	/**
	 * SPEC public constant
	 */
	public final static int SPEC_PUBLIC = 0x40000000;
	/**
	 * Chunk header information structure
	 */
	public  ResChunkHeader  header ;
	/**
	 * Identify the Type ID of the resource, and the Type ID refers to the type ID of the resource. There are several types of resources such as animator, anim, color, drawable, layout, menu, raw, string, and xml, and each of them will be given an ID.
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
	 * The number of resource items of this type, that is, the number of resource items with the same name
	 */
	public int entryCount;

	public ResTableTypeSpec() {
		header  =  new  ResChunkHeader ();
	}

	@Override
	public String toString(){
		return "header: " + header.toString() + ",id: " + id + ",res0: " + res0 +
				",res1: "  +  res1  +  ",entryCount: "  +  entryCount ;
	}

}