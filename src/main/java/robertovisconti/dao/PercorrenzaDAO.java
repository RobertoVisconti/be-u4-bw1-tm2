package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.MezzoDiTrasporto;
import robertovisconti.entities.Percorrenza;
import robertovisconti.entities.Tratta;
import robertovisconti.exceptions.PercorrenzaNonTrovataException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PercorrenzaDAO {

    private final EntityManager em;

    public PercorrenzaDAO(EntityManager em) {
        this.em = em;
    }

    // salvo una nuova percorrenza nel database
    public void savePercorrenza(Percorrenza percorrenza) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(percorrenza);
        tx.commit();
        System.out.println(percorrenza + " Percorrenza salvata con successo.");
    }

    // cerco una percorrenza tramite il suo id;
    // se non la trovo lancio l'eccezione (questo e' il controllo)
    public Percorrenza findById(UUID id) {
        Percorrenza percorrenza = em.find(Percorrenza.class, id);
        if (percorrenza == null) {
            throw new PercorrenzaNonTrovataException(id);
        }
        return percorrenza;
    }

    // aggiorno la percorrenza impostando l'orario di fine,
    // cioe' "chiudo" il viaggio quando il mezzo arriva al capolinea
    public void updatePercorrenza(UUID id, LocalDateTime dataFine) {
        Percorrenza percorrenza = findById(id);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        percorrenza.setDataFine(dataFine);
        tx.commit();
        System.out.println(percorrenza + " Percorrenza aggiornata con successo.");
    }

    // elimino una percorrenza dal database
    public void deletePercorrenza(UUID id) {
        Percorrenza percorrenza = findById(id);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(percorrenza);
        tx.commit();
        System.out.println(percorrenza + " Percorrenza rimossa con successo.");
    }

    // calcolo il tempo medio effettivo di percorrenza di una tratta
    public Duration tempoMedioPercorrenza(Tratta tratta, MezzoDiTrasporto mezzo) {

        // prendo tutte le percorrenze concluse
        TypedQuery<Percorrenza> query = em.createQuery(
                "SELECT p FROM Percorrenza p WHERE p.tratta = :tratta AND p.mezzo = :mezzo AND p.dataFine IS NOT NULL",
                Percorrenza.class
        );
        query.setParameter("tratta", tratta);
        query.setParameter("mezzo", mezzo);
        List<Percorrenza> percorrenze = query.getResultList();

        // se non ci sono percorrenze concluse non posso fare la media
        if (percorrenze.isEmpty()) {
            return Duration.ZERO;
        }

        // sommo la durata di ogni percorrenza
        long secondiTotali = 0;
        for (Percorrenza percorrenza : percorrenze) {
            secondiTotali += Duration.between(percorrenza.getDataInizio(), percorrenza.getDataFine()).getSeconds();
        }

        // divido per il numero di percorrenze per ottenere la media
        long mediaSecondi = secondiTotali / percorrenze.size();
        return Duration.ofSeconds(mediaSecondi);
    }

    public List<Percorrenza> percorrenzeSuTrattaPerMezzo(Tratta tratta, MezzoDiTrasporto mezzo) {
        TypedQuery<Percorrenza> query = em.createQuery("SELECT p FROM Percorrenza p WHERE p.tratta = :tratta AND p.mezzo = :mezzo", Percorrenza.class);
        query.setParameter("tratta", tratta);
        query.setParameter("mezzo", mezzo);
        return query.getResultList();
    }
    
}
