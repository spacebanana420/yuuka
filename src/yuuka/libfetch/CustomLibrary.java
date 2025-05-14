package yuuka.libfetch;

import yuuka.io.stdout;
import yuuka.misc;
import java.io.File;

public class CustomLibrary {
  public String[] properties = new String[]{null, null};

  public String filename() {return properties[0];}
  public String url() {return properties[1];}

  public boolean isValid() {return properties[0] != null && properties[1] != null;}



  public int fetchLibrary() {
    String filename_fixed = (misc.checkFileExtension(filename(), ".jar")) ? filename() : filename() + ".jar";
    String install_path = "lib/" + filename_fixed;

    if (new File(install_path).isFile()) {
      stdout.print_verbose("Dependency " + filename_fixed + " is already installed, skipping");
      return 0;
    }
    stdout.print_verbose("Fetching dependency: " + filename_fixed);

    int result = download.get(url(), install_path);
    if (result != 0) {
      stdout.error("Error fetching library " + filename_fixed + "!");
    }
    return result;
  }
}
