package robertovisconti;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;
import robertovisconti.dao.TesseraDAO;
import robertovisconti.dao.UtenteDAO;
import robertovisconti.entities.Tessera;
import robertovisconti.entities.Utente;
import robertovisconti.enums.Ruolo;
import robertovisconti.exceptions.InputNonValidoException;
import robertovisconti.exceptions.UtenteNonTrovatoException;

import java.util.Scanner;
import java.util.UUID;

public class Application {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("be-u4-bw1-tm2");
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // apro l'EntityManager
        EntityManager em = entityManagerFactory.createEntityManager();

        // DAO pronti per i metodi
        TesseraDAO tesseraDAO = new TesseraDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);

        boolean chiuso = true;
        while (chiuso) {
            System.out.println("******* MENU PRINCIPALE *******");
            System.out.println("1. Genera utenti");
            System.out.println("2. Ricerca utenti");
            System.out.println("0. Esci");
            System.out.print(" Scegli un opzione: ");

            int scelta;
            try {
                try {
                    scelta = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException ex) {

                    throw new InputNonValidoException("Devi inserire un numero valido per il menù.");
                }
            } catch (InputNonValidoException ex) {

                System.out.println("Errore: " + ex.getMessage());
                scelta = -1;
            }

            switch (scelta) {
                case 1 -> creazioneUtenti(tesseraDAO, utenteDAO);
                case 2 -> ricercaUtenti(utenteDAO);
                case 0 -> {
                    System.out.println("Applicazione chiusa");
                    chiuso = false;
                }
                default -> System.out.println("Opzione non valida.");
            }

        }


//        em.close();
//        entityManagerFactory.close();
    }

    // Creazione Utenti
    public static void creazioneUtenti(TesseraDAO tesseraDAO, UtenteDAO utenteDAO) {
        // genera nomi e cognomi
        Faker faker = new Faker();
        for (int i = 0; i < 50; i++) {

            Tessera tessera = new Tessera(UUID.randomUUID());
            tesseraDAO.saveTessera(tessera);

            // (1 su 10) ne creo uno amministratore
            Ruolo ruolo = (i % 10 == 0) ? Ruolo.ADMIN : Ruolo.USER;

            // creo l'utente con nome e cognome finti generati da Faker
            Utente utente = new Utente(faker.name().firstName(), faker.name().lastName(), ruolo);

            utente.setIdTessera(tessera);

            // salvo l'utente nel database
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
        }
    }


}