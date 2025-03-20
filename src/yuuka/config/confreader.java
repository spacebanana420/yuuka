package yuuka.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

class confreader {
  static String[] readConfig(String path) {
    var f = new File(path);
    if (!f.isFile() || !f.canRead()) {return new String[0];}
    try {
      var is = new FileInputStream(path);
      String config = new String(is.readAllBytes());
      is.close();

      var cfg_lines = new ArrayList<String>(); 
      
      String line = "";
      for (int i = 0; i < config.length(); i++) {
        char c = config.charAt(i);
        if (c == '\n') {
          if (isLineValid(line)) {cfg_lines.add(line);}
          line = "";
          continue;
        }
        line += c;
      }
      if (isLineValid(line)) {cfg_lines.add(line);}
      return cfg_lines.toArray(new String[0]);
    }
    catch (IOException e) {return new String[0];}
  }

  static boolean getBool(String[] config, String setting) {
    String value = getValue(config, setting);
    if (value == null) {return false;}
    value = value.toLowerCase();
    return value.equals("true") || value.equals("yes");
  }

  static String getValue(String[] config, String setting) {
    String setting_line = null;
    for (int i = 0; i < config.length; i++) {
      String line = config[i];
      if (isSetting(line, setting)) {setting_line = line; break;}
    }
    return getValue(setting_line, setting, false);
  }

  static String getValue(String line, String setting, boolean verifySetting) {
    boolean isSettingValid = !verifySetting || isSetting(line, setting);
    if (line == null || !isSettingValid) {return null;}
    
    String value = "";
    setting += "=";
    for (int i = setting.length(); i < line.length(); i++) {value += line.charAt(i);}
    value = value.trim();
    
    if (value.length() == 0) {return null;}
    return value;
  }

  private static boolean isLineValid(String line) {return line.length() > 0 && line.charAt(0) != '#';}

  private static boolean isSetting(String line, String setting) {
    if (line.length() <= setting.length() || line.charAt(setting.length()) != '=')
    {return false;}

    for (int i = 0; i < setting.length(); i++) {
      if (line.charAt(i) != setting.charAt(i)) {return false;}
    }
    return true;
  }
}
