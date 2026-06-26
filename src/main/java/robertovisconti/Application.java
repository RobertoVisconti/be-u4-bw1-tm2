package robertovisconti;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import robertovisconti.dao.*;
import robertovisconti.entities.*;
import robertovisconti.exceptions.UtenteEmailNonTrovatoException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class Application {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
            "be-u4-bw1-tm2");
    public static Scanner scanner = new Scanner(System.in);

    //region APPLICATION
    public static void main(String[] args) {
        EntityManager em = entityManagerFactory.createEntityManager();


        TesseraDAO tesseraDAO = new TesseraDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);
        MezzoDiTrasportoDAO mezzoDiTrasportoDAO = new MezzoDiTrasportoDAO(em);
        PuntoDiEmissioneDAO puntoDiEmissioneDAO = new PuntoDiEmissioneDAO(em);
        TrattaDAO trattaDAO = new TrattaDAO(em);
        TitoloViaggioDAO titoloViaggioDAO = new TitoloViaggioDAO(em);
        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);
        GenericDAO genericDAO = new GenericDAO(em);
        ManutenzioneDAO manutenzioneDAO = new ManutenzioneDAO(em, mezzoDiTrasportoDAO);

        Service.creazioneUtenti(tesseraDAO, utenteDAO, genericDAO);
        Service.creazioneMezzi(mezzoDiTrasportoDAO, genericDAO);
        Service.creazionePunti(puntoDiEmissioneDAO, genericDAO);
        Service.creazioneTratte(trattaDAO, genericDAO);
        Service.generaPercorrenze(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO, genericDAO);
        Service.creazioneBiglietti(titoloViaggioDAO, genericDAO, puntoDiEmissioneDAO, mezzoDiTrasportoDAO);


        boolean optionMenu = true;
        while (optionMenu) {
            System.out.println("\nTRASPORTO PUBBLICO\n");
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("0. Chiudi Applicazione");
            System.out.print("\nScegli un'opzione: ");


            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine()
                        .trim());
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido.");
                continue;
            }

            switch (scelta) {

                case 1 -> {
                    System.out.println("Inserisci la tua e-mail:");

                    String email2 = scanner.nextLine()
                            .trim();

                    try {

                        Utente emailScanner = utenteDAO.findByEmail(email2);

                        switch (emailScanner.getRuolo()) {
                            case ADMIN -> caseAdmin(tesseraDAO, utenteDAO, mezzoDiTrasportoDAO, puntoDiEmissioneDAO,
                                    trattaDAO, percorrenzaDAO, titoloViaggioDAO, genericDAO, manutenzioneDAO);
                            case USER -> caseUser(
                                    tesseraDAO,
                                    puntoDiEmissioneDAO,
                                    trattaDAO,
                                    titoloViaggioDAO,
                                    mezzoDiTrasportoDAO,
                                    emailScanner);
                            default -> System.out.println("Ruolo non riconosciuto.");
                        }

                    } catch (UtenteEmailNonTrovatoException ex) {
                        System.out.println("Errore: Nessun utente associato a questa email.");
                    }
                }
                case 2 -> Service.registrazioneUtente(utenteDAO);

                case 0 -> {
                    System.out.println("Applicazione in chiusura...");
                    optionMenu = false;
                }

                default -> System.out.println("Scelta non valida.");
            }
        }

        em.close();
        entityManagerFactory.close();
    }
//endregion

    //region Case Amministratore
    public static void caseAdmin(TesseraDAO tesseraDAO, UtenteDAO utenteDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO, TrattaDAO trattaDAO, PercorrenzaDAO percorrenzaDAO, TitoloViaggioDAO titoloViaggioDAO, GenericDAO genericDAO, ManutenzioneDAO manutenzioneDAO) {
        boolean adminMenu = true;
        while (adminMenu) {
            System.out.println("\n MENU PRINCIPALE ADMIN\n");
            System.out.println("1. Genera utenti / tessera / non tessera");
            System.out.println("2. Creazione mezzi di trasporto");
            System.out.println("3. Creazione punti di emissione");
            System.out.println("4. Ricerca utenti");
            System.out.println("5. Creazione tratte");
            System.out.println("6. Genera percorrenze");
            System.out.println("7. Assegna tratta a un mezzo");
            System.out.println("8. Calcola tempo medio percorrenza");
            System.out.println("9. Storico percorrenze mezzo/tratta");
            System.out.println("10. Menù storici titoli di viaggio");
            System.out.println("11. Storico manutenzioni");
            System.out.println("12. Verifica abbonamento");
            System.out.println("13. Menù conto biglietti vidimati");
            System.out.println("0. Logout");
            System.out.print("\nScegli un'opzione: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine()
                        .trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> menuCreazioneUtenti(tesseraDAO, utenteDAO, genericDAO);
                case 2 -> menuCreazioneMezzi(mezzoDiTrasportoDAO, genericDAO);
                case 3 -> menuCreazionePunti(puntoDiEmissioneDAO, genericDAO);
                case 4 -> Service.ricercaUtenti(utenteDAO);
                case 5 -> menuCreazioneTratte(trattaDAO, genericDAO);
                case 6 -> Service.generaPercorrenze(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO, genericDAO);
                case 7 -> Service.assegnaTrattaMezzo(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 8 -> Service.calcolaTempoMedio(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 9 -> Service.storicoPercorrenzeMezzoTratta(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 10 -> menuCountTitoliViaggio(titoloViaggioDAO, puntoDiEmissioneDAO);
                case 11 -> Service.storicoManutenzione(manutenzioneDAO);
                case 12 -> Service.verificaAbbonamento(titoloViaggioDAO);
                case 13 -> menuCountBigliettiVidimati(titoloViaggioDAO, mezzoDiTrasportoDAO);
                case 0 -> {
                    System.out.println("Logout amministratore effettuato.");
                    adminMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }
//endregion


    // region Menu Creazione Utenti
    public static void menuCreazioneUtenti(TesseraDAO tesseraDAO, UtenteDAO utenteDAO, GenericDAO genericDAO) {
        System.out.println("\n1. Genera utenti in blocco (dati finti)");
        System.out.println("2. Inserisci un utente a mano");
        System.out.print("Scegli un'opzione: ");
        try {
            int scelta = Integer.parseInt(scanner.nextLine().trim());
            switch (scelta) {
                case 1 -> Service.creazioneUtenti(tesseraDAO, utenteDAO, genericDAO);
                case 2 -> Service.creazioneUtenteManuale(utenteDAO);
                default -> System.out.println("Opzione non valida.");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Errore: Inserire un numero valido.");
        }
    }
    // endregion

    // region Menu Creazione Mezzi
    public static void menuCreazioneMezzi(MezzoDiTrasportoDAO mezzoDiTrasportoDAO, GenericDAO genericDAO) {
        while (true) {
            System.out.println("\n--- MENU CREAZIONE MEZZI ---");
            System.out.println("1. Genera mezzi in blocco (dati finti)");
            System.out.println("2. Inserisci un mezzo a mano");
            System.out.print("Scegli un'opzione: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                switch (scelta) {
                    case 1 -> {
                        Service.creazioneMezzi(mezzoDiTrasportoDAO, genericDAO);
                        return;
                    }
                    case 2 -> {
                        Service.creazioneMezzoManuale(mezzoDiTrasportoDAO);
                        return;
                    }
                    default -> System.out.println("Opzione non valida. Inserisci 1 o 2.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
            }

        }
    }

    // endregion

    // region Menu Creazione Punti
    public static void menuCreazionePunti(PuntoDiEmissioneDAO puntoDiEmissioneDAO, GenericDAO genericDAO) {
        while (true) {
            System.out.println("\n--- MENU CREAZIONE PUNTI DI EMISSIONE ---");
            System.out.println("1. Genera punti di emissione in blocco (dati finti)");
            System.out.println("2. Inserisci un punto di emissione a mano");
            System.out.print("Scegli un'opzione: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                switch (scelta) {
                    case 1 -> {
                        Service.creazionePunti(puntoDiEmissioneDAO, genericDAO);
                        return;
                    }
                    case 2 -> {
                        Service.creazionePuntoManuale(puntoDiEmissioneDAO);
                        return;
                    }
                    default -> System.out.println("Opzione non valida. Inserisci 1 o 2.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
            }
        }
    }

    // endregion

    // region Menu Creazione Tratte
    public static void menuCreazioneTratte(TrattaDAO trattaDAO, GenericDAO genericDAO) {
        while (true) {
            System.out.println("\n--- MENU CREAZIONE TRATTE ---");
            System.out.println("1. Genera tratte in blocco (dati finti)");
            System.out.println("2. Inserisci una tratta a mano");
            System.out.print("Scegli un'opzione: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                switch (scelta) {
                    case 1 -> {
                        Service.creazioneTratte(trattaDAO, genericDAO);
                        return;
                    }
                    case 2 -> {
                        Service.creazioneTrattaManuale(trattaDAO);
                        return;
                    }
                    default -> System.out.println("Opzione non valida. Inserisci 1 o 2.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
            }
        }
    }

    // endregion
    // region Case Utente
    public static void caseUser(TesseraDAO tesseraDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO, TrattaDAO trattaDAO, TitoloViaggioDAO titoloViaggioDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, Utente utente) {
        boolean userMenu = true;
        while (userMenu) {
            System.out.println("\n MENU PRINCIPALE UTENTE");
            System.out.println("1. Scegli punto vendita");
            System.out.println("2. Scegli viaggio");
            System.out.println("0. Logout");
            System.out.print("Scegli un'opzione: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine()
                        .trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> {
                    PuntoDiEmissione punto = Service.selezionaPunto(puntoDiEmissioneDAO);
                    if (punto != null) {
                        casePunto(punto, titoloViaggioDAO, tesseraDAO, utente);
                    }
                }

                case 2 -> caseViaggio(trattaDAO, titoloViaggioDAO);
                case 0 -> {
                    System.out.println("Logout utente effettuato.");
                    userMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }
//endregion

    // region Case Punto Vendita
    public static void casePunto(PuntoDiEmissione puntoVendita, TitoloViaggioDAO titoloViaggioDAO, TesseraDAO tesseraDAO, Utente utente) {
        boolean puntoMenu = true;
        while (puntoMenu) {
            System.out.println("\n MENU PUNTO VENDITA");
            System.out.println("1. Compra biglietto");
            System.out.println("2. Compra abbonamento");
            System.out.println("3. Crea tessera");
            System.out.println("4. Rinnova tessera");
            System.out.println("5. Rinnova Abbonamento");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scegli un'opzione: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine()
                        .trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> Service.compraBiglietto(titoloViaggioDAO, puntoVendita);
                case 2 -> Service.compraAbbonamento(titoloViaggioDAO, tesseraDAO, puntoVendita, utente);
                case 3 -> Service.creaTesseraUtente(utente, tesseraDAO);
                case 4 -> Service.rinnovotessera(tesseraDAO, utente);
                case 5 -> Service.rinnovoAbbonamento(titoloViaggioDAO);
                case 0 -> {
                    System.out.println("Torno al menu principale utente");
                    puntoMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }

        }
    }
//endregion

    //region Case Viaggio
    public static void caseViaggio(TrattaDAO trattaDAO, TitoloViaggioDAO titoloViaggioDAO) {
        boolean viaggioMenu = true;
        Tratta trattaSelezionata = null;

        while (viaggioMenu) {
            System.out.println("\n MENU VIAGGI");
            System.out.println("1. Scegli Viaggio");

            // L'opzione 2 compare solo se l'utente ha prima selezionato una tratta valida
            if (trattaSelezionata != null) {
                System.out.println("2. Vidima biglietto su questo mezzo");
            }
            System.out.println("0. Torna al menu principale");
            System.out.print("Scegli un'opzione: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine()
                        .trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> trattaSelezionata = Service.selezionaTratta(trattaDAO);
                case 2 -> {
                    if (trattaSelezionata != null) {
                        // Estraiamo l'oggetto MezzoTrasporto reale e lo passiamo al metodo di vidimazione
                        Service.vidimaBiglietto(titoloViaggioDAO, trattaSelezionata.getMezzoTrasporto());
                    } else {
                        System.out.println("Opzione non valida.");
                    }
                }
                case 0 -> {
                    System.out.println("Torno al menu principale utente");
                    viaggioMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }
//endregion

    //region  Menù Conta Titoli Viaggio
    public static void menuCountTitoliViaggio(TitoloViaggioDAO titoloViaggioDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        while (true) {
            System.out.println("\n SELEZIONA UN'OPZIONE \n");
            System.out.println("1. Conta biglietti emessi in un lasso di tempo.");
            System.out.println("2. Conta biglietti emessi in un lasso di tempo su un punto di emissione.");
            System.out.println("3. Conta abbonamenti emessi in un lasso di tempo.");
            System.out.println("4. Conta abbonamenti emessi in un lasso di tempo su un punto di emissione.");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            int input;
            try {
                input = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Formato errato, inserisci un numero valido.");
                continue;
            }

            if (input == 0) break;

            PuntoDiEmissione puntoDiEmissione = null;
            if (input == 2 || input == 4) {
                try {
                    puntoDiEmissione = Service.selezionaPunto(puntoDiEmissioneDAO);
                    if (puntoDiEmissione == null) {
                        continue;
                    }
                } catch (Exception ex) {
                    System.out.println("Errore nella selezione del punto vendita.");
                    continue;
                }
            }

            LocalDateTime dataInizio = null;
            LocalDateTime dataFine = null;
            if (input >= 1 && input <= 4) {
                while (true) {
                    dataInizio = richiediData("Data di inizio");
                    dataFine = richiediData("Data di fine");

                    if (dataInizio.isAfter(dataFine)) {
                        System.out.println("\n[ERRORE] La data di inizio non può essere successiva alla data di fine. Riprova l'inserimento.");
                    } else {
                        break; // Date cronologicamente corrette, esce dal loop di controllo date
                    }
                }
            }

            switch (input) {
                case 1 -> System.out.println("\nBiglietti emessi: " +
                        titoloViaggioDAO.countBigliettiBetween(dataInizio, dataFine));

                case 2 -> System.out.println("\nBiglietti emessi presso " + puntoDiEmissione.getNome() + ": " +
                        titoloViaggioDAO.countBigliettiBetween(dataInizio, dataFine, puntoDiEmissione));

                case 3 -> System.out.println("\nAbbonamenti emessi: " +
                        titoloViaggioDAO.countAbbonamentiBetween(dataInizio, dataFine));

                case 4 -> System.out.println("\nAbbonamenti emessi presso " + puntoDiEmissione.getNome() + ": " +
                        titoloViaggioDAO.countAbbonamentiBetween(dataInizio, dataFine, puntoDiEmissione));

                default -> System.out.println("Input non valido. Inserisci un numero da 0 a 4.");
            }
        }
    }

    //region  Menù Conta Biglietti Vidimati
    public static void menuCountBigliettiVidimati(TitoloViaggioDAO titoloViaggioDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        while (true) {
            System.out.println("\n SELEZIONA UN'OPZIONE \n");
            System.out.println("1. Conta biglietti vidimati per mezzo.");
            System.out.println("2. Conta biglietti vidimati per periodo.");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            int input;
            try {
                input = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Formato errato, inserisci un numero valido.");
                continue;
            }

            if (input == 0) break;


            LocalDateTime dataInizio = null;
            LocalDateTime dataFine = null;
            if (input >= 1 && input <= 2) {
                while (true) {
                    dataInizio = richiediData("Data di inizio");
                    dataFine = richiediData("Data di fine");

                    if (dataInizio.isAfter(dataFine)) {
                        System.out.println("\n[ERRORE] La data di inizio non può essere successiva alla data di fine. Riprova l'inserimento.");
                    } else {
                        break;
                    }
                }
            }

            switch (input) {
                case 1 -> {
                    MezzoDiTrasporto mezzo = Service.selezionaMezzo(mezzoDiTrasportoDAO);
                    System.out.println("\nBiglietti vidimati: " +
                            titoloViaggioDAO.countBigliettiVidimatiSuMezzo(mezzo));
                }

                case 2 -> System.out.println("\nBiglietti vidimati tra " + dataInizio + " e " + dataFine + ": " +
                        titoloViaggioDAO.countBigliettiVidimatiBetween(dataInizio, dataFine));

                default -> System.out.println("Input non valido. Inserisci un numero da 0 a 4.");
            }
        }
    }

    // METODO DATE TIME FORMATTER
    private static LocalDateTime richiediData(String tipoData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        while (true) {
            try {
                System.out.println("\n--- " + tipoData.toUpperCase() + " ---");

                System.out.print("Inserisci la data (gg/mm/aaaa): ");
                String dataInput = scanner.nextLine().trim();

                System.out.print("Inserisci l'ora (hh:mm): ");
                String oraInput = scanner.nextLine().trim();

                String dataCompleta = dataInput + " " + oraInput;

                return LocalDateTime.parse(dataCompleta, formatter);

            } catch (DateTimeParseException e) {
                System.out.println("Errore: Formato o valori non validi. Assicurati di scrivere la data come gg/mm/aaaa (es. 25/06/2026) e l'ora come hh:mm (es. 12:50). Riprova.");
            }
        }
    }
//endregion
}


