package yuuka.libfetch;

import yuuka.stdout;
import java.io.File;

public class maven {
  public static int fetchLibrary(MavenLibrary lib) {
    String jar_name = lib.name()+"-"+lib.version()+".jar";

    if (new File("lib/"+jar_name).isFile()) {
      stdout.print_verbose("Dependency " + lib.name() + " is already installed, skipping");
      return 0;
    }
    String url =
      "https://mvnrepository.com/artifact/"
      +lib.group()
      +"/"+lib.name()
      +"/"+lib.version()
      +"/"+jar_name
    ;

    stdout.print("Fetching dependency: " + lib.name());
    stdout.print_verbose("Group: " + lib.group() + "\nVersion: " + lib.version());

    int result = download.get(url, "lib/"+jar_name);
    if (result != 0) {
      stdout.print("Error fetching library" + lib.name() + "!");
    }
    return result;
  }
}
