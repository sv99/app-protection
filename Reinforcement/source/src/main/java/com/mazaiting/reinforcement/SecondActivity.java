package com.mazaiting.reinforcement;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * 第二个页面
 */
public class SecondActivity extends AppCompatActivity {
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    TextView tv_content = new TextView(this);
    tv_content.setText("I am Second Activity");
    setContentView(tv_content);
  }
}
