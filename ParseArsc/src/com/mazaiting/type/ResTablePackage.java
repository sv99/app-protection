package com.mazaiting.type;

/**
 * The Package data block records the metadata of the compiled package
 *
 * struct ResTable_package
 {
 struct ResChunk_header header;

 // If this is a base package, its ID.  Package IDs start
 // at 1 (corresponding to the value of the package bits in a
 // resource identifier).  0 means this is not a base package.
 uint32_t id;

 // Actual name of this package, \0-terminated.
 uint16_t name[128];

 // Offset to a ResStringPool_header defining the resource
 // type symbol table.  If zero, this package is inheriting from
 // another base package (overriding specific values in it).
 uint32_t typeStrings;

 // Last index into typeStrings that is for public use by others.
 uint32_t lastPublicType;

 // Offset to a ResStringPool_header defining the resource
 // key symbol table.  If zero, this package is inheriting from
 // another base package (overriding specific values in it).
 uint32_t keyStrings;

 // Last index into keyStrings that is for public use by others.
 uint32_t lastPublicKey;

 uint32_t typeIdOffset;
 };
 *
 * The overall structure of the Package data block
 * String Pool
 * Type String Pool
 * Key String Pool
 * Type Specification
 * Type Info
 *
 * @author mazaiting
 */
public class ResTablePackage {
	/**
	 * Chunk header information data structure
	 */
	public  ResChunkHeader  header ;
	/**
	 * The ID of the package is equal to the package id, the value of the general user package Package Id is 0X7F, and the system resource package Package Id is 0X01
	 * This value will be used when constructing the id value in public.xml
	 */
	public  int  id ;
	/**
	 * Package names
	 */
	public char[] name = new char[128];
	/**
	 * The offset of the type string resource pool relative to the header
	 */
	public int typeStrings;
	/**
	 * The index of the last public type string in the type string resource pool, currently this value is set to the element of the type string resource pool
	 */
	public int lastPublicType;
	/**
	 * The offset of the resource item name string relative to the header
	 */
	public  int  keyStrings ;
	/**
	 * The index of the last exported Public resource item name string in the resource item name string resource pool. Currently, this value is set to the number of elements in the resource item name string resource pool
	 */
	public int lastPublicKey;

	public ResTablePackage() {
		header  =  new  ResChunkHeader ();
	}

	@Override
	public String toString(){
		return "header: " + header.toString() + "\n" + ",id= " + id + ",name: " + name.toString() +
				",typeStrings:" + typeStrings + ",lastPublicType: " + lastPublicType + ",keyStrings: " + keyStrings
				+ ",lastPublicKey: " + lastPublicKey;
	}
}