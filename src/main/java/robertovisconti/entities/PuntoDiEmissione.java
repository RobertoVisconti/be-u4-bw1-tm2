package robertovisconti.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="punti_di_emissione")
public class PuntoDiEmissione {
    @Id
    @GeneratedValue
    protected UUID id;
    protected String nome;

    protected PuntoDiEmissione() {}

    public PuntoDiEmissione(String nome){
        this.nome = nome;
    }


}
