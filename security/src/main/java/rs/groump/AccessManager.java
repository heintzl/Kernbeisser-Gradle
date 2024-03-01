package rs.groump;


public interface AccessManager {

  boolean hasAccess(Object object, PermissionSet keys);
  
  
  //If no AccessManager is set, should throw Exception to warn users!
  static AccessManager UNSET = (object, keys) -> {
	throw new RuntimeException("No AccessManager configured!");
  };
  
  static AccessManager ACCESS_GRANTED = (object, keys) -> true;
  
  static AccessManager ACCESS_DENIED = (AccessManager) (object, keys) -> false;
}
