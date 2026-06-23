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

import java.util.UUID;

public class Application {
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("be-u4-bw1-tm2");

    public static void main(String[] args) {

        // apro l'EntityManager
        EntityManager em = entityManagerFactory.createEntityManager();

        // creo i DAO passandogli l'EntityManager, cosi' posso salvare tessere e utenti
        TesseraDAO tesseraDAO = new TesseraDAO(em);
        UtenteDAO utenteDAO = new UtenteDAO(em);

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

        System.out.println("Generazione dei 50 utenti iniziali completata.");

        em.close();
        entityManagerFactory.close();
    }

}
