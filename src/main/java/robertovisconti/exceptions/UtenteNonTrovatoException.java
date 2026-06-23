package robertovisconti.exceptions;

import java.util.UUID;

public class UtenteNonTrovatoException extends RuntimeException {
    public UtenteNonTrovatoException(UUID id) {
        super("Impossibile trovare l'utente con ID: [ " + id + " ] all'interno del db.");
    }
}
