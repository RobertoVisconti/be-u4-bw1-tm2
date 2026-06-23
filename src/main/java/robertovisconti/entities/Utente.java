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

    // id della tessera associata all'utente.
    @OneToOne
    @JoinColumn(name = "id_tessera", nullable = false, unique = true)
    private Tessera idTessera;

    // ruolo dell'utente (USER o ADMIN).
    @Enumerated(EnumType.STRING)
    @Column(name = "ruolo", nullable = false)
    private Ruolo ruolo;


    protected Utente() {
    }

    // Costruttore che uso io per creare un utente con i suoi dati.
    public Utente(String nome, String cognome, Ruolo ruolo) {
        this.nome = nome;
        this.cognome = cognome;
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
