package com.mazaiting.reforceapk;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

/**
 * 代理Application
 * 步骤：
 *    ------- 在attachBaseContext -------
 *    1. 从当前APK中拿到classes.dex文件，拿到classes.dex文件的二进制数据
 *    2. 从dex的二进制数据中分离出解密后的apk，及so文件
 *    3. 反射获取主线程对象，并从中获取所有已加载的package信息，找到当前LoadApk的弱引用
 *    4. 创建一个新的DexClassLoader，从指定路径加载apk资源
 *    5. 加载被加密的apk主Activity入口
 *    -------  在onCreate方法   ----------
 *    6. 获取配置在清单文件的源apk的Application
 *    7. 替换原有的Application
 *    8. 调用被加密app的Application
 * Created by mazaiting on 2018/6/26.
 */

public class ProxyApplication extends Application {
  private static final String TAG = ProxyApplication.class.getSimpleName();
  /**
   * APP_KEY获取Activity入口
   */
  private static final String APP_KEY = "APPLICATION_CLASS_NAME";
  /**ActivityThread包名*/
  private static final String CLASS_NAME_ACTIVITY_THREAD = "android.app.ActivityThread";
  /**LoadedApk包名*/
  private static final String CLASS_NAME_LOADED_APK = "android.app.LoadedApk";
  /**
   * 源Apk路径
   */
  private String mSrcApkFilePath;
  /**
   * odex路径
   */
  private String mOdexPath;
  /**
   * lib路径
   */
  private String mLibPath;
  /**
   * 加载资源
   */
  protected AssetManager mAssetManager;
  protected Resources mResources;
  protected Resources.Theme mTheme;
  
  /**
   * 最先执行的方法
   */
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    Log.d(TAG, "attachBaseContext: --------onCreate");
    
    try {
      // 创建payload_odex和payload_lib文件夹，payload_odex中放置源apk即源dex，payload_lib放置so文件
      File odex = this.getDir("payload_odex", MODE_PRIVATE);
      File libs = this.getDir("payload_lib", MODE_PRIVATE);
      // 用于存放源apk释放出来的dex
      mOdexPath = odex.getAbsolutePath();
      // 用于存放源apk用到的so文件
      mLibPath = libs.getAbsolutePath();
      // 用于存放解密后的apk
      mSrcApkFilePath = mOdexPath + "/payload.apk";
      
      File srcApkFile = new File(mSrcApkFilePath);
      Log.d(TAG, "attachBaseContext: apk size: " + srcApkFile.length());
      
      // 第一次加载
      if (!srcApkFile.exists()) {
        Log.d(TAG, "attachBaseContext: isFirstLoading");
        srcApkFile.createNewFile();
        // 拿到dex文件
        byte[] dexData = this.readDexFileFromApk();
        // 取出解密后的apk放置在/payload.apk，及其so文件放置在payload_lib下
        this.splitPayLoadFromDex(dexData);
      }
      
      // 配置动态加载环境
      // 反射获取主线程对象，并从中获取所有已加载的package信息，找到当前LoadApk的弱引用
      // 获取主线程对象
      Object currentActivityThread = RefInvoke.invokeStaticMethod(
              CLASS_NAME_ACTIVITY_THREAD, "currentActivityThread",
              new Class[]{}, new Object[]{}
      );
      // 获取当前报名
      String packageName = this.getPackageName();
      // 获取已加载的所有包
      ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldObject(
              CLASS_NAME_ACTIVITY_THREAD, currentActivityThread,
              "mPackages"
      );
      // 获取LoadApk的弱引用
      WeakReference wr = (WeakReference) mPackages.get(packageName);
      
      // 创建一个新的DexClassLoader用于加载源Apk
      // 传入apk路径，dex释放路径，so路径，及父节点的DexClassLoader使其遵循双亲委托模型
      // 反射获取属性ClassLoader
      Object mClassLoader = RefInvoke.getFieldObject(
              CLASS_NAME_LOADED_APK, wr.get(), "mClassLoader"
      );
      // 定义新的DexClassLoader对象，指定apk路径，odex路径，lib路径
      DexClassLoader dLoader = new DexClassLoader(
              mSrcApkFilePath, mOdexPath, mLibPath, (ClassLoader) mClassLoader
      );
      // getClassLoader()等同于 (ClassLoader) RefInvoke.getFieldOjbect()
      // 但是为了替换掉父节点我们需要通过反射来获取并修改其值
      Log.d(TAG, "attachBaseContext: 父ClassLoader: " + mClassLoader);
      
      // 将父节点DexClassLoader替换
      RefInvoke.setFieldObject(
              CLASS_NAME_LOADED_APK,
              "mClassLoader",
              wr.get(),
              dLoader
      );
      
      Log.d(TAG, "attachBaseContext: 子ClassLoader: " + dLoader);
      
      try {
        // 尝试加载源apk的MainActivity
        Object actObj = dLoader.loadClass("com.mazaiting.reinforcement.MainActivity");
        Log.d(TAG, "attachBaseContext: SrcApk_MainActivity: " + actObj);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        Log.d(TAG, "attachBaseContext: LoadSrcActivityErr: " + Log.getStackTraceString(e));
      }
      
    } catch (IOException e) {
      e.printStackTrace();
      Log.d(TAG, "attachBaseContext: error: " + Log.getStackTraceString(e));
    }
    
  }
  
  /**
   * 从Dex中分割出资源
   *
   * @param dexData dex资源
   */
  private void splitPayLoadFromDex(byte[] dexData) throws IOException {
    // 获取dex数据长度
    int len = dexData.length;
    // 存储被加壳apk的长度
    byte[] dexLen = new byte[4];
    // 获取最后4个字节数据
    System.arraycopy(dexData, len - 4, dexLen, 0, 4);
    ByteArrayInputStream bais = new ByteArrayInputStream(dexLen);
    DataInputStream in = new DataInputStream(bais);
    // 获取被加密apk的长度
    int readInt = in.readInt();
    // 打印被加密apk的长度
    Log.d(TAG, "splitPayLoadFromDex: Integer.toHexString(readInt): " + Integer.toHexString(readInt));
    
    // 取出apk
    byte[] enSrcApk = new byte[readInt];
    // 将被加密apk内容复制到二进制数组中
    System.arraycopy(dexData, len - 4 - readInt, enSrcApk, 0, readInt);
    
    // 对源apk解密
    byte[] srcApk = decrypt(enSrcApk);
    
    // 写入源APK文件
    File file = new File(mSrcApkFilePath);
    try {
      FileOutputStream fos = new FileOutputStream(file);
      fos.write(srcApk);
      fos.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    // 分析源apk文件
    ZipInputStream zis = new ZipInputStream(
            new BufferedInputStream(
                    new FileInputStream(file)
            )
    );
    
    // 遍历压缩包
    while (true) {
      ZipEntry entry = zis.getNextEntry();
      // 判断是否有内容
      if (null == entry) {
        zis.close();
        break;
      }
      
      // 依次取出被加壳的apk用到的so文件，放到libPath中(data/data/包名/paytload_lib)
      String name = entry.getName();
      if (name.startsWith("lib/") && name.endsWith(".so")) {
        // 存储文件
        File storeFile = new File(
                mLibPath + "/" + name.substring(name.lastIndexOf('/'))
        );
        storeFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(storeFile);
        byte[] bytes = new byte[1024];
        while (true) {
          int length = zis.read(bytes);
          if (-1 == length) break;
          fos.write(bytes);
        }
        fos.flush();
        fos.close();
      }
      zis.closeEntry();
    }
    zis.close();
  }
  
  /**
   * 解密二进制
   *
   * @param srcData 二进制数
   * @return 解密后的二进制数据
   */
  private byte[] decrypt(byte[] srcData) {
    for (int i = 0; i < srcData.length; i++) {
      srcData[i] ^= 0xFF;
    }
    return srcData;
  }
  
  /**
   * 从ApK文件中获取DEX文件
   *
   * @return dex字节数组
   */
  private byte[] readDexFileFromApk() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ZipInputStream zis = new ZipInputStream(
            new BufferedInputStream(
                    new FileInputStream(this.getApplicationInfo().sourceDir)
            )
    );
    // 遍历压缩包
    while (true) {
      ZipEntry entry = zis.getNextEntry();
      if (null == entry) {
        zis.close();
        break;
      }
      // 获取dex文件
      if ("classes.dex".equals(entry.getName())) {
        byte[] bytes = new byte[1024];
        while (true) {
          int len = zis.read(bytes);
          if (len == -1) break;
          baos.write(bytes, 0, len);
        }
      }
      zis.closeEntry();
    }
    zis.close();
    return baos.toByteArray();
  }
  
  
  @Override
  public void onCreate() {
    super.onCreate();
    
    // 加载源apk资源
    loadResources(mSrcApkFilePath);
    
    Log.d(TAG, "onCreate: ---------------");
    
    // 获取配置在清单文件的源apk的Application路径
    String appClassName = null;
    try {
      // 创建应用信息对象
      ApplicationInfo ai = this.getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
      // 获取metaData数据
      Bundle bundle = ai.metaData;
      if (null != bundle && bundle.containsKey(APP_KEY)) {
        appClassName = bundle.getString(APP_KEY);
      } else {
        Log.d(TAG, "onCreate: have no application class name");
        return;
      }
    } catch (PackageManager.NameNotFoundException e) {
      Log.d(TAG, "onCreate: error: " + Log.getStackTraceString(e));
      e.printStackTrace();
    }
    
    // 获取当前Activity线程
    Object currentActivityThread = RefInvoke.invokeStaticMethod(CLASS_NAME_ACTIVITY_THREAD,
            "currentActivityThread", new Class[]{}, new Object[]{});
    // 获取绑定的应用
    Object mBoundApplication = RefInvoke.getFieldObject(CLASS_NAME_ACTIVITY_THREAD,
            currentActivityThread, "mBoundApplication");
    // 获取加载apk的信息
    Object loadedApkInfo = RefInvoke.getFieldObject(
            CLASS_NAME_ACTIVITY_THREAD + "$AppBindData",
            mBoundApplication, "info"
    );
    // 将LoadedApk中的ApplicationInfo设置为null
    RefInvoke.setFieldObject(CLASS_NAME_LOADED_APK, "mApplication", loadedApkInfo, null);
    // 获取currentActivityThread中注册的Application
    Object oldApplication = RefInvoke.getFieldObject(
            CLASS_NAME_ACTIVITY_THREAD, currentActivityThread, "mInitialApplication"
    );
    // 获取ActivityThread中所有已注册的Application, 并将当前壳Apk的Application从中移除
    ArrayList<Application> mAllApplications = (ArrayList<Application>) RefInvoke.getFieldObject(
            CLASS_NAME_ACTIVITY_THREAD, currentActivityThread, "mAllApplications"
    );
    mAllApplications.remove(oldApplication);
    // 从loadedApk中获取应用信息
    ApplicationInfo appInfoInLoadedApk = (ApplicationInfo) RefInvoke.getFieldObject(
            CLASS_NAME_LOADED_APK, loadedApkInfo, "mApplicationInfo"
    );
    // 从AppBindData中获取应用信息
    ApplicationInfo appInfoInAppBindData = (ApplicationInfo) RefInvoke.getFieldObject(
            CLASS_NAME_ACTIVITY_THREAD + "$AppBindData", mBoundApplication, "appInfo"
    );
    // 替换原来的Application
    appInfoInLoadedApk.className = appClassName;
    appInfoInAppBindData.className = appClassName;
    
    // 注册Application
    Application app = (Application) RefInvoke.invokeMethod(
            CLASS_NAME_LOADED_APK, "makeApplication", loadedApkInfo,
            new Class[]{boolean.class, Instrumentation.class},
            new Object[]{false, null}
    );
    // 替换ActivityThread中的Application
    RefInvoke.setFieldObject(CLASS_NAME_ACTIVITY_THREAD, "mInitialApplication",
            currentActivityThread, app);
    ArrayMap mProviderMap = (ArrayMap) RefInvoke.getFieldObject(
            CLASS_NAME_ACTIVITY_THREAD, currentActivityThread, "mProviderMap"
    );
  
    // 遍历
    for (Object providerClientRecord : mProviderMap.values()) {
      Object localProvider = RefInvoke.getFieldObject(
              CLASS_NAME_ACTIVITY_THREAD + "$ProviderClientRecord",
              providerClientRecord, "mLocalProvider"
      );
      RefInvoke.setFieldObject("android.content.ContentProvider", "mContext",
              localProvider, app);
    }
  
    Log.d(TAG, "onCreate: SrcApp: " + app);
    // 调用新的Application
    app.onCreate();
    
  }
  
  /**
   * 加载资源文件
   *
   * @param srcApkPath 源apk路径
   */
  private void loadResources(String srcApkPath) {
    // 创建一个AssetManager放置源apk资源
    try {
      AssetManager assetManager = AssetManager.class.newInstance();
      Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
      addAssetPath.invoke(assetManager, srcApkPath);
      mAssetManager = assetManager;
    } catch (Exception e) {
      Log.d(TAG, "loadResources: inject: loadResource error: " + Log.getStackTraceString(e));
      e.printStackTrace();
    }
    Resources res = super.getResources();
    res.getDisplayMetrics();
    res.getConfiguration();
    mResources = new Resources(mAssetManager, res.getDisplayMetrics(), res.getConfiguration());
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
  public Resources.Theme getTheme() {
    return mTheme == null ? super.getTheme() : mTheme;
  }
}





















