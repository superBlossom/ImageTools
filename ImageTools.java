package com.hzbank.compressimage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Base64;
import android.util.Log;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
/**
 * 
 * 图片处理工具类
 * @author Administrator
 *
 */
public class ImageTools {
public static final String TAG="ImageTools";
	/** 
     * Get bitmap from specified image path 
     *  
     * @param imgPath 
     * @return 
     */  
    public Bitmap getBitmap(String imgPath) {  
        // Get bitmap through image path  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        newOpts.inJustDecodeBounds = false;  
        newOpts.inPurgeable = true;  
        newOpts.inInputShareable = true;  
        // Do not compress  
        newOpts.inSampleSize = 1;  
        newOpts.inPreferredConfig = Config.RGB_565;  
        return BitmapFactory.decodeFile(imgPath, newOpts);  
    }  
      
    /** 
     * Store bitmap into specified image path 
     *  
     * @param bitmap 
     * @param outPath 
     * @throws FileNotFoundException  
     */  
    public void storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {  
        FileOutputStream os = new FileOutputStream(outPath);  
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);  
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
        int options = 90;  
      
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩  
            baos.reset(); // 重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;// 每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }  
    
    /** 
     * 图片按比例大小压缩方法 
     * 
     * @param srcPath （根据路径获取图片并压缩） 
     * @return 
     */  
    public static Bitmap getimage(String srcPath) {  
      
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空  
      
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        float hh = 800f;// 这里设置高度为800f  
        float ww = 480f;// 这里设置宽度为480f  
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;// be=1表示不缩放  
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;// 设置缩放比例  
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);  
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩  
    }  
    
    
    /** 
     * 图片按比例大小压缩方法 
     * 
     * @param image （根据Bitmap图片压缩） 
     * @return 
     */  
    public static Bitmap compressScale(Bitmap image) {  
      
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
      
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出  
        if (baos.toByteArray().length / 1024 > 1024) {  
            baos.reset();// 重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, 80, baos);// 这里压缩50%，把压缩后的数据存放到baos中  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        Log.i(TAG, w + "---------------" + h);  
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
        // float hh = 800f;// 这里设置高度为800f  
        // float ww = 480f;// 这里设置宽度为480f  
        float hh = 512f;  
        float ww = 512f;  
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;// be=1表示不缩放  
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) { // 如果高度高的话根据高度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be; // 设置缩放比例  
        // newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565  
      
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
      
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩  
      
        //return bitmap;  
    }  
    
    /**
     * 尺寸压缩
     * @param srcPath 图片路径
     * @param desPath  压缩后图片存储路径
     * 
     */
    public static void compressPicture(String srcPath, String desPath) {  
        FileOutputStream fos = null;  
        BitmapFactory.Options op = new BitmapFactory.Options();  
  
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        op.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, op);  
        op.inJustDecodeBounds = false;  
  
        // 缩放图片的尺寸  
        float w = op.outWidth;  
        float h = op.outHeight;  
        float hh = 1024f;//  
        float ww = 1024f;//  
        // 最长宽度或高度1024  
        float be = 1.0f;  
        if (w > h && w > ww) {  
            be = (float) (w / ww);  
        } else if (w < h && h > hh) {  
            be = (float) (h / hh);  
        }  
        if (be <= 0) {  
            be = 1.0f;  
        }  
        op.inSampleSize = (int) be;// 设置缩放比例,这个数字越大,图片大小越小.  
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        bitmap = BitmapFactory.decodeFile(srcPath, op);  
        int desWidth = (int) (w / be);  
        int desHeight = (int) (h / be);  
        bitmap = Bitmap.createScaledBitmap(bitmap, desWidth, desHeight, true);  
        try {  
            fos = new FileOutputStream(desPath);  
            if (bitmap != null) {  
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        }  
    }

}
