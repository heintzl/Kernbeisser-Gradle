package kernbeisser.Enums;

import lombok.Getter;

public enum ArticleConstants {
  PRODUCE(-1),
  BAKERY(-2),
  DEPOSIT(-3),
  SOLIDARITY(-4),
  CUSTOM_PRODUCT(-5);

  @Getter private final int uniqueIdentifier;

  ArticleConstants(int uniqueIdentifier) {
    this.uniqueIdentifier = uniqueIdentifier;
  }
}
