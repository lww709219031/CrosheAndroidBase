package com.croshe.android.base.utils;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created by Janesen on 2017/2/24.
 */

public class FileUtils {


    /**
     * 保存文件
     *
     * @param bitmap
     * @param path
     */
    public static void saveBitmapToPath(Bitmap bitmap, String path, boolean isRecycle) {
        if(bitmap==null)return;
        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FileOutputStream fos = null;
        try {
            if (path.toLowerCase().endsWith(".png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            fos = new FileOutputStream(path);
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
            if (bitmap != null && isRecycle) {
                bitmap.recycle();
            }
        }

    }


    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } else if (isMediaDocument(uri)) {// MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
                // (and
                // general)
                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.//com.android.providers.media.documents
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 打开文件
     */
    public static void openFile(Context context, File file) {
        openFile(context, file, null);
    }

    /**
     * 打开文件
     */
    public static void openFile(Context context, File file, String extension) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setAction(Intent.ACTION_VIEW);
        String url = file.getPath();
        if (StringUtils.isNotEmpty(extension)) {
            url += extension;
        }
        String type = getMimeType(url);
        intent.setDataAndType(getFileUri(context, file), type);
        context.startActivity(intent);
    }

    public static String getMimeType(String url) {
        String type = "*/*";
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }


    /**
     * 递归彻底删除文件
     *
     * @param file
     */
    public static long deleteFiles(File file) {
        long currLength = file.length();
        if (file.isDirectory()) {
            for (File fl : file.listFiles()) {
                currLength += deleteFiles(fl);
            }
            file.delete();
        } else {
            file.delete();
        }
        return currLength;
    }

    /**
     * 运行安装包APK
     */
    public static void executeAPK(String appId, Context context, File apkFile) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, appId + ".fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }


    public static Bitmap createVideoThumbnail(String filePath) {
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);

            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        } catch (Exception ex) {
            // Assume this is a corrupt video file
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }


    /**
     * 获得文件的uri
     *
     * @param context
     * @return
     */
    public static Uri getMediaFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Uri.fromFile(file);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            return uri;
        }
    }


    public static Uri getFileUri(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
            return contentUri;
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 保存图片到系统相册里
     *
     * @param context
     * @param bitmap
     */
    public static void saveImageToGallery(Context context, Bitmap bitmap) {
        File file = new File(context.getFilesDir().getAbsolutePath()
                + "/Croshe/Images/" + MD5Encrypt.MD5(String.valueOf(System.currentTimeMillis())) + ".png");
        saveBitmapToPath(bitmap, file.getAbsolutePath(), true);
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }


    /**
     * 保存图片到系统相册里
     *
     * @param context
     * @param file
     */
    public static void saveImageToGallery(Context context, File file) {

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateFileFromDatabase(context, file);
    }


    public static boolean isFileUrl(String url) {
        return Pattern.matches(".+(.rar|.zip|.docx|.doc|.xls|.xlsx|.pdf|.apk|.txt|.mp4|.mp3|.avi|.rmvb)$", url.toLowerCase());
    }
    public static boolean isVideoUrl(String url) {
        return Pattern.matches(".+(.mp4|.mp3)$", url.toLowerCase());
    }



    public static boolean isImageUrl(String url) {
        return Pattern.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.BMP|.bmp|.PNG|.png|.gif)$", url.toLowerCase());
    }

    public static boolean isStaticImageUrl(String url) {
        return Pattern.matches(".+(.JPEG|.jpeg|.JPG|.jpg|.BMP|.bmp|.PNG|.png)$", url.toLowerCase());
    }


    /**
     * 获得文件名称,包含扩展名，例如croshe.ppt
     * @param url
     * @return
     */
    public static  String getFileName(String url) {
        return url.substring(url.lastIndexOf("/")+1);
    }


    /**
     * 获得文件名，不包含扩展名
     * @param url
     * @return
     */
    public static String getFileShortName(String url) {
        String fileName = getFileName(url);
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot >-1) && (dot < (fileName.length()))) {
                return fileName.substring(0, dot);
            }
        }
        return fileName;
    }

    /**
     * 获得文件扩展名，例如txt
     * @param url
     * @return
     */
    public static String getFileExtName(String url) {
        try {
            String fileName = getFileName(url);
            if ((fileName != null) && (fileName.length() > 0)) {
                int dot = fileName.lastIndexOf('.');
                if ((dot >-1) && (dot < (fileName.length() - 1))) {
                    return fileName.substring(dot + 1);
                }
            }
            return fileName;
        } catch (Exception e) {
        }
        return "";
    }


    /**
     * 是否是gif图片
     * @param file
     * @return
     */
    public static boolean isGifImage(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int[] flags = new int[5];
            flags[0] = inputStream.read();
            flags[1] = inputStream.read();
            flags[2] = inputStream.read();
            flags[3] = inputStream.read();
            inputStream.skip(inputStream.available() - 1);
            flags[4] = inputStream.read();
            inputStream.close();
            return flags[0] == 71 && flags[1] == 73 && flags[2] == 70 && flags[3] == 56 && flags[4] == 0x3B;
        } catch (Exception e) {}
        return false;
    }


    public static String formatSize(long fileLength) {
        long size = (long) Math.floor(fileLength / 1024.0);
        BigDecimal bd = new BigDecimal(size);
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(bd) + "KB";
    }

    /**
     * 打开目录
     * @param context
     * @param path
     */
    public static void openAssignFolder(Context context,String path){
        File file = new File(path);
        if(null==file || !file.exists()){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            context.startActivity(Intent.createChooser(intent,"选择浏览工具"));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新通知媒体库
     * @param context
     * @param file
     */
    public static void updateFileFromDatabase(Context context,File file){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
            MediaScannerConnection.scanFile(context, paths, null, null);
            MediaScannerConnection.scanFile(context, new String[] {
                            file.getAbsolutePath()},
                    null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri)
                        {
                        }
                    });
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }




    public static  void deleteMediaFile(Context context,String path){
        if(!StringUtils.isEmpty(path)){
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = context.getContentResolver();//cutPic.this是一个上下文
            String url =  MediaStore.Images.Media.DATA + "='" + path + "'";
            //删除图片
            contentResolver.delete(uri, url, null);
        }
    }


}

