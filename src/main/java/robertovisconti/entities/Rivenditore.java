package robertovisconti.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Rivenditore extends PuntoDiEmissione {
    @Column(nullable = false)
    private boolean aperto;

    protected Rivenditore() {
    }

    public Rivenditore(String nome, String indirizzo, String citta, String cap, String piva, boolean aperto) {
        super(nome, indirizzo, citta, cap, piva);
        this.aperto = aperto;
    }

    public boolean isAperto() {
        return aperto;
    }

    public void setAperto(boolean aperto) {
        this.aperto = aperto;
    }

    @Override
    public String toString() {
        return "Rivenditore{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", aperto=" + aperto +
                '}';
    }
}
