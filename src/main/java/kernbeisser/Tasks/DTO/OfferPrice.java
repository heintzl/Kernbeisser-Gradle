package kernbeisser.Tasks.DTO;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import kernbeisser.DBEntities.Offer;
import lombok.Getter;

@Getter
public class OfferPrice {
  private Price[] staffel;
  private String ab;
  private String bis;
  private String vk;

  private Offer unpackOffer() {
    Offer offer = new Offer();
    offer.setFromDate(
        Instant.from(
            LocalDate.from(Catalog.DATE_FORMAT.parse(ab)).atStartOfDay(ZoneId.systemDefault())));
    offer.setToDate(
        Instant.from(
            LocalDate.from(Catalog.DATE_FORMAT.parse(bis))
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .minusNanos(1)));
    offer.setSpecialNetPrice(Double.parseDouble(staffel[0].getEk().replace(",", ".")));
    return offer;
  }
}
