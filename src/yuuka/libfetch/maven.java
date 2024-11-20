package yuuka.libfetch;

import yuuka.stdout;

public class maven {
  public static int fetchLibrary(String group, String name, String version) {
    String jar_name = name+"-"+version+".jar";
    String url =
      "https://mvnrepository.com/artifact/"
      +group
      +"/"+name
      +"/"+version
      +"/"+jar_name
    ;

    stdout.print("Fetching dependency: " + name);
    stdout.print_verbose("Group: " + group + "\nVersion: " + version);

    int result = download.get(url, "lib/"+jar_name);
    if (result != 0) {
      stdout.print("Error fetching library" + name + "!");
    }
    return result;
  }
}
