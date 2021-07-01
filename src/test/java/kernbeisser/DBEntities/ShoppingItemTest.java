package kernbeisser.DBEntities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.Instant;
import kernbeisser.Enums.MetricUnits;
import kernbeisser.Enums.ShopRange;
import kernbeisser.Enums.VAT;
import lombok.var;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingItemTest {

  private Article testPriceCalcArticle(double surcharge, double vatValue, double netPrice) {
    PriceList priceList = new PriceList();
    Supplier supplier = new Supplier();
    VAT vat = mock(VAT.class);
    ShopRange shopRange = mock(ShopRange.class);
    SurchargeGroup surchargeGroup = mock(SurchargeGroup.class);
    when(surchargeGroup.getSurcharge()).thenReturn(surcharge);
    when(vat.getValue()).thenReturn(vatValue);
    return new Article(
        0,
        2,
        priceList,
        false,
        true,
        false,
        Instant.now(),
        false,
        "Test",
        "KK",
        netPrice,
        MetricUnits.GRAM,
        supplier,
        42,
        vat,
        200,
        88726434L,
        2.,
        0.25,
        1.5,
        "Test",
        Instant.now(),
        surchargeGroup,
        shopRange,
        false,
        surcharge);
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "0.2,0.07,2,40,true,0.5,1.41",
        "0.2,0.07,4,40,true,0.5,2.82",
        "0.2,0.09,2,40,true,0.5,1.44",
        "0.2,0.07,2,20,true,0.5,1.88",
        "0.1,0.07,2,20,false,0.5,1.88",
        "0.1,0.07,2,20,false,0.7,1.88",
        "0.1,0.07,0.21,20,false,0.8,0.2",
        "0.1,0.12,.3,26,false,0.5,0.27",
        "0.2,0.07,2,20,true,0.5,1.88",
        "0.2,0.42,2,20,true,0.6,2.5",
        "0.2,0.07,2,20,true,0.6,1.88"
      })
  void getItemRetailPrice(
      double surcharge,
      double vatValue,
      double netPrice,
      int discount,
      boolean hasContainerDiscount,
      double containerDiscountReduction,
      double expected) {
    try (var staticMock = mockStatic(ShoppingItem.class)) {
      staticMock
          .when(ShoppingItem::getContainerSurchargeReduction)
          .thenReturn(containerDiscountReduction);
      staticMock.when(ShoppingItem::getOfferPrefix).thenReturn("AK");
    }
    assertEquals(
        new ShoppingItem(
                testPriceCalcArticle(surcharge, vatValue, netPrice),
                0,
                discount,
                hasContainerDiscount)
            .getItemRetailPrice(),
        expected);
  }
}
