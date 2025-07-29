package yuuka.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

class confreader {
  static ArrayList<String> readConfig_str(String path) {
    var f = new File(path);
    if (!f.isFile() || !f.canRead()) {return null;}
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
      
      return cfg_lines;
    }
    catch (IOException e) {return null;}
  }
  
  static ConfOpt[] readConfig(String path) {return ConfOpt.getOptions(readConfig_str(path));}

  static boolean getBool(ConfOpt[] config, String setting) {
    String value = getValue(config, setting);
    if (value == null) {return false;}
    value = value.toLowerCase();
    return value.equals("true") || value.equals("yes");
  }

  static String getValue(ConfOpt[] config, String setting) {
    for (ConfOpt option : config) {
      if (option.keysMatch(setting)) {return option.value;}
    }
    return null;
  }

  //used in libconf.java
  static String getValue(String line, String setting) {
    if (line == null || !isSetting(line, setting)) {return null;}
    
    String value = "";
    setting += "=";
    for (int i = setting.length(); i < line.length(); i++) {value += line.charAt(i);}
    value = value.trim();
    
    if (value.isEmpty()) {return null;}
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
