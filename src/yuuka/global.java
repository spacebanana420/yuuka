package yuuka;

import yuuka.jdk.installer;
import yuuka.io.fileops;
import java.io.File;

//Global variables, used in many other source files
public class global {
  //0 - silent    1 - normal    2 - verbose   3 - debug
  public static byte PRINT_LEVEL = 1;

  public static boolean INGORE_LIB = false;
  public static boolean DISABLE_WARNINGS = false;
  public static boolean DISABLE_JAR_COMPRESSION = false;

  public static String GRAAL_PATH = "native-image";

  public static String MAIN_CLASS = null;
  public static String PROGRAM_NAME = "release.jar";
  
  public static String RELEASE_TARGET = null;
  public static String SOURCE_TARGET = null;
  public static String CLASS_TARGET = null;
  
  public static String INSTALL_PATH = installer.getInstallLocation();
  public static boolean TESTS_INCLUDE_PROJECT = false;
  public static final int RUNTIME_JAVA_VERSION = getRuntimeVersion();
  
  public static boolean mainIsDefined() {
    return MAIN_CLASS != null;
  }
  
  public static boolean mainIsCorrect() {
    return new File("src/"+MAIN_CLASS+".java").isFile();
  }
  
  public static void setProgramName(String name) {
    if (name.contains(".jar")) {PROGRAM_NAME = name;}
    else {PROGRAM_NAME = name + ".jar";}
  }
  
  public static void setReleaseTarget(String version) {
    int version_num = intJavaVersion(version);
    if (version_num <= RUNTIME_JAVA_VERSION && version_num > 0) {RELEASE_TARGET = version;}
  }
  public static void setSourceTarget(String version) {
    int version_num = intJavaVersion(version);
    if (version_num <= RUNTIME_JAVA_VERSION && version_num > 0) {SOURCE_TARGET = version;}
  }
  public static void setClassTarget(String version) {
    int version_num = intJavaVersion(version);
    if (version_num <= RUNTIME_JAVA_VERSION && version_num > 0) {CLASS_TARGET = version;}
  }
  
  private static int getRuntimeVersion() {
    String version = System.getProperty("java.version");
    StringBuilder truncated = new StringBuilder();
    
    for (int i = 0; i < version.length(); i++) {
      char c = version.charAt(i);
      if (c == '.') {break;}
      truncated.append(c);
    }
    return intJavaVersion(truncated.toString());
  }
  
  //manual conversion to int prevents having to try catch
  private static int intJavaVersion(String version) {
    int len = version.length();
    byte[] ver_bytes = version.getBytes();
    int version_number = 0;
    int zero_value = (int)'0';
    
    for (int i = 0; i < len; i++) {
      double scale = Math.pow(10, len-i-1);
      int digit = ver_bytes[i]-zero_value;
      version_number += (digit * scale);
    }
    return version_number;
  }
}
