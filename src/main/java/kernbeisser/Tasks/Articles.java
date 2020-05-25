package kernbeisser.Tasks;

import kernbeisser.DBEntities.Article;
import kernbeisser.DBEntities.Offer;
import kernbeisser.DBEntities.PriceList;
import kernbeisser.DBEntities.Supplier;
import kernbeisser.Enums.*;
import kernbeisser.Exeptions.CannotParseException;
import kernbeisser.Main;
import kernbeisser.Useful.Tools;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Articles {

    public static Article parse(String[] rawArticleValues, HashSet<Long> barcodes, HashSet<String> names, HashMap<String,Supplier> suppliers, HashMap<String,PriceList> priceLists) throws CannotParseException {
            Article article = new Article();
            article.setName(rawArticleValues[1].replace("%","Prozent"));
            if (names.contains(article.getName().toUpperCase().replace(" ", ""))) {
                Main.logger.warn("Ignored " + article.getName() + " because the name is already taken");
                throw new CannotParseException("Article name is already taken");
            } else {
                names.add(article.getName().toUpperCase().replace(" ", ""));
            }
            article.setKbNumber(Integer.parseInt(rawArticleValues[2]));
            article.setAmount(Integer.parseInt(rawArticleValues[3]));
            article.setNetPrice(Integer.parseInt(rawArticleValues[4]) / 100.);
            article.setSupplier(suppliers.get(rawArticleValues[5].replace("GRE", "GR")));
            try {
                Long ib = Long.parseLong(rawArticleValues[6]);
                if (!barcodes.contains(ib)) {
                    article.setBarcode(ib);
                    barcodes.add(ib);
                } else {
                    article.setBarcode(null);
                }
            } catch (NumberFormatException e) {
                article.setBarcode(null);
            }
            //columns[7] look at line 311
            article.setVat(Boolean.parseBoolean(rawArticleValues[8]) ? VAT.LOW : VAT.HIGH);
            article.setSurcharge(Integer.parseInt(rawArticleValues[9]) / 1000.);
            article.setSingleDeposit(Integer.parseInt(rawArticleValues[10]) / 100.);
            article.setContainerDeposit(Integer.parseInt(rawArticleValues[11]) / 100.);
            article.setMetricUnits(
                    MetricUnits.valueOf(rawArticleValues[12].replace("WEIGHT", "GRAM").replace("STACK", "PIECE")));
            article.setPriceList(priceLists.get(rawArticleValues[13]));
            article.setContainerDef(ContainerDefinition.valueOf(rawArticleValues[14]));
            article.setContainerSize(Double.parseDouble(rawArticleValues[15].replaceAll(",", ".")));
            article.setSuppliersItemNumber(Integer.parseInt(rawArticleValues[16]));
            article.setWeighAble(!Boolean.parseBoolean(rawArticleValues[17]));
            article.setListed(Boolean.parseBoolean(rawArticleValues[18]));
            article.setShowInShop(Boolean.parseBoolean(rawArticleValues[19]));
            article.setDeleted(Boolean.parseBoolean(rawArticleValues[20]));
            article.setPrintAgain(Boolean.parseBoolean(rawArticleValues[21]));
            article.setDeleteAllowed(Boolean.parseBoolean(rawArticleValues[22]));
            article.setLoss(Integer.parseInt(rawArticleValues[23]));
            article.setInfo(rawArticleValues[24]);
            article.setSold(Integer.parseInt(rawArticleValues[25]));
            article.setSpecialPriceMonth(
                    extractOffers(Tools.extract(Boolean.class, rawArticleValues[26], "_", Boolean::parseBoolean),
                                  Integer.parseInt(rawArticleValues[7])/100f));
            article.setDelivered(Integer.parseInt(rawArticleValues[27]));
            //TODO: article.setInvShelf(Tools.extract(ArrayList::new, columns[28], "_", Integer::parseInt));
            //TODO: article.setInvStock(Tools.extract(ArrayList::new, columns[29], "_", Integer::parseInt));
            //TODO: article.setInvPrice(Integer.parseInt(columns[30])/100.);
            article.setIntake(Instant.now());
            article.setLastDelivery(Instant.now());
            article.setDeletedDate(null);
            article.setCooling(Cooling.valueOf(rawArticleValues[35]));
            article.setCoveredIntake(Boolean.parseBoolean(rawArticleValues[36]));
            return article;
    }

    private static List<Offer> extractOffers(Boolean[] months, double price) {
        int from = -1;
        ArrayList<Offer> out = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 1; i < months.length + 1; i++) {
            if (months[i - 1]) {
                if (from == -1) {
                    from = i;
                }
                continue;
            }
            if (from == -1) {
                continue;
            }
            Offer offer = new Offer();
            offer.setSpecialNetPrice(price);
            offer.setFromDate(Date.valueOf(LocalDate.of(today.getYear(), from, 1)));
            offer.setToDate(Date.valueOf(
                    LocalDate.of(today.getYear(), from + (i - 1 - from), 1).with(TemporalAdjusters.lastDayOfMonth())));
            offer.setRepeatMode(Repeat.EVERY_YEAR);
            out.add(offer);
            from = -1;
        }
        return out;
    }
}
