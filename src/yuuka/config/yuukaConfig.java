package yuuka.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yuuka.global;
import yuuka.misc;
import yuuka.io.stdout;

public class yuukaConfig {
  public static boolean createConfig() {
    if (new File("build.yuuka").isFile()) {return true;}
    byte[] cfg =
    (
      "Yuuka build config\nThe settings below override Yuuka's defaults\nCLI arguments override this config"
      + "\nTo enable a setting, uncomment it by removing the \"#\" at its start"
      + "\nEmpty or invalid settings are ignored"
      + "\n"
      + "\n#main_class=main"
      + "\n#jar_filename=MyProgram.jar"
      + "\n#release_target=" + global.RUNTIME_JAVA_VERSION
      + "\n#source_target=" + global.RUNTIME_JAVA_VERSION
      + "\n#class_target=" + global.RUNTIME_JAVA_VERSION
      + "\n#disable_jar_compression=false"
      + "\n#disable_warnings=false"
      + "\n#graal_path=native-image"
      + "\n#install_path="
      + "\n#tests_include_src=true"
      + "\n#verbose_level=1"
    ).getBytes();
    try {
      var stream = new FileOutputStream("build.yuuka");
      stream.write(cfg);
      stream.close();
      return true;
    }
    catch (IOException e) {return false;}
  }

  public static void parseConfig(String conf_path) {
    String[] conf = confreader.readConfig(conf_path);
    parseConfig(conf);
  }

  public static void parseConfig(String[] config) {
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
  
  private static void warnError(String message) {
    System.out.println("yuuka.build error: " + message);
  }

  private static void setVerboseLevel(String[] config) {
    String value = confreader.getValue(config, "verbose_level");
    if (value == null) {return;}

    byte level = misc.toByte(value);
    if (level < 0 || level > 3) {
      warnError("Verbosity level " + value + " is incorrect, ignoring. Accepted values range between 0 and 3.");
      return;
    }
    global.PRINT_LEVEL = level;
  }

  private static void setSrcInclusion(String[] config) {
    boolean value = confreader.getBool(config, "tests_include_src");
    global.TESTS_INCLUDE_PROJECT = value;
  }

  private static void setInstallPath(String[] config) {
    String value = confreader.getValue(config, "install_path");
    if (value == null) {return;}
    var f = new File(value);
    if (!f.isDirectory()) {
      warnError
      (
        "The installation path " + value + " is not real"
        + "\nDefaulting to " + global.INSTALL_PATH
      );
      return;
    }
    if (!f.canWrite()) {
      warnError
      (
        "Yuuka lacks permissions to write at the installation path " + value
        + "\nDefaulting to " + global.INSTALL_PATH
      );      
      return;
    }
    global.INSTALL_PATH = value;
  }

  private static void setMainClass(String[] config) {
    String value = confreader.getValue(config, "main_class");
    if (value == null) {return;}
    global.MAIN_CLASS = value;
  }

  private static void setProgramName(String[] config) {
    String value = confreader.getValue(config, "jar_filename");
    if (value == null) {return;}
    global.setProgramName(value);
  }

  private static void setRelease(String[] config) {
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
  
  private static void setDisableWarnings(String[] config) {
    boolean value = confreader.getBool(config, "disable_warnings");
    global.DISABLE_WARNINGS = value;
  }
  
  private static void setDisableCompression(String[] config) {
    boolean value = confreader.getBool(config, "disable_jar_compression");
    global.DISABLE_JAR_COMPRESSION = value;
  }

  private static void setGraalPath(String[] config) {
    String value = confreader.getValue(config, "graal_path");
    if (value == null) {return;}
    var f = new File(value);
    if (!f.isFile() || !f.canExecute()) {
      warnError("The GraalVM binary path of " + value + " is not real or an executable!");
      return;
    }
    global.GRAAL_PATH = value; 
  }
}
