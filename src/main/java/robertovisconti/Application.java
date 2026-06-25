package robertovisconti;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import robertovisconti.dao.*;
import robertovisconti.entities.*;
import robertovisconti.enums.Ruolo;
import robertovisconti.enums.TipoAbbonamento;
import robertovisconti.exceptions.PuntoDiEmissioneNonTrovatoException;
import robertovisconti.exceptions.TesseraNonTrovataException;
import robertovisconti.exceptions.UtenteEmailNonTrovatoException;
import robertovisconti.exceptions.UtenteNonTrovatoException;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
            System.out.println("\nTRASPORTO PUBBLICO");
            System.out.println("\nInserisci la tua email per accedere");
            System.out.println("\n0. Chiudi Applicazione");
            System.out.print("\nScegli un'opzione o inserisci email: ");
            System.out.println("1. Login");
            System.out.println("2. Registrazione");
            System.out.println("0. Chiudi Applicazione");
            System.out.print("Scegli un'opzione: ");

            String email = scanner.nextLine().trim();
            if (Objects.equals(email, "0")) {
                System.out.println("\nApplicazione in chiusura...");
                break;
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

                    String email = scanner.nextLine()
                            .trim();

                    try {

                        Utente emailScanner = utenteDAO.findByEmail(email);

                        switch (emailScanner.getRuolo()) {
                            case ADMIN -> caseAdmin(tesseraDAO, utenteDAO, mezzoDiTrasportoDAO, puntoDiEmissioneDAO,
                                    trattaDAO, percorrenzaDAO, titoloViaggioDAO, genericDAO, manutenzioneDAO);
                            case USER -> caseUser(tesseraDAO, puntoDiEmissioneDAO, trattaDAO, titoloViaggioDAO,
                                    mezzoDiTrasportoDAO);
                            default -> System.out.println("Ruolo non riconosciuto.");
                        }

                    } catch (UtenteEmailNonTrovatoException ex) {
                        System.out.println("Errore: Nessun utente associato a questa email.");
                    }
                }
                case 2 -> registrazioneUtente(utenteDAO);

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
                case 1 -> Service.creazioneUtenti(tesseraDAO, utenteDAO, genericDAO);
                case 2 -> Service.creazioneMezzi(mezzoDiTrasportoDAO, genericDAO);
                case 3 -> Service.creazionePunti(puntoDiEmissioneDAO, genericDAO);
                case 4 -> Service.ricercaUtenti(utenteDAO);
                case 5 -> Service.creazioneTratte(trattaDAO, genericDAO);
                case 6 -> Service.generaPercorrenze(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO, genericDAO);
                case 7 -> Service.assegnaTrattaMezzo(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 8 -> Service.calcolaTempoMedio(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 9 -> Service.storicoPercorrenzeMezzoTratta(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 10 -> menuCountTitoliViaggio(titoloViaggioDAO, puntoDiEmissioneDAO);
                case 11 -> Service.storicoManutenzione(manutenzioneDAO);
                case 12 -> Service.verificaAbbonamento(titoloViaggioDAO);
                case 0 -> {
                    System.out.println("Logout amministratore effettuato.");
                    adminMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }
//endregion

    //region Case Utente
    public static void caseUser(TesseraDAO tesseraDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO, TrattaDAO trattaDAO, TitoloViaggioDAO titoloViaggioDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
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
                        casePunto(punto, titoloViaggioDAO, tesseraDAO);
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

    //region Case Punto Vendita
    public static void casePunto(PuntoDiEmissione puntoVendita, TitoloViaggioDAO titoloViaggioDAO, TesseraDAO tesseraDAO) {
        boolean puntoMenu = true;
        while (puntoMenu) {
            System.out.println("\n MENU PUNTO VENDITA");
            System.out.println("1. Compra biglietto");
            System.out.println("2. Compra abbonamento");
            System.out.println("3. Rinnova tessera");
            System.out.println("4. Rinnova Abbonamento");
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
                case 2 -> Service.compraAbbonamento(titoloViaggioDAO, tesseraDAO, puntoVendita);
                case 3 -> Service.rinnovotessera(tesseraDAO);
                case 4 -> Service.rinnovoAbbonamento(titoloViaggioDAO);
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

    // Registra nuovo user

    public static void registrazioneUtente(UtenteDAO utenteDAO) {

        System.out.println("\nREGISTRAZIONE UTENTE");

        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Cognome: ");
        String cognome = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        try {

            utenteDAO.findByEmail(email);

            System.out.println("Esiste già un account con questa email.");
            return;

        } catch (UtenteEmailNonTrovatoException e) {
            // Nuova email continua la registrazione
        }

        Utente nuovoUtente = new Utente(
                nome,
                cognome,
                email,
                Ruolo.USER
        );

        utenteDAO.saveUtente(nuovoUtente);

        System.out.println("Registrazione completata con successo!");
    }

    // Metodo Compra biglietto
    public static void compraBiglietto(TitoloViaggioDAO titoloViaggioDAO, PuntoDiEmissione puntoVendita) {
        Biglietto nuovoBiglietto = new Biglietto();
        nuovoBiglietto.setDataEmissione(LocalDateTime.now());
        nuovoBiglietto.setPuntoDiEmissione(puntoVendita);
        nuovoBiglietto.setCodiceUnivoco(UUID.randomUUID());
        try {
            titoloViaggioDAO.save(nuovoBiglietto);
            System.out.println("Biglietto acquistato al punto: " + puntoVendita.getNome());
        } catch (Exception ex) {
            System.out.println("Errore durante la vendita del biglietto: " + ex.getMessage());
        }
    }


    // Metodo Compra Abbonamento
    public static void compraAbbonamento(
            TitoloViaggioDAO titoloViaggioDAO,
            TesseraDAO tesseraDAO,
            PuntoDiEmissione puntoVendita) {

        System.out.println("\n--- ACQUISTO ABBONAMENTO ---");

        Tessera tessera;

        // Verifica se l'utente possiede già una tessera
        System.out.println("Hai già una tessera?");
        System.out.println("1. Sì");
        System.out.println("2. No");
        System.out.print("Scelta: ");

        int risposta;

        try {
            risposta = Integer.parseInt(scanner.nextLine()
                    .trim());
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return;
        }

        if (risposta == 1) {

            System.out.print("Inserisci il codice univoco della tessera: ");

            try {

                UUID codice = UUID.fromString(scanner.nextLine()
                        .trim());

                tessera = tesseraDAO.findByUnCode(codice);

                System.out.println("Tessera trovata!");

            } catch (IllegalArgumentException e) {

                System.out.println("Formato UUID non valido.");
                return;

            } catch (TesseraNonTrovataException e) {

                System.out.println(e.getMessage());
                return;
            }

        } else if (risposta == 2) {

            try {

                tessera = tesseraDAO.creaTessera();

                System.out.println("Tessera creata con successo!");
                System.out.println("Codice tessera: " + tessera.getCodiceUnivoco());

            } catch (Exception e) {

                System.out.println("Errore nella creazione della tessera: " + e.getMessage());
                return;
            }

        } else {

            System.out.println("Scelta non valida.");
            return;
        }

        // Selezione tipo abbonamento

        System.out.println("\nSeleziona il tipo di abbonamento:");
        System.out.println("1. Settimanale");
        System.out.println("2. Mensile");
        System.out.println("3. Annuale");
        System.out.print("Scelta: ");

        int scelta;

        try {

            scelta = Integer.parseInt(scanner.nextLine()
                    .trim());

    //region MENU' per cercare titoli di viaggio per periodo o per periodo e punto vendita.

    public static void menuCountTitoliViaggio(TitoloViaggioDAO titoloViaggioDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        while (true) {
            System.out.println("\n SELEZIONA UN'OPZIONE \n");
            System.out.println("1. Conta biglietti emessi in un lasso di tempo.");
            System.out.println("2. Conta biglietti emessi in un lasso di tempo su un punto di emissione.");
            System.out.println("3. Conta abbonamenti emessi in un lasso di tempo.");
            System.out.println("4. Conta abbonamenti emessi in un lasso di tempo su un punto di emissione.");
            System.out.println("0. Torna indietro");
            int input = -1;
            try {
                input = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Formato errato, inserisci un numbero");
                continue;
            }
            if (input == 0) break;

            try {
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

                        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
                            System.out.println("\nLa data di inizio non può essere successiva alla data di fine.\n");
                            continue;
                        }

                        System.out.println("\nBiglietti emessi tra " + dataInizio + " e " + dataFine + ": " + titoloViaggioDAO.countBigliettiBetween(dataInizio, dataFine));
                    }

                    case 2 -> {
                        PuntoDiEmissione puntoDiEmissione = null;
                        try {
                            puntoDiEmissione = Service.selezionaPunto(puntoDiEmissioneDAO);
                            if (puntoDiEmissione == null) {
                                continue;
                            }
                        } catch (PuntoDiEmissioneNonTrovatoException ex) {
                            System.out.println(ex.getMessage());
                            continue;
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
                        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
                            System.out.println("\nLa data di inizio non può essere successiva alla data di fine.\n");
                            continue;
                        }

                        if (puntoDiEmissione == null) {
                            System.out.println("Errore in immissione dati, controllare i dati inseriti e riprovare.\n");
                            continue;
                        }
                        ;
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

                        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
                            System.out.println("\nLa data di inizio non può essere successiva alla data di fine.\n");
                            continue;
                        }

                        System.out.println("\nAbbonamenti emessi tra " + dataInizio + " e " + dataFine + ": " + titoloViaggioDAO.countAbbonamentiBetween(dataInizio, dataFine));
                    }

                    case 4 -> {
                        PuntoDiEmissione puntoDiEmissione = null;
                        try {
                            puntoDiEmissione = Service.selezionaPunto(puntoDiEmissioneDAO);
                            if (puntoDiEmissione == null) {
                                continue;
                            }
                        } catch (PuntoDiEmissioneNonTrovatoException ex) {
                            System.out.println(ex.getMessage());
                            continue;
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

                        if (dataInizio != null && dataFine != null && dataInizio.isAfter(dataFine)) {
                            System.out.println("\nLa data di inizio non può essere successiva alla data di fine.\n");
                            continue;
                        }

                        System.out.println("\nAbbonamenti emessi tra " + dataInizio + " e " + dataFine + " presso " + puntoDiEmissione.getNome() + ": " + titoloViaggioDAO.countAbbonamentiBetween(dataInizio, dataFine, puntoDiEmissione));
                    }

                    default -> System.out.println("Input non valido");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Input errato, inserisci un numero valido");
                continue;
            }
        }
    }
    //endregion
}