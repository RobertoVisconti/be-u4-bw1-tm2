package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.Manutenzione;
import robertovisconti.entities.MezzoDiTrasporto;
import robertovisconti.exceptions.ManutenzioneNonTrovata;

import java.time.LocalDate;
import java.util.List;

public class ManutenzioneDAO {

    private final EntityManager em;

    public ManutenzioneDAO(EntityManager em, MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        this.em = em;
    }

    public void saveManutenzione(Manutenzione manutenzione) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(manutenzione);
        tx.commit();
        System.out.println(manutenzione + " Manutenzione registrata con successo.");
    }

    public Manutenzione getLatestManutenzione(MezzoDiTrasporto mezzo) {
        TypedQuery<Manutenzione> query = em.createQuery("SELECT m FROM Manutenzione m WHERE m.mezzo = :mezzo AND m.dataFine IS NULL ORDER BY m.dataInizio DESC", Manutenzione.class);
        query.setParameter("mezzo", mezzo);
        query.setMaxResults(1);
        return query.getSingleResult();
    }

    public List<Manutenzione> findByTargaManutenzione(String targa) {
        String jpql = "SELECT m FROM manutenzione m JOIN m.mezzoTrasporto mezzo WHERE mezzo.targa = :targa";
        TypedQuery<Manutenzione> query = em.createQuery(jpql, Manutenzione.class);
        query.setParameter("targa", targa);

        List<Manutenzione> result = query.getResultList();
        if (result.isEmpty()) {
            throw new ManutenzioneNonTrovata(targa);
        } else {
            System.out.println("Trovate " + result.size() + " manutenzione per la targa: " + targa);
            return result;
        }
    }

    public void endManutenzione(Manutenzione manutenzione) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        manutenzione.setDataFine(LocalDate.now());
        tx.commit();
        System.out.println("Manutenzione aggiornata con successo");
    }

    public void deleteManutenzione(String targa) {
        List<Manutenzione> manutenzioni = findByTargaManutenzione(targa);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (Manutenzione m : manutenzioni) {
            em.remove(m);
        }
        tx.commit();
        System.out.println("Eliminate con successo " + manutenzioni.size() + " manutenzioni per la targa: " + targa);
    }

    public List<Manutenzione> storicoManutenzioni(String targa) {
        TypedQuery<Manutenzione> query = em.createQuery("SELECT m FROM Manutenzione m WHERE m.mezzo.targa = :targa ORDER BY m.dataInizio", Manutenzione.class);

        query.setParameter("targa", targa);

        return query.getResultList();
    }


}
