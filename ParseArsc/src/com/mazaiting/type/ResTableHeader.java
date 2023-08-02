package com.mazaiting.type;

/**
 * The first structure of the Resources.arsc file is the resource index table header
 * Describe the size of the Resources.arsc file and the number of resource packs
 *
 * struct ResTable_header
 {
 struct ResChunk_header header;

 // The number of ResTable_package structures.
 uint32_t packageCount;
 };
 *
 * @author mazaiting
 */
public class ResTableHeader {
	/**
	 * Standard Chunk header information format
	 */
	public  ResChunkHeader  header ;
	/**
	 * The number of resource bundles compiled
	 * An apk in Android may contain multiple resource bundles. By default, there is only one resource bundle, which is the resource bundle where the package name of the application is located.
	 */
	public int packageCount;

	public ResTableHeader() {
		header  =  new  ResChunkHeader ();
	}

	/**
	 * Get the number of bytes occupied by the current Table Header
	 * @return
	 */
	public int getHeaderSize() {
		return header.getHeaderSize() + 4;
	}

	@Override
	public String toString(){
		return "header:" + header.toString() + "\n" + "packageCount:"+packageCount;
	}
}