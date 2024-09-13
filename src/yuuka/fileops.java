package yuuka;

import java.io.File;
import java.util.ArrayList;

public class fileops {
  public static ArrayList<String> getSourceFiles(String root_path) {
    String[] subpaths = new File(root_path).list();

    ArrayList<String> source_files = new ArrayList<>();
    for (String p : subpaths) {
      File f = new File(root_path + "/" + p);
      if (f.isFile() && f.canRead() && isSourceFile(p)) {
        source_files.add(root_path + "/" + p);
        continue;
      }
      if (f.isDirectory() && f.canRead()) {
        var files = getSourceFiles(root_path + "/" + p);
        source_files.addAll(files); 
      }
    }
    return source_files;
  }

  public static boolean isSourceFile(String name) {
    int l = name.length();
    return
      l > 5
      && name.charAt(l-1) == 'a'
      && name.charAt(l-2) == 'v'
      && name.charAt(l-3) == 'a'
      && name.charAt(l-4) == 'j'
      && name.charAt(l-5) == '.';
  }
}
