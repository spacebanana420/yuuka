package yuuka.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import yuuka.options;
import yuuka.misc;
import yuuka.io.stdout;

//The high-level implementation of yuuka's configuration
//Uses confreader to read a configuration file and ConfOpt to store settings
public class yuukaConfig {
  public static Config readConfig() {
    Config config = new Config("build.yuuka");
    if (config.isEmpty) {
      stdout.print_verbose("Project's build.yuuka file is empty or has no valid settings, ignoring it");
    }
    return config;
  }
  public static void createConfig() {
    if (new File("build.yuuka").isFile()) return;
    String runtimeVersion = options.getRuntimeVersion();
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
      + "\n#release_target=" + runtimeVersion
      + "\n#source_target=" + runtimeVersion
      + "\n#class_target=" + runtimeVersion
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

  public static byte getVerboseLevel(Config config) {
    String value = config.getValue("verbose_level");
    if (value == null) return 1;

    byte level = misc.toByte(value);
    if (level < 0 || level > 3) {
      stdout.error("Verbosity level " + value + " is incorrect, ignoring. Accepted values range between 0 and 3.");
      return 1;
    }
    return level;
  }

  public static String getGraalPath(Config config) {
    String value = config.getValue("graal_path");
    if (value == null) return null;
    var f = new File(value);
    if (!f.isFile() || !f.canExecute()) {
      stdout.error("The GraalVM binary path of " + value + " is not real or an executable!");
      return null;
    }
    return value; 
  }

  public static String getInstallPath(Config config) {
    String value = config.getValue("install_path");
    if (value == null) return null;
    var f = new File(value);
    if (!f.isDirectory()) {
      stdout.error ("The installation path " + value + " is not real");
      return null;
    }
    if (!f.canWrite()) {
      stdout.error("Yuuka lacks permissions to write at the installation path " + value);      
      return null;
    }
    return value;
  }
}
