package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.MezzoDiTrasporto;
import robertovisconti.entities.Percorrenza;
import robertovisconti.entities.Tratta;
import robertovisconti.enums.StatoMezzo;
import robertovisconti.exceptions.PercorrenzaNonTrovataException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // TASK 3b - creo una percorrenza e la salvo subito
    public Percorrenza creaPercorrenza(Tratta tratta, MezzoDiTrasporto mezzo,
                                       LocalDateTime dataInizio, LocalDateTime dataFine) {
        Percorrenza percorrenza = new Percorrenza(tratta, mezzo, dataInizio, dataFine);
        savePercorrenza(percorrenza);
        return percorrenza;
    }

    // TASK 2 - assegno una tratta a un mezzo creando la percorrenza che li collega.
    // Controllo: solo un mezzo IN_SERVIZIO puo' essere assegnato a una tratta.
    public Percorrenza assegnaTrattaAMezzo(Tratta tratta, MezzoDiTrasporto mezzo,
                                           LocalDateTime dataInizio, LocalDateTime dataFine) {

        if (mezzo.getStatoMezzo() != StatoMezzo.IN_SERVIZIO) {
            throw new RuntimeException("Il mezzo non e' in servizio: non puo' essere assegnato a una tratta.");
        }

        // superato il controllo, riuso creaPercorrenza per non duplicare codice
        return creaPercorrenza(tratta, mezzo, dataInizio, dataFine);
    }

    // TASK 2 (controllo extra) - conto quante volte un mezzo ha percorso una tratta
    public long numeroVolteTrattaPercorsa(Tratta tratta, MezzoDiTrasporto mezzo) {
        return percorrenzeSuTrattaPerMezzo(tratta, mezzo).size();
    }

    // tengo traccia di quante volte un mezzo percorre una tratta (= dimensione lista)
    // e di quanto impiega ogni volta (= ogni Duration nella lista)
    public List<Duration> tempiPercorrenza(Tratta tratta, MezzoDiTrasporto mezzo) {
        List<Percorrenza> percorrenze = percorrenzeSuTrattaPerMezzo(tratta, mezzo);
        List<Duration> tempi = new ArrayList<>();
        for (Percorrenza percorrenza : percorrenze) {
            tempi.add(percorrenza.getTempoEffettivo());
        }
        return tempi;
    }

}
