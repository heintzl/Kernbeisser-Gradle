package kernbeisser.Forms.FormImplemetations.Shelf;

import java.util.function.Supplier;
import kernbeisser.DBEntities.Shelf;
import kernbeisser.Exeptions.PermissionKeyRequiredException;
import kernbeisser.Forms.FormController;
import kernbeisser.Forms.ObjectForm.ObjectForm;

public class ShelfController extends FormController<ShelfView, ShelfModel, Shelf> {

  public ShelfController() throws PermissionKeyRequiredException {
    super(new ShelfModel());
  }

  @Override
  public void fillView(ShelfView shelfView) {}

  @Override
  public void addPermission() {}

  @Override
  public void editPermission() {}

  @Override
  public void removePermission() {}

  @Override
  public ObjectForm<Shelf> getObjectContainer() {
    return getView().getObjectForm();
  }

  @Override
  public Supplier<Shelf> defaultFactory() {
    return Shelf::new;
  }
}
