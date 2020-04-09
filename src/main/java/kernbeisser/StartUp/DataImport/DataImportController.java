package kernbeisser.StartUp.DataImport;

import at.favre.lib.crypto.bcrypt.BCrypt;
import kernbeisser.Config.ConfigManager;
import kernbeisser.DBEntities.*;
import kernbeisser.Enums.*;
import kernbeisser.Useful.Tools;
import kernbeisser.Windows.Controller;
import kernbeisser.Windows.Model;
import kernbeisser.Windows.Window;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class DataImportController implements Controller {
    private DataImportView view;
    private DataImportModel model;

    public DataImportController(Window current) {
        this.view = new DataImportView(current, this);
        model = new DataImportModel();
    }

    void openFileExplorer() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jFileChooser.setFileFilter(new FileNameExtensionFilter("Config-File", "JSON", "json"));
        jFileChooser.addActionListener(e -> {
            if (jFileChooser.getSelectedFile() == null) return;
            view.setFilePath(jFileChooser.getSelectedFile().getAbsolutePath());
            checkDataSource();
        });
        jFileChooser.showOpenDialog(view);
    }

    private boolean isValidDataSource() {
        return view.getFilePath().toUpperCase().endsWith(".JSON") && new File(view.getFilePath()).exists();
    }


    void checkDataSource() {
        if (isValidDataSource()) {
            view.setValidDataSource(true);
            JSONObject dataConfig = extractJSON();
            if (dataConfig.has("UserData")) {
                JSONObject jsonObject = dataConfig.getJSONObject("UserData");
                view.userSourceFound(jsonObject.has("Users") && jsonObject.has("Jobs"));
            } else {
                view.userSourceFound(false);
            }
            if (dataConfig.has("ItemData")) {
                JSONObject jsonObject = dataConfig.getJSONObject("ItemData");
                view.itemSourceFound(jsonObject.has("Suppliers") && jsonObject.has("Items") && jsonObject.has("PriceLists"));
            } else {
                view.itemSourceFound(false);
            }
        } else {
            view.setValidDataSource(false);
            view.userSourceFound(false);
            view.itemSourceFound(false);
        }

    }

    private JSONObject extractJSON() {
        StringBuilder sb = new StringBuilder();
        try {
            Files.readAllLines(new File(view.getFilePath()).toPath()).forEach(sb::append);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(sb.toString());
    }

    void importData() {
        if (isValidDataSource()) {
            String jsonPath = view.getFilePath();
            String relativePath = jsonPath.substring(0, jsonPath.lastIndexOf("/")) + "/"; // Bei Windows muss es lastIndexOf("\\") heißen.
            JSONObject path = extractJSON();
            if (view.importItems()) {
                JSONObject itemPath = path.getJSONObject("ItemData");
                File suppliers = new File(relativePath + itemPath.getString("Suppliers"));
                File priceLists = new File(relativePath + itemPath.getString("PriceLists"));
                File items = new File(relativePath + itemPath.getString("Items"));
                if (suppliers.exists() && priceLists.exists() && items.exists()) {
                    new Thread(() -> {
                        view.setItemProgress(0);
                        parseSuppliers(suppliers);
                        parsePriceLists(priceLists);
                        parseItems(items);
                    }).start();
                } else {
                    view.itemSourceFound(false);
                    view.itemSourcesNotExists();
                }
            }
            if (view.importUser()) {
                JSONObject userPath = path.getJSONObject("UserData");
                File users = new File(relativePath + userPath.getString("Users"));
                File jobs = new File(relativePath + userPath.getString("Jobs"));
                if (jobs.exists() && users.exists()) {
                    new Thread(() -> {
                        view.setUserProgress(0);
                        parseJobs(jobs);
                        parseUsers(users);
                    }).start();
                } else {
                    view.userSourceFound(false);
                    view.userSourcesNotExists();
                }
            }
            ConfigManager.getHeader().put("dbIsInitialized", true);
            ConfigManager.updateFile();
            if (view.createStandardAdmin()) {
                Permission admin = new Permission();
                admin.getKeySet().addAll(Arrays.asList(Key.values()));
                admin.setName("Admin(System Created)");
                User user = new User();
                user.setFirstName("System");
                user.setSurname("Admin");
                user.setUsername("Admin");
                String password;
                do {
                    password = view.requestPassword();
                } while (password.equals(""));
                user.setPassword(BCrypt.withDefaults().hashToString(12, password.toCharArray()));
                model.saveWithPermission(user, admin);
            }
        }
    }

    private void parseJobs(File f) {
        try {
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            Collection<Job> jobs = new ArrayList<>((int) (lines.size() * 1.5));
            for (String line : lines) {
                String[] columns = line.split(";");
                Job job = new Job();
                job.setName(columns[0]);
                job.setDescription(columns[1]);
                jobs.add(job);
            }
            view.setUserProgress(1);
            model.batchSaveAll(jobs);
            view.setUserProgress(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseUsers(File f) {
        try {
            HashSet<String> usernames = new HashSet<>();
            HashMap<String, Job> jobs = new HashMap<>();
            Job.getAll(null).forEach(e -> jobs.put(e.getName(), e));
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            String defaultPassword = BCrypt.withDefaults().hashToString(12, "start".toCharArray());
            for (String l : lines) {
                String[] columns = l.split(";");
                User user = new User();
                User secondary = new User();
                UserGroup userGroup = new UserGroup();
                userGroup.setInterestThisYear((int) (Float.parseFloat(columns[2].replace(",", "."))));
                user.setShares(Integer.parseInt(columns[3]));
                user.setSolidaritySurcharge(Integer.parseInt(columns[4]));
                secondary.setFirstName(columns[5]);
                secondary.setSurname(columns[6]);
                user.setExtraJobs(columns[7]);
                user.setJobs(Tools.extract(HashSet::new, columns[8], "§", jobs::get));
                DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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
                    if (s.equals("")) continue;
                    try {
                        user.setTownCode(Integer.parseInt(s));
                    } catch (NumberFormatException e) {
                        user.setTown(s);
                    }
                }
                switch (Integer.parseInt(columns[20])) {
                    //TODO
                }
                user.setEmail(columns[21]);
                //CreateDate: is't used(create new CreateDate), column 22
                userGroup.setValue((int) (Float.parseFloat(columns[23].replace(",", ".")) * 100));
                //TransactionDates: not used, column 24
                //TransactionValues: not used, column 25
                user.setStreet(columns[26]);
                user.setUserGroup(userGroup);
                user.setPassword(defaultPassword);
                secondary.setPassword(defaultPassword);
                generateUsername(usernames, user);
                generateUsername(usernames, secondary);
                secondary.setUserGroup(userGroup);
                model.saveUser(user, secondary.getFirstName().equals("") ? null : secondary, userGroup);
            }
            view.setUserProgress(4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateUsername(HashSet<String> usernames, User user) {
        for (int i = 1; i < user.getSurname().length(); i++) {
            String generatedUsername = (user.getFirstName().split(" ")[0]+"."+user.getSurname().substring(0,i)).toLowerCase();
            if (!usernames.contains(generatedUsername)) {
                user.setUsername(generatedUsername);
                usernames.add(generatedUsername);
                break;
            }
        }
        if(user.getUsername()==null){
            user.setUsername(user.getFirstName()+"."+user.getSurname()+new Random().nextLong());
        }
    }

    private void parsePriceLists(File f) {
        try {
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            HashMap<String, PriceList> priceLists = new HashMap<>();
            for (String l : lines) {
                String[] columns = l.split(";");
                PriceList pl = new PriceList();
                pl.setName(columns[0]);
                pl.setSuperPriceList(priceLists.get(columns[1]));
                priceLists.put(pl.getName(), pl);
            }
            view.setItemProgress(3);
            model.saveAll(priceLists.values());
            view.setItemProgress(4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseSuppliers(File f) {
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
            model.batchSaveAll(suppliers);
            view.setItemProgress(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseItems(File f) {
        try {
            List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
            HashSet<Long> barcode = new HashSet<>(lines.size());
            HashMap<String, PriceList> priceListHashMap = new HashMap<>();
            HashMap<String, Supplier> suppliers = new HashMap<>();
            Collection<Article> articles = new ArrayList<>(lines.size());
            Supplier.getAll(null).forEach(e -> suppliers.put(e.getShortName(), e));
            PriceList.getAll(null).forEach(e -> priceListHashMap.put(e.getName(), e));
            for (String l : lines) {
                String[] columns = l.split(";");
                Article article = new Article();
                article.setName(columns[1]);
                article.setKbNumber(Integer.parseInt(columns[2]));
                article.setAmount(Integer.parseInt(columns[3]));
                article.setNetPrice(Integer.parseInt(columns[4])/100.);
                article.setSupplier(suppliers.get(columns[5].replace("GRE", "GR")));
                try {
                    Long ib = Long.parseLong(columns[6]);
                    if (!barcode.contains(ib)) {
                        article.setBarcode(ib);
                        barcode.add(ib);
                    } else {
                        article.setBarcode(null);
                    }
                } catch (NumberFormatException e) {
                    article.setBarcode(null);
                }
                //columns[7] look at line 311
                article.setVAT(Boolean.parseBoolean(columns[8]) ? VAT.LOW : VAT.HIGH);
                article.setSurcharge(Integer.parseInt(columns[9])/100.);
                article.setSingleDeposit(Integer.parseInt(columns[10])/100.);
                article.setCrateDeposit(Integer.parseInt(columns[11])/100.);
                article.setMetricUnits(MetricUnits.valueOf(columns[12].replace("WEIGHT", "GRAM")));
                article.setPriceList(priceListHashMap.get(columns[13]));
                article.setContainerDef(ContainerDefinition.valueOf(columns[14]));
                article.setContainerSize(Double.parseDouble(columns[15].replaceAll(",", ".")));
                article.setSuppliersItemNumber(Integer.parseInt(columns[16]));
                article.setWeighAble(!Boolean.parseBoolean(columns[17]));
                article.setListed(Boolean.parseBoolean(columns[18]));
                article.setShowInShop(Boolean.parseBoolean(columns[19]));
                article.setDeleted(Boolean.parseBoolean(columns[20]));
                article.setPrintAgain(Boolean.parseBoolean(columns[21]));
                article.setDeleteAllowed(Boolean.parseBoolean(columns[22]));
                article.setLoss(Integer.parseInt(columns[23]));
                article.setInfo(columns[24]);
                article.setSold(Integer.parseInt(columns[25]));
                article.setSpecialPriceMonth(extractOffers(Tools.extract(Boolean.class, columns[26], "_", Boolean::parseBoolean), Integer.parseInt(columns[7])));
                article.setDelivered(Integer.parseInt(columns[27]));
                //TODO: article.setInvShelf(Tools.extract(ArrayList::new, columns[28], "_", Integer::parseInt));
                //TODO: article.setInvStock(Tools.extract(ArrayList::new, columns[29], "_", Integer::parseInt));
                //TODO: article.setInvPrice(Integer.parseInt(columns[30])/100.);
                article.setIntake(java.sql.Date.valueOf(LocalDate.now()));
                article.setLastBuy(null);
                article.setLastDelivery(Date.valueOf(LocalDate.now()));
                article.setDeletedDate(null);
                article.setCooling(Cooling.valueOf(columns[35]));
                article.setCoveredIntake(Boolean.parseBoolean(columns[36]));
                articles.add(article);
            }
            view.setItemProgress(5);
            model.saveAllItems(articles);
            view.setItemProgress(6);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void cancel() {
        view.back();
        ConfigManager.getHeader().put("dbIsInitialized", true);
        ConfigManager.updateFile();
    }

    private List<Offer> extractOffers(Boolean[] months, int price) {
        int from = -1;
        ArrayList<Offer> out = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 1; i < months.length + 1; i++) {
            if (months[i - 1]) {
                if (from == -1) from = i;
                continue;
            }
            if (from == -1) continue;
            Offer offer = new Offer();
            offer.setSpecialNetPrice(price);
            offer.setFromDate(Date.valueOf(LocalDate.of(today.getYear(), from, 1)));
            offer.setToDate(Date.valueOf(LocalDate.of(today.getYear(), from + (i - 1 - from), 1).with(TemporalAdjusters.lastDayOfMonth())));
            offer.setRepeatMode(Repeat.EVERY_YEAR);
            out.add(offer);
            from = -1;
        }
        return out;
    }

    @Override
    public DataImportView getView() {
        return view;
    }

    @Override
    public Model getModel() {
        return model;
    }
}
