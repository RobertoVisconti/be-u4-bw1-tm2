package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import robertovisconti.entities.Tratta;
import robertovisconti.enums.StatoTratta;

import java.util.List;
import java.util.UUID;

public class TrattaDAO {
    public final EntityManager entityManager;

    public TrattaDAO(EntityManager em) {
        this.entityManager = em;
    }

    public void save(Tratta newTratta) {
        EntityTransaction transaction = this.entityManager.getTransaction();

        transaction.begin();

        this.entityManager.persist(newTratta);

        transaction.commit();
    }

    // TASK 3a - creo una tratta e la salvo subito
    public Tratta creaTratta(String puntoDiPartenza, String capolinea, int tempoPercorrenzaStimato, StatoTratta statoTratta) {
        Tratta tratta = new Tratta(puntoDiPartenza, capolinea, tempoPercorrenzaStimato, statoTratta);
        save(tratta);
        return tratta;
    }

    // restituisco tutte le tratte presenti nel database
    public List<Tratta> findAll() {
        return entityManager.createQuery("SELECT t FROM Tratta t", Tratta.class).getResultList();
    }

    public Tratta findById(UUID id) {

        Tratta found = entityManager.find(Tratta.class, id);

        if (found == null) {
            throw new RuntimeException("Tratta non trovata!");
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
