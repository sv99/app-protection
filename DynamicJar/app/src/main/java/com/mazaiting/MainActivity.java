package com.mazaiting;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.mazaiting.dynamicjar.Dynamic;
import com.mazaiting.dynamicjar.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;
import dalvik.system.InMemoryDexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {
  /**缓存文件夹*/
  private File mCacheDir;
  /**目标文件*/
  private String mInternalPath;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    copyFile();
    setContentView(R.layout.activity_main);
    
    printMoreClassLoader();
  }
  
  private static final String TAG = "MainActivity";
  /**
   * 打印各种类加载器
   */
  private void printMoreClassLoader() {
    Log.d(TAG, "Context类的类加载器：" + Context.class.getClassLoader());
    Log.d(TAG, "ListView类的类加载器：" + ListView.class.getClassLoader());
    Log.d(TAG, "应用程序默认的类加载器：" + getClassLoader());
    Log.d(TAG, "系统类加载器：" + ClassLoader.getSystemClassLoader());
    Log.d(TAG, "系统类加载器和Context类的类加载器是否相等：" + (Context.class.getClassLoader() ==ClassLoader.getSystemClassLoader()));
    Log.d(TAG, "系统类加载器和应用程序默认加载器是否相等：" + (getClassLoader() == ClassLoader.getSystemClassLoader()));
  
    Log.d(TAG, "================================================");
    
    Log.d(TAG, "打印应用程序默认加载器的委派机制：");
    ClassLoader classLoader = getClassLoader();
    while (null != classLoader) {
      Log.d(TAG, "类加载器： " + classLoader);
      classLoader = classLoader.getParent();
    }
  
    Log.d(TAG, "================================================");
    
    Log.d(TAG, "打印系统加载器的委派机制：");
    classLoader = ClassLoader.getSystemClassLoader();
    while (null != classLoader) {
      Log.d(TAG, "类加载器：" + classLoader);
      classLoader = classLoader.getParent();
    }
    
  }
  
  /**
   * 拷贝文件
   */
  private void copyFile() {
    // 获取缓存路径
    mCacheDir = FileUtil.getCacheDir(getApplicationContext());
    // 获取dex文件路径
    mInternalPath = mCacheDir.getAbsolutePath() + File.separator + "dynamic.jar";
    File file = new File(mInternalPath);
    if (!file.exists()) {
      try {
        file.createNewFile();
        FileUtil.copyFiles(this, "dynamic_dex.jar", file);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  /**
   * 动态加载按钮
   */
  public void loadDex(View view) {
    loadDex();
  }
  
  /**
   * 动态加载
   */
  private void loadDex() {
    // 新建DexClassLoader
    // 第一个参数：dex压缩文件的路径
    // 第二个参数：dex解压缩后的文件路径
    // 第三个参数：C/C++依赖的本地库文件
    // 第四个参数：上一级的类加载器
    DexClassLoader dexClassLoader = new DexClassLoader(
            mInternalPath, mCacheDir.getAbsolutePath(), null, getClassLoader());
    try {
      // 加载的类名为jar文件中的完整类名
      Class clazz = dexClassLoader.loadClass("com.mazaiting.dynamicjar.impl.DynamicImpl");
      Dynamic dynamic = (Dynamic) clazz.newInstance();
      if (null != dynamic) {
        Toast.makeText(this, dynamic.say(), Toast.LENGTH_SHORT).show();
      }
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      e.printStackTrace();
    }
  }
}
