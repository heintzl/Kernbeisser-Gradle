package kernbeisser.Windows.Inventory.Report;

import java.util.Collection;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import kernbeisser.DBEntities.Shelf;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class InventoryReportDTO {
  Collection<ShelfReportDTO> shelves;
  double shelvesSum;

  public static InventoryReportDTO generate(EntityManager em) {
    Collection<ShelfReportDTO> shelfReportDTOS =
        em.createQuery("select s from Shelf s", Shelf.class)
            .getResultStream()
            .map(e -> ShelfReportDTO.ofShelf(em, e))
            .collect(Collectors.toList());
    return new InventoryReportDTO(
        shelfReportDTOS, shelfReportDTOS.stream().mapToDouble(ShelfReportDTO::getShelfSum).sum());
  }
}
