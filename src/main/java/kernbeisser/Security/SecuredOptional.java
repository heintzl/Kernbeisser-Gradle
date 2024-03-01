package kernbeisser.Security;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import rs.groump.AccessDeniedException;

/**
 * same as optional but filters permission key required exceptions
 *
 * @param <T>
 */
public class SecuredOptional<T> {
  private static final SecuredOptional<?> EMPTY = new SecuredOptional<>();

  /** If non-null, the value; if null, indicates no value is present */
  private final T value;

  /**
   * Constructs an empty instance.
   *
   * @implNote Generally only one empty instance, {@link SecuredOptional#EMPTY}, should exist per
   *     VM.
   */
  private SecuredOptional() {
    this.value = null;
  }

  /**
   * Returns an empty {@code SecureOptional} instance. No value is present for this SecureOptional.
   *
   * @apiNote Though it may be tempting to do so, avoid testing if an object is empty by comparing
   *     with {@code ==} against instances returned by {@code Option.empty()}. There is no guarantee
   *     that it is a singleton. Instead, use {@link #isPresent()}.
   * @param <T> Type of the non-existent value
   * @return an empty {@code SecureOptional}
   */
  public static <T> SecuredOptional<T> empty() {
    @SuppressWarnings("unchecked")
    SecuredOptional<T> t = (SecuredOptional<T>) EMPTY;
    return t;
  }

  /**
   * Constructs an instance with the value present.
   *
   * @param value the non-null value to be present
   * @throws NullPointerException if value is null
   */
  private SecuredOptional(T value) {
    this.value = Objects.requireNonNull(value);
  }

  /**
   * Returns an {@code SecureOptional} with the specified present non-null value.
   *
   * @param <T> the class of the value
   * @param value the value to be present, which must be non-null
   * @return an {@code SecureOptional} with the value present
   * @throws NullPointerException if value is null
   */
  public static <T> SecuredOptional<T> of(T value) {
    return new SecuredOptional<>(value);
  }

  /**
   * Returns an {@code SecureOptional} describing the specified value, if non-null, otherwise
   * returns an empty {@code SecureOptional}.
   *
   * @param <T> the class of the value
   * @param value the possibly-null value to describe
   * @return an {@code SecureOptional} with a present value if the specified value is non-null,
   *     otherwise an empty {@code SecureOptional}
   */
  public static <T> SecuredOptional<T> ofNullable(T value) {
    return value == null ? empty() : of(value);
  }

  /**
   * If a value is present in this {@code SecureOptional}, returns the value, otherwise throws
   * {@code NoSuchElementException}.
   *
   * @return the non-null value held by this {@code SecureOptional}
   * @throws NoSuchElementException if there is no value present
   * @see SecuredOptional#isPresent()
   */
  public T get() {
    if (value == null) {
      throw new NoSuchElementException("No value present");
    }
    return value;
  }

  /**
   * Return {@code true} if there is a value present, otherwise {@code false}.
   *
   * @return {@code true} if there is a value present, otherwise {@code false}
   */
  public boolean isPresent() {
    return value != null;
  }

  /**
   * If a value is present, invoke the specified consumer with the value, otherwise do nothing.
   *
   * @param consumer block to be executed if a value is present
   * @throws NullPointerException if value is present and {@code consumer} is null
   */
  public void ifPresent(Consumer<? super T> consumer) {
    if (value != null) consumer.accept(value);
  }

  /**
   * If a value is present, and the value matches the given predicate, return an {@code
   * SecureOptional} describing the value, otherwise return an empty {@code SecureOptional}.
   *
   * @param predicate a predicate to apply to the value, if present
   * @return an {@code SecureOptional} describing the value of this {@code SecureOptional} if a
   *     value is present and the value matches the given predicate, otherwise an empty {@code
   *     SecureOptional}
   * @throws NullPointerException if the predicate is null
   */
  public SecuredOptional<T> filter(Predicate<? super T> predicate) {
    Objects.requireNonNull(predicate);
    if (!isPresent()) return this;
    else return predicate.test(value) ? this : empty();
  }

  /**
   * If a value is present, apply the provided mapping function to it, and if the result is
   * non-null, return an {@code SecureOptional} describing the result. Otherwise return an empty
   * {@code SecureOptional}.
   *
   * @apiNote This method supports post-processing on SecureOptional values, without the need to
   *     explicitly check for a return status. For example, the following code traverses a stream of
   *     file names, selects one that has not yet been processed, and then opens that file,
   *     returning an {@code SecureOptional<FileInputStream>}:
   *     <pre>{@code
   * SecureOptional<FileInputStream> fis =
   *     names.stream().filter(name -> !isProcessedYet(name))
   *                   .findFirst()
   *                   .map(name -> new FileInputStream(name));
   * }</pre>
   *     Here, {@code findFirst} returns an {@code SecureOptional<String>}, and then {@code map}
   *     returns an {@code SecureOptional<FileInputStream>} for the desired file if one exists.
   * @param <U> The type of the result of the mapping function
   * @param mapper a mapping function to apply to the value, if present
   * @return an {@code SecureOptional} describing the result of applying a mapping function to the
   *     value of this {@code SecureOptional}, if a value is present, otherwise an empty {@code
   *     SecureOptional}
   * @throws NullPointerException if the mapping function is null
   */
  public <U> SecuredOptional<U> map(Function<? super T, ? extends U> mapper) {
    Objects.requireNonNull(mapper);
    if (!isPresent()) return empty();
    else {
      try {
        return SecuredOptional.ofNullable(mapper.apply(value));
      } catch (AccessDeniedException e) {
        return empty();
      }
    }
  }

  /**
   * If a value is present, apply the provided {@code SecureOptional}-bearing mapping function to
   * it, return that result, otherwise return an empty {@code SecureOptional}. This method is
   * similar to {@link #map(Function)}, but the provided mapper is one whose result is already an
   * {@code SecureOptional}, and if invoked, {@code flatMap} does not wrap it with an additional
   * {@code SecureOptional}.
   *
   * @param <U> The type parameter to the {@code SecureOptional} returned by
   * @param mapper a mapping function to apply to the value, if present the mapping function
   * @return the result of applying an {@code SecureOptional}-bearing mapping function to the value
   *     of this {@code SecureOptional}, if a value is present, otherwise an empty {@code
   *     SecureOptional}
   * @throws NullPointerException if the mapping function is null or returns a null result
   */
  public <U> SecuredOptional<U> flatMap(Function<? super T, SecuredOptional<U>> mapper) {
    Objects.requireNonNull(mapper);
    if (!isPresent()) return empty();
    else {
      try {
        return Objects.requireNonNull(mapper.apply(value));
      } catch (AccessDeniedException e) {
        return empty();
      }
    }
  }

  /**
   * Return the value if present, otherwise return {@code other}.
   *
   * @param other the value to be returned if there is no value present, may be null
   * @return the value, if present, otherwise {@code other}
   */
  public T orElse(T other) {
    return value != null ? value : other;
  }

  /**
   * Return the value if present, otherwise invoke {@code other} and return the result of that
   * invocation.
   *
   * @param other a {@code Supplier} whose result is returned if no value is present
   * @return the value if present otherwise the result of {@code other.get()}
   * @throws NullPointerException if value is not present and {@code other} is null
   */
  public T orElseGet(Supplier<? extends T> other) {
    return value != null ? value : other.get();
  }

  /**
   * Return the contained value, if present, otherwise throw an exception to be created by the
   * provided supplier.
   *
   * @apiNote A method reference to the exception constructor with an empty argument list can be
   *     used as the supplier. For example, {@code IllegalStateException::new}
   * @param <X> Type of the exception to be thrown
   * @param exceptionSupplier The supplier which will return the exception to be thrown
   * @return the present value
   * @throws X if there is no value present
   * @throws NullPointerException if no value is present and {@code exceptionSupplier} is null
   */
  public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
    if (value != null) {
      return value;
    } else {
      throw exceptionSupplier.get();
    }
  }

  /**
   * Indicates whether some other object is "equal to" this SecureOptional. The other object is
   * considered equal if:
   *
   * <ul>
   *   <li>it is also an {@code SecureOptional} and;
   *   <li>both instances have no value present or;
   *   <li>the present values are "equal to" each other via {@code equals()}.
   * </ul>
   *
   * @param obj an object to be tested for equality
   * @return {code true} if the other object is "equal to" this object otherwise {@code false}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof SecuredOptional)) {
      return false;
    }

    SecuredOptional<?> other = (SecuredOptional<?>) obj;
    return Objects.equals(value, other.value);
  }

  /**
   * Returns the hash code value of the present value, if any, or 0 (zero) if no value is present.
   *
   * @return hash code value of the present value or 0 if no value is present
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  /**
   * Returns a non-empty string representation of this SecureOptional suitable for debugging. The
   * exact presentation format is unspecified and may vary between implementations and versions.
   *
   * @implSpec If a value is present the result must include its string representation in the
   *     result. Empty and present SecureOptionals must be unambiguously differentiable.
   * @return the string representation of this instance
   */
  @Override
  public String toString() {
    return value != null ? String.format("SecureOptional[%s]", value) : "SecureOptional.empty";
  }
}
