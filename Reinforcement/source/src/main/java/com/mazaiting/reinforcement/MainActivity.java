package com.mazaiting.reinforcement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * 应用主入口
 */
public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Log.d(TAG, "onCreate: app: " + R.layout.activity_main);
    Log.d(TAG, "onCreate: app: "+ R.class.getClass().getSimpleName());
  }
  
  /**
   * 开启新页面
   * @param view
   */
  public void startSecondActivity(View view) {
    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
    startActivity(intent);
  }
}
