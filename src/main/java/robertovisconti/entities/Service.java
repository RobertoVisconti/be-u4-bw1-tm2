package robertovisconti.entities;

import net.datafaker.Faker;
import robertovisconti.dao.*;
import robertovisconti.enums.*;
import robertovisconti.exceptions.TesseraNonTrovataException;
import robertovisconti.exceptions.UtenteEmailNonTrovatoException;
import robertovisconti.exceptions.UtenteNonTrovatoException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

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
                capienza = 120;
            } else {
                capienza = 250;
            }

            String targa = faker.vehicle().licensePlate();

            MezzoDiTrasporto mezzo = new MezzoDiTrasporto(tipo, capienza, stato, targa);
            mezzoDiTrasportoDAO.save(mezzo);
        }
        System.out.println("Creazione 20 mezzi completata con successo.");
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
            risposta = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return;
        }

        if (risposta == 1) {

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

            scelta = Integer.parseInt(scanner.nextLine().trim());

        } catch (NumberFormatException e) {

            System.out.println("Input non valido.");
            return;
        }

        TipoAbbonamento tipoAbbonamento;


        switch (scelta) {

            case 1 -> {

                tipoAbbonamento = TipoAbbonamento.SETTIMANALE;


            }

            case 2 -> {

                tipoAbbonamento = TipoAbbonamento.MENSILE;


            }

            case 3 -> {

                tipoAbbonamento = TipoAbbonamento.ANNUALE;


            }

            default -> {

                System.out.println("Scelta non valida.");
                return;
            }
        }

        try {

            Abbonamento nuovoAbbonamento = new Abbonamento(LocalDateTime.now(), puntoVendita, UUID.randomUUID(), tipoAbbonamento, tessera);


            titoloViaggioDAO.save(nuovoAbbonamento);

            System.out.println("\nAbbonamento acquistato con successo!");
            System.out.println("Tipo: " + tipoAbbonamento);
            System.out.println("Punto vendita: " + puntoVendita.getNome());

        } catch (Exception e) {

            System.out.println("Errore durante il salvataggio: " + e.getMessage());
        }
    }
//endregion

    //region Metodo Rinnovo Tessera
    public static void rinnovotessera(TesseraDAO tesseraDAO) {

        System.out.println("\n--- RINNOVO TESSERA ---");
        System.out.print("Inserisci il Codice Univoco della tessera: ");

        UUID codiceUnivoco;

        try {
            codiceUnivoco = UUID.fromString(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("UUID non valido.");
            return;
        }

        System.out.println("\nPer quanto tempo vuoi rinnovare?");
        System.out.println("1. 1 anno");
        System.out.println("2. 2 anni");
        System.out.println("3. 3 anni");
        System.out.print("Scelta: ");

        int scelta;

        try {
            scelta = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return;
        }

        int anni;

        switch (scelta) {
            case 1 -> anni = 1;
            case 2 -> anni = 2;
            case 3 -> anni = 3;
            default -> {
                System.out.println("Scelta non valida.");
                return;
            }
        }

        try {

            Tessera tessera = tesseraDAO.findByUnCode(codiceUnivoco);

            LocalDate vecchiaScadenza = tessera.getDataScadenza();

            LocalDate nuovaScadenza;

            if (vecchiaScadenza.isAfter(LocalDate.now())) {
                nuovaScadenza = vecchiaScadenza.plusYears(anni);
            } else {
                nuovaScadenza = LocalDate.now().plusYears(anni);
            }

            tesseraDAO.updateTessera(
                    codiceUnivoco,
                    tessera.getDataEmissione(),
                    nuovaScadenza
            );

            System.out.println("Tessera rinnovata!");
            System.out.println("Nuova scadenza: " + nuovaScadenza);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
//endregion

    //region Metodo Rinnovo Abbonamento
    public static void rinnovoAbbonamento(TitoloViaggioDAO titoloViaggioDAO) {

        System.out.println("\n--- RINNOVO ABBONAMENTO ---");
        System.out.print("Inserisci il Codice Univoco dell'abbonamento: ");

        UUID codiceUnivoco;

        try {
            codiceUnivoco = UUID.fromString(scanner.nextLine().trim());
        } catch (IllegalArgumentException e) {
            System.out.println("UUID non valido.");
            return;
        }

        System.out.println("\nSeleziona il tipo di rinnovo:");
        System.out.println("1. Settimanale");
        System.out.println("2. Mensile");
        System.out.println("3. Annuale");
        System.out.print("Scelta: ");

        int scelta;

        try {
            scelta = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
            return;
        }

        TipoAbbonamento tipoAbbonamento;

        switch (scelta) {
            case 1 -> tipoAbbonamento = TipoAbbonamento.SETTIMANALE;
            case 2 -> tipoAbbonamento = TipoAbbonamento.MENSILE;
            case 3 -> tipoAbbonamento = TipoAbbonamento.ANNUALE;
            default -> {
                System.out.println("Scelta non valida.");
                return;
            }
        }

        try {

            titoloViaggioDAO.updateAbbonamento(codiceUnivoco, tipoAbbonamento, LocalDateTime.now());

            System.out.println("Abbonamento rinnovato con successo!");

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }
    }
//endregion

    //region Ricerca utente
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
//endregion

    //region Mostra l'elenco dei punti vendita
    public static PuntoDiEmissione selezionaPunto(PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        List<PuntoDiEmissione> punti = puntoDiEmissioneDAO.findAllPuntiDiEmissione();

        if (punti.isEmpty()) {
            System.out.println("Nessun punto di emissione presente");
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
//endregion

    //region Verifica abbonamento

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
//endregion

    //region ADMIN: Calcola il tempo medio di percorrenza di una tratta da parte di un mezzo
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
//endregion

    //region Storico: quante volte un mezzo ha percorso una tratta e quanto ha impiegato ogni volta
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
//endregion

    //region Storico: Manutenzioni
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
//endregion
}
