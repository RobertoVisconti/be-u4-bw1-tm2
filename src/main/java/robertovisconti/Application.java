package robertovisconti;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.datafaker.Faker;
import robertovisconti.dao.MezzoDiTrasportoDAO;
import robertovisconti.dao.TesseraDAO;
import robertovisconti.dao.UtenteDAO;
import robertovisconti.entities.MezzoDiTrasporto;
import robertovisconti.entities.Tessera;
import robertovisconti.entities.Utente;
import robertovisconti.enums.Ruolo;
import robertovisconti.enums.StatoMezzo;
import robertovisconti.enums.TipoMezzo;

import java.util.Random;
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

        // creo il DAO dei mezzi
        MezzoDiTrasportoDAO mezzoDAO = new MezzoDiTrasportoDAO(em);

        // mi servono i valori degli enum e un Random
        TipoMezzo[] tipi = TipoMezzo.values();
        StatoMezzo[] stati = StatoMezzo.values();
        Random random = new Random();

        // genero 20 mezzi di trasporto
        for (int i = 0; i < 20; i++) {

            // scelgo a caso il tipo (BUS o TRAM) e lo stato (IN_SERVIZIO o IN_MANUTENZIONE)
            TipoMezzo tipo = tipi[random.nextInt(tipi.length)];
            StatoMezzo stato = stati[random.nextInt(stati.length)];

            // capienza tra 30 e 200 posti
            int capienza = random.nextInt(30, 201);

            // targa finta generata da Faker 
            String targa = faker.vehicle().licensePlate();

            // creo il mezzo e lo salvo nel database
            MezzoDiTrasporto mezzo = new MezzoDiTrasporto(tipo, capienza, stato, targa);
            mezzoDAO.save(mezzo);
        }

        System.out.println("Generazione dei 20 mezzi di trasporto completata.");

        em.close();
        entityManagerFactory.close();
    }
}
