package yuuka.libfetch;

import yuuka.io.stdout;
import java.io.File;

public class MavenLibrary {
  public String[] properties = new String[]{null, null, null};

  public String group() {return properties[0];}
  public String name() {return properties[1];}
  public String version() {return properties[2];}
  public String jar_name() {return properties[1]+"-"+properties[2]+".jar";}

  public boolean isValid() {return properties[0] != null && properties[1] != null && properties[2] != null;}



  public int fetchLibrary() {
    String jar_name = name()+"-"+version()+".jar";

    if (new File("lib/"+jar_name).isFile()) {
      stdout.print_verbose("Dependency " + name() + " is already installed, skipping");
      return 0;
    }
    String url =
      "https://repo1.maven.org/maven2/"
      +group().replace('.', '/')
      +"/"+name()
      +"/"+version()
      +"/"+jar_name
    ;

    stdout.print_verbose("Fetching dependency: " + name());
    stdout.print_debug("  Group: " + group() + "\n  Version: " + version());

    int result = download.get(url, "lib/"+jar_name);
    if (result != 0) {
      stdout.print("Error fetching library " + name() + "!");
    }
    return result;
  }
}
