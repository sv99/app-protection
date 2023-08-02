package com.mazaiting.reforceapk;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射类
 * Created by mazaiting on 2018/6/26.
 */

public class RefInvoke {
  /**
   * 反射执行类的静态函数（public）
   *
   * @param className  类名
   * @param methodName 方法名
   * @param pareTypes  函数的参数类型
   * @param pareValues 调用函数时传入的参数
   * @return
   */
  public static Object invokeStaticMethod(String className, String methodName, Class[] pareTypes, Object[] pareValues) {
    try {
      Class objClass = Class.forName(className);
      Method method = objClass.getMethod(methodName, pareTypes);
      return method.invoke(null, pareValues);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * 反射执行的函数（public）
   *
   * @param className  类名
   * @param methodName 方法名
   * @param obj        对象
   * @param pareTypes  参数类型
   * @param pareValues 调用方法传入的参数
   * @return
   */
  public static Object invokeMethod(String className, String methodName, Object obj, Class[] pareTypes, Object[] pareValues) {
    try {
      Class objClass = Class.forName(className);
      Method method = objClass.getMethod(methodName, pareTypes);
      return method.invoke(obj, pareValues);
    } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * 反射得到类的属性（包括私有和保护）
   *
   * @param className 类名
   * @param obj       对象
   * @param fieldName 属性名
   * @return
   */
  public static Object getFieldObject(String className, Object obj, String fieldName) {
    try {
      Class objClass = Class.forName(className);
      Field field = objClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(obj);
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * 反射得到类的静态属性（包括私有和保护）
   *
   * @param className 类名
   * @param fieldName 属性名
   * @return
   */
  public static Object getStaticFieldObject(String className, String fieldName) {
    try {
      Class objClass = Class.forName(className);
      Field field = objClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field.get(null);
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * 设置类的属性（包括私有和保护）
   *
   * @param className  类名
   * @param fieldName  属性名
   * @param obj        对象
   * @param fieldValue 字段值
   */
  public static void setFieldObject(String className, String fieldName, Object obj, Object fieldValue) {
    try {
      Class objClass = Class.forName(className);
      Field field = objClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(obj, fieldValue);
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 设置类的静态属性（包括私有和保护）
   *
   * @param className  类名
   * @param fieldName  属性名
   * @param fieldValue 属性值
   */
  public static void setStaticObject(String className, String fieldName, String fieldValue) {
    try {
      Class objClass = Class.forName(className);
      Field field = objClass.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(null, fieldValue);
    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
  
}
