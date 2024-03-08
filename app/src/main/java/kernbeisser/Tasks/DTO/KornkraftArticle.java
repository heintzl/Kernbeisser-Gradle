package kernbeisser.Tasks.DTO;

import lombok.Getter;

@Getter
public class KornkraftArticle {
  private String inetwg;
  private long artnr;
  private String image_src;
  private String regaltext;
  private double vpe;
  private String gebinde;
  private String hersteller;
  private Source herkunft;
  private String[] merkmale;
  private Price[] staffelpreise;
  private String rabatt;
  private String vk;
  private String kennung;
  private String regaltextzusatz;
  private OfferPrice aktionspreise;
  private String artikelgruppe;
}
