package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.DistributoreAutomatico;
import robertovisconti.entities.PuntoDiEmissione;
import robertovisconti.entities.Rivenditore;
import robertovisconti.enums.StatoDistributoreAutomatico;
import robertovisconti.exceptions.PuntoDiEmissioneNonTrovatoException;

import java.util.List;
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
    }

    public PuntoDiEmissione findPuntoDiEmissioneById(UUID puntoEmissioneId) {
        PuntoDiEmissione found = em.find(PuntoDiEmissione.class, puntoEmissioneId);

        if (found == null) throw new PuntoDiEmissioneNonTrovatoException(puntoEmissioneId);

        return found;
    }

    public void updateStatoDistributoreById(UUID id, StatoDistributoreAutomatico stato) {

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        DistributoreAutomatico distributore = em.find(DistributoreAutomatico.class, id);

        if (distributore == null) {
            transaction.rollback();
            throw new PuntoDiEmissioneNonTrovatoException(id);
        }

        distributore.setStato(stato);

        transaction.commit();

        System.out.println(
                "Il distributore automatico " + distributore.getNome()
                        + " è stato aggiornato allo stato " + stato
        );
    }

    public void updateStatoRivenditoreById(UUID id, boolean stato) {

        EntityTransaction transaction = em.getTransaction();
        transaction.begin();

        Rivenditore rivenditore = em.find(Rivenditore.class, id);

        if (rivenditore == null) {
            transaction.rollback();
            throw new PuntoDiEmissioneNonTrovatoException(id);
        }

        rivenditore.setAperto(stato);

        transaction.commit();

        System.out.println(
                "Il rivenditore " + rivenditore.getNome()
                        + " è stato aggiornato allo stato "
                        + (stato ? "APERTO" : "CHIUSO")
        );
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

    public void printAllNamesAndUuids() {

        Query query = em.createQuery("SELECT p.nome, p.id FROM PuntoDiEmissione p");
        List<Object[]> result = query.getResultList();
        for (Object[] row : result) {
            String nome = (String) row[0];
            UUID id = (UUID) row[1];
            System.out.println(nome + " - " + id);
        }

    }

    public List<PuntoDiEmissione> findAllPuntiDiEmissione() {

        TypedQuery<PuntoDiEmissione> query = em.createQuery(
                "SELECT p FROM PuntoDiEmissione p",
                PuntoDiEmissione.class
        );

        return query.getResultList();
    }
}
