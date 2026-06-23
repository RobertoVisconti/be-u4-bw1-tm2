package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.Abbonamento;
import robertovisconti.entities.Biglietto;
import robertovisconti.entities.TitoloViaggio;
import robertovisconti.enums.TipoAbbonamento;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
            LocalDate dataEmissione) {

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
}
