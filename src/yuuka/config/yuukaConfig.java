package yuuka.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yuuka.globalvariables;
import yuuka.misc;
import yuuka.stdout;

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
      + "\n#program_name=MyProgram.jar"
      + "\n#release_target=" + globalvariables.RUNTIME_JAVA_VERSION
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
    stdout.print_verbose("Found build file \"build.yuuka\", reading file.");

    setMainClass(config);
    setProgramName(config);
    setRelease(config);
    setGraalPath(config);
    setInstallPath(config);
    setSrcInclusion(config);
    setVerboseLevel(config);
  }

  private static void setVerboseLevel(String[] config) {
    String value = confreader.getValue(config, "verbose_level");
    if (value == null) {return;}

    byte level = misc.toByte(value);
    if (level < 0 || level > 3) {
      stdout.print_verbose("Verbosity level " + value + " is incorrect, ignoring. Accepted values range between 0 and 3.");
      return;
    }
    stdout.print_verbose("Setting verbosity level to " + value);
    globalvariables.PRINT_LEVEL = level;
  }

  private static void setSrcInclusion(String[] config) {
    boolean value = confreader.getBool(config, "tests_include_src");
    if (value) {stdout.print_verbose("Enabling project source inclusion into tests.");}
    globalvariables.TESTS_INCLUDE_PROJECT = value;
  }

  private static void setInstallPath(String[] config) {
    String value = confreader.getValue(config, "install_path");
    if (value == null) {return;}
    var f = new File(value);
    if (!f.isDirectory()) {
      stdout.print_verbose
      (
        "The installation path " + value + " is not real"
        + "\nDefaulting to " + globalvariables.INSTALL_PATH
      );
      return;
    }
    if (!f.canWrite()) {
      stdout.print_verbose
      (
        "Yuuka lacks permissions to write at the installation path " + value
        + "\nDefaulting to " + globalvariables.INSTALL_PATH
      );      
      return;
    }
    stdout.print_verbose("Setting installation path to \"" + value + "\".");
    globalvariables.INSTALL_PATH = value;
  }

  private static void setMainClass(String[] config) {
    String value = confreader.getValue(config, "main_class");
    if (value == null) {return;}

    stdout.print_verbose("Setting main class to \"" + value + "\".");
    globalvariables.MAIN_CLASS = value;

  }

  private static void setProgramName(String[] config) {
    String value = confreader.getValue(config, "program_name");
    if (value == null) {return;}

    stdout.print_verbose("Setting program_name to \"" + value + "\".");
    globalvariables.setProgramName(value);
  }

  private static void setRelease(String[] config) {
    String value;
    
    value = confreader.getValue(config, "release_version");
    if (value != null && misc.isInt(value)) {
      stdout.print_verbose("Setting Java target release to \"" + value + "\".");
      globalvariables.setReleaseTarget(value);
    }
    value = confreader.getValue(config, "source_version");
    if (value != null && misc.isInt(value)) {
      stdout.print_verbose("Setting Java source target version to \"" + value + "\".");
      globalvariables.setSourceTarget(value);
    }
    value = confreader.getValue(config, "class_version");
    if (value != null && misc.isInt(value)) {
      stdout.print_verbose("Setting Java class target version to \"" + value + "\".");
      globalvariables.setClassTarget(value);
    }
  }

  private static void setGraalPath(String[] config) {
    String value = confreader.getValue(config, "graal_path");
    if (value == null) {return;}
    var f = new File(value);
    if (!f.isFile() || !f.canExecute()) {return;}

    stdout.print_verbose("Setting GraalVM's \"native-image\" path to \"" + value + "\".");
    globalvariables.GRAAL_PATH = value; 
  }
}
