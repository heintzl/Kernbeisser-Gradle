package kernbeisser.DataImport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import kernbeisser.DBConnection.DBConnection;

public class ComplexSetter {

  private final Method setter;
  private final ComplexGetter objectGetter = new ComplexGetter();
  private final Class clazz;
  private final List<?> allCandidates;

  public ComplexSetter(Method setter) {
    this.setter = setter;
    clazz = setter.getParameterTypes()[0];
    allCandidates = Collections.unmodifiableList(DBConnection.getAll(clazz));
  }

  public void addGetterMethod(Method m) {
    objectGetter.addMethod(m);
  }

  private Object adjustArgType(String arg) throws NumberFormatException, ClassCastException {
    String typeName = setter.getParameterTypes()[1].getName();
    switch (typeName) {
      case "java.lang.String":
        return (String) arg;
      case "java.lang.Integer":
      case "integer":
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
      GenericCSVImport.getLogger()
          .warn(
              "could not update object, because more than one value was found for "
                  + clazz.getName());
      throw new NonUniqueResultException();
    }
    if (candidates.isEmpty()) {
      GenericCSVImport.getLogger()
          .warn("could not update object, because no value was found for " + clazz.getName());
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
