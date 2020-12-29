package kernbeisser.Security.StaticMethodTransformer;

import kernbeisser.Enums.PermissionKey;

public interface RestrictedAccess extends StaticInterface{

  @StaticAccessPoint
  default PermissionKey[] getRequiredKeys(){
    return new PermissionKey[0];
  }

}
