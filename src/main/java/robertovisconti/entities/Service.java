package robertovisconti.entities;

import net.datafaker.Faker;
import robertovisconti.dao.*;
import robertovisconti.enums.*;
import robertovisconti.exceptions.TesseraNonTrovataException;
import robertovisconti.exceptions.UtenteEmailNonTrovatoException;
import robertovisconti.exceptions.UtenteNonTrovatoException;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import static robertovisconti.Application.scanner;

public class Service {


    //region CREAZIONE AUTOMATICA TABELLE
    // Creazione Utenti
    public static void creazioneUtenti(TesseraDAO tesseraDAO, UtenteDAO utenteDAO, GenericDAO genericDAO) {
        if (!genericDAO.isTableEmpty(Utente.class)) {
            return;
        }
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {

            Ruolo ruolo = (i % 10 == 0) ? Ruolo.ADMIN : Ruolo.USER;
            String email = faker.internet().emailAddress();

            Utente utente = new Utente(faker.name().firstName(), faker.name().lastName(), email, ruolo);
            if (i % 5 == 0) {
                Tessera tessera = tesseraDAO.creaTessera();
                utente.setIdTessera(tessera);
            }
            utenteDAO.saveUtente(utente);
        }
        System.out.println("Creazione utenti avvenuta con successo.");
    }

    public static void creazioneUtenteSingolo(TesseraDAO tesseraDAO, UtenteDAO utenteDAO) {
        Faker faker = new Faker();
        Random random = new Random();

        Ruolo ruolo = random.nextInt(10) == 0 ? Ruolo.ADMIN : Ruolo.USER;
        String email = faker.internet().emailAddress();

        Utente utente = new Utente(faker.name().firstName(), faker.name().lastName(), email, ruolo);
        if (random.nextInt(5) == 0) {
            Tessera tessera = tesseraDAO.creaTessera();
            utente.setIdTessera(tessera);
        }
        utenteDAO.saveUtente(utente);
        System.out.println("Utente creato con successo.");
    }

    // Creazione Mezzi di trasporti
    public static void creazioneMezzi(MezzoDiTrasportoDAO mezzoDiTrasportoDAO, GenericDAO genericDAO) {
        if (!genericDAO.isTableEmpty(MezzoDiTrasporto.class)) {
            return;
        }
        Faker faker = new Faker();
        Random random = new Random();

        TipoMezzo[] tipi = TipoMezzo.values();
        StatoMezzo[] stati = StatoMezzo.values();

        for (int i = 0; i < 20; i++) {
            TipoMezzo tipo = tipi[random.nextInt(tipi.length)];
            StatoMezzo stato = stati[random.nextInt(stati.length)];

            // la capienza dipende dal tipo di mezzo
            int capienza;
            if (tipo == TipoMezzo.BUS) {
                capienza = 80;
            } else {
                capienza = 220;
            }

            String targa = faker.vehicle().licensePlate();

            MezzoDiTrasporto mezzo = new MezzoDiTrasporto(tipo, capienza, stato, targa);
            mezzoDiTrasportoDAO.save(mezzo);
        }
        System.out.println("Creazione 20 mezzi completata con successo.");
    }

    public static void creazioneMezzoSingolo(MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        Faker faker = new Faker();
        Random random = new Random();

        TipoMezzo[] tipi = TipoMezzo.values();
        StatoMezzo[] stati = StatoMezzo.values();

        TipoMezzo tipo = tipi[random.nextInt(tipi.length)];
        StatoMezzo stato = stati[random.nextInt(stati.length)];

        int capienza = (tipo == TipoMezzo.BUS) ? 120 : 250;
        String targa = faker.vehicle().licensePlate();

        MezzoDiTrasporto mezzo = new MezzoDiTrasporto(tipo, capienza, stato, targa);
        mezzoDiTrasportoDAO.save(mezzo);
        System.out.println("Mezzo creato con successo.");
    }

    // Creazione Punti Vendita
    public static void creazionePunti(PuntoDiEmissioneDAO puntoDiEmissioneDAO, GenericDAO genericDAO) {
        if (!genericDAO.isTableEmpty(PuntoDiEmissione.class)) {
            return;
        }
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

    public static void creazionePuntoSingolo(PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        Faker faker = new Faker(new Locale("it", "IT"));
        Random random = new Random();

        StatoDistributoreAutomatico[] statoDistributore = StatoDistributoreAutomatico.values();

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
        System.out.println("Punto di emissione creato con successo.");
    }

    // Creazione tratte in blocco
    public static void creazioneTratte(TrattaDAO trattaDAO, GenericDAO genericDAO) {
        if (!genericDAO.isTableEmpty(Tratta.class)) {
            return;
        }
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

    public static void creazioneTrattaSingola(TrattaDAO trattaDAO) {
        Faker faker = new Faker(new Locale("it", "IT"));
        Random random = new Random();

        String partenza = faker.address().city();
        String capolinea = faker.address().city();
        int tempoStimato = random.nextInt(10, 91);

        trattaDAO.creaTratta(partenza, capolinea, tempoStimato);
        System.out.println("Tratta creata con successo.");
    }

    // Creazione biglietti in blocco
    public static void creazioneBiglietti(TitoloViaggioDAO titoloViaggioDAO, GenericDAO genericDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        if (!genericDAO.isTableEmpty(TitoloViaggio.class)) {
            return;
        }
        List<PuntoDiEmissione> tuttiIPunti = puntoDiEmissioneDAO.findAllPuntiDiEmissione();
        List<MezzoDiTrasporto> tuttiIMezzi = mezzoDiTrasportoDAO.findAll();
        Random random = new Random();

        for (int i = 0; i < 25; i++) {
            PuntoDiEmissione puntoRandom = tuttiIPunti.get(random.nextInt(0, tuttiIPunti.size()));
            MezzoDiTrasporto mezzoRandom = tuttiIMezzi.get(random.nextInt(0, tuttiIPunti.size()));
            Biglietto biglietto = new Biglietto(LocalDateTime.now(), puntoRandom, mezzoRandom);
            titoloViaggioDAO.save(biglietto);
        }
        System.out.println("Creazione 25 biglietti completata.");
    }

    // Genera percorrenze in blocco collegando tratte e mezzi gia' esistenti
    public static void generaPercorrenze(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO, GenericDAO genericDAO) {
        if (!genericDAO.isTableEmpty(Percorrenza.class)) {
            return;
        }
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
    //endregion

    //region CREAZIONE MANUALE (Admin)
    public static void creazioneUtenteManuale(UtenteDAO utenteDAO) {
        System.out.println("\nINSERIMENTO UTENTE");

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);

        String nome = "";
        while (nome.isEmpty()) {
            System.out.print("Nome: ");
            nome = scanner.nextLine().trim();
            if (nome.isEmpty()) {
                System.out.println("Il nome non può essere vuoto. Riprova.");
            }
        }

        String cognome = "";
        while (cognome.isEmpty()) {
            System.out.print("Cognome: ");
            cognome = scanner.nextLine().trim();
            if (cognome.isEmpty()) {
                System.out.println("Il cognome non può essere vuoto. Riprova.");
            }
        }


        String email2 = "";
        while (true) {
            System.out.print("Email: ");
            email2 = scanner.nextLine().trim();

            if (email2.isEmpty()) {
                System.out.println("L'email non può essere vuota. Riprova.");
                continue;
            }

            if (!pattern.matcher(email2).matches()) {
                System.out.println("Errore: Il formato dell'email non è valido (es. esempio@gmail.com).");
                continue;
            }

            if (!verificaDominioEsistente(email2)) {
                System.out.println("Errore: Il dominio di questa email non esiste o non può ricevere messaggi. Riprova.");
                continue;
            }


            try {
                utenteDAO.findByEmail(email2);
                System.out.println("Errore: Esiste già un account con questa email. Riprova con un'altra.");
            } catch (UtenteEmailNonTrovatoException e) {

                break;
            }
        }

        Ruolo ruolo = null;
        while (ruolo == null) {
            System.out.println("\nRuolo:");
            System.out.println("1. Utente");
            System.out.println("2. Amministratore");
            System.out.print("Scelta: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> ruolo = Ruolo.USER;
                case "2" -> ruolo = Ruolo.ADMIN;
                default -> System.out.println("Scelta non valida. Inserisci 1 o 2.");
            }
        }

        Utente utente = new Utente(nome, cognome, email2, ruolo);
        utenteDAO.saveUtente(utente);
        System.out.println("\nUtente creato con successo!");
    }

    public static void creazioneMezzoManuale(MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {

        System.out.println("\nINSERIMENTO MEZZO DI TRASPORTO");

        TipoMezzo tipo = null;

        while (tipo == null) {
            System.out.println("\nTipo di mezzo:");
            System.out.println("1. Bus");
            System.out.println("2. Tram");
            System.out.print("Scelta: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> tipo = TipoMezzo.BUS;
                case "2" -> tipo = TipoMezzo.TRAM;
                default -> System.out.println("Scelta non valida. Inserisci 1 o 2.");
            }
        }

        int capienza = -1;
        int maxCapienza = (tipo == TipoMezzo.BUS) ? 80 : 220;

        while (capienza <= 0 || capienza > maxCapienza) {

            System.out.print("\nCapienza massima (max " + maxCapienza + "): ");

            try {
                capienza = Integer.parseInt(scanner.nextLine().trim());

                if (capienza <= 0) {
                    System.out.println("La capienza deve essere maggiore di 0.");
                } else if (capienza > maxCapienza) {
                    System.out.println("Capienza troppo alta! Max consentito per " + tipo + " = " + maxCapienza);
                }

            } catch (NumberFormatException e) {
                System.out.println("Capienza non valida. Inserisci un numero valido.");
            }
        }

        StatoMezzo stato = null;

        while (stato == null) {
            System.out.println("\nStato:");
            System.out.println("1. In servizio");
            System.out.println("2. In manutenzione");
            System.out.print("Scelta: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> stato = StatoMezzo.IN_SERVIZIO;
                case "2" -> stato = StatoMezzo.IN_MANUTENZIONE;
                default -> System.out.println("Scelta non valida. Inserisci 1 o 2.");
            }
        }

        String targa;

        while (true) {

            System.out.print("\nTarga (formato XXX-1111): ");
            targa = scanner.nextLine().trim().toUpperCase();


            if (targa.length() > 8) {
                System.out.println("Errore: la targa non può superare 8 caratteri.");
                continue;
            }

            if (!targa.matches("[A-Z]{3}-\\d{4}")) {
                System.out.println("Formato non valido! Esempio corretto: ABC-1234");
                continue;
            }

            try {
                mezzoDiTrasportoDAO.findByTarga(targa);
                System.out.println("Errore: targa già esistente. Inseriscine un'altra.");
                continue;
            } catch (Exception e) {

                break;
            }
        }

        MezzoDiTrasporto mezzo = new MezzoDiTrasporto(tipo, capienza, stato, targa);
        mezzoDiTrasportoDAO.save(mezzo);

        System.out.println("\nMezzo creato con successo.");
    }

    public static void creazionePuntoManuale(PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        System.out.println("\nINSERIMENTO PUNTO DI EMISSIONE");

        String tipoScelto = "";
        while (!tipoScelto.equals("1") && !tipoScelto.equals("2")) {
            System.out.println("\nTipo di punto:");
            System.out.println("1. Distributore automatico");
            System.out.println("2. Rivenditore");
            System.out.print("Scelta: ");
            tipoScelto = scanner.nextLine().trim();

            if (!tipoScelto.equals("1") && !tipoScelto.equals("2")) {
                System.out.println("Scelta non valida. Inserisci 1 o 2.");
            }
        }

        System.out.print("\nNome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("Indirizzo: ");
        String indirizzo = scanner.nextLine().trim();

        System.out.print("Città: ");
        String citta = scanner.nextLine().trim();

        System.out.print("CAP: ");
        String cap = scanner.nextLine().trim();

        System.out.print("Partita IVA: ");
        String piva = scanner.nextLine().trim();

        if (tipoScelto.equals("1")) {
            StatoDistributoreAutomatico stato = null;
            while (stato == null) {
                System.out.println("\nStato:");
                System.out.println("1. Attivo");
                System.out.println("2. Non attivo");
                System.out.print("Scelta: ");

                switch (scanner.nextLine().trim()) {
                    case "1" -> stato = StatoDistributoreAutomatico.ATTIVO;
                    case "2" -> stato = StatoDistributoreAutomatico.NON_ATTIVO;
                    default -> System.out.println("Scelta non valida. Inserisci 1 o 2.");
                }
            }
            DistributoreAutomatico distributore = new DistributoreAutomatico(nome, indirizzo, citta, cap, piva, stato);
            puntoDiEmissioneDAO.savePuntoDiEmissione(distributore);

        } else {
            Boolean aperto = null;
            while (aperto == null) {
                System.out.println("\nAperto?");
                System.out.println("1. Sì");
                System.out.println("2. No");
                System.out.print("Scelta: ");

                switch (scanner.nextLine().trim()) {
                    case "1" -> aperto = true;
                    case "2" -> aperto = false;
                    default -> System.out.println("Scelta non valida. Inserisci 1 o 2.");
                }
            }
            Rivenditore rivenditore = new Rivenditore(nome, indirizzo, citta, cap, piva, aperto);
            puntoDiEmissioneDAO.savePuntoDiEmissione(rivenditore);
        }

        System.out.println("\nPunto di emissione creato con successo.");
    }

    public static void creazioneTrattaManuale(TrattaDAO trattaDAO) {
        System.out.println("\nINSERIMENTO TRATTA");

        System.out.print("Punto di partenza: ");
        String partenza = scanner.nextLine().trim();

        System.out.print("Capolinea: ");
        String capolinea = scanner.nextLine().trim();

        int tempoStimato = -1;
        while (tempoStimato <= 0) {
            System.out.print("Tempo di percorrenza stimato (minuti): ");
            try {
                tempoStimato = Integer.parseInt(scanner.nextLine().trim());
                if (tempoStimato <= 0) {
                    System.out.println("Il tempo di percorrenza deve essere un numero maggiore di 0.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Valore non valido. Inserisci un numero intero (es. 45). Riprova.");
            }
        }
        trattaDAO.creaTratta(partenza, capolinea, tempoStimato);

        System.out.println("\nTratta creata con successo.");
    }
    
    //endregion

    //region Metodo Compra biglietto
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
//endregion

    //region Metodo Compra Abbonamento
    public static void compraAbbonamento(
            TitoloViaggioDAO titoloViaggioDAO,
            TesseraDAO tesseraDAO,
            PuntoDiEmissione puntoVendita,
            Utente utente) {

        System.out.println("\nACQUISTO ABBONAMENTO");

        Tessera tessera;

        int risposta;


        while (true) {
            System.out.println("\nHai già una tessera?");
            System.out.println("1. Sì");
            System.out.println("2. No");
            System.out.print("Scelta: ");

            try {
                risposta = Integer.parseInt(scanner.nextLine().trim());

                if (risposta == 1 || risposta == 2) {
                    break;
                }

                System.out.println("Scelta non valida. Scegli opzione 1 o 2.");

            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
            }
        }


        if (risposta == 1) {


            if (utente.getIdTessera() != null) {

                tessera = utente.getIdTessera();

                System.out.println("Hai già una tessera associata al tuo account.");
                System.out.println("Codice tessera: " +
                        tessera.getCodiceUnivoco());

            } else {

                System.out.println("Non hai una tessera associata al tuo account.");

                int scelta;

                while (true) {
                    System.out.println("\nScegli tra le seguinti opzioni:");
                    System.out.println("1. Inserire codice tessera esistente");
                    System.out.println("2. Creare nuova tessera");
                    System.out.print("Scelta: ");

                    try {
                        scelta = Integer.parseInt(scanner.nextLine().trim());

                        if (scelta == 1 || scelta == 2) {
                            break;
                        }

                        System.out.println("Scelta non valida.");

                    } catch (NumberFormatException e) {
                        System.out.println("Input non valido.");
                    }
                }

                if (scelta == 1) {

                    System.out.print("Inserisci il codice univoco della tessera: ");

                    try {

                        UUID codice = UUID.fromString(scanner.nextLine().trim());

                        tessera = tesseraDAO.findByUnCode(codice);

                        System.out.println("Tessera trovata!");

                    } catch (IllegalArgumentException e) {
                        System.out.println("Formato UUID non valido.");
                        return;

                    } catch (TesseraNonTrovataException e) {
                        System.out.println(e.getMessage());
                        return;
                    }

                } else {

                    try {

                        tessera = tesseraDAO.creaTessera();
                        utente.setIdTessera(tessera);

                        System.out.println("Tessera creata con successo!");
                        System.out.println("Codice tessera: " +
                                tessera.getCodiceUnivoco());

                    } catch (Exception e) {
                        System.out.println("Errore nella creazione della tessera: "
                                + e.getMessage());
                        return;
                    }
                }
            }


        } else {

            if (utente.getIdTessera() != null) {

                System.out.println("Hai già una tessera associata al tuo account.");
                System.out.println("Codice tessera: " +
                        utente.getIdTessera().getCodiceUnivoco());

                tessera = utente.getIdTessera();

            } else {

                try {

                    tessera = tesseraDAO.creaTessera();
                    utente.setIdTessera(tessera);

                    System.out.println("Tessera creata con successo!");
                    System.out.println("Codice tessera: " +
                            tessera.getCodiceUnivoco());

                } catch (Exception e) {
                    System.out.println("Errore nella creazione della tessera: "
                            + e.getMessage());
                    return;
                }
            }
        }


        int scelta;

        while (true) {
            System.out.println("\nSeleziona il tipo di abbonamento:");
            System.out.println("1. Settimanale");
            System.out.println("2. Mensile");
            System.out.println("3. Annuale");
            System.out.print("Scelta: ");

            try {
                scelta = Integer.parseInt(scanner.nextLine().trim());

                if (scelta >= 1 && scelta <= 3) {
                    break;
                }

                System.out.println("Scelta non valida.");

            } catch (NumberFormatException e) {
                System.out.println("Input non valido.");
            }
        }

        TipoAbbonamento tipoAbbonamento = switch (scelta) {
            case 1 -> TipoAbbonamento.SETTIMANALE;
            case 2 -> TipoAbbonamento.MENSILE;
            case 3 -> TipoAbbonamento.ANNUALE;
            default -> throw new IllegalStateException("Unexpected value: " + scelta);
        };


        try {

            Abbonamento nuovoAbbonamento = new Abbonamento(
                    LocalDateTime.now(),
                    puntoVendita,
                    UUID.randomUUID(),
                    tipoAbbonamento,
                    tessera
            );

            titoloViaggioDAO.save(nuovoAbbonamento);

            System.out.println("\nAbbonamento acquistato con successo!");
            System.out.println("Tipo: " + tipoAbbonamento);
            System.out.println("Scadenza: " + nuovoAbbonamento.getDataScadenza());
            System.out.println("Punto vendita: " + puntoVendita.getNome());

        } catch (Exception e) {
            System.out.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }
//endregion

    //region Metodo crea tessera

    public static void creaTesseraUtente(Utente utente, TesseraDAO tesseraDAO) {

        if (utente == null) {
            System.out.println("Errore: utente non valido.");
            return;
        }

        if (utente.getIdTessera() != null) {
            System.out.println("Hai già una tessera associata al tuo account.");
            System.out.println("Codice tessera: " + utente.getIdTessera().getCodiceUnivoco());
            return;
        }

        try {
            Tessera nuovaTessera = tesseraDAO.creaTessera();

            utente.setIdTessera(nuovaTessera);

            System.out.println("Tessera creata con successo!");
            System.out.println("Codice tessera: " + nuovaTessera.getCodiceUnivoco());

        } catch (Exception e) {
            System.out.println("Errore nella creazione della tessera: " + e.getMessage());
        }
    }

    //endregion

    //region Metodo Rinnovo Tessera
    public static void rinnovotessera(TesseraDAO tesseraDAO, Utente utente) {
        System.out.println("\n--- RINNOVO TESSERA ---");

        if (utente.getIdTessera() == null) {
            System.out.println("Non hai una tessera associata al tuo account.");
            System.out.println("Crea prima una tessera per procedere.");
            return;
        }

        Tessera tessera = utente.getIdTessera();
        UUID codiceUnivoco = tessera.getCodiceUnivoco();

        System.out.println("Tessera trovata, codice: " + codiceUnivoco);

        try {
            LocalDate vecchiaScadenza = tessera.getDataScadenza();
            LocalDate oggi = LocalDate.now();
            LocalDate nuovaScadenza;

            if (vecchiaScadenza != null && vecchiaScadenza.isAfter(oggi)) {

                long giorniMancanti = ChronoUnit.DAYS.between(oggi, vecchiaScadenza);

                if (giorniMancanti > 30) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    System.out.println("\nNon puoi rinnovare la tessera adesso.");
                    System.out.println("Scade il " + vecchiaScadenza.format(formatter) + " (mancano ancora " + giorniMancanti + " giorni).");
                    System.out.println("Il rinnovo è consentito solo a partire da 30 giorni prima della scadenza.");
                    return;
                }


                nuovaScadenza = vecchiaScadenza.plusYears(1);
            } else {

                nuovaScadenza = oggi.plusYears(1);
            }

            tesseraDAO.updateTessera(
                    codiceUnivoco,
                    tessera.getDataEmissione(),
                    nuovaScadenza
            );

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            System.out.println("\nTessera rinnovata con successo.");
            System.out.println("Nuova scadenza: " + nuovaScadenza.format(formatter));

        } catch (Exception e) {
            System.out.println("Errore durante il rinnovo: " + e.getMessage());
        }
    }
//endregion

    //region Metodo Rinnovo Abbonamento
    public static void rinnovoAbbonamento(TitoloViaggioDAO titoloViaggioDAO) {
        System.out.println("\n--- RINNOVO ABBONAMENTO ---");

        Abbonamento abbonamento = null;
        UUID codiceUnivoco = null;

        while (abbonamento == null) {
            System.out.print("Inserisci il Codice Univoco dell'abbonamento (UUID): ");
            String inputCodice = scanner.nextLine().trim();

            if (inputCodice.isEmpty()) {
                System.out.println("Il codice non può essere vuoto. Riprova.");
                continue;
            }

            try {
                codiceUnivoco = UUID.fromString(inputCodice);

                abbonamento = titoloViaggioDAO.getUltimoAbbonamento(codiceUnivoco);

                if (abbonamento == null) {
                    System.out.println("Nessun abbonamento trovato con questo codice. Riprova.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Formato UUID non valido. Riprova.");
                codiceUnivoco = null;
            } catch (RuntimeException e) {
                System.out.println("Errore di database: " + e.getMessage() + ". Riprova.");
                codiceUnivoco = null;
            }
        }

        LocalDateTime dataScadenzaAttuale = abbonamento.getDataScadenza();
        LocalDateTime adesso = LocalDateTime.now();

        if (dataScadenzaAttuale != null && dataScadenzaAttuale.isAfter(adesso)) {
            long giorniMancanti = ChronoUnit.DAYS.between(adesso, dataScadenzaAttuale);
            TipoAbbonamento tipoAttuale = abbonamento.getTipoAbbonamento();

            int giorniLimite = 0;
            if (tipoAttuale == TipoAbbonamento.SETTIMANALE) {
                giorniLimite = 3;
            } else if (tipoAttuale == TipoAbbonamento.MENSILE) {
                giorniLimite = 7;
            } else if (tipoAttuale == TipoAbbonamento.ANNUALE) {
                giorniLimite = 30;
            }

            if (giorniMancanti > giorniLimite) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                System.out.println("\nNon puoi ancora rinnovare questo abbonamento.");
                System.out.println("L'abbonamento attuale è: " + tipoAttuale + " e scade il " + dataScadenzaAttuale.format(formatter) + " (mancano: " + giorniMancanti + " giorni).");
                System.out.println("Il rinnovo per questa tipologia è consentito solo da " + giorniLimite + " giorni prima della scadenza.");
                return;
            }
        }

        TipoAbbonamento nuovoTipoAbbonamento = null;
        while (nuovoTipoAbbonamento == null) {
            System.out.println("\nSeleziona il tipo di rinnovo:");
            System.out.println("1. Settimanale");
            System.out.println("2. Mensile");
            System.out.println("3. Annuale");
            System.out.print("Scelta: ");

            String scelta = scanner.nextLine().trim();
            switch (scelta) {
                case "1" -> nuovoTipoAbbonamento = TipoAbbonamento.SETTIMANALE;
                case "2" -> nuovoTipoAbbonamento = TipoAbbonamento.MENSILE;
                case "3" -> nuovoTipoAbbonamento = TipoAbbonamento.ANNUALE;
                default -> System.out.println("Scelta non valida. Inserisci un numero da 1 a 3.");
            }
        }

        try {

            titoloViaggioDAO.updateAbbonamento(codiceUnivoco, nuovoTipoAbbonamento, adesso);

            System.out.println("\nAbbonamento rinnovato con successo in: " + nuovoTipoAbbonamento);
        } catch (Exception e) {
            System.out.println("Errore durante il salvataggio del rinnovo: " + e.getMessage());
        }
    }
//endregion

    //region Ricerca utente
    public static void ricercaUtenti(UtenteDAO utenteDAO) {
        while (true) {
            try {
                System.out.print("Inserisci l'UUID dell'utente da cercare: ");
                String inserito = scanner.nextLine().trim();

                UUID id = UUID.fromString(inserito);
                Utente trovato = utenteDAO.findByID(id);

                System.out.println("\nUtente trovato:");
                System.out.println(trovato);
                break;
            } catch (IllegalArgumentException ex) {
                System.out.println("Errore: Formato UUID non valido. Riprova.");
            } catch (UtenteNonTrovatoException ex) {
                System.out.println("Errore: " + ex.getMessage() + " Riprova.");
            }
        }
    }
//endregion

    //region Mostra l'elenco dei punti vendita
    public static PuntoDiEmissione selezionaPunto(PuntoDiEmissioneDAO puntoDiEmissioneDAO) {

        List<PuntoDiEmissione> punti = puntoDiEmissioneDAO.findAllPuntiDiEmissione();

        if (punti.isEmpty()) {
            System.out.println("Nessun punto di emissione presente");
            return null;
        }

        while (true) {

            System.out.println("\nPunti vendita:");

            for (int i = 0; i < punti.size(); i++) {
                PuntoDiEmissione p = punti.get(i);
                System.out.println((i + 1) + ". " + p.getNome()
                        + " -> " + p.getIndirizzo()
                        + " " + p.getCitta());
            }

            System.out.print("Scegli Punto vendita: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());

                if (scelta < 1 || scelta > punti.size()) {
                    System.out.println("Numero non valido. Riprova.");
                    continue;
                }

                PuntoDiEmissione selezionato = punti.get(scelta - 1);

                if (selezionato instanceof Rivenditore rivenditore) {

                    if (!rivenditore.isAperto()) {
                        System.out.println("Questo rivenditore è chiuso. Scegli un altro punto.");
                        continue;
                    }
                }

                if (selezionato instanceof DistributoreAutomatico distributore) {

                    if (distributore.getStato() == StatoDistributoreAutomatico.NON_ATTIVO) {
                        System.out.println("Questo distributore automatico non è attivo. Scegli un altro punto.");
                        continue;
                    }
                }
                return selezionato;

            } catch (NumberFormatException ex) {
                System.out.println("Devi inserire un numero. Riprova.");
            }
        }
    }
//endregion

    //region aggiorna stati punto di emissione

    public static void aggiornaStatoRivenditore(PuntoDiEmissioneDAO puntoDAO) {

        System.out.println("\n--- AGGIORNA STATO RIVENDITORE ---");
        System.out.print("Inserisci ID  del rivenditore: ");

        UUID id;

        try {
            id = UUID.fromString(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("ID non valido.");
            return;
        }

        System.out.println("1. Apri rivenditore");
        System.out.println("2. Chiudi rivenditore");
        System.out.print("Scelta: ");

        boolean stato;

        try {
            int scelta = Integer.parseInt(scanner.nextLine().trim());

            switch (scelta) {
                case 1 -> stato = true;
                case 2 -> stato = false;
                default -> {
                    System.out.println("Scelta non valida.");
                    return;
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return;
        }

        try {
            puntoDAO.updateStatoRivenditoreById(id, stato);
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    public static void aggiornaStatoDistributore(PuntoDiEmissioneDAO puntoDAO) {

        System.out.println("\n--- AGGIORNA STATO DISTRIBUTORE ---");
        System.out.print("Inserisci ID del distributore: ");

        UUID id;

        try {
            id = UUID.fromString(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("ID non valido.");
            return;
        }

        System.out.println("1. Attiva distributore");
        System.out.println("2. Disattiva distributore");
        System.out.print("Scelta: ");

        StatoDistributoreAutomatico stato;

        try {
            int scelta = Integer.parseInt(scanner.nextLine().trim());

            switch (scelta) {
                case 1 -> stato = StatoDistributoreAutomatico.ATTIVO;
                case 2 -> stato = StatoDistributoreAutomatico.NON_ATTIVO;
                default -> {
                    System.out.println("Scelta non valida.");
                    return;
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return;
        }

        try {
            puntoDAO.updateStatoDistributoreById(id, stato);
        } catch (Exception e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }

    //endregion

    //region Mostra l'elenco dei mezzi
    public static MezzoDiTrasporto selezionaMezzo(MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        List<MezzoDiTrasporto> mezzi = mezzoDiTrasportoDAO.findAll();

        if (mezzi.isEmpty()) {
            System.out.println("Nessun mezzo di trasporto presente");
            return null;
        }

        while (true) {
            System.out.println("\nMezzi di Trasporto:");
            for (int i = 0; i < mezzi.size(); i++) {
                MezzoDiTrasporto m = mezzi.get(i);
                System.out.println((i + 1) + ". " + m.getTipoMezzo() + " -> Targa: " + m.getTarga());
            }
            System.out.print("Scegli Mezzo di Trasporto: ");

            try {
                int scelta = Integer.parseInt(scanner.nextLine().trim());
                if (scelta >= 1 && scelta <= mezzi.size()) {
                    return mezzi.get(scelta - 1);
                } else {
                    System.out.println("Numero non valido. Per favore, riprova.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Devi inserire un numero. Per favore, riprova.");
            }
        }
    }
//endregion

    //region Verifica abbonamento

    public static void verificaAbbonamento(TitoloViaggioDAO titoloDAO) {
        System.out.println("\n--- VERIFICA VALIDITÀ ABBONAMENTO ---");

        UUID codice = null;
        Abbonamento abbonamento = null;

        while (codice == null) {
            System.out.print("Inserisci il codice della tessera: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Il codice non può essere vuoto. Riprova.");
                continue;
            }

            try {
                codice = UUID.fromString(input);

                abbonamento = titoloDAO.getUltimoAbbonamento(codice);

            } catch (IllegalArgumentException ex) {
                System.out.println("Errore: Formato codice non valido (deve essere un UUID). Riprova.");
                codice = null; // Forza il ciclo a continuare
            } catch (RuntimeException ex) {
                System.out.println("Errore durante la verifica sul database: " + ex.getMessage() + ". Riprova.");
                codice = null;
            }
        }

        if (abbonamento != null) {
            LocalDateTime scadenza = abbonamento.getDataScadenza();


            if (scadenza.isAfter(LocalDateTime.now())) {

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm");
                String dataFormattata = scadenza.format(formatter);

                System.out.println("\nAbbonamento VALIDO! Scade il: " + dataFormattata);
            } else {
                System.out.println("\n[ATTENZIONE] Abbonamento SCADUTO.");
            }
        } else {
            System.out.println("\n[ATTENZIONE] Tessera INESISTENTE o nessun abbonamento associato.");
        }
    }
//endregion

    //region Vidima biglietto

    public static void vidimaBiglietto(TitoloViaggioDAO titoloViaggioDAO, MezzoDiTrasporto mezzo) {
        System.out.println("\n--- VIDIMAZIONE BIGLIETTO ---");
        System.out.print("Inserisci il codice univoco del biglietto: ");

        try {
            UUID codiceBiglietto = UUID.fromString(scanner.nextLine().trim());

            // Invochiamo il DAO passando l'UUID e l'oggetto MezzoTrasporto
            titoloViaggioDAO.vidimaBiglietto(codiceBiglietto, mezzo);

        } catch (IllegalArgumentException e) {
            System.out.println("Errore: Il formato del codice biglietto non è un UUID valido.");
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
//endregion

    //region Registra nuovo user

    public static void registrazioneUtente(UtenteDAO utenteDAO) {
        System.out.println("\nREGISTRAZIONE UTENTE");

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);

        String nome = "";
        while (nome.isEmpty()) {
            System.out.print("Nome: ");
            nome = scanner.nextLine().trim();
            if (nome.isEmpty()) {
                System.out.println("Il nome non può essere vuoto. Riprova.");
            }
        }


        String cognome = "";
        while (cognome.isEmpty()) {
            System.out.print("Cognome: ");
            cognome = scanner.nextLine().trim();
            if (cognome.isEmpty()) {
                System.out.println("Il cognome non può essere vuoto. Riprova.");
            }
        }

        String email = "";
        while (true) {
            System.out.print("Email: ");
            email = scanner.nextLine().trim();

            if (email.isEmpty()) {
                System.out.println("L'email non può essere vuota. Riprova.");
                continue;
            }

            if (!pattern.matcher(email).matches()) {
                System.out.println("Errore: Il formato dell'email non è valido (es. esempio@dominio.com). Riprova.");
                continue;
            }

            if (!verificaDominioEsistente(email)) {
                System.out.println("Errore: Il dominio di questa email non esiste o non può ricevere messaggi. Riprova.");
                continue;
            }

            try {
                utenteDAO.findByEmail(email);
                System.out.println("Errore: Esiste già un account con questa email. Scegli un altro indirizzo.");
            } catch (UtenteEmailNonTrovatoException e) {
                break;
            }
        }

        Utente nuovoUtente = new Utente(
                nome,
                cognome,
                email,
                Ruolo.USER
        );

        utenteDAO.saveUtente(nuovoUtente);
        System.out.println("\nRegistrazione completata con successo!");
    }


    public static boolean verificaDominioEsistente(String email) {
        try {
            // Estraggo il dominio
            String dominio = email.substring(email.indexOf("@") + 1);

            // Inizializzo le richieste alla rete
            InitialDirContext ctx = new InitialDirContext();

            // Chiedo il Mail Exchange ( MX ) per il dominio
            Attributes attrs = ctx.getAttributes("dns:///" + dominio, new String[]{"MX"});
            Attribute mx = attrs.get("MX");

            return (mx != null && mx.size() > 0);

        } catch (NamingException e) {
            System.out.println("[Info Server] Impossibile verificare online il dominio. Controllo saltato.");
            return true;
        }
    }
//endregion

    //region ADMIN: Seleziona Tratta - Mostra l'elenco numerato delle tratte e restituisce quella scelta
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
//endregion

    //region ADMIN: Assegna una tratta a un mezzo
    public static void assegnaTrattaMezzo(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO) {
        System.out.println("\n--- ASSEGNAZIONE TRATTA A MEZZO ---");

        MezzoDiTrasporto mezzo = null;
        while (mezzo == null) {
            System.out.print("Targa del mezzo: ");
            String targa = scanner.nextLine().trim();

            if (targa.isEmpty()) {
                System.out.println("La targa non può essere vuota. Riprova.");
                continue;
            }

            try {
                mezzo = mezzoDiTrasportoDAO.findByTarga(targa);
                if (mezzo == null) {
                    System.out.println("Nessun mezzo trovato con questa targa. Riprova.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Errore nella ricerca del mezzo: " + ex.getMessage() + ". Riprova.");
            }
        }

        Tratta tratta = null;
        while (tratta == null) {
            tratta = selezionaTratta(trattaDAO);
            if (tratta == null) {
                System.out.println("Selezione non valida. Devi scegliere una tratta dall'elenco. Riprova.");
            }
        }

        try {
            LocalDateTime inizio = LocalDateTime.now();
            LocalDateTime fine = inizio.plusMinutes(tratta.getTempoPercorrenzaStimato());

            Percorrenza percorrenza = percorrenzaDAO.assegnaTrattaAMezzo(tratta, mezzo, inizio, fine);
            System.out.println("\nTratta assegnata al mezzo con successo!");
            System.out.println("Dettagli: " + percorrenza);

        } catch (IllegalArgumentException ex) {
            System.out.println("Errore: Formato UUID non valido.");
        } catch (RuntimeException ex) {
            System.out.println("Errore durante il salvataggio: " + ex.getMessage());
        }
    }
//endregion

    //region ADMIN: Calcola il tempo medio di percorrenza di una tratta da parte di un mezzo
    public static void calcolaTempoMedio(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO) {
        System.out.println("\n--- CALCOLO TEMPO MEDIO DI PERCORRENZA ---");

        MezzoDiTrasporto mezzo = null;
        while (mezzo == null) {
            System.out.print("Targa del mezzo: ");
            String targa = scanner.nextLine().trim();

            if (targa.isEmpty()) {
                System.out.println("La targa non può essere vuota. Riprova.");
                continue;
            }

            try {
                mezzo = mezzoDiTrasportoDAO.findByTarga(targa);
                if (mezzo == null) {
                    System.out.println("Nessun mezzo trovato con questa targa. Riprova.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Errore nella ricerca del mezzo: " + ex.getMessage() + ". Riprova.");
            }
        }

        Tratta tratta = null;
        while (tratta == null) {
            tratta = selezionaTratta(trattaDAO);
            if (tratta == null) {
                System.out.println("Selezione non valida. Devi scegliere una tratta dall'elenco. Riprova.");
            }
        }

        try {
            Duration media = percorrenzaDAO.tempoMedioPercorrenza(tratta, mezzo);
            if (media.isZero()) {
                System.out.println("\nNessuna percorrenza conclusa trovata per questo mezzo su questa tratta.");
            } else {
                long minuti = media.toMinutes();
                long secondi = media.minusMinutes(minuti).getSeconds();
                System.out.println("\nTempo medio di percorrenza: " + minuti + " min " + secondi + " sec.");
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Errore: Formato UUID non valido.");
        } catch (RuntimeException ex) {
            System.out.println("Errore durante il calcolo: " + ex.getMessage());
        }
    }
//endregion

    //region Storico: quante volte un mezzo ha percorso una tratta e quanto ha impiegato ogni volta
    public static void storicoPercorrenzeMezzoTratta(TrattaDAO trattaDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PercorrenzaDAO percorrenzaDAO) {
        System.out.println("\n--- STORICO PERCORRENZE MEZZO/TRATTA ---");

        MezzoDiTrasporto mezzo = null;
        while (mezzo == null) {
            System.out.print("Targa del mezzo: ");
            String targa = scanner.nextLine().trim();

            if (targa.isEmpty()) {
                System.out.println("La targa non può essere vuota. Riprova.");
                continue;
            }

            try {
                mezzo = mezzoDiTrasportoDAO.findByTarga(targa);
                if (mezzo == null) {
                    System.out.println("Nessun mezzo trovato con questa targa. Riprova.");
                }
            } catch (RuntimeException ex) {
                System.out.println("Errore nella ricerca del mezzo: " + ex.getMessage() + ". Riprova.");
            }
        }

        Tratta tratta = null;
        while (tratta == null) {
            tratta = selezionaTratta(trattaDAO);
            if (tratta == null) {
                System.out.println("Selezione non valida. Devi scegliere una tratta dall'elenco. Riprova.");
            }
        }

        try {
            List<Duration> tempi = percorrenzaDAO.tempiPercorrenza(tratta, mezzo);

            System.out.println("\nIl mezzo " + mezzo.getTarga() + " ha percorso questa tratta " + tempi.size() + " volte:");

            if (tempi.isEmpty()) {
                System.out.println("(nessuna percorrenza registrata)");
            } else {
                int n = 1;
                for (Duration t : tempi) {
                    long minuti = t.toMinutes();
                    long secondi = t.minusMinutes(minuti).getSeconds();
                    System.out.println(n + ") " + minuti + " min " + secondi + " sec");
                    n++;
                }
            }
        } catch (RuntimeException ex) {
            System.out.println("Errore durante il recupero dello storico: " + ex.getMessage());
        }
    }
//endregion

    //region Storico: Manutenzioni
    public static void storicoManutenzione(ManutenzioneDAO dao, MezzoDiTrasportoDAO mezzoDAO) {
        System.out.println("\n--- STORICO MANUTENZIONI MEZZO ---");

        List<Manutenzione> lista;
        String targa;

        while (true) {
            System.out.print("Inserisci la targa del mezzo: ");
            targa = scanner.nextLine().trim();

            if (targa.isEmpty()) {
                System.out.println("La targa non può essere vuota. Riprova.");
                continue;
            }

            try {
                MezzoDiTrasporto mezzo = mezzoDAO.findByTarga(targa);

                if (mezzo == null) {
                    System.out.println("Errore: Il mezzo con targa '" + targa + "' non esiste. Riprova.");
                    continue;
                }

                lista = dao.storicoManutenzioni(targa);
                break;

            } catch (RuntimeException ex) {
                System.out.println("Mezzo non trovato o errore di ricerca. Riprova.");
            }
        }

        if (lista == null || lista.isEmpty()) {
            System.out.println("\nIl mezzo con targa " + targa + " esiste ma non ha nessuna manutenzione registrata.");
            return;
        }

        // Stampa dello storico
        System.out.println("\nStorico Manutenzioni trovate (" + lista.size() + "):");
        for (Manutenzione m : lista) {
            System.out.println("\n---------------------------------");
            System.out.println("Inizio : " + m.getDataInizio());
            System.out.println("Fine   : " + (m.getDataFine() != null ? m.getDataFine() : "In corso..."));
            System.out.println("Motivo : " + m.getMotivo());
        }
    }
//endregion

    //region Cambia Stato Mezzo
    public static void cambiaStatoMezzo(MezzoDiTrasportoDAO mezzoDiTrasportoDAO) {
        System.out.println("\n--- CAMBIO STATO MEZZO (SERVIZIO / MANUTENZIONE) ---");

        MezzoDiTrasporto mezzo = null;

        while (mezzo == null) {
            System.out.print("Inserisci la targa del mezzo: ");
            String targa = scanner.nextLine().trim();

            try {
                mezzo = mezzoDiTrasportoDAO.findByTarga(targa);

                if (mezzo == null) {
                    System.out.println("Nessun mezzo trovato con questa targa. Riprova.");
                }
            } catch (Exception e) {
                System.out.println("Errore nella ricerca: Mezzo non trovato. Riprova.");
            }
        }

        System.out.println("Mezzo trovato! Stato attuale: " + mezzo.getStatoMezzo());

        StatoMezzo nuovoStato = null;
        while (nuovoStato == null) {
            System.out.println("\nSeleziona il nuovo stato:");
            System.out.println("1. In servizio");
            System.out.println("2. In manutenzione");
            System.out.print("Scelta: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> nuovoStato = StatoMezzo.IN_SERVIZIO;
                case "2" -> nuovoStato = StatoMezzo.IN_MANUTENZIONE;
                default -> {
                    System.out.println("Scelta non valida.");
                    continue;
                }
            }

            if (nuovoStato == mezzo.getStatoMezzo()) {
                System.out.println("Il mezzo è già " + nuovoStato + ".");
                nuovoStato = null;
            }
        }

        mezzoDiTrasportoDAO.updateMezzo(
                mezzo.getTarga(),
                mezzo.getTipoMezzo(),
                mezzo.getCapienza(),
                nuovoStato
        );

        System.out.println("Stato del mezzo aggiornato con successo in: " + nuovoStato);
    }
    // endregion


//    public Biglietto vendiBiglietto(MezzoDiTrasporto mezzoDiTrasporto) {
//        if ((this instanceof DistributoreAutomatico && ((DistributoreAutomatico) this).getStato() == StatoDistributoreAutomatico.NON_ATTIVO) || !(this instanceof Rivenditore && ((Rivenditore) this).isAperto())) {
//            throw new RuntimeException("Punto di Emissione CHIUSO.");
//        } else {
//            Biglietto biglietto = new Biglietto(LocalDateTime.now(), this, mezzoDiTrasporto);
//            System.out.println("Il biglietto " + biglietto + " è stato creato e venduto!");
//            return biglietto;
//        }
//    }
}
