package robertovisconti.entities;

import jakarta.persistence.*;
import robertovisconti.enums.StatoDistributoreAutomatico;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "punti_di_emissione")
public class PuntoDiEmissione {
    @Id
    @GeneratedValue
    @Column(name = "punto_di_emissione_id")
    protected UUID id;

    @Column(nullable = false)
    protected String nome;

    @Column(nullable = false)
    private String indirizzo;

    @Column(nullable = false)
    private String citta;

    @Column(nullable = false)
    private String cap;

    @Column(nullable = false, unique = true)
    private String piva;

    protected PuntoDiEmissione() {
    }

    public PuntoDiEmissione(String nome, String indirizzo, String citta, String cap, String piva) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.citta = citta;
        this.cap = cap;
        this.piva = piva;
    }



    public UUID getId() {
        return id;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public String getCitta() {
        return citta;
    }

    public String getCap() {
        return cap;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return "PuntoDiEmissione{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", indirizzo='" + indirizzo + '\'' +
                ", citta='" + citta + '\'' +
                ", cap='" + cap + '\'' +
                '}';
    }
}
