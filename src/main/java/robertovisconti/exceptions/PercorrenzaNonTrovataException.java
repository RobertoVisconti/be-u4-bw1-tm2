package robertovisconti.exceptions;

import java.util.UUID;

public class PercorrenzaNonTrovataException extends RuntimeException {
    public PercorrenzaNonTrovataException(UUID id) {
        super("Impossibile trovare la percorrenza con ID: [ " + id + " ] all'interno del db.");
    }
}
