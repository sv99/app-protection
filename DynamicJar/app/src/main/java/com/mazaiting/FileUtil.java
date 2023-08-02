package com.mazaiting;

import android.content.Context;
import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件操作工具类
 * Created by mazaiting on 2018/6/26.
 */

public class FileUtil {
  /**
   * 拷贝文件
   * @param context 上下文
   * @param fileName 文件名
   * @param desFile 目标文件
   */
  public static void copyFiles(Context context, String fileName, File desFile) {
    InputStream is = null;
    OutputStream os = null;
    try {
      // 打开Assets文件夹下的文件
      is = context.getApplicationContext().getAssets().open(fileName);
      // 创建输出流
      os = new FileOutputStream(desFile.getAbsoluteFile());
      byte[] bytes = new byte[1024];
      int len;
      while ((len = is.read(bytes)) != -1) {
        os.write(bytes, 0, len);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeStream(is);
      closeStream(os);
    }
  }
  
  /**
   * 关闭流
   * @param closeable 流接口
   */
  private static void closeStream(Closeable closeable) {
    if (null != closeable) {
      try {
        closeable.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * 获取文件缓存路径
   * @param context 上下文
   * @return
   */
  public static File getCacheDir(Context context) {
    File cache;
    // 判断是否有内存卡
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      cache = context.getExternalCacheDir();
    } else {
      cache = context.getCacheDir();
    }
    if (!cache.exists()) {
      cache.mkdirs();
    }
    return cache;
  }
}
