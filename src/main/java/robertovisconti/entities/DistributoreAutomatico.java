package robertovisconti.entities;

import jakarta.persistence.*;
import robertovisconti.enums.StatoDistributoreAutomatico;

@Entity
@Table(name="distributori_automatici")
public class DistributoreAutomatico extends PuntoDiEmissione{
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatoDistributoreAutomatico stato;

    protected DistributoreAutomatico() {}

    public DistributoreAutomatico(String nome, StatoDistributoreAutomatico stato) {
        super(nome);
        this.stato = stato;
    }

    public void setStato(StatoDistributoreAutomatico stato) {
        this.stato = stato;
    }

    public StatoDistributoreAutomatico getStato() {
        return stato;
    }

    @Override
    public String toString() {
        return "DistributoreAutomatico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", stato=" + stato +
                '}';
    }


}
