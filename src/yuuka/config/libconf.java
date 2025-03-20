package yuuka.config;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import yuuka.io.stdout;
import yuuka.libfetch.MavenLibrary;
import yuuka.libfetch.CustomLibrary;

public class libconf {
  public static int createConfig() {
    if (new File("libs.yuuka").isFile()) {return 1;}
    String conf =
      "#Yuuka dependency file"
      +"\n"
      +"\n#Use this file to fetch third-party libraries"
      +"\n#If a library file is already in \"lib\", then it won't be re-downloaded to spare resources"
      +"\n#Use \"library\" to fetch Maven libraries being hosted in repo1.maven.org"
      +"\n#use \"library_custom\" to fetch a JAR from anywhere else"
      +"\n"
      +"\n#Format: library=GROUP%NAME%VERSION"
      +"\n#Format: library_custom=JAR_FILENAME%URL"
      +"\n"
      +"\n#Example: library=org.randomdev%pngdecoder%1.0.2"
      +"\n#Example: library_custom=name.jar%https://repo.com/library/library.jar"
    ;
    try {
      var os = new FileOutputStream("libs.yuuka");
      os.write(conf.getBytes());
      os.close();
      return 0;
    }
    catch (IOException e) {stdout.print("Failed to create dependency config file!"); return -1;}
  }
  
  public static String[] readConfig() {return confreader.readConfig("libs.yuuka");}

  public static MavenLibrary[] getMavenLibraries(String[] conf) {
    var libs = new ArrayList<MavenLibrary>();
    for (String line : conf) {addLib(libs, line);}

    return libs.toArray(new MavenLibrary[0]);
  }

  public static CustomLibrary[] getCustomLibraries(String[] conf) {
    var libs = new ArrayList<CustomLibrary>();
    for (String line : conf) {addLib_custom(libs, line);}
    
    return libs.toArray(new CustomLibrary[0]);
  }

  private static void addLib(ArrayList<MavenLibrary> libs, String line) {
    String value = confreader.getValue(line, "library", true);
    if (value == null || value.equals("")) {return;}
    
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

  //lots of duplicate code
  private static void addLib_custom(ArrayList<CustomLibrary> libs, String line) {
    String value = confreader.getValue(line, "library_custom", true);
    if (value == null || value.equals("")) {return;}

    stdout.print_debug("Found dependency: " + value);  
    var lib = new CustomLibrary();
    int lib_i = 0;
    String temp = "";
    for (int i = 0; i < value.length() && lib_i < 2; i++) {
      char c = value.charAt(i);
      if (c == '%') {
        if (!temp.equals("")) {lib.properties[lib_i] = temp;}
        lib_i++;
        temp = "";
      }
      else {temp+=c;}
    }
    if (lib_i < 2 && temp != null) {lib.properties[lib_i] = temp;}
    if (lib.isValid()) {libs.add(lib);}
    else {
      stdout.print_debug(
        "Incorrect dependency configuration"
        + "\nFile name: " + lib.filename()
        + "\nURL: " + lib.url()
      );
    }
  }
}
