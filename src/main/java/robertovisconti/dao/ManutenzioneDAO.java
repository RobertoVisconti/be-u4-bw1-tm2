package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import robertovisconti.entities.Manutenzione;

import java.time.LocalDate;
import java.util.List;

public class ManutenzioneDAO {

    private final EntityManager em;
    private final MezzoDiTrasportoDAO mezzoDiTrasportoDAO;

    public ManutenzioneDAO(EntityManager em, MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        this.em = em;
        this.mezzoDiTrasportoDAO = mezzoDiTrasportoDAO;
    }

    public void saveManutenzione(Manutenzione manutenzione) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(manutenzione);
        tx.commit();
        System.out.println(manutenzione + " Manutenzione registrata con successo.");
    }

   public void updateManutenzione(String targa, LocalDate dataFine){
        EntityTransaction tx = em.getTransaction();
        List<Manutenzione> manutenzioni = 
   }


}
