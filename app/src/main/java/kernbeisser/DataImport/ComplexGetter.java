package kernbeisser.DataImport;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ComplexGetter {

  private final List<Method> getterChain = new ArrayList<>();

  public void addMethod(Method m) {
    getterChain.add(m);
  }

  public boolean isEmpty() {
    return getterChain.isEmpty();
  }

  public String invokeToString(Object obj)
      throws InvocationTargetException, IllegalAccessException {
    Object chainResult = obj;
    for (Method method : getterChain) {
      chainResult = method.invoke(chainResult);
    }
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

  boolean match(Object obj, String arg) {
    try {
      return arg.equals(invokeToString(obj));
    } catch (InvocationTargetException | IllegalAccessException | ClassCastException e) {
      return false;
    }
  }
}
