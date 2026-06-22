package robertovisconti.entities;

import jakarta.persistence.*;
import robertovisconti.enums.Ruolo;

import java.util.UUID;

@Entity
@Table(name = "user")
public class User {

    // id dell'utente
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    // nome dell'utente
    @Column(name = "nome")
    private String nome;

    // cognome dell'utente
    @Column(name = "cognome")
    private String cognome;

    // id della tessera associata all'utente.
    @Column(name = "id_tessera")
    private UUID idTessera;

    // ruolo dell'utente (USER o ADMIN).
    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo")
    private Ruolo ruolo;


    protected User() {
    }

    // Costruttore che uso io per creare un utente con i suoi dati.
    public User(String nome, String cognome, UUID idTessera, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.idTessera = idTessera;
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

    public UUID getIdTessera() {
        return idTessera;
    }

    public Ruolo getRuolo() {
        return ruolo;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cognome='" + cognome + '\'' +
                ", idTessera=" + idTessera +
                ", ruolo=" + ruolo +
                '}';
    }
}
