package yuuka.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import yuuka.stdout;

public class libconf {
  public static void createConfig() {
    if (new File("libs.yuuka").isFile()) {return;}
    String conf =
      "#Yuuka dependency file"
      +"\n#Yuuka fetches library dependencies that are written here, based on the Maven repository"
      +"\n#Format: library=GROUP%NAME%VERSION"
      +"\n#Example: library=org.randomdev%pngdecoder%1.0.2"
    ;
    try {
      var os = new FileOutputStream("libs.yuuka");
      os.write(conf.getBytes());
      os.close();
    }
    catch (IOException e) {stdout.print("Failed to create dependency config file!");}
  }

  public static MavenLibrary[] getLibraries() {
    String[] conf = confreader.readConfig("libs.yuuka");
    var libs = new ArrayList<MavenLibrary>();
    for (String line : conf) {addLib(libs, line);}

    return libs.toArray(new MavenLibrary[0]);
  }

  private static void addLib(ArrayList<MavenLibrary> libs, String line) {
    String value = confreader.parseLine(line, "library=");
    if (value.equals("")) {return;}
    var lib = new MavenLibrary();
    int lib_i = 0;
    String temp = "";
    for (int i = 0; i < value.length() || i < 3; i++) {
      char c = value.charAt(i);
      if (c == '%') {
        if (!temp.equals("")) {lib.properties[lib_i] = temp;}
        lib_i++;
        temp = "";
      }
      else {temp+=c;}
    }
    if (lib.isValid()) {libs.add(lib);}
  }
}

class MavenLibrary {
  public String[] properties = new String[]{null, null, null};

  public String group() {return properties[0];}
  public String name() {return properties[1];}
  public String version() {return properties[2];}

  public boolean isValid() {return properties[0] != null && properties[1] != null && properties[2] != null;}
}
