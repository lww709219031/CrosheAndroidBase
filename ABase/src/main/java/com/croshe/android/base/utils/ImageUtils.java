package com.croshe.android.base.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.croshe.android.base.R;
import com.croshe.android.base.extend.glide.GlideApp;
import com.croshe.android.base.views.CornerTransform;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageUtils {

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        try {
            //旋转图片 动作
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            // 创建新的图片
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return resizedBitmap;
        } catch (OutOfMemoryError e) {
            System.gc();
        } catch (Exception e) {
        }
        return bitmap;
    }

    /**
     * 获得图片的角度
     */
    public static int getImageDegree(String filePath) {
        filePath = filePath.replace("file://", "");
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (Exception e) {
            exif = null;
        }
        if (exif != null) {
            // 读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            // 计算旋转角度
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        }
        return 0;
    }


    /**
     * 计算图片的缩放值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    /**
     * 计算图片的缩放值
     *
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(int sourceWidth, int sourceHeiht,
                                            int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (sourceHeiht > reqHeight || sourceWidth > reqWidth) {

            final int heightRatio = Math.round((float) sourceHeiht
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) sourceWidth / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    /**
     * 根据路径获得并压缩返回bitmap用于显示
     *
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 压缩图片质量压缩
     *
     * @return
     */
    public static void compressImage(Bitmap bitmap, String targetPath) {
        compressImage(bitmap, targetPath, 300);
    }


    /**
     * 压缩图片质量压缩
     *
     * @return
     */
    public static void compressImage(Bitmap bitmap, String targetPath, int minSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > minSize) { //压缩到300kb以内
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        File file = new File(targetPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }


    /**
     * 压缩图片大小和质量
     *
     * @param sourcePath
     * @param targetPath
     */
    public static void doCompressImage(String sourcePath, String targetPath) {
        if (sourcePath.startsWith("file://")) {
            sourcePath = Uri.parse(sourcePath).getPath();
        }
        int degree = getImageDegree(sourcePath);
        Bitmap bitmap = getSmallBitmap(sourcePath);
        compressImage(rotaingImageView(degree, bitmap), targetPath);
    }

    /**
     * 压缩图片大小和质量
     *
     * @param bitmap
     * @param targetPath
     */
    public static void doCompressImage(Bitmap bitmap, String targetPath) {
        compressImage(bitmap, targetPath);
    }

    /**
     * 压缩图片大小和质量
     *
     * @param bitmap
     * @param targetPath
     */
    public static void doCompressImage(Bitmap bitmap, String targetPath, int minSize) {
        compressImage(bitmap, targetPath, minSize);
    }


    /**
     * 获得资源文件的Bitmap
     *
     * @param context
     * @param vectorDrawableId
     * @return
     */
    public static Bitmap getDrawableBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }


    /**
     * 获得视频的第一帧图片
     *
     * @param videoPath
     * @return
     */
    public static Bitmap getVideoFirstImage(String videoPath) {
        try {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoPath);
            Bitmap bitmap = media.getFrameAtTime();
            media.release();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * drawable 转成 bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    /**
     * 显示图片，使用的是ImageLoader
     *
     * @param imageView
     * @param url
     */
    public static void displayImage(ImageView imageView, String url) {
        glideImage(imageView, url);
    }

    /**
     * 显示图片，使用的是ImageLoader
     *
     * @param imageView
     * @param url
     */
    public static void displayImage(ImageView imageView, String url, int defaultImageResource) {
        glideImage(imageView, url, defaultImageResource);
    }


    /**
     * 显示图片，使用的是Glide
     *
     * @param imageView
     * @param url
     */
    public static void glideImage(ImageView imageView, String url, int defaultImageResource) {
        if (imageView == null) return;
        GlideApp.with(imageView.getContext().getApplicationContext())
                .load(url)
                .fitCenter()
                .placeholder(defaultImageResource)
                .error(defaultImageResource)
                .fallback(defaultImageResource)
                .into(imageView);
    }

    /**
     * 显示图片，使用的是Glide
     * 圆角
     */
    public static void glideCornerImage(ImageView imageView, String url, int radius, int defaultImageResource) {
        if (imageView == null) return;
        CornerTransform transform = new CornerTransform(imageView.getContext(), radius);
        transform.setExceptCorner(false, false, false, false);
        GlideApp.with(imageView.getContext().getApplicationContext())
                .load(url)
                .fitCenter()
                .placeholder(defaultImageResource)
                .error(defaultImageResource)
                .fallback(defaultImageResource)
                .transform(transform)
                .skipMemoryCache(true)
                .into(imageView);
    }

    /**
     * 显示图片，使用的是Glide
     *
     * @param imageView
     * @param url
     */
    public static void glideImage(ImageView imageView, String url) {
        if (imageView == null) return;
        GlideApp.with(imageView.getContext().getApplicationContext())
                .load(url)
                .error(R.drawable.android_base_default_img)
                .fallback(R.drawable.android_base_default_img)
                .into(imageView);
    }

    /**
     * 显示图片，使用的是Glide
     *
     * @param imageView
     * @param url
     * @param width     宽 单位dp
     * @param height    高 单位dp
     */
    public static void glideImage(ImageView imageView, String url, int defaultImageResource, int width, int height) {
        if (imageView == null) return;
        GlideApp.with(imageView.getContext().getApplicationContext())
                .load(url)
                .error(defaultImageResource)
                .override(width, height)
                .fallback(defaultImageResource)
                .into(imageView);
    }


    /**
     * 显示图片，使用的是Glide
     *
     * @param imageView
     * @param url
     * @param width     宽 单位dp
     * @param height    高 单位dp
     */
    public static void glideImage(ImageView imageView, String url, int width, int height) {
        if (imageView == null) return;
        GlideApp.with(imageView.getContext().getApplicationContext())
                .load(url)
                .error(R.drawable.android_base_default_img)
                .override(width, height)
                .fallback(R.drawable.android_base_default_img)
                .into(imageView);
    }


    public static String formatImagePath(String path) {
        if (path.toLowerCase().startsWith("http://")
                || path.toLowerCase().startsWith("https://")
                || path.toLowerCase().startsWith("file://")
                || path.toLowerCase().startsWith("assets://")) {
            return path;
        }
        return "file://" + path;
    }


    /**
     * base64保存图片
     *
     * @param string
     * @return
     */
    public static Bitmap base64toBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    public static boolean isBase64Image(String url) {
//        String regStr = "data:image/.*;base64";
        return url.startsWith("data:image/") && url.contains("base64");
    }


    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }


    public static Bitmap getImageFromAssetsFile(Context context, String filePath, float width, float height) {
        try {
            Bitmap bitmap = LruCacheUtils.getInstance().getBitmapFromMemCache(filePath + "//" + width + height);
            if (bitmap == null) {
                AssetManager am = context.getResources().getAssets();
                InputStream is = am.open(filePath);
                Bitmap image = BitmapFactory.decodeStream(is);
                Bitmap scaleBitmap = Bitmap.createScaledBitmap(image, (int) width, (int) height, true);
                is.close();
                LruCacheUtils.getInstance().addBitmapToMemoryCache(filePath + "//" + width + height, scaleBitmap);
                return scaleBitmap;
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 判断图片是否是gif图片
     *
     * @param localPath
     * @return
     */
    public static boolean isGif(String localPath) {
        return FileUtils.isGifImage(new File(localPath));
    }


    /**
     * 压缩文件
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image, int minKb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > minKb) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 98;
        while (baos.toByteArray().length / 1024 > 3072) { // 循环判断如果压缩后图片是否大于 3Mb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 2;// 每次都减少2
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 把图片保存到相册
     */
    public static void saveImageInsertAlbum(Context context, Bitmap bitmap, String title, String imgaeName) {
        ContentResolver resolver = context.getContentResolver();
        MediaStore.Images.Media.insertImage(resolver, bitmap, title, imgaeName);
    }


    /**
     * 截屏，这里就是截屏的地方了，我这里是截屏RelativeLayout，
     * 只要你将需要的信息放到这个RelativeLayout里面去就可以截取下来了
     *
     * @param waterPhoto waterPhoto
     * @return Bitmap
     */
    public static Bitmap getScreenPhoto(RelativeLayout waterPhoto) {
        View view = waterPhoto;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        int width = view.getWidth();
        int height = view.getHeight();
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        view.destroyDrawingCache();
        return bitmap1;
    }

    /**
     * 根据路径和名字保存图片
     *
     * @return createPath
     */
    public static String saveBitmap(String path, String imgName, Bitmap bitmap) {
        String savePath = null;
        if (path == null) { //if path is null
            File fileSDCardDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            String imgPath = fileSDCardDir.getAbsolutePath() + "/dcxj/";
            File fileDir = new File(imgPath);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            String photoName = imgName + ".JPG";
            imgPath = imgPath + photoName;
            File fileIphoto = new File(imgPath);
            if (!fileIphoto.exists()) {
                try {
                    fileIphoto.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            savePath = fileIphoto.getPath();
            saveBitmap(bitmap, fileIphoto);
            return savePath;
        } else { //if path isn't null, override the photo
            File oldFile = new File(path);
            if (oldFile.exists()) {
                oldFile.delete();
            }
            File newFile = new File(path);
            if (!newFile.exists()) {
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            saveBitmap(bitmap, newFile);
            savePath = newFile.getPath();
            return savePath;
        }
    }

    private static void saveBitmap(Bitmap bitmap, File fileIphoto) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(fileIphoto));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
