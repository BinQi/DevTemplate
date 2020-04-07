package wbq.frame.util.safe;

import java.nio.charset.Charset;
import java.util.Arrays;

public class Base64 {
    private static final int BYTES_PER_ENCODED_BLOCK = 4;
    private static final int BITS_PER_ENCODED_BYTE = 6;
    protected static final int MASK_8BITS = 255;
    static final byte[] CHUNK_SEPARATOR = new byte[]{13, 10};
    private static final byte[] STANDARD_ENCODE_TABLE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_SAFE_ENCODE_TABLE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final byte[] DECODE_TABLE = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};
    private byte[] encodeTable;
    private byte[] decodeTable;
    private byte[] lineSeparator;
    private int decodeSize;
    private int encodeSize;
    private int unencodedBlockSize;
    private int encodedBlockSize;
    protected int lineLength;
    private int chunkSeparatorLength;
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    protected Base64(int unencodedBlockSize, int encodedBlockSize, int lineLength, int chunkSeparatorLength) {
        this.unencodedBlockSize = unencodedBlockSize;
        this.encodedBlockSize = encodedBlockSize;
        boolean useChunking = lineLength > 0 && chunkSeparatorLength > 0;
        this.lineLength = useChunking ? lineLength / encodedBlockSize * encodedBlockSize : 0;
        this.chunkSeparatorLength = chunkSeparatorLength;
    }

    public Base64() {
        this(0);
    }

    public Base64(boolean urlSafe) {
        this(76, CHUNK_SEPARATOR, urlSafe);
    }

    public Base64(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    public Base64(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }

    public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
        this(3, 4, lineLength, lineSeparator == null ? 0 : lineSeparator.length);
        this.decodeTable = DECODE_TABLE;
        if (lineSeparator != null) {
            if (this.containsAlphabetOrPad(lineSeparator)) {
                String sep = newStringUtf8(lineSeparator);
                throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + sep + "]");
            }

            if (lineLength > 0) {
                this.encodeSize = 4 + lineSeparator.length;
                this.lineSeparator = new byte[lineSeparator.length];
                System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
            } else {
                this.encodeSize = 4;
                this.lineSeparator = null;
            }
        } else {
            this.encodeSize = 4;
            this.lineSeparator = null;
        }

        this.decodeSize = this.encodeSize - 1;
        this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
    }

    public static String newStringUtf8(byte[] bytes) {
        return bytes == null ? null : new String(bytes, UTF_8);
    }

    public static String encodeBase64URLSafeString(byte[] binaryData) {
        return newStringUtf8(encodeBase64(binaryData, false, true));
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, 2147483647);
    }

    public static String decodeBase64URLSafeString(byte[] binaryData) {
        return newStringUtf8(decodeBase64(binaryData));
    }

    public static byte[] decodeBase64(String base64String) {
        return (new Base64()).decode(base64String);
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        return (new Base64()).decode(base64Data);
    }

    protected boolean containsAlphabetOrPad(byte[] arrayOctet) {
        if (arrayOctet == null) {
            return false;
        } else {
            byte[] arr$ = arrayOctet;
            int len$ = arrayOctet.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                byte element = arr$[i$];
                if (61 == element || this.isInAlphabet(element)) {
                    return true;
                }
            }

            return false;
        }
    }

    protected boolean isInAlphabet(byte octet) {
        return octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1;
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
        if (binaryData != null && binaryData.length != 0) {
            Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
            long len = b64.getEncodedLength(binaryData);
            if (len > (long)maxResultSize) {
                throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maximum size of " + maxResultSize);
            } else {
                return b64.encode(binaryData);
            }
        } else {
            return binaryData;
        }
    }

    protected byte[] ensureBufferSize(int size, Base64.Context context) {
        return context.buffer != null && context.buffer.length >= context.pos + size ? context.buffer : this.resizeBuffer(context);
    }

    private byte[] resizeBuffer(Base64.Context context) {
        if (context.buffer == null) {
            context.buffer = new byte[this.getDefaultBufferSize()];
            context.pos = 0;
            context.readPos = 0;
        } else {
            byte[] b = new byte[context.buffer.length * 2];
            System.arraycopy(context.buffer, 0, b, 0, context.buffer.length);
            context.buffer = b;
        }

        return context.buffer;
    }

    protected int getDefaultBufferSize() {
        return 8192;
    }

    void encode(byte[] in, int inPos, int inAvail, Base64.Context context) {
        if (!context.eof) {
            if (inAvail < 0) {
                context.eof = true;
                if (0 == context.modulus && this.lineLength == 0) {
                    return;
                }

                byte[] i = this.ensureBufferSize(this.encodeSize, context);
                int buffer = context.pos;
                switch(context.modulus) {
                case 0:
                    break;
                case 1:
                    i[context.pos++] = this.encodeTable[context.ibitWorkArea >> 2 & 63];
                    i[context.pos++] = this.encodeTable[context.ibitWorkArea << 4 & 63];
                    if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                        i[context.pos++] = 61;
                        i[context.pos++] = 61;
                    }
                    break;
                case 2:
                    i[context.pos++] = this.encodeTable[context.ibitWorkArea >> 10 & 63];
                    i[context.pos++] = this.encodeTable[context.ibitWorkArea >> 4 & 63];
                    i[context.pos++] = this.encodeTable[context.ibitWorkArea << 2 & 63];
                    if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                        i[context.pos++] = 61;
                    }
                    break;
                default:
                    throw new IllegalStateException("Impossible modulus " + context.modulus);
                }

                context.currentLinePos += context.pos - buffer;
                if (this.lineLength > 0 && context.currentLinePos > 0) {
                    System.arraycopy(this.lineSeparator, 0, i, context.pos, this.lineSeparator.length);
                    context.pos += this.lineSeparator.length;
                }
            } else {
                for(int var8 = 0; var8 < inAvail; ++var8) {
                    byte[] var9 = this.ensureBufferSize(this.encodeSize, context);
                    context.modulus = (context.modulus + 1) % 3;
                    int b = in[inPos++];
                    if (b < 0) {
                        b += 256;
                    }

                    context.ibitWorkArea = (context.ibitWorkArea << 8) + b;
                    if (0 == context.modulus) {
                        var9[context.pos++] = this.encodeTable[context.ibitWorkArea >> 18 & 63];
                        var9[context.pos++] = this.encodeTable[context.ibitWorkArea >> 12 & 63];
                        var9[context.pos++] = this.encodeTable[context.ibitWorkArea >> 6 & 63];
                        var9[context.pos++] = this.encodeTable[context.ibitWorkArea & 63];
                        context.currentLinePos += 4;
                        if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
                            System.arraycopy(this.lineSeparator, 0, var9, context.pos, this.lineSeparator.length);
                            context.pos += this.lineSeparator.length;
                            context.currentLinePos = 0;
                        }
                    }
                }
            }
        }

    }

    void decode(byte[] in, int inPos, int inAvail, Base64.Context context) {
        if (!context.eof) {
            if (inAvail < 0) {
                context.eof = true;
            }

            for(int i = 0; i < inAvail; ++i) {
                byte[] buffer = this.ensureBufferSize(this.decodeSize, context);
                byte b = in[inPos++];
                if (b == 61) {
                    context.eof = true;
                    break;
                }

                if (b >= 0 && b < DECODE_TABLE.length) {
                    int result = DECODE_TABLE[b];
                    if (result >= 0) {
                        context.modulus = (context.modulus + 1) % 4;
                        context.ibitWorkArea = (context.ibitWorkArea << 6) + result;
                        if (context.modulus == 0) {
                            buffer[context.pos++] = (byte)(context.ibitWorkArea >> 16 & 255);
                            buffer[context.pos++] = (byte)(context.ibitWorkArea >> 8 & 255);
                            buffer[context.pos++] = (byte)(context.ibitWorkArea & 255);
                        }
                    }
                }
            }

            if (context.eof && context.modulus != 0) {
                byte[] buffer = this.ensureBufferSize(this.decodeSize, context);
                switch(context.modulus) {
                case 1:
                    break;
                case 2:
                    context.ibitWorkArea >>= 4;
                    buffer[context.pos++] = (byte)(context.ibitWorkArea & 255);
                    break;
                case 3:
                    context.ibitWorkArea >>= 2;
                    buffer[context.pos++] = (byte)(context.ibitWorkArea >> 8 & 255);
                    buffer[context.pos++] = (byte)(context.ibitWorkArea & 255);
                    break;
                default:
                    throw new IllegalStateException("Impossible modulus " + context.modulus);
                }
            }

        }
    }

    public byte[] encode(byte[] pArray) {
        if (pArray != null && pArray.length != 0) {
            Base64.Context context = new Base64.Context();
            this.encode(pArray, 0, pArray.length, context);
            this.encode(pArray, 0, -1, context);
            byte[] buf = new byte[context.pos - context.readPos];
            this.readResults(buf, 0, buf.length, context);
            return buf;
        } else {
            return pArray;
        }
    }

    public byte[] decode(byte[] pArray) {
        if (pArray != null && pArray.length != 0) {
            Base64.Context context = new Base64.Context();
            this.decode(pArray, 0, pArray.length, context);
            this.decode(pArray, 0, -1, context);
            byte[] result = new byte[context.pos];
            this.readResults(result, 0, result.length, context);
            return result;
        } else {
            return pArray;
        }
    }

    public byte[] decode(String pArray) {
        return this.decode(pArray.getBytes(Charset.defaultCharset()));
    }

    int available(Base64.Context context) {
        return context.buffer != null ? context.pos - context.readPos : 0;
    }

    int readResults(byte[] b, int bPos, int bAvail, Base64.Context context) {
        if (context.buffer != null) {
            int len = Math.min(this.available(context), bAvail);
            System.arraycopy(context.buffer, context.readPos, b, bPos, len);
            context.readPos += len;
            if (context.readPos >= context.pos) {
                context.buffer = null;
            }

            return len;
        } else {
            return context.eof ? -1 : 0;
        }
    }

    public long getEncodedLength(byte[] pArray) {
        long len = (long)((pArray.length + this.unencodedBlockSize - 1) / this.unencodedBlockSize) * (long)this.encodedBlockSize;
        if (this.lineLength > 0) {
            len += (len + (long)this.lineLength - 1L) / (long)this.lineLength * (long)this.chunkSeparatorLength;
        }

        return len;
    }

    static class Context {
        int ibitWorkArea;
        long lbitWorkArea;
        byte[] buffer;
        int pos;
        int readPos;
        boolean eof;
        int currentLinePos;
        int modulus;

        Context() {
        }

        public String toString() {
            return String.format("%s[buffer=%s, currentLinePos=%s, eof=%s, ibitWorkArea=%s, lbitWorkArea=%s, modulus=%s, pos=%s, readPos=%s]", this.getClass().getSimpleName(), Arrays.toString(this.buffer), this.currentLinePos, this.eof, this.ibitWorkArea, this.lbitWorkArea, this.modulus, this.pos, this.readPos);
        }
    }
}