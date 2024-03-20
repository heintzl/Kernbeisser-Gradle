package kernbeisser.DBConnection;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.jetbrains.annotations.NotNull;

// X is the source for the paths, for example if the criteria query is an Article query first the
// source is article,
// but then on a member field property the field is the field for example on Article.priceList.id is
// the source PriceList.
// source is an abstraction of Join<X,Y> and Root<X> combining there common behavior.
public interface Source<X> {
  <T> Source<T> join(FieldIdentifier<X, T> futureExpressionIdentifier);

  <Y> Path<Y> get(FieldIdentifier<X, Y> futureExpressionIdentifier);

  From<?, X> getFrom();

  public static <X> Source<X> rootSource(@NotNull Root<X> root) {
    return new Source<X>() {
      @Override
      public <T> Source<T> join(FieldIdentifier<X, T> futureExpressionIdentifier) {
        return joinSource(root.join(futureExpressionIdentifier.getName()));
      }

      @Override
      public <Y> Path<Y> get(FieldIdentifier<X, Y> futureExpressionIdentifier) {
        return root.get(futureExpressionIdentifier.getName());
      }

      @Override
      public From<?, X> getFrom() {
        return root;
      }
    };
  }

  public static <A, X> Source<X> joinSource(@NotNull Join<A, X> join) {
    return new Source<X>() {
      @Override
      public <T> Source<T> join(FieldIdentifier<X, T> futureExpressionIdentifier) {
        return joinSource(join.join(futureExpressionIdentifier.getName()));
      }

      @Override
      public <Y> Path<Y> get(FieldIdentifier<X, Y> futureExpressionIdentifier) {
        return join.get(futureExpressionIdentifier.getName());
      }

      @Override
      public From<?, X> getFrom() {
        return join;
      }
    };
  }
}
