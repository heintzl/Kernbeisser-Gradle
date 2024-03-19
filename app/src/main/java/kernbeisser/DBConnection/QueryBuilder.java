package kernbeisser.DBConnection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class QueryBuilder<P> {

  private final Class<P> tableClass;
  @NotNull private final Collection<PredicateFactory<P>> conditions = new ArrayList<>();
  @NotNull private final Collection<OrderFactory<P>> orders = new ArrayList<>();

  public static <P> QueryBuilder<P> queryTable(Class<P> tableClass) {
    return new QueryBuilder<>(tableClass);
  }

  public QueryBuilder<P> where(PredicateFactory<P>... conditions) {
    return where(Arrays.stream(conditions).toList());
  }

  public QueryBuilder<P> where(Collection<PredicateFactory<P>> conditions) {
    this.conditions.addAll(conditions);
    return this;
  }

  public QueryBuilder<P> orderBy(Collection<OrderFactory<P>> fieldIdentifiers) {
    this.orders.addAll(fieldIdentifiers);
    return this;
  }

  @SuppressWarnings("unchecked")
  public QueryBuilder<P> orderBy(OrderFactory<P>... fieldIdentifiers) {
    return orderBy(Arrays.stream(fieldIdentifiers).toList());
  }

  public List<P> getResultList(EntityManager em) {
    return buildQuery(em).getResultList();
  }

  public List<P> getResultList() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getResultList(em);
  }

  public Stream<P> getResultStream(EntityManager em) {
    return buildQuery(em).getResultStream();
  }

  public <R> R consumeSteam(Function<Stream<P>, R> streamConsumer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return streamConsumer.apply(buildQuery(em).getResultStream());
  }

  public <R> void consumeSteam(Consumer<Stream<P>> streamConsumer) {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    streamConsumer.accept(buildQuery(em).getResultStream());
  }

  public P getSingleResult(EntityManager em) {
    return buildQuery(em).getSingleResult();
  }

  public P getSingleResult() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getSingleResult(em);
  }

  public Optional<P> getSingleResultOptional(EntityManager em) {
    try {
      return Optional.of(buildQuery(em).getSingleResult());
    } catch (NoResultException noResultException) {
      return Optional.empty();
    }
  }

  public Optional<P> getSingleResultOptional() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getSingleResultOptional(em);
  }

  public TypedQuery<P> buildQuery(EntityManager em) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<P> cr = cb.createQuery(tableClass);
    Root<P> root = cr.from(tableClass);
    cr.select(root);
    return em.createQuery(
        cr.where(
                conditions.stream()
                    .map(e -> e.createPredicate(Source.rootSource(root), cb))
                    .toArray(Predicate[]::new))
            .orderBy(
                orders.stream()
                    .map(
                        fieldIdentifier -> fieldIdentifier.createOrder(Source.rootSource(root), cb))
                    .toArray(Order[]::new)));
  }
}
