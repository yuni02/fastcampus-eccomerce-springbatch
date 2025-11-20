package fastcampus.ecommerce.batch.util;

import java.io.File;
import java.util.List;

public class FileUtils {

  public static List<File> splitCsv(File csvFile, int fileCount) {
    return List.of(csvFile);
  }
}
