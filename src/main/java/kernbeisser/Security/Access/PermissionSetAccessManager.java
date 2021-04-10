package kernbeisser.Security.Access;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import kernbeisser.Enums.PermissionKey;
import kernbeisser.Security.Key;
import kernbeisser.Security.PermissionSet;
import org.jetbrains.annotations.NotNull;

public class PermissionSetAccessManager implements PermissionKeyBasedAccessManager {

  private final PermissionSet keySet;

  public PermissionSetAccessManager(@NotNull PermissionSet permissions) {
    this.keySet = permissions;
  }

  @Override
  public boolean hasAccess(
      Object object, String methodName, String signature, PermissionKey[] keys) {
    return keySet.hasPermissions(keys);
  }

  private String getSignature(Method m) {
    String sig;
    try {
      Field gSig = Method.class.getDeclaredField("signature");
      gSig.setAccessible(true);
      sig = (String) gSig.get(m);
      if (sig != null) return sig;
    } catch (IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
    }
    StringBuilder sb = new StringBuilder("(");
    for (Class<?> c : m.getParameterTypes())
      sb.append((sig = Array.newInstance(c, 0).toString()), 1, sig.indexOf('@'));
    return sb.append(')')
        .append(
            m.getReturnType() == void.class
                ? "V"
                : (sig = Array.newInstance(m.getReturnType(), 0).toString())
                    .substring(1, sig.indexOf('@')))
        .toString();
  }

  private PermissionKey[] runCallerAnalyse(Object object, String methodName, String signature) {
    signature = signature.replace("/", ".");
    for (Method declaredMethod : object.getClass().getDeclaredMethods()) {
      if (declaredMethod.getName().equals(methodName)
          && getSignature(declaredMethod).equals(signature)) {
        Key key = declaredMethod.getAnnotation(Key.class);
        if (key == null) return new PermissionKey[0];
        return key.value();
      }
    }
    throw new UnsupportedOperationException(
        "cannot find method with name: " + methodName + " and signature: " + signature);
  }

  @Override
  public boolean hasPermission(PermissionSet keys) {
    return keySet.contains(keys);
  }
}
