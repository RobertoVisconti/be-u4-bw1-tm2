package robertovisconti.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="punti_di_emissione")
public class PuntoDiEmissione {
    @Id
    @GeneratedValue
    @Column(name="punto_di_emissione_id")
    protected UUID id;
    @Column(nullable = false)
    protected String nome;

    protected PuntoDiEmissione() {}

    public PuntoDiEmissione(String nome){
        this.nome = nome;
    }

    public Biglietto vendiBiglietto(MezzoDiTrasporto mezzoDiTrasporto){
        Biglietto biglietto = new Biglietto(LocalDateTime.now(), this, mezzoDiTrasporto, null);
        System.out.println("Il biglietto " + biglietto + " è stato creato e venduto!");
        return biglietto;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
