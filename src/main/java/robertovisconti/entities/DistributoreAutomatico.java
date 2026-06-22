package robertovisconti.entities;

import robertovisconti.enums.StatoDistributoreAutomatico;

public class DistributoreAutomatico extends PuntoDiEmissione{
    private StatoDistributoreAutomatico stato;

    public DistributoreAutomatico() {}

    public DistributoreAutomatico(String nome, StatoDistributoreAutomatico stato) {
        super(nome);
        this.stato = stato;
    }
}
