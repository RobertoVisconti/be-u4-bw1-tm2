package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.MezzoDiTrasporto;
import robertovisconti.enums.StatoMezzo;
import robertovisconti.enums.TipoMezzo;

import java.util.List;
import java.util.UUID;

public class MezzoDiTrasportoDAO {
    public final EntityManager entityManager;

    public MezzoDiTrasportoDAO (EntityManager em) {this.entityManager = em;}

    public void save(MezzoDiTrasporto newMezzo) {
        EntityTransaction transaction = this.entityManager.getTransaction();

        transaction.begin();

        this.entityManager.persist(newMezzo);

        transaction.commit();

        System.out.println(newMezzo + "creato con successo!");
    }

    // restituisco tutti i mezzi presenti nel database
    public List<MezzoDiTrasporto> findAll() {
        return entityManager.createQuery("SELECT m FROM MezzoDiTrasporto m", MezzoDiTrasporto.class).getResultList();
    }

    public MezzoDiTrasporto findByTarga(String targa) {

        TypedQuery<MezzoDiTrasporto> query = entityManager.createQuery(
                "SELECT m FROM MezzoDiTrasporto m WHERE m.targa = :targa",
                MezzoDiTrasporto.class
        );

        query.setParameter("targa", targa);

        return query.getSingleResult();
    }
    public void deleteByTarga(String targa) {

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        MezzoDiTrasporto found = findByTarga(targa);

        entityManager.remove(found);

        transaction.commit();

        System.out.println("Mezzo eliminato con successo!");
    }

    public void updateMezzo(
            String targa,
            TipoMezzo tipoMezzo,
            int capienza,
            StatoMezzo statoMezzo
    ) {

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        MezzoDiTrasporto found = findByTarga(targa);

        found.setTipoMezzo(tipoMezzo);
        found.setCapienza(capienza);
        found.setStatoMezzo(statoMezzo);

        transaction.commit();

        System.out.println("Mezzo aggiornato con successo!");
    }
}
