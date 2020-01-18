package kernbeisser.Windows.CatalogInput;

import kernbeisser.DBEntities.ItemKK;
import kernbeisser.Enums.Unit;
import kernbeisser.Exeptions.FileReadException;
import kernbeisser.Exeptions.ObjectParseException;
import kernbeisser.Windows.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

class CatalogInputController implements Controller {
    private CatalogInputModel model;
    private CatalogInputView view;

    CatalogInputController(CatalogInputView view){
        this.view=view;
    }

    boolean importData(File f) throws ObjectParseException, FileReadException {
        StringBuilder sb = new StringBuilder();
        try {
            Files.readAllLines(f.toPath(), StandardCharsets.ISO_8859_1).forEach(e-> sb.append(e).append("\n"));
            return importData(sb.toString());
        } catch (IOException e) {
            throw new FileReadException(f,StandardCharsets.ISO_8859_1);
        }
    }

    boolean importData(String s) throws ObjectParseException {
        ArrayList<ItemKK> catalog = new ArrayList<>();
        for(String line : s.split("\n")) {
            ItemKK item = extractItemKK(line.replace("'", ""));
            if (item != null)
                catalog.add(item);
        }
        HashMap<Integer,Integer> deposit = new HashMap<>();
        catalog.forEach(e -> deposit.put(e.getKkNumber(),e.getNetPrice()));
        for (ItemKK item : catalog) {
            if (item.getSingleDeposit() != 0)
                item.setSingleDeposit(deposit.get(item.getSingleDeposit()));
            if (item.getCrateDeposit() != 0)
                item.setCrateDeposit(deposit.get(item.getCrateDeposit()));
        }
        model.clearCatalog();
        model.saveAll(catalog);
        return true;
    }
    ItemKK extractItemKK(String line) throws ObjectParseException {
        ItemKK item = new ItemKK();
        String[] values = line.split(";");
        try {
            if(values.length<42||values[23].equals("Display")||values[23].equals("Sets"))return null;
            item.setKkNumber(Integer.parseInt(values[0]));
            item.setBarcode(values[4]);
            item.setName(values[6]);
            item.setProducer(values[10]);
            item.setContainerSize(Double.parseDouble(values[22].replaceAll(",", ".")));
            item.setUnit(findUnit(values[23]));
            item.setAmount(extractAmount(values[23].replaceAll("\\D",""),item.getUnit()));
            item.setVatLow(values[33].equals("1"));
            item.setNetPrice((int) Math.round(Double.parseDouble(values[37].replace(",","."))*100));
            if(!values[26].equals(""))item.setSingleDeposit(Integer.parseInt(values[26]));
            if(!values[27].equals(""))item.setCrateDeposit(Integer.parseInt(values[27]));
        }catch (Exception e){
            throw new ObjectParseException(line,ItemKK.class);
        }
        return item;
    }
    private Unit findUnit(String s){
        if(s.toUpperCase().contains("L"))return Unit.LITER;
        else
        if(s.toUpperCase().contains("ML"))return Unit.MILLILITER;
        else
        if(s.toUpperCase().contains("KG"))return Unit.KILOGRAM;
        else
        if(s.toUpperCase().contains("G"))return Unit.GRAM;
        else
            return Unit.STACK;
    }
    private int extractAmount(String s,Unit u){
        try {
            double d = Double.parseDouble(s.replaceAll(",", "."));
            switch (u) {
                case LITER:
                case KILOGRAM:
                    return (int) (d * 1000);
                case STACK:
                case GRAM:
                case MILLILITER:
                default:
                    return (int) d;
            }
        }catch (NumberFormatException n){
            return extractAmount("1",u);
        }
    }

    @Override
    public void refresh() {

    }

    @Override
    public CatalogInputView getView() {
        return view;
    }

    @Override
    public CatalogInputModel getModel() {
        return model;
    }
}
