package uk.ac.ucl.comp0010.utils;

import com.opencsv.CSVReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ucl.comp0010.exception.CustomException;
import uk.ac.ucl.comp0010.exception.ServerException;

/**
 * Utilities used to handle csv files.
 *
 * @author Jack Pan
 * @since 2024-10-26
 */

public class CsvUtils {

  /**
   * Functions for reading csv files.
   */

  public static <T> List<T> readCsv(MultipartFile file, Class<T> clazz) {
    if (file.isEmpty()) {
      throw new CustomException("The file is empty!");
    }
    List<T> resultList = new ArrayList<>();
    try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream(),
        StandardCharsets.UTF_8))) {
      // Reading Csv Header
      String[] headers = csvReader.readNext();

      // Save Fields
      Map<String, Field> fieldMap = new HashMap<>();
      for (String header : headers) {
        Field field = clazz.getDeclaredField(header);
        field.setAccessible(true);
        fieldMap.put(header, field);
      }

      String[] dataRow;
      while ((dataRow = csvReader.readNext()) != null) {
        T obj = clazz.getDeclaredConstructor().newInstance();
        for (int i = 0; i < headers.length; i++) {
          String header = headers[i];
          String value = dataRow[i];
          Field field = fieldMap.get(header);
          setFieldValue(obj, field, value);
        }
        resultList.add(obj);
      }
    } catch (NoSuchFieldException e) {
      throw new CustomException("Csv file data format invalid");
    } catch (Exception e) {
      throw new ServerException("Reading csv file error");
    }

    return resultList;
  }

  /**
   * Parse the value and set value to the object.
   */

  private static <T> void setFieldValue(T obj, Field field, String value)
      throws IllegalAccessException {
    Class<?> fieldType = field.getType();
    if (fieldType == int.class || fieldType == Integer.class) {
      field.set(obj, Integer.parseInt(value));
    } else if (fieldType == Long.class) {
      field.set(obj, Long.parseLong(value));
    } else if (fieldType == String.class) {
      field.set(obj, value);
    } else if (fieldType == LocalDate.class) {
      field.set(obj, LocalDate.parse(value));
    } else {
      throw new ServerException("Field not recognized");
    }
  }

}
