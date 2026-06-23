package robertovisconti.exceptions;

public class UtenteEmailNonTrovatoException extends RuntimeException {
    public UtenteEmailNonTrovatoException(String email) {
        super("L'utente corrispondente all'email:  [ " + email + " ] non si trova all'interno del db.");
    }
}
