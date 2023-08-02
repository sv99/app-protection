package com.mazaiting.skinchange;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class MainActivity extends BaseActivity {
  private static final String TAG = "MainActivity";
  private TextView mTextView;
  private ImageView mImageView;
  private ClassLoader mClassLoader;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mTextView = findViewById(R.id.tv_show);
    mImageView = findViewById(R.id.iv_show);
  }
  
  /**
   * 切换主题1
   */
  public void changeThemeOne(View view) {
    // 获取缓存路径
    String fileDir = getCacheDir().getAbsolutePath();
    // 获取文件路径
    String filePath = fileDir + File.separator + "spinone-release.apk";
    mClassLoader = new DexClassLoader(filePath, fileDir, null, getClassLoader());
    loadResources(filePath);
    setContentOne();
  }
  
  /**
   * 切换主题2
   */
  public void changeThemeTwo(View view) {
    // 获取缓存路径
    String fileDir = getCacheDir().getAbsolutePath();
    // 获取文件路径
    String filePath = fileDir + File.separator + "spintwo-release.apk";
    mClassLoader = new DexClassLoader(filePath, fileDir, null, getClassLoader());
    loadResources(filePath);
    setContent();
  }
  
  /**
   * 设置主题内容
   */
  private void setContent() {
    try {
      Class clazz = mClassLoader.loadClass("com.mazaiting.UiUtil");
      // 设置TextView内容
      Method method = clazz.getMethod("getTextString", Context.class);
      String string = (String) method.invoke(null, this);
      mTextView.setText(string);
      // 设置ImageView背景
      method = clazz.getMethod("getImageDrawable",Context.class);
      Drawable drawable = (Drawable) method.invoke(null,this);
      mImageView.setBackground(drawable);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 设置主题1
   */
  private void setContentOne() {
    int stringId = getTextStringId();
    int drawableId = getImgDrawableId();
    mTextView.setText(stringId);
    mImageView.setBackgroundColor(drawableId);
    Log.d(TAG, "stringId: " + stringId + ",  drawableId: " + drawableId);
  }
  
  /**
   * 获取图片ID
   * @return
   */
  private int getImgDrawableId() {
    try {
      // "com.mazaiting.spinone.R$color" -- spinone module 下的R.color.img
      Class clazz = mClassLoader.loadClass("com.mazaiting.spinone.R$color");
      Field field = clazz.getField("img");
      int resId = (int) field.get(null);
      return resId;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
  
  /**
   * 获取字符串ID
   * @return
   */
  private int getTextStringId() {
    try {
      // "com.mazaiting.spinone.R$string" -- spinone module下的R.string.text
      Class clazz = mClassLoader.loadClass("com.mazaiting.spinone.R$string");
      Field field = clazz.getField("text");
      int resId = (int) field.get(null);
      return resId;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
  
  
}
