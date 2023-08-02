package com.mazaiting;

public class AttributeData {
	public int nameSpaceUri;
	public int name;
	public int valueString;
	public int type;
	public int data;
	
	public String getNameSpaceUri() {
		if (nameSpaceUri < 0) {
			return "";
		}
		return ParseChunkUtil.getStringContent(nameSpaceUri);
	}
	
	public String getName() {
		if (name < 0) {
			return "";
		}
		return ParseChunkUtil.getStringContent(name);
	}
	
	public String getData() {
		if (data < 0) {
			return "";
		}
		return ParseChunkUtil.getStringContent(data);
	}
}
