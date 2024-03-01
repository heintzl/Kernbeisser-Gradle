package kernbeisser.Tasks.DTO;

import kernbeisser.DBEntities.Offer;
import lombok.Getter;

@Getter
public class OfferPrice {
  private Price[] staffel;
  private String ab;
  private String bis;
  private String vk;

  private Offer unpackOffer() {
    throw new UnsupportedOperationException();
  }
}
