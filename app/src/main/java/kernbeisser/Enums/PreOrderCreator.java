package kernbeisser.Enums;

import jiconfont.icons.font_awesome.FontAwesome;
import kernbeisser.DBEntities.PreOrder;
import kernbeisser.Useful.Icons;
import kernbeisser.Useful.Named;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

public enum PreOrderCreator implements Named {
  PRE_ORDER_MANAGER("Bestelldienst"),
  SELF("selbst"),
  ONLINE("online"),
  POS("LD");

  @Getter private final String name;

  PreOrderCreator(String name) {
    this.name = name;
  }

  public Icon getIcon() {
    switch (this) {
      case SELF -> {
        return Icons.preorderSelfIcon;
      }
      case ONLINE -> {
        return Icons.preorderCloudIcon;
      }
      case POS -> {
        return Icons.preoorderPosIcon;
      }
      default -> {
        return Icons.preoorderShopManagerIcon;
      }
    }
  }

  public String getIconCode() {
    switch (this) {
      case SELF -> {
        return "\uf007";
      }
      case ONLINE -> {
        return "\uf0c2";
      }
      case POS -> {
        return "\uf291";
      }
      default -> {
        return "\uf044";
      }
    }
  }


  @Override
  public String toString() {
    return this.getName();
  }
}
