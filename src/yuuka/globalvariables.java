package yuuka;

public class globalvariables {
  //0 - silent    1 - normal    2 - verbose   3 - debug
  public static byte PRINT_LEVEL = 1;

  public static boolean INGORE_LIB = false;
  public static boolean DISABLE_WARNINGS = false;

  public static String GRAAL_PATH = "native-image";

  public static String MAIN_CLASS = fileops.findMainClass();
  public static String PROGRAM_NAME = misc.guessJARName(MAIN_CLASS);
  public static String RELEASE_TARGET = "";
  public static String INSTALL_PATH = installer.getInstallLocation();

  public static boolean TESTS_INCLUDE_PROJECT = false;
  
  public static final int RUNTIME_JAVA_VERSION = getRuntimeVersion();
  
  public static void setProgramName(String name) {
    if (name.contains(".jar")) {PROGRAM_NAME = name;}
    else {PROGRAM_NAME = name + ".jar";}
  }
  
  public static void setReleaseTarget(String version) {
    int version_num = intJavaVersion(version);
    if (version_num <= RUNTIME_JAVA_VERSION && version_num > 0) {RELEASE_TARGET = version;}
  }
  
  private static int getRuntimeVersion() {
    String version = System.getProperty("java.version");
    int len = version.length();
    String truncated = "";
    
    for (int i = 0; i < len; i++) {
      char c = version.charAt(i);
      if (c == '.') {break;}
      truncated += c;
    }
    return intJavaVersion(truncated);
  }
  
  //manual conversion to int prevents having to try catch
  private static int intJavaVersion(String version) {
    int len = version.length();
    int version_number = 0;
    char[] digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    for (int i = 0; i < len; i++) {
      double scale = Math.pow(10, len-i-1);
      char c = version.charAt(i);
      for (int d = 0; d < digits.length; d++) {if (c == digits[d]) {version_number += (int)(d * scale); break;}}
    }
    return version_number;
  }
}
