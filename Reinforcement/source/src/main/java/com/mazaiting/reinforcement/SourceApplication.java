package com.mazaiting.reinforcement;

import android.app.Application;
import android.util.Log;

/**
 * 源Apk的全局Application
 * Created by mazaiting on 2018/6/26.
 */

public class SourceApplication extends Application {
  private static final String TAG = SourceApplication.class.getSimpleName();
  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate: --------");
  }
}
