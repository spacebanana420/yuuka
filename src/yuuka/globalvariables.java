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
  
  public static void setProgramName(String name) {
    if (name.contains(".jar")) {PROGRAM_NAME = name;}
    else {PROGRAM_NAME = name + ".jar";}
  }
}
