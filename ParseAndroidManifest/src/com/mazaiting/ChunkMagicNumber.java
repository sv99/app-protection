package com.mazaiting;

/**
 * Chunk Magic Number 魔数各值代表的意义
 * @author mazaiting
 *
 */
public class ChunkMagicNumber {
	/**头*/
	public static final int CHUNK_HEAD = 0x00080003;
	/**字符串*/
	public static final int CHUNK_STRING = 0x001c0001;
	/**命名空间开始*/
	public static final int CHUNK_START_NS = 0x00100100;
	/**命名空间结束*/
	public static final int CHUNK_END_NS = 0x00100101;
	/**TAG 开始*/
	public static final int CHUNK_START_TAG = 0x00100102;
	/**TAG 结束*/
	public static final int CHUNK_END_TAG = 0x00100103;
	/**文本*/
	public static final int CHUNK_TEXT = 0x00100104;
}
