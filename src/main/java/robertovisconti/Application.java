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
import robertovisconti.exceptions.UtenteEmailNonTrovatoException;
import robertovisconti.exceptions.UtenteNonTrovatoException;

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


        utenteDAO.saveUtente(new Utente("Roberto", "Admin", "ciaosonounadmin@adming.it", Ruolo.ADMIN));

        boolean optionMenu = true;
        while (optionMenu) {
            System.out.println("\n******* TRASPORTO PUBBLICO *******");
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
                    case ADMIN -> caseAdmin(tesseraDAO, utenteDAO, mezzoDiTrasportoDAO, puntoDiEmissioneDAO);
                    case USER -> caseUser(tesseraDAO);
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
    public static void caseAdmin(TesseraDAO tesseraDAO, UtenteDAO utenteDAO, MezzoDiTrasportoDAO mezzoDiTrasportoDAO, PuntoDiEmissioneDAO puntoDiEmissioneDAO) {
        boolean adminMenu = true;
        while (adminMenu) {
            System.out.println("\n******* MENU PRINCIPALE ADMIN *******");
            System.out.println("1. Genera utenti / tessera / non tessera");
            System.out.println("2. Creazione mezzi di trasporto");
            System.out.println("3. Ricerca utenti");
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
                case 0 -> {
                    System.out.println("Logout amministratore effettuato.");
                    adminMenu = false;
                }
                default -> System.out.println("Opzione non valida.");
            }
        }
    }

    //Case Utente
    public static void caseUser(TesseraDAO tesseraDAO) {
        boolean userMenu = true;
        while (userMenu) {
            System.out.println("\n******* MENU PRINCIPALE UTENTE *******");
            System.out.println("1. Visualizza stato della tessera");
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
                case 1 -> System.out.println("Funzionalità utente in sviluppo...");
                case 0 -> {
                    System.out.println("Logout utente effettuato.");
                    userMenu = false;
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
        for(Manutenzione m : lista) {
            System.out.println("\nInizio: " + m.getDataInizio());
            System.out.println("Fine: " + m.getDataFine());
            System.out.println("Motivo: " + m.getMotivo());
        }
    }

}