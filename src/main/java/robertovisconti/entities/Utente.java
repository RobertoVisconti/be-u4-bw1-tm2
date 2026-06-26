package robertovisconti.entities;

import jakarta.persistence.*;
import robertovisconti.enums.Ruolo;

import java.util.UUID;

@Entity
@Table(name = "utente")
public class Utente {

    // id dell'utente
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    // nome dell'utente
    @Column(name = "nome", nullable = false)
    private String nome;

    // cognome dell'utente
    @Column(name = "cognome", nullable = false)
    private String cognome;

    // email utente
    @Column(name = "e_mail", nullable = false, unique = true)
    private String email;

    // id della tessera associata all'utente.
    @OneToOne(optional = true)
    @JoinColumn(name = "id_tessera", unique = true)
    private Tessera idTessera;

    // ruolo dell'utente (USER o ADMIN).
    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo", nullable = false)
    private Ruolo ruolo;


    protected Utente() {
    }

    // Costruttore che uso io per creare un utente con i suoi dati.
    public Utente(String nome, String cognome, String email, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.ruolo = ruolo;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public Tessera getIdTessera() {
        return idTessera;
    }

    // setter aggiunto per poter collegare la tessera all'utente dopo averlo creato
    public void setIdTessera(Tessera idTessera) {
        this.idTessera = idTessera;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    @Override
    public String toString() {
        String BLUE = "\u001B[34m";
        String RESET = "\u001B[0m";

        return BLUE + "User: " +
                "\n nome= '" + nome + '\'' +
                "\n cognome= '" + cognome + '\'' +
                "\n ruolo= " + ruolo
                + RESET;
    }
}
