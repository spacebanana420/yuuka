package yuuka.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import yuuka.stdout;
import yuuka.libfetch.MavenLibrary;

public class libconf {
  public static int createConfig() {
    if (new File("libs.yuuka").isFile()) {return 1;}
    String conf =
      "#Yuuka dependency file"
      +"\n#Use this file to fetch third-party libraries from the Maven repository"
      +"\n#If a library file is already in \"lib\", then it won't be re-downloaded to spare resources"
      +"\n#Format: library=GROUP%NAME%VERSION"
      +"\n#Example: library=org.randomdev%pngdecoder%1.0.2"
    ;
    try {
      var os = new FileOutputStream("libs.yuuka");
      os.write(conf.getBytes());
      os.close();
      return 0;
    }
    catch (IOException e) {stdout.print("Failed to create dependency config file!"); return -1;}
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
    
    stdout.print_debug("Found dependency: " + value);
    var lib = new MavenLibrary();
    int lib_i = 0;
    String temp = "";
    for (int i = 0; i < value.length() && lib_i < 3; i++) {
      char c = value.charAt(i);
      if (c == '%') {
        if (!temp.equals("")) {lib.properties[lib_i] = temp;}
        lib_i++;
        temp = "";
      }
      else {temp+=c;}
    }
    if (lib_i < 3 && temp != null) {lib.properties[lib_i] = temp;}
    if (lib.isValid()) {libs.add(lib);}
    else {
      stdout.print_debug(
        "Incorrect dependency configuration"
        + "\nGroup found: " + lib.group()
        + "\nName found: " + lib.name()
        + "\nVersion found: " + lib.version()
      );
    }
  }
}
