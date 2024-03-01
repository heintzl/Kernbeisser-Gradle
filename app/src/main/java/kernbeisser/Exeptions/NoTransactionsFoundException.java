package kernbeisser.Exeptions;

import jakarta.persistence.NoResultException;

public class NoTransactionsFoundException extends NoResultException {

  public NoTransactionsFoundException() {
    super();
  }
}
