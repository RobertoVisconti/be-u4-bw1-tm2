package robertovisconti.dao;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import robertovisconti.entities.Tessera;
import robertovisconti.entities.Utente;
import robertovisconti.exceptions.UtenteEmailNonTrovatoException;
import robertovisconti.exceptions.UtenteNonTrovatoException;

import java.util.Optional;
import java.util.UUID;


public class UtenteDAO {

    private final EntityManager em;

    public UtenteDAO(EntityManager em) {
        this.em = em;
    }

    public void saveUtente(Utente utente) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(utente);
        tx.commit();
    }

    public Utente findByID(UUID id) {
        Utente utente = em.find(Utente.class, id);
        if (utente == null) {
            throw new UtenteNonTrovatoException(id);
        }
        return utente;
    }

    public Utente findByEmail(String email) {
        TypedQuery<Utente> query = em.createQuery("SELECT u FROM Utente u WHERE u.email = :email", Utente.class);
        query.setParameter("email", email);

        Optional<Utente> userOptional = query.getResultStream().findFirst();

        if (userOptional.isPresent()) {

            Utente utente = userOptional.get();

            System.out.println(utente);

            return utente;
        } else {
            throw new UtenteEmailNonTrovatoException(email);
        }
    }

    public void deleteUtente(UUID id) {
        Utente utente = findByID(id);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.remove(utente);
        tx.commit();
        System.out.println(utente + " Rimosso con successo.");
    }

    public void setIdTessera(Utente utente, Tessera tessera) {
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        utente.setIdTessera(tessera);
        transaction.commit();
        System.out.println("Tessera associata con successo!");
    }

}
