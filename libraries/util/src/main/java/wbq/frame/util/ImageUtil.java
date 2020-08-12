package wbq.frame.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author jerry
 * @created 2020/8/4 15:01
 */
public final class ImageUtil {
    /**
     * Returns the allocated byte size of the given bitmap.
     *
     * @see #getBitmapByteSize(android.graphics.Bitmap)
     * @deprecated Use {@link #getBitmapByteSize(android.graphics.Bitmap)} instead. Scheduled to be
     *     removed in Glide 4.0.
     */
    @Deprecated
    public static int getSize(@NonNull Bitmap bitmap) {
        return getBitmapByteSize(bitmap);
    }

    /** Returns the in memory size of the given {@link Bitmap} in bytes. */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int getBitmapByteSize(@NonNull Bitmap bitmap) {
        // The return value of getAllocationByteCount silently changes for recycled bitmaps from the
        // internal buffer size to row bytes * height. To avoid random inconsistencies in caches, we
        // instead assert here.
        if (bitmap.isRecycled()) {
            throw new IllegalStateException(
                    "Cannot obtain size for recycled Bitmap: "
                            + bitmap
                            + "["
                            + bitmap.getWidth()
                            + "x"
                            + bitmap.getHeight()
                            + "] "
                            + bitmap.getConfig());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Workaround for KitKat initial release NPE in Bitmap, fixed in MR1. See issue #148.
            try {
                return bitmap.getAllocationByteCount();
            } catch (
                    @SuppressWarnings("PMD.AvoidCatchingNPE")
                            NullPointerException e) {
                // Do nothing.
            }
        }
        return bitmap.getHeight() * bitmap.getRowBytes();
    }

    /**
     * Returns the in memory size of {@link android.graphics.Bitmap} with the given width, height, and
     * {@link android.graphics.Bitmap.Config}.
     */
    public static int getBitmapByteSize(int width, int height, @Nullable Bitmap.Config config) {
        return width * height * getBytesPerPixel(config);
    }

    private static int getBytesPerPixel(@Nullable Bitmap.Config config) {
        // A bitmap by decoding a GIF has null "config" in certain environments.
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }

        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            case RGBA_F16:
                bytesPerPixel = 8;
                break;
            case ARGB_8888:
            default:
                bytesPerPixel = 4;
                break;
        }
        return bytesPerPixel;
    }
}
