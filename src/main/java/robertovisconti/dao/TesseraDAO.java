package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.Tessera;
import robertovisconti.exceptions.TesseraNonTrovataException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class TesseraDAO {

    private final EntityManager em;

    public TesseraDAO(EntityManager em) {
        this.em = em;
    }

    public void saveTessera(Tessera tessera) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(tessera);
        tx.commit();
    }


    // creo una nuova tessera (con codice univoco generato automaticamente) e la salvo subito
    public Tessera creaTessera() {
        Tessera tessera = new Tessera(UUID.randomUUID());
        saveTessera(tessera);
        return tessera;
    }

    public Tessera findByUnCode(UUID codiceUnivoco) {
        TypedQuery<Tessera> query = em.createQuery("SELECT e FROM Tessera e WHERE e.codiceUnivoco = :codiceUnivoco", Tessera.class);
        query.setParameter("codiceUnivoco", codiceUnivoco);
        Optional<Tessera> tesseraOptional = query.getResultStream().findFirst();
        if (tesseraOptional.isPresent()) {
            System.out.println(tesseraOptional + " Tessera trovata con successo.");
            return tesseraOptional.get();
        } else {
            throw new TesseraNonTrovataException(codiceUnivoco);
        }
    }

    public void updateTessera(UUID codiceUnivoco, LocalDate nuovaEmissione, LocalDate nuovaScadenza) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Tessera tessera = findByUnCode(codiceUnivoco);
        tessera.setDataEmissione(nuovaEmissione);
        tessera.setDataScadenza(nuovaScadenza);
        tx.commit();
        System.out.println(tessera + " Rinnovata con successo.");
    }

    public void deleteTessera(UUID codiceUnivoco) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Tessera tessera = findByUnCode(codiceUnivoco);
        em.remove(tessera);
        tx.commit();
        System.out.println(tessera + " Eliminata con successo.");
    }


}
