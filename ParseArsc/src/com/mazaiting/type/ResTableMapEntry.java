package com.mazaiting.type;

/**
 * ResTable_entry depends on the flags, and the following data is also different.
 * If the bit of flags is 1, then ResTable_entry is ResTable_map_entry
 * struct ResTable_map_entry : public ResTable_entry
{
    // Resource identifier of the parent mapping, or 0 if there is none.
    // This is always treated as a TYPE_DYNAMIC_REFERENCE.
    ResTable_ref parent;
    // Number of name/value pairs that follow for FLAG_COMPLEX.
    uint32_t count;
};
 * @author mazaiting
 */
public class ResTableMapEntry extends ResTableEntry{
	public ResTableRef parent;
	public int count;
	
	public ResTableMapEntry() {
		parent = new ResTableRef();
	}
	
	@Override
	public int getSize() {
		return super.getSize() + parent.getSize() + 4;
	}
	
	@Override
	public String toString(){
		return super.toString() + ",parent: " + parent.toString() + ",count: " + count;
	}

	
}
