package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.Manutenzione;
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

    public void updateManutenzione(String targa, LocalDate dataFine) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        List<Manutenzione> manutenzioni = findByTargaManutenzione(targa);
        for (Manutenzione m : manutenzioni) {
            if (m.getDataFine() == null) {
                m.setDataFine(dataFine);
                m.setMotivo(m.getMotivo() + " - Manutenzione effettuata.");
            }
        }
        tx.commit();
        System.out.println("Manutenzuioni aggiornate con successo " + manutenzioni);
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

}
