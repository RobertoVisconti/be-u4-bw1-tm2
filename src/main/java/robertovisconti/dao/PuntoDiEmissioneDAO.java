package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import robertovisconti.entities.DistributoreAutomatico;
import robertovisconti.entities.PuntoDiEmissione;
import robertovisconti.entities.Rivenditore;
import robertovisconti.enums.StatoDistributoreAutomatico;
import robertovisconti.exceptions.PuntoDiEmissioneNonTrovatoException;

import java.util.UUID;

public class PuntoDiEmissioneDAO {
    private final EntityManager em;

    public PuntoDiEmissioneDAO(EntityManager em) {
        this.em = em;
    }

    public void savePuntoDiEmissione(PuntoDiEmissione newPuntoDiEmissione) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        em.persist(newPuntoDiEmissione);

        transaction.commit();

        System.out.println("Il punto di emissione " + newPuntoDiEmissione + " è stato correttamente salvato!");
    }

    public PuntoDiEmissione findPuntoDiEmissioneById(UUID puntoEmissioneId) {
        PuntoDiEmissione found = em.find(PuntoDiEmissione.class, puntoEmissioneId);

        if (found == null) throw new PuntoDiEmissioneNonTrovatoException(puntoEmissioneId);

        return found;
    }

    public void updateStatoDistributoreById(UUID id, StatoDistributoreAutomatico stato) {
        PuntoDiEmissione found = findPuntoDiEmissioneById(id);
        if (!(found instanceof DistributoreAutomatico)) {
            throw new PuntoDiEmissioneNonTrovatoException(id);
        }
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Query query = em.createQuery("UPDATE DistributoreAutomatico d SET d.stato = :newStato WHERE d.id = :id");
        query.setParameter("newStato", stato);
        query.setParameter("id", id);

        query.executeUpdate(); // <-- Questa riga esegue la query nella transazione

        transaction.commit();
        System.out.println("Il distributore automatico " + found.getNome() + " è stato aggiornato allo stato " + stato);
    }

    public void updateStatoRivenditoreById(UUID id, boolean stato) {
        PuntoDiEmissione found = findPuntoDiEmissioneById(id);
        if (!(found instanceof Rivenditore)) {
            throw new PuntoDiEmissioneNonTrovatoException(id);
        }
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Query query = em.createQuery("UPDATE Rivenditore r SET r.aperto = :newStato WHERE r.id = :id");
        query.setParameter("newStato", stato);
        query.setParameter("id", id);

        query.executeUpdate(); // <-- Questa riga esegue la query nella transazione

        transaction.commit();
        System.out.println("Il rivenditore " + found.getNome() + " è stato aggiornato allo stato " + (stato ? "'APERTO'" : "'CHIUSO'"));
    }

    public void findByIdAndDelete(UUID id) {
        PuntoDiEmissione found = findPuntoDiEmissioneById(id);

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Query query = em.createQuery("DELETE FROM PuntoDiEmissione p WHERE p.id = :id ");
        query.setParameter("id", id);

        query.executeUpdate(); // <-- Questa riga esegue la query nella transazione

        transaction.commit();

        System.out.println("Il punto emissione " + found.getNome() + " è stato cancellato dal DB.");
    }
}
