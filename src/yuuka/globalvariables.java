package yuuka;

public class globalvariables {
  public static boolean VERBOSE = false;
  public static boolean SILENT = false;
  public static boolean INGORE_LIB = false;
  public static boolean DISABLE_WARNINGS = false;

  public static String MAIN_CLASS = fileops.findMainClass("src");
  public static String PROGRAM_NAME = misc.guessJARName(MAIN_CLASS);
  public static String RELEASE_TARGET = "";
}