package yuuka.libfetch;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import yuuka.stdout;

public class download {
  public static int get(String url, String output) {
    try {
      stdout.print_debug("Downloading from URL: " + url);
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
