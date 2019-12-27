package kernbeisser.StartUp.DataImport;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.DBEntitys.*;
import kernbeisser.Enums.ContainerDefinition;
import kernbeisser.Enums.Cooling;
import kernbeisser.Enums.Unit;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.View;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataImportController implements Controller {
    private DataImportView view;
    private DataImportModel model;
    DataImportController(DataImportView dataImportView) {
        this.view=dataImportView;
        model=new DataImportModel();
    }

    void openFileExplorer(){
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setFileFilter(new FileNameExtensionFilter("Config-File","JSON","json"));
        jFileChooser.addActionListener(e -> {
            if(jFileChooser.getSelectedFile()==null)return;
            view.setFilePath(jFileChooser.getSelectedFile().getAbsolutePath());
            checkDataSource();
        });
        jFileChooser.showOpenDialog(view);
    }

    private boolean isValidDataSource(){
        return view.getFilePath().toUpperCase().endsWith(".JSON") && new File(view.getFilePath()).exists();
    }


    void checkDataSource(){
        if(isValidDataSource()){
            view.setValidDataSource(true);
            JSONObject dataConfig = extractJSON();
            if(dataConfig.has("UserData")){
                JSONObject jsonObject = dataConfig.getJSONObject("UserData");
                view.userSourceFound(jsonObject.has("Users")&&jsonObject.has("Jobs"));
            }else {
                view.userSourceFound(false);
            }
            if(dataConfig.has("ItemData")){
                JSONObject jsonObject = dataConfig.getJSONObject("ItemData");
                view.itemSourceFound(jsonObject.has("Suppliers")&&jsonObject.has("Items")&&jsonObject.has("PriceLists"));
            }else {
                view.itemSourceFound(false);
            }
        }else {
            view.setValidDataSource(false);
            view.userSourceFound(false);
            view.itemSourceFound(false);
        }

    }

    private JSONObject extractJSON(){
        StringBuilder sb = new StringBuilder();
        try {
            Files.readAllLines(new File(view.getFilePath()).toPath()).forEach(sb::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(sb.toString());
    }

    void importData(){
        if(isValidDataSource()){
            String jsonPath =  view.getFilePath();
            String relativePath = jsonPath.substring(0,jsonPath.lastIndexOf("\\"))+"/";
            JSONObject path = extractJSON();
            if(view.importItems()){
                JSONObject itemPath = path.getJSONObject("ItemData");
                File suppliers = new File(relativePath+itemPath.getString("Suppliers"));
                File priceLists = new File(relativePath+itemPath.getString("PriceLists"));
                File items = new File(relativePath+itemPath.getString("Items"));
                if(suppliers.exists()&&priceLists.exists()&&items.exists()){
                    new Thread(() -> {
                        view.setItemProgress(0);
                        parseSuppliers(suppliers);
                        parsePriceLists(priceLists);
                        parseItems(items);
                    }).start();
                }else {
                    view.itemSourceFound(false);
                    view.itemSourcesNotExists();
                }
            }
            if(view.importUser()){
                JSONObject userPath = path.getJSONObject("UserData");
                File users = new File(relativePath+userPath.getString("Users"));
                File jobs = new File(relativePath+userPath.getString("Jobs"));
                if(jobs.exists()&&users.exists()){
                    new Thread(() -> {
                        view.setUserProgress(0);
                        parseJobs(jobs);
                        parseUsers(users);
                    }).start();
                }
                else {
                    view.userSourceFound(false);
                    view.userSourcesNotExists();
                }
            }
        }
    }

    private void parseJobs(File f){
        try{
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            Collection<Job> jobs = new ArrayList<>((int)(lines.size()*1.5));
            for (String line : lines) {
                String[] columns = line.split(";");
                Job job = new Job();
                job.setName(columns[0]);
                job.setDescription(columns[1]);
                jobs.add(job);
            }
            view.setUserProgress(1);
            model.saveAll(jobs);
            view.setUserProgress(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void parseUsers(File f){
        try {
            HashMap<String, Job> jobs = new HashMap<>();
            Job.getAll(null).forEach(e -> jobs.put(e.getName(),e));
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            String defaultPassword = BCrypt.withDefaults().hashToString(12,"start".toCharArray());
            for (String l : lines) {
                String[] columns = l.split(";");
                User user = new User();
                User secondary = new User();
                UserGroup userGroup = new UserGroup();
                user.setSalesThisYear((int)(Float.parseFloat(columns[0].replace(",","."))*100));
                user.setSalesLastYear((int)(Float.parseFloat(columns[1].replace(",","."))*100));
                userGroup.setInterestThisYear((int)(Float.parseFloat(columns[2].replace(",","."))*100));
                user.setShares(Integer.parseInt(columns[3]));
                user.setSolidaritySurcharge(Integer.parseInt(columns[4]));
                secondary.setFirstName(columns[5]);
                secondary.setSurname(columns[6]);
                user.setExtraJobs(columns[7]);
                user.setJobs(Tools.extract(HashSet::new,columns[8],"§",jobs::get));
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                user.setLastBuy(Date.valueOf(LocalDate.parse(columns[9].replace("Noch nie","11.03.4000"),df)));
                user.setKernbeisserKey(Boolean.parseBoolean(columns[10]));
                user.setEmployee(Boolean.parseBoolean(columns[11]));
                //IdentityCode: Unused, column 12
                //Username: Unknown, column 13
                //Password: Start, column 14
                user.setFirstName(columns[15]);
                user.setSurname(columns[16]);
                user.setPhoneNumber1(columns[17]);
                user.setPhoneNumber2(columns[18]);
                for (String s : columns[19].split(" ")) {
                    if(s.equals(""))continue;
                    try{
                        user.setTownCode(Integer.parseInt(s));
                    }catch (NumberFormatException e){
                        user.setTown(s);
                    }
                }
                switch (Integer.parseInt(columns[20])){
                    //TODO
                }
                user.setEmail(columns[21]);
                //CreateDate: is't used(create new CreateDate), column 22
                userGroup.setValue((int) (Float.parseFloat(columns[23].replace(",","."))*100));
                //TransactionDates: not used, column 24
                //TransactionValues: not used, column 25
                user.setStreet(columns[26]);
                user.setUserGroup(userGroup);
                user.setPassword(defaultPassword);
                secondary.setPassword(defaultPassword);
                user.setUsername(user.getFirstName()+"."+user.getSurname()+user.getTownCode());
                secondary.setUsername(secondary.getFirstName()+"."+secondary.getSurname()+secondary.getTownCode());
                secondary.setUserGroup(userGroup);
                model.saveUser(user,secondary.getFirstName().equals("") ? null : secondary,userGroup);
            }
            view.setUserProgress(4);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void parsePriceLists(File f){
        try {
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            HashMap<String, PriceList> priceLists = new HashMap<>();
            for (String l : lines) {
                String[] columns = l.split(";");
                PriceList pl = new PriceList();
                pl.setName(columns[0]);
                pl.setSuperPriceList(priceLists.get(columns[1]));
            }
            view.setItemProgress(3);
            model.saveAll(priceLists.values());
            view.setItemProgress(4);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parseSuppliers(File f){
        try {
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            Collection<Supplier> suppliers = new ArrayList<>(lines.size());
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
            view.setItemProgress(1);
            model.saveAll(suppliers);
            view.setItemProgress(2);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void parseItems(File f) {
        try {
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            HashSet<Long> barcode = new HashSet<>(lines.size());
            HashMap<String, PriceList> priceListHashMap = new HashMap<>();
            HashMap<String, Supplier> suppliers = new HashMap<>();
            Collection<Item> items = new ArrayList<>(lines.size());
            Supplier.getAll(null).forEach(e -> suppliers.put(e.getShortName(), e));
            PriceList.getAll(null).forEach(e -> priceListHashMap.put(e.getName(), e));
            for (String l : lines) {
                String[] columns = l.split(";");
                Item item = new Item();
                item.setName(columns[1]);
                item.setKbNumber(Integer.parseInt(columns[2]));
                item.setAmount(Integer.parseInt(columns[3]));
                item.setNetPrice(Integer.parseInt(columns[4]));
                item.setSupplier(suppliers.get(columns[5].replace("GRE", "GR")));
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
                item.setPriceList(priceListHashMap.get(columns[13]));
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
                item.setIntake(java.sql.Date.valueOf(LocalDate.now()));
                item.setLastBuy(null);
                item.setLastDelivery(Date.valueOf(LocalDate.now()));
                item.setDeletedDate(null);
                item.setCooling(Cooling.valueOf(columns[35]));
                item.setCoveredIntake(Boolean.parseBoolean(columns[36]));
                items.add(item);
            }
            view.setItemProgress(5);
            model.saveAll(items);
            view.setItemProgress(6);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    void cancel(){
        view.back();
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
