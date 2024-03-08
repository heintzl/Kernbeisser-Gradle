package kernbeisser.DataImport;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import kernbeisser.Useful.Tools;

public class ComplexGetter {

  private final List<Method> getterChain = new ArrayList<>();

  public void addMethod(Method m) {
    getterChain.add(m);
  }

  public boolean isEmpty() {
    return getterChain.isEmpty();
  }

  public Object invoke(Object obj) throws InvocationTargetException, IllegalAccessException {
    Object chainResult = obj;
    for (Method method : getterChain) {
      chainResult = method.invoke(chainResult);
    }
    return chainResult;
  }

  public String invokeToString(Object obj)
      throws InvocationTargetException, IllegalAccessException {
    Object chainResult = invoke(obj);
    String typeName = chainResult.getClass().getName();
    switch (typeName) {
      case "java.lang.String":
        return (String) chainResult;
      case "java.lang.Integer":
      case "integer":
        return Integer.toString((Integer) chainResult);
      case "java.lang.Double":
      case "double":
        return Double.toString((Double) chainResult);
      case "java.lang.Boolean":
      case "boolean":
        return Boolean.toString((Boolean) chainResult);
      case "java.lang.Byte":
      case "byte":
        return Byte.toString((Byte) chainResult);
      default:
        throw new ClassCastException("invalid field Type " + typeName);
    }
  }

  private static Method getFieldGetter(final Class clazz, final String fieldName)
      throws NoSuchFieldException, NoSuchMethodException {
    Field targetField = clazz.getDeclaredField(fieldName);
    String getterPrefix = targetField.getType().equals(Boolean.class) ? "is" : "get";
    return clazz.getDeclaredMethod(getterPrefix + Tools.capitalize1st(fieldName));
  }

  public static ComplexGetter of(Class clazz, final String fieldName)
      throws NoSuchFieldException, NoSuchMethodException {
    ComplexGetter result = new ComplexGetter();
    String[] fieldParts = fieldName.split("\\.");
    int chainLength = fieldParts.length;
    for (int i = 0; i < chainLength; i++) {
      if (i > 0) {
        Field targetField = clazz.getDeclaredField(fieldParts[i]);
        clazz = targetField.getType();
      }
      result.addMethod(getFieldGetter(clazz, fieldParts[i]));
    }
    return result;
  }

  boolean match(Object obj, String arg) {
    try {
      return arg.equals(invokeToString(obj));
    } catch (InvocationTargetException | IllegalAccessException | ClassCastException e) {
      return false;
    }
  }
}
