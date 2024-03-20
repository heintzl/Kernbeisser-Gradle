package kernbeisser.DBConnection;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.util.*;
import java.util.stream.Stream;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class QueryBuilder<P, R> {

  private final Class<P> tableClass;

  private final Class<R> resultClass;
  
  @NotNull private final Collection<? extends SelectionFactory<P>> selections;
  
  private final boolean multiselect;
  private int maxResults = Integer.MAX_VALUE;
  @NotNull private final Collection<PredicateFactory<P>> conditions = new ArrayList<>();
  @NotNull private final Collection<OrderFactory<P>> orders = new ArrayList<>();

  @NotNull private final Collection<ExpressionFactory<P, ?>> groupBy = new ArrayList<>();

  

  public static <P> QueryBuilder<P, P> selectAll(Class<P> tableClass) {
    return new QueryBuilder<>(
        tableClass, tableClass, Collections.emptyList(), false);
  }

  public static <P> QueryBuilder<P, Tuple> select(
      Class<P> sourceClass, Collection<? extends SelectionFactory<P>> selections) {
    if (selections.isEmpty())
      throw new IllegalArgumentException("query without selection is not allowed!");
    return new QueryBuilder<>(sourceClass, Tuple.class, selections, true);
  }

  public static <P, S extends SelectionFactory<P>> QueryBuilder<P, Tuple> select(
      Class<P> sourceClass, S... selections) {
    return select(sourceClass, Arrays.stream(selections).toList());
  }

  public static <P> QueryBuilder<P, Tuple> select(FieldIdentifier<P, ?>... selections) {
    if(selections.length == 0) throw new IllegalArgumentException("query without selection is not allowed!");
    return select(
        selections[0].getTableClass(),
        Arrays.stream(selections).map(e -> (SelectionFactory<P>) e).toList());
  }
  
  
  public static <P,R> QueryBuilder<P, R> select(FieldIdentifier<P, R> selection) {
    return new QueryBuilder<>(selection.getTableClass(), selection.getPropertyClass(), Collections.singleton(selection), true);
  }

  public QueryBuilder<P, R> where(PredicateFactory<P>... conditions) {
    return where(Arrays.stream(conditions).toList());
  }

  public QueryBuilder<P, R> where(Collection<PredicateFactory<P>> conditions) {
    this.conditions.addAll(conditions);
    return this;
  }

  public QueryBuilder<P, R> groupBy(Collection<ExpressionFactory<P, ?>> groupings) {
    this.groupBy.addAll(groupings);
    return this;
  }

  public QueryBuilder<P, R> groupBy(ExpressionFactory<P, ?>... groupings) {
    return groupBy(Arrays.stream(groupings).toList());
  }

  public QueryBuilder<P, R> orderBy(Collection<OrderFactory<P>> fieldIdentifiers) {
    this.orders.addAll(fieldIdentifiers);
    return this;
  }

  @SuppressWarnings("unchecked")
  public QueryBuilder<P, R> orderBy(OrderFactory<P>... fieldIdentifiers) {
    return orderBy(Arrays.stream(fieldIdentifiers).toList());
  }

  public QueryBuilder<P, R> limit(int maxResults) {
    this.maxResults = maxResults;
    return this;
  }

  public List<R> getResultList(EntityManager em) {
    return buildQuery(em).getResultList();
  }

  public List<R> getResultList() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getResultList(em);
  }

  public Stream<R> getResultStream(EntityManager em) {
    return buildQuery(em).getResultStream();
  }

  public R getSingleResult(EntityManager em) {
    return buildQuery(em).getSingleResult();
  }

  public R getSingleResult() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getSingleResult(em);
  }

  public Optional<R> getSingleResultOptional(EntityManager em) {
    try {
      return Optional.of(buildQuery(em).getSingleResult());
    } catch (NoResultException noResultException) {
      return Optional.empty();
    }
  }

  public Optional<R> getSingleResultOptional() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getSingleResultOptional(em);
  }

  public TypedQuery<R> buildQuery(EntityManager em) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<R> cr = cb.createQuery(resultClass);
    Root<P> root = cr.from(tableClass);
    Source<P> source = Source.rootSource(root);
    var selection = this.selections.stream()
            .map(e -> e.createSelection(source, cb))
            .toArray(Selection[]::new);
    var whereConditions = conditions.stream()
            .map(e -> e.createPredicate(source, cb))
            .toArray(Predicate[]::new);
    var groupByExpressions = groupBy.stream()
            .map(e -> e.createExpression(source, cb))
            .toArray(Expression[]::new);
    var orderBy = orders.stream()
            .map(fieldIdentifier -> fieldIdentifier.createOrder(source, cb))
            .toArray(Order[]::new);
    if(multiselect) {
      cr.multiselect(selection);
    }
    else {
      cr.select((Root<R>)root);
    }
    return em.createQuery(
            cr.where(whereConditions)
                .groupBy(groupByExpressions)
                .orderBy(orderBy))
        .setMaxResults(maxResults);
  }

  public <O, X extends Exception> O consumeStream(ThrowableFunction<Stream<R>, O, X> function)
      throws X {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return function.apply(getResultStream(em));
  }

  public static <P, V> boolean propertyWithThatValueExists(FieldIdentifier<P, V> property, V eq) {
    return QueryBuilder.select(property)
        .where(property.eq(eq))
        .consumeStream(steam -> steam.findAny().isPresent());
  }

  public static <P, V> Optional<P> getByPropertyOptional(FieldIdentifier<P, V> property, V eq) {
    return QueryBuilder.selectAll(property.getTableClass())
        .where(property.eq(eq))
        .getSingleResultOptional();
  }

  public static <P, V> P getByProperty(FieldIdentifier<P, V> property, V eq)
      throws NoResultException {
    return QueryBuilder.selectAll(property.getTableClass())
        .where(property.eq(eq))
        .getSingleResult();
  }
  
  public boolean hasResult() {
    return consumeStream(
            stream -> stream.findAny().isPresent()
    );
  }
  
  public interface ThrowableFunction<I, O, X extends Exception> {
    O apply(I input) throws X;
  }
}
