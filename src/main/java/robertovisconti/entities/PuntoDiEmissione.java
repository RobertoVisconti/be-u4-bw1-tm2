package robertovisconti.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name="punti_di_emissione")
public class PuntoDiEmissione {
    @Id
    @GeneratedValue
    private UUID id;
    private String nome;

    public PuntoDiEmissione() {}

    public PuntoDiEmissione(String nome){
        this.nome = nome;
    }
}
