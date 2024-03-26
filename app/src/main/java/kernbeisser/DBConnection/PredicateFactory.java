package kernbeisser.DBConnection;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;
import java.util.Collection;

public interface PredicateFactory<P> extends ExpressionFactory<P, Boolean> {
  Predicate createPredicate(Source<P> source, CriteriaBuilder cb);

  default PredicateFactory<P> not() {
    return ((source, cb) -> createPredicate(source, cb).not());
  }

  static <P, V> PredicateFactory<P> isNull(ExpressionFactory<P, V> expressionFactory) {
    return ((source, cb) -> cb.isNull(expressionFactory.createExpression(source, cb)));
  }

  static <P, V> PredicateFactory<P> eq(ExpressionFactory<P, V> expressionFactory, Object object) {
    return ((source, cb) -> cb.equal(expressionFactory.createExpression(source, cb), object));
  }

  @SafeVarargs
  static <P, V> PredicateFactory<P> in(ExpressionFactory<P, V> expressionFactory, V... objects) {
    return ((source, cb) -> expressionFactory.createExpression(source, cb).in((Object[]) objects));
  }

  static <P, V> PredicateFactory<P> in(
      ExpressionFactory<P, V> expressionFactory, Collection<V> objects) {
    return ((source, cb) -> expressionFactory.createExpression(source, cb).in(objects.toArray()));
  }

  static <P, V> PredicateFactory<P> inExpression(
      ExpressionFactory<P, V> target, Collection<ExpressionFactory<P, V>> in) {
    return ((source, cb) ->
        target
            .createExpression(source, cb)
            .in(
                in.stream()
                    .map(expressionFactory -> expressionFactory.createExpression(source, cb))
                    .toArray(Expression[]::new)));
  }

  static <P, V extends Comparable<? super V>> PredicateFactory<P> between(
      V inBetween, ExpressionFactory<P, V> a, ExpressionFactory<P, V> b) {
    return ((source, cb) ->
        cb.between(
            cb.literal(inBetween), a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P> PredicateFactory<P> like(
      ExpressionFactory<P, String> expressionFactory, String pattern) {
    return ((source, cb) -> cb.like(expressionFactory.createExpression(source, cb), pattern));
  }

  @SafeVarargs
  static <P> PredicateFactory<P> and(PredicateFactory<P>... predicateFactories) {
    return ((source, cb) ->
        cb.and(
            Arrays.stream(predicateFactories)
                .map(predicateFactory -> predicateFactory.createPredicate(source, cb))
                .toArray(Predicate[]::new)));
  }

  @SafeVarargs
  static <P> PredicateFactory<P> or(PredicateFactory<P>... predicateFactories) {
    return ((source, cb) ->
        cb.or(
            Arrays.stream(predicateFactories)
                .map(predicateFactory -> predicateFactory.createPredicate(source, cb))
                .toArray(Predicate[]::new)));
  }

  static <P, V extends Comparable<V>> PredicateFactory<P> lessOrEq(
      ExpressionFactory<P, V> a, ExpressionFactory<P, V> b) {
    return ((source, cb) ->
        cb.lessThanOrEqualTo(a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P, V extends Comparable<V>> PredicateFactory<P> greaterOrEq(
      ExpressionFactory<P, V> a, ExpressionFactory<P, V> b) {
    return ((source, cb) ->
        cb.greaterThanOrEqualTo(a.createExpression(source, cb), b.createExpression(source, cb)));
  }

  static <P, C extends Collection<E>, E> PredicateFactory<P> isMember(
      ExpressionFactory<P, E> member, ExpressionFactory<P, C> collection) {
    return (source, cb) ->
        cb.isMember(member.createExpression(source, cb), collection.createExpression(source, cb));
  }

  @Override
  default Expression<Boolean> createExpression(Source<P> source, CriteriaBuilder cb) {
    return createPredicate(source, cb);
  }
}
