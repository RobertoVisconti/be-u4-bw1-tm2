package robertovisconti.entities;

import robertovisconti.enums.StatoDistributoreAutomatico;

public class DistributoreAutomatico extends PuntoDiEmissione{
    private StatoDistributoreAutomatico stato;

    public DistributoreAutomatico() {}

    public DistributoreAutomatico(String nome, StatoDistributoreAutomatico stato) {
        super(nome);
        this.stato = stato;
    }

    public void setStato(StatoDistributoreAutomatico stato) {
        this.stato = stato;
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
