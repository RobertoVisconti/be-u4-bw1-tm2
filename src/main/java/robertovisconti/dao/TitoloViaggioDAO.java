package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.*;
import robertovisconti.enums.TipoAbbonamento;

import java.time.LocalDateTime;
import java.util.UUID;


public class TitoloViaggioDAO {
    public final EntityManager entityManager;

    public TitoloViaggioDAO(EntityManager em) {
        this.entityManager = em;
    }

    public void save(TitoloViaggio newTitoloViaggio) {
        EntityTransaction transaction = this.entityManager.getTransaction();

        transaction.begin();

        this.entityManager.persist(newTitoloViaggio);

        transaction.commit();

        System.out.println(newTitoloViaggio + "creato con successo!");
    }

    public TitoloViaggio findByCodiceUnivoco(UUID codiceUnivoco) {

        TypedQuery<TitoloViaggio> query = entityManager.createQuery(
                "SELECT t FROM TitoloViaggio t WHERE t.codiceUnivoco = :codice",
                TitoloViaggio.class
        );

        query.setParameter("codice", codiceUnivoco);

        return query.getSingleResult();
    }

    public void delete(UUID codiceUnivoco) {
        TitoloViaggio found = findByCodiceUnivoco(codiceUnivoco);

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        entityManager.remove(found);

        transaction.commit();
    }

    public void updateAbbonamento(
            UUID codiceUnivoco,
            TipoAbbonamento tipoAbbonamento,
            LocalDateTime dataEmissione) {

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        TitoloViaggio found = findByCodiceUnivoco(codiceUnivoco);

        if (found instanceof Abbonamento abbonamento) {

            abbonamento.setTipoAbbonamento(tipoAbbonamento);

            LocalDateTime baseDate;

            if (abbonamento.getDataScadenza().isAfter(LocalDateTime.now())) {
                baseDate = abbonamento.getDataScadenza();
            } else {
                baseDate = LocalDateTime.now();
            }

            switch (tipoAbbonamento) {

                case SETTIMANALE -> abbonamento.setDataScadenza(baseDate.plusWeeks(1));

                case MENSILE -> abbonamento.setDataScadenza(baseDate.plusMonths(1));

                case ANNUALE -> abbonamento.setDataScadenza(baseDate.plusYears(1));
            }

            abbonamento.setDataEmissione(dataEmissione);
        }

        transaction.commit();

        System.out.println("Abbonamento aggiornato!");
    }

    public void updateBiglietto(UUID codiceUnivoco, LocalDateTime dataValidazione) {

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        TitoloViaggio found = findByCodiceUnivoco(codiceUnivoco);

        if (found instanceof Biglietto biglietto) {
            biglietto.setDataValidazione(dataValidazione);
        }

        transaction.commit();

        System.out.println("Biglietto aggiornato!");
    }

    public boolean isAbbonamentoValido(UUID codiceTessera) {

        TypedQuery<Abbonamento> query = entityManager.createQuery(
                "SELECT a FROM Abbonamento a WHERE a.tessera.codiceUnivoco = :codice ORDER BY a.dataScadenza DESC",
                Abbonamento.class
        );

        query.setParameter("codice", codiceTessera);

        try {
            Abbonamento a = query.getSingleResult();

            return a.getDataScadenza().isAfter(LocalDateTime.now());

        } catch (Exception e) {
            return false;
        }
    }

    public int countBigliettiBetween(LocalDateTime inizio, LocalDateTime fine) {
        TypedQuery<Biglietto> query = entityManager.createQuery("SELECT b FROM Biglietto b WHERE b.dataEmissione BETWEEN :inizio AND :fine", Biglietto.class);
        query.setParameter("inizio", inizio);
        query.setParameter("fine", fine);
        return query.getResultList().size();
    }

    public int countBigliettiBetween(LocalDateTime inizio, LocalDateTime fine, PuntoDiEmissione puntoDiEmissione) {
        TypedQuery<Biglietto> query = entityManager.createQuery("SELECT b FROM Biglietto b WHERE b.dataEmissione BETWEEN :inizio AND :fine AND b.puntoDiEmissione = :puntoDiEmissione", Biglietto.class);
        query.setParameter("inizio", inizio);
        query.setParameter("fine", fine);
        query.setParameter("puntoDiEmissione", puntoDiEmissione);
        return query.getResultList().size();
    }

    public int countAbbonamentiBetween(LocalDateTime inizio, LocalDateTime fine) {
        TypedQuery<Abbonamento> query = entityManager.createQuery("SELECT a FROM Abbonamento a WHERE a.dataEmissione BETWEEN :inizio AND :fine", Abbonamento.class);
        query.setParameter("inizio", inizio);
        query.setParameter("fine", fine);
        return query.getResultList().size();
    }

    public int countAbbonamentiBetween(LocalDateTime inizio, LocalDateTime fine, PuntoDiEmissione puntoDiEmissione) {
        TypedQuery<Abbonamento> query = entityManager.createQuery("SELECT a FROM Abbonamento a WHERE a.dataEmissione BETWEEN :inizio AND :fine AND a.puntoDiEmissione = :puntoDiEmissione", Abbonamento.class);
        query.setParameter("inizio", inizio);
        query.setParameter("fine", fine);
        query.setParameter("puntoDiEmissione", puntoDiEmissione);
        return query.getResultList().size();
    }

    public void vidimaBiglietto(UUID codiceUnivoco, MezzoDiTrasporto mezzo) {
        TitoloViaggio titolo;
        try {
            titolo = findByCodiceUnivoco(codiceUnivoco);
        } catch (jakarta.persistence.NoResultException e) {
            System.out.println("Errore: Il codice univoco inserito non esiste nel database.");
            return;
        }

        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if (titolo instanceof Biglietto biglietto) {
                if (biglietto.getDataValidazione() == null) {

                    biglietto.setMezzoDiTrasporto(mezzo);
                    biglietto.setDataValidazione(LocalDateTime.now());

                    System.out.println("Biglietto vidimato con successo!");
                } else {
                    System.out.println("Errore: Biglietto già vidimato il: " + biglietto.getDataValidazione());
                }
            } else {
                System.out.println("Errore: Il codice inserito appartiene ad un abbonamento.");
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.out.println("Errore imprevisto durante la vidimazione: " + e.getMessage());
        }
    }
}
