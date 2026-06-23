package robertovisconti.exceptions;

public class ManutenzioneNonTrovata extends RuntimeException {
    public ManutenzioneNonTrovata(String targa) {
        super("Impossibile trovare la manutenzione della targa: [ " + targa + " ] all'interno del db.");
    }
}
