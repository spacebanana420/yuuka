package yuuka.libfetch;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;


public class download {
  public static int get(String url, String output) {
    try {
      InputStream in = new URI(url).toURL().openStream();
      Path out_p = Path.of(output);
      Files.copy(in, out_p);
      return 0;
    }
    catch(Exception e) {
      return 1;
    }
  }
}
