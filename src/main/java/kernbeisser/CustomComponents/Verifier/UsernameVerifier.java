package kernbeisser.CustomComponents.Verifier;

public class UsernameVerifier extends DBResultVerifier {

    private UsernameVerifier(){}

    @Override
    public String getQuery() {
        return "select u from User u where u.username = :s";
    }

    @Override
    public String unexpectedResult() {
        return "Es kann kein Nutzer mit diesem Nutzernamen gefunden werden";
    }

    @Override
    boolean allowAlreadyExists() {
        return true;
    }

    public static UsernameVerifier checkUsernameUnused(){
        return new UsernameVerifier(){
            @Override
            boolean allowAlreadyExists() {
                return false;
            }

            @Override
            public String unexpectedResult() {
                return "Der Nutzername ist bereits vergeben";
            }
        };
    }

    public static UsernameVerifier checkUsernameExists(){
        return new UsernameVerifier(){
            @Override
            boolean allowAlreadyExists() {
                return true;
            }

            @Override
            public String unexpectedResult() {
                return "Es kann kein Nutzer mit diesem Nutzernamen gefunden werden";
            }
        };
    }

    public static UsernameVerifier checkUsernameUnused(String allow){
        return new UsernameVerifier(){
            @Override
            boolean allowAlreadyExists() {
                return false;
            }

            @Override
            public String unexpectedResult() {
                return "Der Nutzername ist bereits vergeben";
            }

            @Override
            public String getQuery() {
                return "select u from User u where u.username != '"+allow+"' and u.username like :s";
            }
        };
    }


}
