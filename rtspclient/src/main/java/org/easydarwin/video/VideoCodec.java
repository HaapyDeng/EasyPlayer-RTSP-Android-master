package org.easydarwin.video;

/**
 * Created by John on 2017/1/5.
 */

public class VideoCodec {

    static {
        System.loadLibrary("proffmpeg");
        System.loadLibrary("VideoCodecer");
    }

    public static final int DECODER_H264 = 0;
    public static final int DECODER_H265 = 1;

    private native long create(Object surface, int codec);

    private native void close(long handle);

    protected long mHandle;

    private native int decode(long handle, byte[] in, int offset, int length,int []size);

    public int decoder_create(Object surface, int codec) {
        mHandle = create(surface, codec);
        if (mHandle != 0) {
            return 0;
        }
        return -1;
    }

    public int decoder_decode(byte[] in, int offset, int length, int[]size) {
        int result = decode(mHandle, in, offset, length, size);
        return result;
    }

    public void decoder_close() {
        if (mHandle == 0) {
            return;
        }
        close(mHandle);
        mHandle = 0;
    }


    public static class VideoDecoderLite extends VideoCodec {

        private int[] mSize;
        private Object surface;

        public void create(Object surface, boolean h264) {
            if (surface == null) {
                throw new NullPointerException("surface is null!");
            }
            this.surface = surface;
            decoder_create(surface, h264 ? 0 : 1);
            mSize = new int[2];
        }

        public void close() {
            decoder_close();
        }

        protected int decodeFrame(RTSPClient.FrameInfo aFrame, int[] size) {
            int nRet = 0;
            nRet = decoder_decode( aFrame.buffer, aFrame.offset, aFrame.length, size);
            return nRet;
        }
    }
}
