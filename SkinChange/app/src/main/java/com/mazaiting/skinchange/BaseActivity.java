package com.mazaiting.skinchange;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Method;

/**
 * Activity基类
 * Created by mazaiting on 2018/6/27.
 */

public abstract class BaseActivity extends AppCompatActivity {
  /**资源管理器*/
  protected AssetManager mAssetManager;
  /**资源*/
  protected Resources mResources;
  /**主题*/
  protected Theme mTheme;
  
  /**
   * 加载资源
   * @param dexPath dex路径
   */
  protected void loadResources(String dexPath) {
    try {
      AssetManager assetManager = AssetManager.class.newInstance();
      Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",String.class);
      addAssetPath.invoke(assetManager, dexPath);
      mAssetManager = assetManager;
    } catch (Exception e) {
      e.printStackTrace();
    }
    Resources superRes = super.getResources();
    superRes.getDisplayMetrics();
    superRes.getConfiguration();
    mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
    mTheme = mResources.newTheme();
    mTheme.setTo(super.getTheme());
  }
  
  @Override
  public AssetManager getAssets() {
    return mAssetManager == null ? super.getAssets() : mAssetManager;
  }
  
  @Override
  public Resources getResources() {
    return mResources == null ? super.getResources() : mResources;
  }
  
  @Override
  public Theme getTheme() {
    return mTheme == null ? super.getTheme() : mTheme;
  }
}
















