package kernbeisser.DBEntitys;

public class Shelf {

    private int id;

    private Integer[] priceLists = new Integer[20];

    private String note;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer[] getPriceLists() {
        return priceLists;
    }

    public void setPriceLists(Integer[] priceLists) {
        this.priceLists = priceLists;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
