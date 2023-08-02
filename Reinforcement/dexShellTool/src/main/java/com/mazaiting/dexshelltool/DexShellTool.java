package com.mazaiting.dexshelltool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

/**
 * 加密APK
 * 步骤：
 *    1. 获取待加密的APK, 并对其二进制数据加密
 *    2. 取出壳DEX, 并获取其二进制数据
 *    3. 计算拼接后的DEX应用的大小, 并创建二进制数组
 *    4. 依次将解壳DEX,加密后的源APK,加密后的源APK大小,拼接出新的DEX
 *    5. 修改DEX的头，fileSize字段
 *    6. 修改DEX的头，SHA1字段
 *    7. 修改DEX的头，CheckNum字段
 *    8. 输出新的DEX文件
 */
public class DexShellTool {
  public static void main(String[] args) {
    try {
      // 需要加壳的源APK, 以二进制形式读取，并进行加密处理
      File srcApkFile = new File("force/source.apk");
      System.out.println("apk path: " + srcApkFile.getAbsolutePath());
      System.out.println("apk size: " + srcApkFile.length());
      // 加密并返回元apk数据
      byte[] enSrcApkArray = encrypt(readFileBytes(srcApkFile));
      
      // 需要解壳的dex, 以二进制形式读出dex
      File unShellDexFile = new File("force/shell.dex");
      byte[] unShellDexArray = readFileBytes(unShellDexFile);
      
      // 将源APK长度和需要解壳的DEX长度相加并加上存放源APK大小的四位得到总长度
      int enSrcApkLen = enSrcApkArray.length;
      int unShellDexLen = unShellDexArray.length;
      // 多出的四位存放加密后的dex长度
      int totalLen = enSrcApkLen + unShellDexLen + 4;
      
      // 依次将解壳DEX,加密后的源APK,加密后的源APK大小,拼接出新的DEX
      byte[] newDex = new byte[totalLen];
      // 复制加壳数据
      System.arraycopy(unShellDexArray, 0, newDex, 0, unShellDexLen);
      // 复制加密apk数据
      System.arraycopy(enSrcApkArray, 0, newDex, unShellDexLen, enSrcApkLen);
      // 赋值加壳后的dex大小
      System.arraycopy(intToByte(enSrcApkLen), 0, newDex, totalLen - 4, 4);
      
      // 修改DEX file size 文件头
      fixFileSizeHeader(newDex);
      // 修改DEX SHA1 文件头
      fixSHA1Header(newDex);
      // 修改DEX CheckNum文件头
      fixCheckSumHeader(newDex);
      
      // 写出新的DEX
      String str = "force/classes.dex";
      File file = new File(str);
      if (!file.exists()) {
        file.createNewFile();
      }
      FileOutputStream fos = new FileOutputStream(str);
      fos.write(newDex);
      fos.flush();
      fos.close();
      
    } catch (IOException | NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * 修改DEX头，CheckSum校验码
   *
   * @param dexBytes 要修改的二进制数据
   */
  private static void fixCheckSumHeader(byte[] dexBytes) {
    Adler32 adler = new Adler32();
    // 从12到文件末尾计算校验码
    adler.update(dexBytes, 12, dexBytes.length - 12);
    long value = adler.getValue();
    int va = (int) value;
    byte[] newCs = intToByte(va);
    // 高低位互换位置
    byte[] reCs = new byte[4];
    for (int i = 0; i < 4; i++) {
      reCs[i] = newCs[newCs.length - 1 - i];
      System.out.println("fixCheckSumHeader:" + Integer.toHexString(newCs[i]));
    }
    // 校验码赋值（8-11）
    System.arraycopy(reCs, 0, dexBytes, 8, 4);
    System.out.println("fixCheckSumHeader:" + Long.toHexString(value));
  }
  
  /**
   * 修改DEX头， sha1值
   *
   * @param dexBytes 要修改的二进制数组
   */
  private static void fixSHA1Header(byte[] dexBytes) throws NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("SHA-1");
    // 从32位到结束计算sha-1
    md.update(dexBytes, 32, dexBytes.length - 32);
    byte[] newDt = md.digest();
    // 修改sha-1值（12-21）
    System.arraycopy(newDt, 0, dexBytes, 12, 20);
    // 输出sha-1值
    StringBuilder hexStr = new StringBuilder();
    for (byte aNewDt : newDt) {
      hexStr.append(Integer.toString((aNewDt & 0xFF) + 0x100, 16).substring(1));
    }
    System.out.println("fixSHA1Header:" + hexStr.toString());
  }
  
  /**
   * 修改DEX头， file_size值
   *
   * @param dexBytes 二进制数据
   */
  private static void fixFileSizeHeader(byte[] dexBytes) {
    // 新文件长度
    byte[] newFs = intToByte(dexBytes.length);
    System.out.println("fixFileSizeHeader: " + Integer.toHexString(dexBytes.length));
    byte[] reFs = new byte[4];
    // 高低位换位置
    for (int i = 0; i < 4; i++) {
      reFs[i] = newFs[newFs.length - 1 - i];
      System.out.println("fixFileSizeHeader: " + Integer.toHexString(newFs[i]));
    }
    // 修改32-35
    System.arraycopy(reFs, 0, dexBytes, 32, 4);
  }
  
  /**
   * int 转 byte[]
   *
   * @param number 整型
   * @return 返回字节数组
   */
  private static byte[] intToByte(int number) {
    byte[] b = new byte[4];
    for (int i = 3; i >= 0; i--) {
      b[i] = (byte) (number % 256);
      number >>= 8;
    }
    return b;
  }
  
  /**
   * 加密二进制数据
   *
   * @param srcData 字节数组
   * @return 加密后的二进制数组
   */
  private static byte[] encrypt(byte[] srcData) {
    for (int i = 0; i < srcData.length; i++) {
      srcData[i] ^= 0xFF;
    }
    return srcData;
  }
  
  /**
   * 以二进制读出文件内容
   *
   * @param file 文件
   * @return 二进制数据
   */
  private static byte[] readFileBytes(File file) throws IOException {
    byte[] bytes = new byte[1024];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    FileInputStream fis = new FileInputStream(file);
    while (true) {
      int len = fis.read(bytes);
      if (-1 == len) break;
      baos.write(bytes, 0, len);
    }
    byte[] byteArray = baos.toByteArray();
    fis.close();
    baos.close();
    return byteArray;
  }
}
