package fastcampus.ecommerce.batch.util;

import java.time.LocalDate;

public class DateTimeUtil {

  public static LocalDate toLocalDate(String date) {
    return LocalDate.parse(date);
  }
}
