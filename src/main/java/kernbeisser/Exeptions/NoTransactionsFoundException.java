package kernbeisser.Exeptions;

import javax.persistence.NoResultException;

public class NoTransactionsFoundException extends NoResultException {

  public NoTransactionsFoundException() {
    super();
  }
}
