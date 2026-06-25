package robertovisconti.entities;

import net.datafaker.Faker;
import robertovisconti.dao.*;
import robertovisconti.enums.Ruolo;
import robertovisconti.enums.StatoDistributoreAutomatico;
import robertovisconti.enums.StatoMezzo;
import robertovisconti.enums.TipoMezzo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Service {

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
}
