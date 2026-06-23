package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.Tessera;
import robertovisconti.exceptions.TesseraNonTrovataException;

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
        System.out.println(tessera + " Associata con successo.");
    }

    public Tessera findByUnCode(UUID codiceUnivoco) {
        TypedQuery<Tessera> query = em.createQuery("SELECT e FROM Tessera e WHERE e.codiceUnivoco = :codiceUnivoco", Tessera.class);
        query.setParameter("codiceUnivoco", codiceUnivoco);
        Optional<Tessera> tesseraOptional = query.getResultStream().findFirst();
        if (tesseraOptional.isPresent()) {
            System.out.println(tesseraOptional + " Tessera trovata con successo.");
        } else {
            throw new TesseraNonTrovataException(codiceUnivoco);
        }
        return null;
    }


}
