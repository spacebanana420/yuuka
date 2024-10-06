package yuuka.config;

import java.io.File;
import java.io.FileInputStream;
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
      + "\n\n#main_class=main\n#program_name=MyProgram\n#release_target=" + System.getProperty("java.version")
    ).getBytes();
    try {
      var stream = new FileOutputStream("build.yuuka");
      stream.write(cfg);
      stream.close();
      return true;
    }
    catch (IOException e) {return false;}
  }

  public static String[] readConfig() {
    var f = new File("build.yuuka");
    if (!f.isFile() || !f.canRead()) {return new String[0];}
    try {
      var is = new FileInputStream("build.yuuka");
      String config = new String(is.readAllBytes());
      is.close();

      int line_count = 0;
      for (int i = 0; i < config.length(); i++) {
        if (config.charAt(i) == '\n') {line_count++;}
      }
      String line = "";
      String[] final_cfg = new String[line_count];
      line_count = 0;
      for (int i = 0; i < config.length(); i++) {
        char c = config.charAt(i);
        if (c == '\n') {
          if (line != "") {
            final_cfg[line_count] = line;
            line_count++;
            line = "";
          }
          continue;
        }
        line += c;
      }
      if (line != "") {final_cfg[line_count] = line;}
      return final_cfg;
    }
    catch (IOException e) {return new String[0];}
  }

  public static void parseConfig(String[] config) {
    stdout.print_verbose("Found build file \"build.yuuka\", reading file.");

    setMainClass(config);
    setProgramName(config);
    setRelease(config);
  }

  private static void setMainClass(String[] config) {
    String value = getValue(config, "main_class");
    if (value == null) {return;}

    stdout.print_verbose("Setting main class to \"" + value + "\".");
    globalvariables.MAIN_CLASS = value;

  }

  private static void setProgramName(String[] config) {
    String value = getValue(config, "program_name");
    if (value == null) {return;}

    stdout.print_verbose("Setting program_name to \"" + value + "\".");
    globalvariables.PROGRAM_NAME = value;
  }

  private static void setRelease(String[] config) {
    String value = getValue(config, "release_version");
    if (value == null || !misc.isInt(value)) {return;}

    stdout.print_verbose("Setting Java target release to \"" + value + "\".");
    globalvariables.RELEASE_TARGET = value;
  }

  private static String getValue(String[] config, String setting) {
    String setting_line = null;
    for (int i = 0; i < config.length; i++) {
      String line = config[i];
      if (isSetting(line, setting)) {setting_line = line; break;}
    }
    if (setting_line == null) {return null;}
    String value = parseLine(setting, setting);
    if (value.length() == 0) {return null;}
    return value;
  }

  private static String parseLine(String line, String setting) {
    String buf = "";
    for (int i = setting.length(); i < line.length(); i++) {
      buf += line.charAt(i);
    }
    return buf;
  }

  private static boolean isSetting(String line, String setting) {
    if (line.length() <= setting.length() || line.charAt(setting.length()) != '=')
    {return false;}

    for (int i = 0; i < setting.length(); i++) {
      if (line.charAt(i) != setting.charAt(i)) {return false;}
    }
    return true;
  }
}
