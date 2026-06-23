package robertovisconti.exceptions;

import java.util.UUID;

public class TesseraNonTrovataException extends RuntimeException {
    public TesseraNonTrovataException(UUID codiceUnivoco) {
        super("Impossibile trovare la tessera con il codice univoco: [ " + codiceUnivoco + " ] all'interno del db.");
    }
}
