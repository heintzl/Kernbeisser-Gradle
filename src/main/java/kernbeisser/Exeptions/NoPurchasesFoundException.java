package kernbeisser.Exeptions;

import javax.persistence.NoResultException;

public class NoPurchasesFoundException extends NoResultException {

  public NoPurchasesFoundException() {
    super();
  }
}
