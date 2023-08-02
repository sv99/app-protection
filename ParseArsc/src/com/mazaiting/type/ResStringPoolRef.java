package com.mazaiting.type;

/**
 * ResStringPool_ref has a fixed value of 0XFFFFFFFF in the back as a placeholder
 * struct ResStringPool_ref
 {
 // Index into the string pool table (uint32_t-offset from the indices
 // immediately after ResStringPool_header) at which to find the location
 // of the string data in the pool.
 uint32_t index;
 };
 * @author mazaiting
 */
public class ResStringPoolRef {
	/**
	 * logo
	 */
	public int index;
	/**
	 * The byte size occupied by String Pool Ref
	 * @return
	 */
	public int getSize() {
		return 4;
	}

	@Override
	public String toString() {
		return "index: " + index;
	}
}