package kernbeisser.DataImport;

import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import kernbeisser.DBConnection.DBConnection;
import kernbeisser.Useful.Tools;
import org.apache.logging.log4j.Level;

public class ComplexSetter {

  private final Method setter;
  private ComplexGetter objectGetter = new ComplexGetter();
  private final Class clazz;
  private final List<?> allCandidates;
  private BiConsumer<Level, String> logConsumer;

  public ComplexSetter(Method setter) {
    this.setter = setter;
    // this looks extremely unsafe - what if a getter has more than 1 Parameter? That would be a bit
    // aside the getter/setter pattern and anyway the module can currently pass only one value to
    // each target field
    clazz = setter.getParameterTypes()[0];
    if (GenericCSVImport.isDBEntity(clazz)) {
      allCandidates = Collections.unmodifiableList(DBConnection.getAll(clazz));
    } else {
      allCandidates = null;
    }
  }

  public ComplexSetter(Method setter, BiConsumer<Level, String> logConsumer) {
    this(setter);
    this.logConsumer = logConsumer;
  }

  public static ComplexSetter of(
      Class clazz, final String fieldName, BiConsumer<Level, String> logConsumer)
      throws NoSuchFieldException, NoSuchMethodException {
    String[] fieldParts = fieldName.split("\\.");
    String setterName = "set" + Tools.capitalize1st(fieldParts[0]);
    Method setter = null;
    for (Method method : clazz.getMethods()) {
      if (method.getName().equals(setterName)) {
        setter = method;
        break;
      }
    }
    if (setter == null) {
      throw new NoSuchMethodException();
    }
    ComplexSetter result = new ComplexSetter(setter, logConsumer);
    String getterFieldName = fieldName.substring(fieldParts[0].length() + 1);
    if (!getterFieldName.isEmpty()) {
      result.objectGetter = ComplexGetter.of(setter.getParameterTypes()[0], getterFieldName);
    }
    return result;
  }

  private void writeLog(Level level, String message) {
    if (logConsumer != null) {
      logConsumer.accept(level, message);
    }
  }

  private Object adjustArgType(String arg) throws NumberFormatException, ClassCastException {
    String typeName = clazz.getName();
    switch (typeName) {
      case "java.lang.String":
        return (String) arg;
      case "java.lang.Integer":
      case "int":
        return Integer.parseInt(arg);
      case "java.lang.Double":
      case "double":
        return Double.parseDouble(arg);
      case "java.lang.Boolean":
      case "boolean":
        return Boolean.parseBoolean(arg);
      case "java.lang.Byte":
      case "byte":
        return Byte.parseByte(arg);
      default:
        throw new ClassCastException("invalid setter field Type " + typeName);
    }
  }

  private Object getObject(String arg) throws NonUniqueResultException, NoResultException {
    if (objectGetter.isEmpty()) {
      return adjustArgType(arg);
    }
    List<?> candidates =
        allCandidates.stream().filter(o -> objectGetter.match(o, arg)).collect(Collectors.toList());
    if (candidates.size() > 1) {
      writeLog(
          Level.WARN,
          "could not update object, because more than one value was found for " + clazz.getName());
      throw new NonUniqueResultException();
    }
    if (candidates.isEmpty()) {
      writeLog(
          Level.WARN, "could not update object, because no value was found for " + clazz.getName());
      throw new NoResultException();
    }
    return candidates.get(0);
  }

  public boolean invoke(Object obj, String arg)
      throws InvocationTargetException, IllegalAccessException {
    try {
      setter.invoke(obj, getObject(arg));
      return true;
    } catch (NonUniqueResultException | NoResultException e) {
      return false;
    }
  }
}
