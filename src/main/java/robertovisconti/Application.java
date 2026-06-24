package robertovisconti;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;
import robertovisconti.dao.*;
import robertovisconti.entities.*;
import robertovisconti.enums.Ruolo;
import robertovisconti.enums.StatoDistributoreAutomatico;
import robertovisconti.enums.StatoMezzo;
import robertovisconti.enums.TipoMezzo;
import robertovisconti.exceptions.PuntoDiEmissioneNonTrovatoException;
import robertovisconti.exceptions.UtenteEmailNonTrovatoException;
import robertovisconti.exceptions.UtenteNonTrovatoException;

import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Application {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("be-u4-bw1-tm2");
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        EntityManager em = entityManagerFactory.createEntityManager();

        TesseraDAO tesseraDAO = new TesseraDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);
        MezzoDiTrasportoDAO mezzoDiTrasportoDAO = new MezzoDiTrasportoDAO(em);
        PuntoDiEmissioneDAO puntoDiEmissioneDAO = new PuntoDiEmissioneDAO(em);
        TrattaDAO trattaDAO = new TrattaDAO(em);
        TitoloViaggioDAO titoloViaggioDAO = new TitoloViaggioDAO(em);
        PercorrenzaDAO percorrenzaDAO = new PercorrenzaDAO(em);


//        utenteDAO.saveUtente(new Utente("Roberto", "Admin", "ciaosonounadmin@adming.it", Ruolo.ADMIN));

        boolean optionMenu = true;
        while (optionMenu) {
            System.out.println("\nTRASPORTO PUBBLICO");
            System.out.println("Inserisci la tua email per accedere");
            System.out.println("0. Chiudi Applicazione");
            System.out.print("Scegli un'opzione o inserisci email: ");

            String email = scanner.nextLine().trim();
            if (Objects.equals(email, "0")) {
                System.out.println("Applicazione in chiusura...");
                break;
            }

            try {

                Utente emailScanner = utenteDAO.findByEmail(email);

                switch (emailScanner.getRuolo()) {
                    case ADMIN ->
                            caseAdmin(tesseraDAO, utenteDAO, mezzoDiTrasportoDAO, puntoDiEmissioneDAO, trattaDAO, percorrenzaDAO, titoloViaggioDAO);
                    case USER -> caseUser(tesseraDAO, puntoDiEmissioneDAO, trattaDAO);
                    default -> System.out.println("Ruolo non riconosciuto.");
                }

            } catch (UtenteEmailNonTrovatoException ex) {
                System.out.println("Errore: Nessun utente associato a questa email.");
            }
        }

        em.close();
        entityManagerFactory.close();
    }


    // Case Amministratore
    public static void caseAdmin(TesseraDAO tesseraDAO, UtenteDAO utenteDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO, TrattaDAO trattaDAO, PercorrenzaDAO percorrenzaDAO, TitoloViaggioDAO titoloViaggioDAO) {
        boolean adminMenu = true;
        while (adminMenu) {
            System.out.println("\n MENU PRINCIPALE ADMIN ");
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
            System.out.println("0. Logout");
            System.out.print("Scegli un'opzione: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> creazioneUtenti(tesseraDAO, utenteDAO);
                case 2 -> creazioneMezzi(mezzoDiTrasportoDAO);
                case 3 -> creazionePunti(puntoDiEmissioneDAO);
                case 4 -> ricercaUtenti(utenteDAO);
                case 5 -> creazioneTratte(trattaDAO);
                case 6 -> generaPercorrenze(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 7 -> assegnaTrattaMezzo(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 8 -> calcolaTempoMedio(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 9 -> storicoPercorrenzeMezzoTratta(trattaDAO, mezzoDiTrasportoDAO, percorrenzaDAO);
                case 10 -> menuCountTitoliViaggio(titoloViaggioDAO, puntoDiEmissioneDAO);
                case 0 -> {
                    System.out.println("Logout amministratore effettuato.");
                    adminMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    //Case Utente
    public static void caseUser(TesseraDAO tesseraDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO, TrattaDAO trattaDAO) {
        boolean userMenu = true;
        while (userMenu) {
            System.out.println("\n MENU PRINCIPALE UTENTE");
            System.out.println("1. Scegli punto vendita");
            System.out.println("2. Scegli viaggio");
            System.out.println("0. Logout");
            System.out.print("Scegli un'opzione: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> {
                    PuntoDiEmissione punto = selezionaPunto(puntoDiEmissioneDAO);
                    if (punto != null) {
                        casePunto(punto);
                    }
                }
                case 2 -> caseViaggio(trattaDAO);
                case 0 -> {
                    System.out.println("Logout utente effettuato.");
                    userMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    // Case Punto Vendita
    public static void casePunto(PuntoDiEmissione punto) {
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
                scelta = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> System.out.println("1. Compra biglietto");
                case 2 -> System.out.println("2. Compra abbonamento");
                case 3 -> System.out.println("3. Rinnova tessera");
                case 4 -> System.out.println("4. Rinnova Abbonamento");
                case 0 -> {
                    System.out.println("Torno al menu principale utente");
                    puntoMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }

        }
    }

    // Case Viaggio
    public static void caseViaggio(TrattaDAO trattaDAO) {
        boolean viaggioMenu = true;

        while (viaggioMenu) { // Il ciclo inizia qui
            System.out.println("\n MENU VIAGGI");
            System.out.println("1. Scegli Viaggio");
            System.out.println("0. Torna al menu principale");
            System.out.print("Scegli un'opzione: ");

            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException ex) {
                System.out.println("Errore: Inserire un numero valido.");
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> selezionaTratta(trattaDAO);
                case 0 -> {
                    System.out.println("Torno al menu principale utente");
                    viaggioMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    // Creazione Utenti
    public static void creazioneUtenti(TesseraDAO tesseraDAO, UtenteDAO utenteDAO) {
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {

            Ruolo ruolo = (i % 10 == 0) ? Ruolo.ADMIN : Ruolo.USER;
            String email = faker.internet().emailAddress();

            Utente utente = new Utente(faker.name().firstName(), faker.name().lastName(), email, ruolo);
            if (i % 5 == 0) {
                Tessera tessera = new Tessera(UUID.randomUUID());
                tesseraDAO.saveTessera(tessera);
                utente.setIdTessera(tessera);
            }
            utenteDAO.saveUtente(utente);
        }
        System.out.println("Creazione utenti avvenuta con successo.");
    }

    // Ricerca utente
    public static void ricercaUtenti(UtenteDAO utenteDAO) {
        try {
            System.out.print("Inserisci l'UUID dell'utente da cercare: ");
            String inserito = scanner.nextLine().trim();
            UUID id = UUID.fromString(inserito);
            Utente trovato = utenteDAO.findByID(id);
            System.out.println(trovato);
        } catch (UtenteNonTrovatoException ex) {
            System.out.println("Errore: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("Errore: Formato UUID non valido.");
        }
    }

    // Creazione Mezzi di trasporti
    public static void creazioneMezzi(MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        Faker faker = new Faker();
        Random random = new Random();

        TipoMezzo[] tipi = TipoMezzo.values();
        StatoMezzo[] stati = StatoMezzo.values();

        for (int i = 0; i < 20; i++) {
            TipoMezzo tipo = tipi[random.nextInt(tipi.length)];
            StatoMezzo stato = stati[random.nextInt(stati.length)];
            int capienza = random.nextInt(30, 201);
            String targa = faker.vehicle().licensePlate();

            MezzoDiTrasporto mezzo = new MezzoDiTrasporto(tipo, capienza, stato, targa);
            mezzoDiTrasportoDAO.save(mezzo);
        }
        System.out.println("Creazione 20 mezzi completata con successo.");
    }

    // Creazione Punti Vendita
    public static void creazionePunti(PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        Faker faker = new Faker(new Locale("it", "IT"));
        Random random = new Random();

        StatoDistributoreAutomatico[] statoDistributore = StatoDistributoreAutomatico.values();

        for (int i = 0; i < 20; i++) {

            String indirizzo = faker.address().streetAddress();
            String citta = faker.address().city();
            String cap = faker.address().zipCode();
            String piva = faker.number().digits(11);

            if (random.nextBoolean()) {
                String nome = "Distributore Automatico H24 - " + faker.address().streetAddress();
                StatoDistributoreAutomatico stato = statoDistributore[random.nextInt(statoDistributore.length)];
                DistributoreAutomatico distributore = new DistributoreAutomatico(nome, indirizzo, citta, cap, piva, stato);

                puntoDiEmissioneDAO.savePuntoDiEmissione(distributore);
            } else {
                String[] prefissi = {"Ticket Point ", "Biglietteria ", "Ricevitoria ", "Tabaccheria "};
                String nome = prefissi[random.nextInt(prefissi.length)] + faker.name().lastName();
                boolean isAperto = random.nextBoolean();
                Rivenditore rivenditore = new Rivenditore(nome, indirizzo, citta, cap, piva, isAperto);
                puntoDiEmissioneDAO.savePuntoDiEmissione(rivenditore);
            }

        }
        System.out.println("Creazione punti di emissione avvenuta con successo.");
    }


    // Creazione tratte in blocco
    public static void creazioneTratte(TrattaDAO trattaDAO) {
        Faker faker = new Faker(new Locale("it", "IT"));
        Random random = new Random();

        for (int i = 0; i < 15; i++) {
            String partenza = faker.address().city();
            String capolinea = faker.address().city();
            int tempoStimato = random.nextInt(10, 91); // minuti stimati, tra 10 e 90

            trattaDAO.creaTratta(partenza, capolinea, tempoStimato);
        }
        System.out.println("Creazione 15 tratte completata.");
    }

    // Genera percorrenze in blocco collegando tratte e mezzi gia' esistenti
    public static void generaPercorrenze(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO) {
        List<Tratta> tratte = trattaDAO.findAll();
        List<MezzoDiTrasporto> mezzi = mezzoDiTrasportoDAO.findAll();

        if (tratte.isEmpty() || mezzi.isEmpty()) {
            System.out.println("Servono prima delle tratte e dei mezzi.");
            return;
        }

        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            Tratta tratta = tratte.get(random.nextInt(tratte.size()));
            MezzoDiTrasporto mezzo = mezzi.get(random.nextInt(mezzi.size()));

            // data di inizio casuale negli ultimi 30 giorni
            LocalDateTime inizio = LocalDateTime.now().minusDays(random.nextInt(0, 30)).minusHours(random.nextInt(0, 24));
            // la fine = inizio + tempo stimato della tratta, con qualche minuto di variazione
            LocalDateTime fine = inizio.plusMinutes(tratta.getTempoPercorrenzaStimato() + random.nextInt(-5, 11));

            percorrenzaDAO.creaPercorrenza(tratta, mezzo, inizio, fine);
        }
        System.out.println("Creazione 30 percorrenze completata.");
    }

    // Assegna una tratta a un mezzo: l'admin sceglie mezzo e tratta
    public static void assegnaTrattaMezzo(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO) {
        try {
            System.out.print("Targa del mezzo: ");
            String targa = scanner.nextLine().trim();
            MezzoDiTrasporto mezzo = mezzoDiTrasportoDAO.findByTarga(targa);

            // scelgo la tratta da un elenco numerato
            Tratta tratta = selezionaTratta(trattaDAO);
            if (tratta == null) {
                return;
            }

            LocalDateTime inizio = LocalDateTime.now();
            LocalDateTime fine = inizio.plusMinutes(tratta.getTempoPercorrenzaStimato());

            Percorrenza percorrenza = percorrenzaDAO.assegnaTrattaAMezzo(tratta, mezzo, inizio, fine);
            System.out.println("Tratta assegnata al mezzo: " + percorrenza);
        } catch (IllegalArgumentException ex) {
            System.out.println("Errore: Formato UUID non valido.");
        } catch (RuntimeException ex) {
            System.out.println("Errore: " + ex.getMessage());
        }
    }

    // Calcola il tempo medio di percorrenza di una tratta da parte di un mezzo
    public static void calcolaTempoMedio(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO) {
        try {
            System.out.print("Targa del mezzo: ");
            String targa = scanner.nextLine().trim();
            MezzoDiTrasporto mezzo = mezzoDiTrasportoDAO.findByTarga(targa);

            // scelgo la tratta da un elenco numerato
            Tratta tratta = selezionaTratta(trattaDAO);
            if (tratta == null) {
                return;
            }

            Duration media = percorrenzaDAO.tempoMedioPercorrenza(tratta, mezzo);
            if (media.isZero()) {
                System.out.println("Nessuna percorrenza conclusa trovata per questo mezzo su questa tratta.");
            } else {
                long minuti = media.toMinutes();
                long secondi = media.minusMinutes(minuti).getSeconds();
                System.out.println("Tempo medio di percorrenza: " + minuti + " min " + secondi + " sec.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Errore: Formato UUID non valido.");
        } catch (RuntimeException ex) {
            System.out.println("Errore: " + ex.getMessage());
        }
    }

    // Storico: quante volte un mezzo ha percorso una tratta e quanto ha impiegato ogni volta
    public static void storicoPercorrenzeMezzoTratta(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO) {
        try {
            System.out.print("Targa del mezzo: ");
            String targa = scanner.nextLine().trim();
            MezzoDiTrasporto mezzo = mezzoDiTrasportoDAO.findByTarga(targa);

            // scelgo la tratta da un elenco numerato
            Tratta tratta = selezionaTratta(trattaDAO);
            if (tratta == null) {
                return;
            }

            List<Duration> tempi = percorrenzaDAO.tempiPercorrenza(tratta, mezzo);

            System.out.println("\nIl mezzo " + targa + " ha percorso questa tratta " + tempi.size() + " volte:");
            if (tempi.isEmpty()) {
                System.out.println("(nessuna percorrenza registrata)");
            }
            int n = 1;
            for (Duration t : tempi) {
                long minuti = t.toMinutes();
                long secondi = t.minusMinutes(minuti).getSeconds();
                System.out.println(n + ") " + minuti + " min " + secondi + " sec");
                n++;
            }
        } catch (RuntimeException ex) {
            System.out.println("Errore: " + ex.getMessage());
        }
    }

    // Mostra l'elenco numerato delle tratte e restituisce quella scelta
    public static Tratta selezionaTratta(TrattaDAO trattaDAO) {
        List<Tratta> tratte = trattaDAO.findAll();

        if (tratte.isEmpty()) {
            System.out.println("Nessuna tratta presente.");
            return null;
        }

        System.out.println("\nScegli una tratta:");
        for (int i = 0; i < tratte.size(); i++) {
            Tratta t = tratte.get(i);
            System.out.println((i + 1) + ". " + t.getPuntoDiPartenza() + " -> " + t.getCapolinea()
                    + " (stimato " + t.getTempoPercorrenzaStimato() + " min)");
        }
        System.out.print("Numero della tratta: ");

        try {
            int scelta = Integer.parseInt(scanner.nextLine().trim());
            if (scelta < 1 || scelta > tratte.size()) {
                System.out.println("Numero non valido.");
                return null;
            }
            return tratte.get(scelta - 1);
        } catch (NumberFormatException ex) {
            System.out.println("Devi inserire un numero.");
            return null;
        }
    }

    // Mostra l'elenco dei punti vendita
    public static PuntoDiEmissione selezionaPunto(PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        List<PuntoDiEmissione> punti = puntoDiEmissioneDAO.findAllPuntiDiEmissione();

        if (punti.isEmpty()) {
            System.out.println("Nessuna tratta presente: creane prima con l'opzione 5.");
            return null;
        }

        System.out.println("\nPunti vendita:");
        for (int i = 0; i < punti.size(); i++) {
            PuntoDiEmissione p = punti.get(i);
            System.out.println((i + 1) + ". " + p.getNome() + " -> " + p.getIndirizzo()
                    + " " + p.getCitta());
        }
        System.out.print("Scegli Punto vendita: ");

        try {
            int scelta = Integer.parseInt(scanner.nextLine().trim());
            if (scelta < 1 || scelta > punti.size()) {
                System.out.println("Numero non valido.");
                return null;
            }
            return punti.get(scelta - 1);
        } catch (NumberFormatException ex) {
            System.out.println("Devi inserire un numero.");
            return null;
        }
    }

    //Storico Manutenzione
    public static void storicoManutenzione(ManutenzioneDAO dao) {
        System.out.println("\nInserisci la targa:");
        String targa = scanner.nextLine();

        List<Manutenzione> lista = dao.storicoManutenzioni(targa);

        if (lista.isEmpty()) {
            System.out.println("Nessuna manutenzione trovata.");
            return;
        }

        System.out.println("\nStorico Manutenzioni:");
        for (Manutenzione m : lista) {
            System.out.println("\nInizio: " + m.getDataInizio());
            System.out.println("Fine: " + m.getDataFine());
            System.out.println("Motivo: " + m.getMotivo());
        }
    }

    //Verifica abbonamento

    public static void verificaAbbonamento(TitoloViaggioDAO titoloDAO) {

        System.out.println("Inserisci il codice della tessera:");

        UUID codice = UUID.fromString(scanner.nextLine());

        boolean valido = titoloDAO.isAbbonamentoValido(codice);

        if (valido) {
            System.out.println("Abbonamento valido.");
        } else {
            System.out.println("Abbonamento scaduto o inesistente.");
        }
    }

    // VIdima biglietto

    public static void vidimaBiglietto(TitoloViaggioDAO titoloDAO) {

        try {

            System.out.println("Inserisci il codice univoco del biglietto:");

            UUID codice = UUID.fromString(scanner.nextLine());

            titoloDAO.vidimaBiglietto(codice);

        } catch (IllegalArgumentException e) {

            System.out.println("UUID non valido.");

        } catch (RuntimeException e) {

            System.out.println(e.getMessage());
        }
    }


//    // MENU' per cercare titoli di viaggio per periodo o per periodo e punto vendita.

    public static void menuCountTitoliViaggio(TitoloViaggioDAO titoloViaggioDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        while (true) {
            System.out.println("\n******* SELEZIONA UN'OPZIONE *******\n");
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
                    try {
                        puntoDiEmissione = Application.selezionaPunto(puntoDiEmissioneDAO);
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

                    System.out.println("\nAbbonamenti emessi tra " + dataInizio + " e " + dataFine + ": " + titoloViaggioDAO.countAbbonamentiBetween(dataInizio, dataFine));
                }

                case 4 -> {
                    PuntoDiEmissione puntoDiEmissione = null;
                    try {
                        puntoDiEmissione = Application.selezionaPunto(puntoDiEmissioneDAO);

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