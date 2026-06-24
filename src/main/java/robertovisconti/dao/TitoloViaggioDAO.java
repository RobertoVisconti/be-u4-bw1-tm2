package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.Application;
import robertovisconti.entities.Abbonamento;
import robertovisconti.entities.Biglietto;
import robertovisconti.entities.PuntoDiEmissione;
import robertovisconti.entities.TitoloViaggio;
import robertovisconti.enums.TipoAbbonamento;
import robertovisconti.exceptions.PuntoDiEmissioneNonTrovatoException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



public class TitoloViaggioDAO {
     public final EntityManager entityManager;

     public TitoloViaggioDAO(EntityManager em) {this.entityManager =em;}

    public void save (TitoloViaggio newTitoloViaggio) {
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

            abbonamento.setDataEmissione(dataEmissione);

            switch (tipoAbbonamento) {
                case SETTIMANALE ->
                        abbonamento.setDataScadenza(dataEmissione.plusDays(7));

                case MENSILE ->
                        abbonamento.setDataScadenza(dataEmissione.plusMonths(1));

                case ANNUALE ->
                        abbonamento.setDataScadenza(dataEmissione.plusYears(1));
            }
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

    public void vidimaBiglietto(UUID codiceUnivoco) {

        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        TitoloViaggio titolo = findByCodiceUnivoco(codiceUnivoco);

        if (titolo instanceof Biglietto biglietto) {

            if (biglietto.getDataValidazione() == null) {

                biglietto.setDataValidazione(LocalDateTime.now());

                System.out.println("Biglietto vidimato con successo!");

            } else {

                System.out.println("Biglietto già vidimato il: " + biglietto.getDataValidazione());
            }

        } else {

            System.out.println("Il codice inserito non appartiene ad un biglietto.");
        }

        transaction.commit();
    }
}
