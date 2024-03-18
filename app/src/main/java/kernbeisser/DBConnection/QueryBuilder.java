package kernbeisser.DBConnection;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.*;
import java.util.stream.Stream;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class QueryBuilder<T> {

  private final Class<T> tableClass;
  @NotNull private final Collection<Condition> conditions = new ArrayList<>();
  @NotNull private final Collection<FieldOrder<T>> orders = new ArrayList<>();

  public static <T> QueryBuilder<T> queryTable(Class<T> tableClass) {
    return new QueryBuilder<>(tableClass);
  }

  public QueryBuilder<T> where(Condition... conditions) {
    return where(Arrays.stream(conditions).toList());
  }

  public QueryBuilder<T> where(Collection<Condition> conditions) {
    this.conditions.addAll(conditions);
    return this;
  }

  public QueryBuilder<T> orderBy(Collection<FieldOrder<T>> fieldIdentifiers) {
    this.orders.addAll(fieldIdentifiers);
    return this;
  }

  @SuppressWarnings("unchecked")
  public QueryBuilder<T> orderBy(FieldOrder<T>... fieldIdentifiers) {
    return orderBy(Arrays.stream(fieldIdentifiers).toList());
  }

  public List<T> getResultList(EntityManager em) {
    return buildQuery(em).getResultList();
  }

  public List<T> getResultList() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getResultList(em);
  }

  public Stream<T> getResultStream(EntityManager em) {
    return buildQuery(em).getResultStream();
  }

  public Stream<T> getResultStream() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return buildQuery(em).getResultStream();
  }

  public T getSingleResult(EntityManager em) {
    return buildQuery(em).getSingleResult();
  }

  public T getSingleResult() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getSingleResult(em);
  }
  
  public Optional<T> getSingleResultOptional(EntityManager em) {
    try {
      return Optional.of(buildQuery(em).getSingleResult());
    }catch (NoResultException noResultException){
      return Optional.empty();
    }
    
  }
  
  public Optional<T> getSingleResultOptional() {
    @Cleanup EntityManager em = DBConnection.getEntityManager();
    @Cleanup("commit")
    EntityTransaction et = em.getTransaction();
    et.begin();
    return getSingleResultOptional(em);
  }

  public TypedQuery<T> buildQuery(EntityManager em) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<T> cr = cb.createQuery(tableClass);
    Root<T> root = cr.from(tableClass);
    cr.select(root);
    return em.createQuery(
        cr.where(conditions.stream().map(e -> e.buildPredicate(cb, root)).toArray(Predicate[]::new))
            .orderBy(
                orders.stream()
                    .map(fieldIdentifier -> fieldIdentifier.toOrder(root, cb))
                    .toArray(Order[]::new)));
  }
}
