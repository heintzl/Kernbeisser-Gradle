package kernbeisser;

public class Translator {

    public String translate(Unit unit) {
        switch (unit) {
            case GRAM:
                return "Gramm";
            case LITER:
                return "Liter";
            case STACK:
                return "St\u00fcck";
            case KILOGRAM:
                return "Kilogramm";
            case MILLILITER:
                return "Milliter";
            default:
                return "No Translation";
        }
    }
    public String translate(Cooling c) {
        switch (c) {
            case COLD:
                return "Kalt(K\u00fchlschrank)";
            case NONE:
                return "Keine K\u00fchlung bei Raumtemperatur";
            case EXTRA_COLD:
                return "Gefroren(Gefrierer)";
            default:
                return "no Translation";
        }
    }
    public String translate(Permission permission) {
        switch (permission) {
            case BEGINNER:
                return "Beginner";
            case STANDARD:
                return "Standart";
            case MANAGER:
                return "Ladendienst";
            case MONEY_MANAGER:
                return "Geld BeauftragteR";
            case ADMIN:
                return "Adminestator";
            default:
                return "No Translation";
        }
    }
    public String translate(ContainerDefinition c) {
        switch (c) {
            case STATIC:
                return "Fester Wert";
            case ROUNDED:
                return "Nicht genauer Wert";
            case UNKNOWN:
                return "Unbekannter Wert";
            default:
                return "No Translation";
        }
    }
    public String translate(boolean b){
        return b ? "Ja" : "Nein";
    }
    public <T> T translate(Class<T> e, String s) {
        if (e.equals(Permission.class)) {
            switch (s) {
                case "Beginner":
                    return (T) Permission.BEGINNER;
                case "Standart":
                    return (T) Permission.STANDARD;
                case "Ladendienst":
                    return (T) Permission.MANAGER;
                case "Geld BeauftragteR":
                    return (T) Permission.MONEY_MANAGER;
                case "Adminestator":
                    return (T) Permission.ADMIN;
                case "No Translation":
                    return null;
            }
        } else if (e.equals(Unit.class)) {
            switch (s) {
                case "Gramm":
                    return (T) Unit.GRAM;
                case "Liter":
                    return (T) Unit.LITER;
                case "St\u00fcck":
                    return (T) Unit.STACK;
                case "Kilogramm":
                    return (T) Unit.KILOGRAM;
                case "Milliter":
                    return (T) Unit.MILLILITER;
                case "No Translation":
                    return null;
            }
        } else if (e.equals(ContainerDefinition.class)) {
            switch (s) {
                case "Fester Wert":
                    return (T) ContainerDefinition.STATIC;
                case "Nicht genauer Wert":
                    return (T) ContainerDefinition.ROUNDED;
                case "Unbekannter Wert":
                    return (T) ContainerDefinition.UNKNOWN;
                default:
                    return null;
            }
        } else if (e.equals(Cooling.class)) {
            switch (s) {
                case "Kalt(K\u00fchlschrank)":
                    return (T) Cooling.COLD;
                case "Keine K\u00fchlung bei Raumtemperatur":
                    return (T) Cooling.NONE;
                case "Gefroren(Gefrierer)":
                    return (T) Cooling.EXTRA_COLD;
                default:
                    return null;
            }
        }
        return null;
    }
}
