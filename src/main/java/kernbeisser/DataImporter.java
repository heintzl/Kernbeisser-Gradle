package kernbeisser;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DataImporter {
    public DataImporter(File f) {
        if (!f.isDirectory()) return;
        HashMap<String, String> contents = new HashMap<>();
        for (File file : f.listFiles()) {
            contents.put(file.getName().toUpperCase(), getFileContent(file));
        }
        extractSuppliers(contents.get("SUPPLIERS.TXT"));
        extractPriceLists(contents.get("PRICELISTS.TXT"));
        extractItems(contents.get("ITEMS.TXT"));
        //extractUser(contents.get("USER.TXT"));
    }

    public static void OpenDialog(Component c) {
        JFileChooser fc = new JFileChooser();
        fc.addActionListener(e -> {
            File f = fc.getSelectedFile();
            if (f == null) return;
            new DataImporter(f);
        });
        fc.showOpenDialog(c);
    }

    private String getFileContent(File f) {
        StringBuilder fileData = new StringBuilder();
        try {
            Files.readAllLines(f.toPath(), StandardCharsets.UTF_8).forEach(e -> fileData.append(e).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileData.toString();
    }

    private void extractUser(String s) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        String[] lines = s.split("\n");
        ArrayList<User> users = new ArrayList<>();
        for (String l : lines) {
            String[] columns = l.split(";");
            User user = new User();
            em.persist(user);
            em.flush();
        }
        et.commit();
    }

    private void extractItems(String s) {
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        String[] lines = s.split("\n");
        ArrayList<Item> items = new ArrayList<>();
        HashSet<Long> barcode = new HashSet<>();
        for (String l : lines) {
            String[] columns = l.split(";");
            Item item = new Item();
            item.setName(columns[1]);
            item.setKbNumber(Integer.parseInt(columns[2]));
            item.setAmount(Integer.parseInt(columns[3]));
            item.setNetPrice(Integer.parseInt(columns[4]));
            item.setSupplier(em.createQuery("select s from Supplier s where shortName like '" + columns[5].replace("GRE", "GR") + "'", Supplier.class).getSingleResult());
            try {
                Long ib = Long.parseLong(columns[6]);
                if (!barcode.contains(ib)) {
                    item.setBarcode(ib);
                    barcode.add(ib);
                } else {
                    item.setBarcode(null);
                }
            } catch (NumberFormatException e) {
                item.setBarcode(null);
            }
            item.setSpecialPriceNet(Integer.parseInt(columns[7]));
            item.setVatLow(Boolean.parseBoolean(columns[8]));
            item.setSurcharge(Integer.parseInt(columns[9]));
            item.setSingleDeposit(Integer.parseInt(columns[10]));
            item.setCrateDeposit(Integer.parseInt(columns[11]));
            item.setUnit(Unit.valueOf(columns[12].replace("WEIGHT", "GRAM")));
            item.setPriceList(em.createQuery("select p from PriceList p where name like '" + columns[13] + "'", PriceList.class).getSingleResult());
            item.setContainerDef(ContainerDefinition.valueOf(columns[14]));
            item.setContainerSize(Double.parseDouble(columns[15].replaceAll(",", ".")));
            item.setSuppliersItemNumber(Integer.parseInt(columns[16]));
            item.setWeighAble(Boolean.parseBoolean(columns[17]));
            item.setListed(Boolean.parseBoolean(columns[18]));
            item.setShowInShop(Boolean.parseBoolean(columns[19]));
            item.setDeleted(Boolean.parseBoolean(columns[20]));
            item.setPrintAgain(Boolean.parseBoolean(columns[21]));
            item.setDeleteAllowed(Boolean.parseBoolean(columns[22]));
            item.setLoss(Integer.parseInt(columns[23]));
            item.setInfo(columns[24]);
            item.setSold(Integer.parseInt(columns[25]));
            item.setSpecialPriceMonth(Tools.extract(ArrayList::new, columns[26], "_", Boolean::parseBoolean));
            item.setDelivered(Integer.parseInt(columns[27]));
            item.setInvShelf(Tools.extract(ArrayList::new, columns[28], "_", Integer::parseInt));
            item.setInvStock(Tools.extract(ArrayList::new, columns[29], "_", Integer::parseInt));
            item.setInvPrice(Integer.parseInt(columns[30]));
            item.setIntake(Date.valueOf(LocalDate.now()));
            item.setLastBuy(null);
            item.setLastDelivery(Date.valueOf(LocalDate.now()));
            item.setDeletedDate(null);
            item.setCooling(Cooling.valueOf(columns[35]));
            item.setCoveredIntake(Boolean.parseBoolean(columns[36]));
            items.add(item);
        }
        et.begin();
        items.subList(0, items.size() / 2).forEach(em::persist);
        items.subList(items.size() / 2, items.size() - 1).forEach(em::persist);
        em.flush();
        et.commit();
        em.close();
    }

    private void extractPriceLists(String s) {
        String[] lines = s.split("\n");
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        for (String l : lines) {
            et.begin();
            String[] columns = l.split(";");
            PriceList pl = new PriceList();
            pl.setName(columns[0]);
            if (!columns[1].equals("NULL")) {
                try {
                    pl.setSuperPriceList(em.createQuery("select p from PriceList p where name like  '" + columns[1] + "'", PriceList.class).getSingleResult());
                } catch (NoResultException e) {
                    pl.setSuperPriceList(null);
                }
            }
            em.persist(pl);
        }
        em.flush();
        et.commit();
        em.close();
    }

    private void extractSuppliers(String s) {
        String[] lines = s.split("\n");
        ArrayList<Supplier> suppliers = new ArrayList<>();
        for (String l : lines) {
            String[] columns = l.split(";");
            Supplier supplier = new Supplier();
            supplier.setShortName(columns[0]);
            supplier.setName(columns[1]);
            supplier.setPhoneNumber(columns[2]);
            supplier.setEmail(columns[3]);
            supplier.setAddress(columns[4] + ";" + columns[5]);
            supplier.setKeeper(columns[6]);
            suppliers.add(supplier);
        }
        EntityManager em = DBConnection.getEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        suppliers.forEach(em::persist);
        em.flush();
        et.commit();
        em.close();
    }
}
