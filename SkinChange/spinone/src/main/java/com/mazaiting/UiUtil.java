package com.mazaiting;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.mazaiting.spinone.R;

/**
 * Created by mazaiting on 2018/6/27.
 */

public class UiUtil {
  /**
   * 获取字符串
   */
  public static String getTextString(Context ctx) {
    return ctx.getResources().getString(R.string.text);
  }
  
  /**
   * 获取图片
   */
  public static Drawable getImageDrawable(Context ctx) {
    return ctx.getResources().getDrawable(R.color.img);
  }
  
  public static int getTextStringId(){
    return R.string.text;
  }
  
  public static int getImageDrawableId(){
    return R.color.img;
  }
}
