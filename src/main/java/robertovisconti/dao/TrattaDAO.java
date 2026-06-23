package robertovisconti.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import robertovisconti.entities.Tratta;

import java.util.UUID;

public class TrattaDAO {
    public final EntityManager entityManager;

    public TrattaDAO(EntityManager em) {this.entityManager = em;}

    public void save (Tratta newTratta){
        EntityTransaction transaction = this.entityManager.getTransaction();

        transaction.begin();

        this.entityManager.persist(newTratta);

        transaction.commit();

        System.out.println(newTratta + "creata con successo!");
    }

    public Tratta findById(UUID id) {

        Tratta found = entityManager.find(Tratta.class, id);

        if (found == null) {
            throw new RuntimeException("Mezzo non trovato!");
        }

        return found;
    }

    public void update(
            UUID id,
            String puntoDiPartenza,
            String capolinea,
            int tempoPercorrenzaStimato
    ) {

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Tratta found = entityManager.find(Tratta.class, id);

        if (found == null) {
            throw new RuntimeException("Tratta non trovata con id: " + id);
        }

        found.setPuntoDiPartenza(puntoDiPartenza);
        found.setCapolinea(capolinea);
        found.setTempoPercorrenzaStimato(tempoPercorrenzaStimato);

        transaction.commit();

        System.out.println("Tratta aggiornata con successo!");
    }

    public void delete(UUID id) {

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Tratta found = entityManager.find(Tratta.class, id);

        if (found == null) {
            throw new RuntimeException("Tratta non trovata con id: " + id);
        }

        entityManager.remove(found);

        transaction.commit();

        System.out.println("Tratta eliminata con successo!");
    }
    
}
