package kernbeisser.Windows.CatalogInput;

import kernbeisser.DBConnection.DBConnection;
import kernbeisser.DBEntitys.ItemKK;
import kernbeisser.Enums.Unit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

class CatalogInputController {
    private boolean importData(File f){
        StringBuilder sb = new StringBuilder();
        try {
            Files.readAllLines(f.toPath(), StandardCharsets.ISO_8859_1).forEach(e-> sb.append(e).append("\n"));
            return importData(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean importData(String s){
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        ArrayList<ItemKK> catalog = new ArrayList<>();
        et.begin();
        em.createNativeQuery("truncate catalog").executeUpdate();
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
        for (ItemKK kk : catalog) {
            em.persist(kk);
        }
        em.flush();
        et.commit();
        em.close();
        return true;
    }
    private ItemKK extractItemKK(String line){
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
            e.printStackTrace();
            int i = 0;
            for (String value : values) {
                if(value!=null)System.out.print("r:"+i+"["+value+"]");
            }
            System.out.println();
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
}
