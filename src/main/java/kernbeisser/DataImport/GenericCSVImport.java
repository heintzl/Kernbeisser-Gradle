package kernbeisser.DataImport;

import com.google.common.reflect.ClassPath;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.NonUniqueResultException;

public class GenericCSVImport {

  @Getter private static final Logger logger = LogManager.getLogger(GenericCSVImport.class);

  @Getter private Set<String> allDBEntityNames = null;
  private final Path filePath;
  private Class<?> clazz;
  private final List<ComplexGetter> identificationGetters = new ArrayList<>();
  private final List<ComplexSetter> setters = new ArrayList<>();
  private final List<String[]> data = new ArrayList<>();

  public GenericCSVImport(Path filePath) {
    this.filePath = filePath;
    try {
      allDBEntityNames =
          ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses().stream()
              .map(ClassPath.ClassInfo::getName)
              .filter(s -> s.contains("kernbeisser.DBEntities"))
              .collect(Collectors.toSet());
      logger.error(readFile(filePath));

    } catch (IOException e) {
      Tools.showUnexpectedErrorWarning(e);
    }
  }

  private String capitalize1st(String s) {
    return (s.substring(0, 1).toUpperCase()) + s.substring(1);
  }

  private String readFile(Path filePath) throws IOException {
    String entityName = "";
    String fieldName = "";
    try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
      String line = reader.readLine();
      String[] infoContent = line.split(":");
      if (!infoContent[0].equals("#Separator") || infoContent[1].isEmpty()) {
        return "Fehlendes Trennzeichen in der 1. Zeile";
      }
      String separator = infoContent[1];
      line = reader.readLine();
      infoContent = line.split(":");
      if (!infoContent[0].equals("#Entity") || infoContent[1].isEmpty()) {
        return "Fehlende Entity in der 2. Zeile";
      }
      entityName = infoContent[1];
      String finalEntityName = entityName;
      Optional<String> clazzCandidate =
          allDBEntityNames.stream().filter(e -> e.endsWith("." + finalEntityName)).findAny();
      if (!clazzCandidate.isPresent()) {
        throw new ClassNotFoundException();
      }
      clazz = Class.forName(clazzCandidate.get());
      line = reader.readLine();
      infoContent = line.split(":");
      if (!infoContent[0].equals("#Identity Columns") || !StringUtils.isNumeric(infoContent[1])) {
        return "Fehlende Anzahl Identitätsspalten in der 3. Zeile";
      }
      int identityColumns = Integer.parseInt(infoContent[1]);
      line = reader.readLine();
      infoContent = line.split(":");
      if (!infoContent[0].equals("#Data") || infoContent[1].isEmpty()) {
        return "Fehlende Feldnamen in der 3. Zeile";
      }
      String[] targetFields = infoContent[1].split(separator);
      for (int i = 0; i < identityColumns; i++) {
        fieldName = targetFields[i];
        identificationGetters.add(buildGetterChain(clazz, fieldName));
      }
      for (int i = identityColumns; i < targetFields.length; i++) {
        fieldName = targetFields[i];
        setters.add(buildSetter(clazz, fieldName));
      }
      CSVParser csvParser = new CSVParserBuilder().withSeparator(separator.charAt(0)).build();

      CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(csvParser).build();
      data.addAll(csvReader.readAll());
      processData();
      return "";
    } catch (NoSuchFieldException e) {
      return "Ungültiger Feldname \"" + fieldName + "\"";
    } catch (NoSuchMethodException f) {
      return "Ungeeigneter Feldname \"" + fieldName + "\"";
    } catch (ClassNotFoundException e) {
      return "Ungültige Entity \"" + entityName + "\" in der 2. Zeile";
    } catch (CsvException e) {
      return "Lesefehler in CSV-Datei";
    } catch (InvocationTargetException | IllegalAccessException e) {
      Tools.showUnexpectedErrorWarning(e);
      return "Lesefehler";
    }
  }

  private Method buildGetter(final Class clazz, final String fieldName)
      throws NoSuchFieldException, NoSuchMethodException {
    Field targetField = clazz.getDeclaredField(fieldName);
    String getterPrefix = targetField.getType().equals(Boolean.class) ? "is" : "get";
    return clazz.getDeclaredMethod(getterPrefix + capitalize1st(fieldName));
  }

  private ComplexGetter buildGetterChain(Class clazz, final String fieldName)
      throws NoSuchFieldException, NoSuchMethodException {
    ComplexGetter result = new ComplexGetter();
    String[] fieldParts = fieldName.split("\\.");
    int chainLength = fieldParts.length;
    for (int i = 0; i < chainLength; i++) {
      if (i > 0) {
        Field targetField = clazz.getDeclaredField(fieldParts[i]);
        clazz = targetField.getType();
      }
      result.addMethod(buildGetter(clazz, fieldParts[i]));
    }
    return result;
  }

  private ComplexSetter buildSetter(Class clazz, final String fieldName)
      throws NoSuchFieldException, NoSuchMethodException {
    String[] fieldParts = fieldName.split("\\.");
    ComplexSetter result = null;
    int chainLength = fieldParts.length;
    String setterName = "set" + capitalize1st(fieldParts[0]);
    for (Method setter : allSetters(clazz)) {
      if (setter.getName().equals(setterName)) {
        result = new ComplexSetter(setter);
        clazz = setter.getParameterTypes()[0];
        break;
      }
    }
    if (result == null) {
      throw new NoSuchMethodException();
    }

    for (int i = 1; i < chainLength; i++) {
      Field targetField = clazz.getDeclaredField(fieldParts[i]);
      result.addGetterMethod(buildGetter(clazz, fieldParts[i]));
      clazz = targetField.getType();
    }
    return result;
  }

  private Set<Method> allSetters(Class clazz) {
    Method[] methods = clazz.getDeclaredMethods();
    return Arrays.stream(methods)
        .filter(m -> m.getName().startsWith("set"))
        .collect(Collectors.toSet());
  }

  void processData()
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    Method idGetter = clazz.getDeclaredMethod("getId");
    final List<?> clazzObjects = DBConnection.getAll(clazz);
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup(value = "commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    for (String[] row : data) {
      List<?> targetObjects = clazzObjects;
      Object targetObject = null;
      int index = 0;
      targetObjects =
          clazzObjects.stream()
              .filter(o -> identificationGetters.get(0).match(o, row[0]))
              .collect(Collectors.toList());
      for (int i = 1; i < identificationGetters.size(); i++) {
        final int localIndex = i;
        targetObjects =
            targetObjects.stream()
                .filter(o -> identificationGetters.get(localIndex).match(o, row[localIndex]))
                .collect(Collectors.toList());
      }
      int num = targetObjects.size();
      if (num > 1) {
        logger.warn("found more than one target object. Your data seems to be ambigious");
        throw new NonUniqueResultException(num);
      }
      if (num == 0) {
        logger.warn(
            "skipped row ["
                + rowToString(row)
                + "] because no matching "
                + clazz.getName()
                + " exists");
        continue;
      }
      targetObject = em.find(clazz, idGetter.invoke(targetObjects.get(0)));
      if (targetObject == null) {
        continue;
      }
      boolean success = true;
      for (ComplexSetter setter : setters) {
        index++;
        success = setter.invoke(targetObject, row[index]);
        if (!success) {
          break;
        }
      }
      if (success) {
        em.merge(targetObject);
        logger.info("updated " + clazz.getName() + " with row " + rowToString(row));
      }
    }
    em.flush();
    et.commit();
  }

  static String rowToString(String[] values) {
    return String.join("; ", values);
  }
}
