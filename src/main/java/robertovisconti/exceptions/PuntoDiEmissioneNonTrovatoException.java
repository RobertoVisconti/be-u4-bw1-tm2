package robertovisconti.exceptions;


import java.util.UUID;

public class PuntoDiEmissioneNonTrovatoException extends RuntimeException {
  public PuntoDiEmissioneNonTrovatoException(UUID id) {
      super("Impossibile trovare il punto di emissione con ID: [ " + id + " ] all'interno del db.");
  }
}
