package com.mazaiting.type;

import com.mazaiting.Util;

/**
 * The Resource.arsc file format is composed of a series of chunks, each chunk contains ResChunk_header
 * <p>
 * struct ResChunk_header
 * {
 * // Type identifier for this chunk.  The meaning of this value depends
 * // on the containing chunk.
 * uint16_t type;
 * <p>
 * // Size of the chunk header (in bytes).  Adding this value to
 * // the address of the chunk allows you to find its associated data
 * // (if any).
 * uint16_t headerSize;
 * <p>
 * // Total size of this chunk (in bytes).  This is the chunkSize plus
 * // the size of any data associated with the chunk.  Adding this value
 * // to the chunk allows you to completely skip its contents (including
 * // any child chunks).  If this value is the same as chunkSize, there is
 * // no data associated with the chunk.
 * uint32_t size;
 * };
 *
 * @author mazaiting
 */
public class ResChunkHeader {
    /**
     * The type of the current chunk
     */
    public short type;
    /**
     * The head size of the current chunk
     */
    public short headerSize;
    /**
     * The size of the current chunk
     */
    public int size;

    /**
     * Get the number of bytes occupied by the Chunk Header
     *
     * @return
     */
    public int getHeaderSize() {
        return 2 + 2 + 4;
    }

    @Override
    public String toString() {
        return "type: " + Util.bytesToHexString(Util.int2Byte(type)) +
                ", headerSize: " + headerSize +
                ", size: " + size;
    }
}