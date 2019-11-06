package kernbeisser;

public enum VAT {
    LOW(7),
    HIGH(19);
    private int value;
    VAT(int value){
        this.value=value;
    }
    public int getValue(){
        return value;
    }
}