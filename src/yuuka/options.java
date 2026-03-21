package yuuka;

import yuuka.jdk.installer;
import yuuka.io.*;
import yuuka.cli.cli;
import yuuka.cli.parser;
import yuuka.config.yuukaConfig;
import yuuka.config.Config;

import java.io.File;

//Retrieves Yuuka's settings by reading from the config file build.yuuka then the CLI
//CLI arguments take priority over the config file
public class options {
  //Global variables are settings that are commonly used in multiple places in Yuuka
  public static byte PRINT_LEVEL = 1;
  public static String MAIN_CLASS = null;
  public static String JAR_FILENAME = "release.jar";
  public static boolean IGNORE_LIB;

  private static String[] args;
  private static int parse_break;
  private static Config config;
  
  public static void setGlobalValues(String[] input_args, int input_parse_break, Config input_config) {
    args = input_args;
    parse_break = input_parse_break;
    config = input_config;
    
    MAIN_CLASS = getMainClass();
    PRINT_LEVEL = getVerboseLevel();
    JAR_FILENAME = getJARFilename();
    IGNORE_LIB = ignoreLib();
  }
  
  public static boolean mainIsDefined() {return MAIN_CLASS != null;}
  public static boolean mainIsCorrect() {return new File("src/"+MAIN_CLASS+".java").isFile();}

  //Converts main class path from slashes to dots
  //Dots are the standard Java way and the only way that works with GraalVM's native-image
  public static String mainClassDot() {return MAIN_CLASS.replaceAll("/", ".");}
  
  public static boolean ignoreLib() {return parser.hasArgument(args, parse_break, "-i", "--ignore-lib");}
  public static boolean disableWarnings() {
    return parser.hasArgument(args, parse_break, "-nw", "--no-warnings") || config.getBool("disable_warnings");
  }
  public static boolean disableJARCompression() {
    return parser.hasArgument(args, parse_break, "-0", "--no-compress") || config.getBool("disable_jar_compression");
  }
  public static boolean staticBinary() {return parser.hasArgument(args, parse_break, "--static");}
  public static boolean staticBinary_nolibc() {return parser.hasArgument(args, parse_break, "--static-nolibc");}

  public static boolean includeSource() {
    return parser.hasArgument(args, parse_break, "-is", "--include-src") || config.getBool("tests_include_src");
  }

  public static byte getVerboseLevel() {
    if (parser.hasArgument(args, parse_break, "-d", "--debug")) return 3;
    else if (parser.hasArgument(args, parse_break, "-v", "--verbose")) return 2;
    else if (parser.hasArgument(args, parse_break, "-s", "--silent")) return 0;
    return yuukaConfig.getVerboseLevel(config);
  }
  
  public static String getMainClass() {
    String mainClass = cli.getMainClass(args, parse_break);
    if (mainClass != null) return mainClass;

    mainClass = config.getValue("main_class");
    if (mainClass != null) return mainClass; //Manually-specified main class
    if (!config.getBool("autodetect_main", true)) return null;
    
    stdout.print_verbose("Attempting to auto-detect main class");
    return fileops.findMainClass(); //Automatically-specified
  }

  public static String getJARFilename() {
    String filename = cli.getOutputFilename(args, parse_break);
    if (filename != null) {
      return filename.contains(".jar") ? filename : filename + ".jar";
    }
    filename = config.getValue("jar_filename");
    if (filename == null) return "release.jar";
    return filename.contains(".jar") ? filename : filename + ".jar";
  }

  public static String getGraalPath() {
    String path = cli.getGraalPath(args, parse_break);
    if (path != null) return path;
    path = yuukaConfig.getGraalPath(config);
    if (path == null) return "native-image";
    return path;
  }

  //todo: clampversion should check for failed toInt conversion
  public static String getJavaVersion() {
    String version = parser.getArgumentValue(args, parse_break, "-r", "--releaase");
    if (version != null && misc.isInt(version)) return version;
    
    version = config.getValue("release_target");
    if (version != null && misc.isInt(version)) return version;
    return null;
  }

  public static String getSourceVersion() {
    String version = parser.getArgumentValue(args, parse_break, "-src", "--source");
    if (version != null && misc.isInt(version)) return version;
    
    version = config.getValue("source_target");
    if (version != null && misc.isInt(version)) return version;
    return null;
  }

  public static String getClassVersion() {
    String version = parser.getArgumentValue(args, parse_break, "-t", "--target");
    if (version != null && misc.isInt(version)) return version;
    
    version = config.getValue("class_target");
    if (version != null && misc.isInt(version)) return version;
    return null;
  }
  
  public static String getInstallPath() {
    String path = cli.getInstallPath(args, parse_break);
    if (path != null) return path;
    path = yuukaConfig.getInstallPath(config);
    if (path != null) return path;
    return installer.getInstallLocation();
  }

  public static String getRuntimeVersion() {
    String version = System.getProperty("java.version");
    StringBuilder truncated = new StringBuilder();
    
    for (int i = 0; i < version.length(); i++) {
      char c = version.charAt(i);
      if (c == '.') break;
      truncated.append(c);
    }
    return truncated.toString();
  }
}
