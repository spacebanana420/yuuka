package yuuka.config;

import yuuka.io.fileops;
import java.util.ArrayList;

//Stores all the settings from a configuration file such as build.yuuka
//Each setting follows the style of key=value
//Comments are represented with character "#"
public class Config {
  public boolean isEmpty = true;
  private String[] keys = null;
  private String[] values = null;
  
  public Config(String filePath) {
    ArrayList<String> config = fileops.readFile(filePath);
    if (config == null) return;

    var keys = new ArrayList<String>();
    var values = new ArrayList<String>();
    //Get all the settings from each line
    for (String line : config) {
      var key = new StringBuilder();
      var value = new StringBuilder();
      int value_start = -1; //After the "=" character in a setting
      int lineLength = line.length();
      
      //Find comment start, after that point the line should be discarded
      for (int i = 0; i < line.length(); i++) {
        if (line.charAt(i) == '#') {lineLength = i; break;}
      }
      
      //Get key
      for (int i = 0; i < lineLength; i++) {
        char c = line.charAt(i);
        if (c == '=') {value_start = i+1; break;}
        else key.append(c);
      }
      if (key.length() == 0 || value_start == -1) continue;

      //Get value
      for (int i = value_start; i < lineLength; i++) {value.append(line.charAt(i));}
      if (value.length() == 0) continue;
      
      keys.add(key.toString().trim().toLowerCase());
      values.add(value.toString().trim());
      this.isEmpty = false;
    }
    this.keys = keys.toArray(new String[0]);
    this.values = values.toArray(new String[0]);
  }

  public boolean hasKey(String key) {return getKeyIndex(key) != -1;}

  public String getValue(String key) {
    int i = getKeyIndex(key);
    return i == -1 ? null : this.values[i];
  }

  public boolean getBool(String key) {
    String value = getValue(key);
    if (value == null) return false;
    value = value.toLowerCase();
    return value.equals("true") || value.equals("yes");
  }

  public String[] getAllValues(String key) {
    var filteredValues = new ArrayList<String>();
    for (int i = 0; i < this.keys.length; i++) {
      String currentKey = this.keys[i];
      if (currentKey.equals(key)) {filteredValues.add(this.values[i]);}
    }
    return filteredValues.toArray(new String[0]);
  }
  
  private int getKeyIndex(String key) {
    for (int i = 0; i < this.keys.length; i++) {
      String availableKey = this.keys[i];
      if (key.equals(availableKey)) return i;
    }
    return -1;
  }
}
