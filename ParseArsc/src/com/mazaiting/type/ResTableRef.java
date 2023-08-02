package com.mazaiting.type;

import com.mazaiting.Util;

/**
 * 
 * struct ResTable_ref
{
    uint32_t ident;
};
 * @author mazaiting
 */
public class ResTableRef {
	public int ident;
	
	public int getSize() {
		return 4;
	}
	
	@Override
	public String toString(){
		return "ident: 0x" + Util.bytesToHexString(Util.int2Byte(ident));
	}
}
