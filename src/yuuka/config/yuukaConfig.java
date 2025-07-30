package yuuka.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yuuka.global;
import yuuka.misc;
import yuuka.io.*;

public class yuukaConfig {
  public static void createConfig() {
    if (new File("build.yuuka").isFile()) {return;}
    byte[] cfg =
    (
      "Yuuka build config\nThe settings below override Yuuka's defaults\nCLI arguments override this config"
      + "\nTo enable a setting, uncomment it by removing the \"#\" at its start"
      + "\nEmpty or invalid settings are ignored"
      + "\n"
      + "\n# The path to the main class, for example yuuka/main for src/yuuka/main.java"
      + "\n#main_class=main"
      + "\n"
      + "\n# The name of the JAR to create during compilation and packaging"
      + "\n#jar_filename=MyProgram.jar"
      + "\n"
      + "\n# Alternative to main_class, tries to autodetect the main class of your project"
      + "\n#autodetect_main=false"
      + "\n"
      + "\n# To compile your project to run on a specific Java version and above, you can specify it here"
      + "\n#release_target=" + global.RUNTIME_JAVA_VERSION
      + "\n#source_target=" + global.RUNTIME_JAVA_VERSION
      + "\n#class_target=" + global.RUNTIME_JAVA_VERSION
      + "\n"
      + "\n# JAR files are ZIP archives and by default use compression, you can disable it for possibly faster program startup times"
      + "\n#disable_jar_compression=false"
      + "\n"
      + "\n# Disable Java compiler warnings"
      + "\n#disable_warnings=false"
      + "\n"
      + "\n#The path to GraalVM's native-image binary for executing projects into native binary executables"
      + "\n#graal_path=native-image"
      + "\n"
      + "\n# Specify a custom path for installing projects. The default path varies between operating systems"
      + "\n#install_path="
      + "\n"
      + "\n# Import your project's source when running tests so you can call its functions and classes"
      + "\n#tests_include_src=true"
      + "\n"
      + "\n# The default verbosity level of Yuuka."
      + "\n# 0 = quiet; 1 = normal; 2 = verbose; 3 = debug mode"
      + "\n#verbose_level=1"
    ).getBytes();
    try {
      var stream = new FileOutputStream("build.yuuka");
      stream.write(cfg);
      stream.close();
    }
    catch (IOException e) {stdout.error("Failed to create build.yuuka file!");}
  }

  public static void setConfigValues(String conf_path) {
    if (!new File(conf_path).isFile()) {return;}
    ConfOpt[] config = confreader.readConfig(conf_path);
    if (config == null) {
      stdout.error("Error loading project's build.yuuka file! Make sure the file has read permission!");
      return;
    }
    if (config.length == 0) {return;}
    var t = new Thread(() -> {
      setVerboseLevel(config);
      setMainClass(config);
      setProgramName(config);
      setRelease(config);
    }); t.start();
    
    setDisableWarnings(config);
    setDisableCompression(config);
    setGraalPath(config);
    setInstallPath(config);
    setSrcInclusion(config);
    
    try{t.join();}
    catch(InterruptedException e) {e.printStackTrace();}
  }

  private static void setVerboseLevel(ConfOpt[] config) {
    String value = confreader.getValue(config, "verbose_level");
    if (value == null) {return;}

    byte level = misc.toByte(value);
    if (level < 0 || level > 3) {
      stdout.error("Verbosity level " + value + " is incorrect, ignoring. Accepted values range between 0 and 3.");
      return;
    }
    global.PRINT_LEVEL = level;
  }

  private static void setSrcInclusion(ConfOpt[] config) {
    global.TESTS_INCLUDE_PROJECT = confreader.getBool(config, "tests_include_src");
  }

  private static void setInstallPath(ConfOpt[] config) {
    String value = confreader.getValue(config, "install_path");
    if (value == null) {return;}
    var f = new File(value);
    if (!f.isDirectory()) {
      stdout.error
      (
        "The installation path " + value + " is not real"
        + "\nDefaulting to " + global.INSTALL_PATH
      );
      return;
    }
    if (!f.canWrite()) {
      stdout.error
      (
        "Yuuka lacks permissions to write at the installation path " + value
        + "\nDefaulting to " + global.INSTALL_PATH
      );      
      return;
    }
    global.INSTALL_PATH = value;
  }

  private static void setMainClass(ConfOpt[] config) {
    String main_class = confreader.getValue(config, "main_class");
    if (main_class != null) {global.MAIN_CLASS = main_class; return;} //Manually-specified
    if (!confreader.getBool(config, "autodetect_main")){return;}
    
    stdout.print_verbose("Attempting to auto-detect main class");
    global.MAIN_CLASS = fileops.findMainClass(); //Automatically-specified
  }

  private static void setProgramName(ConfOpt[] config) {
    String value = confreader.getValue(config, "jar_filename");
    if (value != null) {global.setProgramName(value);}
  }

  private static void setRelease(ConfOpt[] config) {
    String value;
    
    value = confreader.getValue(config, "release_target");
    if (value != null && misc.isInt(value)) {
      global.setReleaseTarget(value);
    }
    value = confreader.getValue(config, "source_target");
    if (value != null && misc.isInt(value)) {
      global.setSourceTarget(value);
    }
    value = confreader.getValue(config, "class_target");
    if (value != null && misc.isInt(value)) {
      global.setClassTarget(value);
    }
  }
  
  private static void setDisableWarnings(ConfOpt[] config) {
    global.DISABLE_WARNINGS = confreader.getBool(config, "disable_warnings");
  }
  
  private static void setDisableCompression(ConfOpt[] config) {
    global.DISABLE_JAR_COMPRESSION = confreader.getBool(config, "disable_jar_compression");
  }

  private static void setGraalPath(ConfOpt[] config) {
    String value = confreader.getValue(config, "graal_path");
    if (value == null) {return;}
    var f = new File(value);
    if (!f.isFile() || !f.canExecute()) {
      stdout.error("The GraalVM binary path of " + value + " is not real or an executable!");
      return;
    }
    global.GRAAL_PATH = value; 
  }
}
