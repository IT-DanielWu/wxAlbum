package ysn.com.wxalbum.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * @Author yangsanning
 * @ClassName ImageUtils
 * @Description 图片工具类
 * @Date 2019/12/27
 * @History 2019/12/27 author: description:
 */
public class ImageUtils {

    /**
     * 缩放图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int reqWidth, int reqHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 计算缩放比例
        float scaleWidth = ((float) reqWidth) / width;
        float scaleHeight = ((float) reqHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    /**
     * 根据计算的inSampleSize，得到压缩后图片
     */
    @SuppressLint("NewApi")
    public static Bitmap decodeSampledBitmapFromFile(Context context, String pathName, int reqWidth, int reqHeight) {

        int degree = 0;

        Uri uri = UriUtils.getImageContentUri(context, pathName);
        ParcelFileDescriptor parcelFileDescriptor;
        FileDescriptor fileDescriptor;
        try {

            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            ExifInterface exifInterface;
            if (AndroidVersionUtils.isAndroidQ()) {
                exifInterface = new ExifInterface(fileDescriptor);
            } else {
                exifInterface = new ExifInterface(pathName);
            }

            int result = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (result) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            // 第一次解析将inJustDecodeBounds设置为true, 来获取图片大小
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            if (AndroidVersionUtils.isAndroidQ()) {
                BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            } else {
                BitmapFactory.decodeFile(pathName, options);
            }
            // 调用上面定义的方法计算inSampleSize值
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // 使用获取到的inSampleSize值再次解析图片
            options.inJustDecodeBounds = false;
//            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bitmap;
            if (AndroidVersionUtils.isAndroidQ()) {
                bitmap = getBitmapFromUri(context, uri, options);
            } else {
                bitmap = BitmapFactory.decodeFile(pathName, options);
            }

            parcelFileDescriptor.close();

            if (degree != 0) {
                Bitmap newBitmap = rotateImageView(bitmap, degree);
                bitmap.recycle();
                return newBitmap;
            }
            return bitmap;
        } catch (OutOfMemoryError error) {
            Log.e("JackPhotos", "内存泄露！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 计算inSampleSize，用于压缩图片
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // 源图片的宽度
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;

        if (width > reqWidth && height > reqHeight) {
            // 计算出实际宽度和目标宽度的比率
            int widthRatio = Math.round((float) width / (float) reqWidth);
            int heightRatio = Math.round((float) height / (float) reqHeight);
            inSampleSize = Math.max(widthRatio, heightRatio);
        }

        return inSampleSize;
    }

    /**
     * 根据uri获取Bitmap
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) {
        return getBitmapFromUri(context, uri, null);
    }

    /**
     * 根据uri获取Bitmap
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri, BitmapFactory.Options options) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 旋转图片
     */
    public static Bitmap rotateImageView(Bitmap bitmap, int angle) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 加载图片
     */
    public static void loadImage(Context context, Uri uri, ImageView imageView) {
        Glide.with(context).load(uri)
            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(imageView);
    }

    /**
     * 加载图片
     */
    public static void loadImage(Context context, String imagePath, ImageView imageView) {
        Glide.with(context).load(imagePath)
            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(imageView);
    }

    /**
     * 加载图片
     */
    public static void loadImageAsBitmap(Context context, Uri uri, ImageView imageView) {
        Glide.with(context).asBitmap().load(uri)
            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(imageView);
    }

    /**
     * 加载图片
     */
    public static void loadImageAsBitmap(Context context, String imagePath, ImageView imageView) {
        Glide.with(context).asBitmap().load(imagePath)
            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(imageView);
    }
}
