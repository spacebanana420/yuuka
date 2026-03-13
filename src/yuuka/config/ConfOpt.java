package yuuka.config;

import java.util.ArrayList;

//Object for storing a key and its value of a configuration file (e.g. build.yuuka)
//Each setting follows the style of key=value
//Comments are represented with character "#"
public class ConfOpt {
  public String key = null;
  public String value = null;
  
  public ConfOpt(String line) {
    var key = new StringBuilder();
    var value = new StringBuilder();
    int line_end = line.length(); //used to filter out comments in a line
    int value_start = -1;
    
    //find comment start
    for (int i = 0; i < line.length(); i++) {
      if (line.charAt(i) == '#') {line_end = i; break;}
    }
    
    //get key
    for (int i = 0; i < line_end; i++) {
      char c = line.charAt(i);
      if (c == '=') {value_start = i+1; break;}
      else {key.append(c);}
    }
    if (key.length() == 0 || value_start == -1) return;
    
    //get value
    for (int i = value_start; i < line_end; i++) {value.append(line.charAt(i));}
    if (value.length() == 0) return;
    
    this.key = key.toString().trim();
    this.value = value.toString().trim();
  }
  
  static ConfOpt[] getOptions(ArrayList<String> lines) {
    if (lines == null) {return null;}
    var opts = new ArrayList<ConfOpt>();
    for (String line: lines) {
      var o = new ConfOpt(line);
      if (o.isCorrect()) {opts.add(o);}
    }
    return opts.toArray(new ConfOpt[0]);
  }
  
  private boolean isCorrect() {return key != null && value != null;}
  
  public boolean keysMatch(String input_key) {return input_key.equals(key);}
}
