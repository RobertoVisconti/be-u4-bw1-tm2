package robertovisconti.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
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

    public static void menuCountTitoliViaggio(TitoloViaggioDAO titoloViaggioDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
         while (true) {
             System.out.println("******* SELEZIONA UN'OPZIONE *******");
             System.out.println("1. Conta biglietti emessi in un lasso di tempo.");
             System.out.println("2. Conta biglietti emessi in un lasso di tempo su un punto di emissione.");
             System.out.println("3. Conta abbonamenti emessi in un lasso di tempo.");
             System.out.println("4. Conta abbonamenti emessi in un lasso di tempo su un punto di emissione.");
             System.out.println("0. Torna indietro");
             int input = -1;
             try {
                 input = Integer.parseInt(scanner.nextLine().trim());
             }
             catch (NumberFormatException ex) {
                 System.out.println("Formato errato, inserisci un numbero");
                 continue;
             }
             if (input == 0) break;

             switch (input) {
                 case 1 -> {
                     LocalDateTime dataInizio = null;
                     LocalDateTime dataFine = null;
                     System.out.println("Data di inizio:");
                     System.out.println("Inserisci il giorno:");
                     int giornoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoInizio = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataInizio = LocalDateTime.of(annoInizio, meseInizio, giornoInizio, oraInizio, minutoInizio);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("Data di fine:");
                     System.out.println("Inserisci il giorno:");
                     int giornoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoFine = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataFine = LocalDateTime.of(annoFine, meseFine, giornoFine, oraFine, minutoFine);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("\nBiglietti emessi tra " + dataInizio + " e " + dataFine + ": " + titoloViaggioDAO.countBigliettiBetween(dataInizio, dataFine));
                 }

                 case 2 -> {
                     PuntoDiEmissione puntoDiEmissione = null;
                     System.out.println("Punti vendita:\n");
                     puntoDiEmissioneDAO.printAllNamesAndUuids();
                     System.out.println("Inserisci l'UUID del punto vendita per sceglierlo, poi premi invio.");
                     String puntoUuid = scanner.nextLine();
                     try {
                         puntoDiEmissione = puntoDiEmissioneDAO.findPuntoDiEmissioneById(UUID.fromString(puntoUuid));
                     } catch (PuntoDiEmissioneNonTrovatoException ex) {
                         System.out.println(ex.getMessage());
                     }

                     LocalDateTime dataInizio = null;
                     LocalDateTime dataFine = null;
                     System.out.println("Data di inizio:");
                     System.out.println("Inserisci il giorno:");
                     int giornoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoInizio = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataInizio = LocalDateTime.of(annoInizio, meseInizio, giornoInizio, oraInizio, minutoInizio);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("Data di fine:");
                     System.out.println("Inserisci il giorno:");
                     int giornoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoFine = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataFine = LocalDateTime.of(annoFine, meseFine, giornoFine, oraFine, minutoFine);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("\nBiglietti emessi tra " + dataInizio + " e " + dataFine + " presso " + puntoDiEmissione.getNome() + ": " + titoloViaggioDAO.countBigliettiBetween(dataInizio, dataFine, puntoDiEmissione));
                 }

                 case 3 -> {
                     LocalDateTime dataInizio = null;
                     LocalDateTime dataFine = null;
                     System.out.println("Data di inizio:");
                     System.out.println("Inserisci il giorno:");
                     int giornoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoInizio = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataInizio = LocalDateTime.of(annoInizio, meseInizio, giornoInizio, oraInizio, minutoInizio);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("Data di fine:");
                     System.out.println("Inserisci il giorno:");
                     int giornoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoFine = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataFine = LocalDateTime.of(annoFine, meseFine, giornoFine, oraFine, minutoFine);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("\nAbbonamenti emessi tra " + dataInizio + " e " + dataFine + ": " + titoloViaggioDAO.countAbbonamentiBetween(dataInizio, dataFine));
                 }

                 case 4 -> {
                     PuntoDiEmissione puntoDiEmissione = null;
                     System.out.println("Punti vendita:\n");
                     puntoDiEmissioneDAO.printAllNamesAndUuids();
                     System.out.println("Inserisci l'UUID del punto vendita per sceglierlo, poi premi invio.");
                     String puntoUuid = scanner.nextLine();
                     try {
                         puntoDiEmissione = puntoDiEmissioneDAO.findPuntoDiEmissioneById(UUID.fromString(puntoUuid));
                     } catch (PuntoDiEmissioneNonTrovatoException ex) {
                         System.out.println(ex.getMessage());
                     }

                     LocalDateTime dataInizio = null;
                     LocalDateTime dataFine = null;
                     System.out.println("Data di inizio:");
                     System.out.println("Inserisci il giorno:");
                     int giornoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraInizio = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoInizio = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataInizio = LocalDateTime.of(annoInizio, meseInizio, giornoInizio, oraInizio, minutoInizio);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("Data di fine:");
                     System.out.println("Inserisci il giorno:");
                     int giornoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il mese:");
                     int meseFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'anno:");
                     int annoFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci l'ora:");
                     int oraFine = Integer.parseInt(scanner.nextLine().trim());
                     System.out.println("Inserisci il minuto");
                     int minutoFine = Integer.parseInt(scanner.nextLine().trim());

                     try {
                         dataFine = LocalDateTime.of(annoFine, meseFine, giornoFine, oraFine, minutoFine);
                     } catch (DateTimeException ex) {
                         System.out.println("Data non valida: " + ex.getMessage());
                     }

                     System.out.println("\nAbbonamenti emessi tra " + dataInizio + " e " + dataFine + " presso " + puntoDiEmissione.getNome() + ": " + titoloViaggioDAO.countAbbonamentiBetween(dataInizio, dataFine, puntoDiEmissione));
                 }

                 default -> System.out.println("Input non valido");
             }
         }
    }
}
