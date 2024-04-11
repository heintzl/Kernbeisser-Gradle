package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Selection;
import java.util.Arrays;
import java.util.Collection;

public interface ExpressionFactory<P, V> extends SelectionFactory<P> {
  Expression<V> createExpression(Source<P> source, CriteriaBuilder cb);

  default PredicateFactory<P> isNull() {
    return PredicateFactory.isNull(this);
  }

  default PredicateFactory<P> eq(Object value) {
    return PredicateFactory.eq(this, value);
  }

  default <T> PredicateFactory<P> in(Collection<V> values) {
    return PredicateFactory.in(this, values);
  }

  default PredicateFactory<P> in(V... objects) {
    return PredicateFactory.in(this, objects);
  }

  default PredicateFactory<P> inExpression(ExpressionFactory<P, V>... expressionFactories) {
    return PredicateFactory.inExpression(this, Arrays.stream(expressionFactories).toList());
  }

  default <N> ExpressionFactory<P, N> as(Class<N> newClass) {
    return ((source, cb) -> this.createExpression(source, cb).as(newClass));
  }

  default OrderFactory<P> asc() {
    return ((source, cb) -> cb.asc(this.createExpression(source, cb)));
  }

  default OrderFactory<P> desc() {
    return ((source, cb) -> cb.desc(this.createExpression(source, cb)));
  }

  @Override
  default Selection<?> createSelection(Source<P> source, CriteriaBuilder cb) {
    return createExpression(source, cb);
  }

  static <P> ExpressionFactory<P, String> upper(
      ExpressionFactory<P, String> stringExpressionFactory) {
    return ((source, cb) -> cb.upper(stringExpressionFactory.createExpression(source, cb)));
  }

  static <P> ExpressionFactory<P, String> lower(
      ExpressionFactory<P, String> stringExpressionFactory) {
    return ((source, cb) -> cb.lower(stringExpressionFactory.createExpression(source, cb)));
  }

  static <P, V extends Number> ExpressionFactory<P, V> diff(
      ExpressionFactory<P, V> a, ExpressionFactory<P, V> b) {
    return ((source, cb) ->
        cb.diff(a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P> ExpressionFactory<P, Integer> mod(
      ExpressionFactory<P, Integer> a, ExpressionFactory<P, Integer> b) {
    return ((source, cb) -> cb.mod(a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P, V extends Number> ExpressionFactory<P, V> minus(
      ExpressionFactory<P, V> a, ExpressionFactory<P, V> b) {
    return ((source, cb) ->
        cb.sum(a.createExpression(source, cb), cb.neg(b.createExpression(source, cb))));
  }

  static <P, V extends Number> ExpressionFactory<P, V> plus(
      ExpressionFactory<P, V> a, ExpressionFactory<P, V> b) {
    return ((source, cb) -> cb.sum(a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P, V extends Number> ExpressionFactory<P, V> moreOrEq(
      ExpressionFactory<P, V> a, ExpressionFactory<P, V> b) {
    return ((source, cb) -> cb.sum(a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P, V extends Number> ExpressionFactory<P, V> sum(ExpressionFactory<P, V> a) {
    return ((source, cb) -> cb.sum(a.createExpression(source, cb)));
  }

  static <P, V extends Comparable<V>> ExpressionFactory<P, V> max(ExpressionFactory<P, V> a) {
    return ((source, cb) -> cb.greatest(a.createExpression(source, cb)));
  }

  static <P, V extends Comparable<V>> ExpressionFactory<P, V> min(ExpressionFactory<P, V> a) {
    return ((source, cb) -> cb.least(a.createExpression(source, cb)));
  }

  static <P> ExpressionFactory<P, String> concat(
      ExpressionFactory<P, String> a, ExpressionFactory<P, String> b) {
    return ((source, cb) ->
        cb.concat(a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P, V> ExpressionFactory<P, V> asExpression(V v) {
    return ((source, cb) -> cb.literal(v));
  }
}
