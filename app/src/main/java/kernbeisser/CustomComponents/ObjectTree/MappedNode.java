package kernbeisser.CustomComponents.ObjectTree;

import java.util.List;
import java.util.Map;

public class MappedNode<T> extends CachedNode<T> {

  MappedNode(Map<T, List<T>> map, T startPoint) {
    super(null, startPoint, map::get);
  }
}
