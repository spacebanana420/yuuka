package yuuka.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class confreader {
  public static String[] readConfig(String path) {
    var f = new File(path);
    if (!f.isFile() || !f.canRead()) {return new String[0];}
    try {
      var is = new FileInputStream(path);
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

  public static boolean getBool(String[] config, String setting) {
    String value = getValue(config, setting);
    if (value == null) {return false;}
    value = value.toLowerCase();
    return value.equals("true") || value.equals("yes");
  }

  public static String getValue(String[] config, String setting) {
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

  public static String parseLine(String line, String setting) {
    String buf = "";
    for (int i = setting.length(); i < line.length(); i++) {
      buf += line.charAt(i);
    }
    return buf;
  }

  public static boolean isSetting(String line, String setting) {
    if (line.length() <= setting.length() || line.charAt(setting.length()) != '=')
    {return false;}

    for (int i = 0; i < setting.length(); i++) {
      if (line.charAt(i) != setting.charAt(i)) {return false;}
    }
    return true;
  }

  public static String truncateVersion(String ver) {
    String truncated = "";
    for (int i = 0; i < ver.length(); i++) {
      char c = ver.charAt(i);
      if (c == '.') {return truncated;}
      truncated += c;
    }
    return truncated;
  }
}
