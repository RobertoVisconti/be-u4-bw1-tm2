package robertovisconti.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Rivenditore extends PuntoDiEmissione {
    @Column(nullable = false)
    private boolean aperto;

    protected Rivenditore(){
    }

    public Rivenditore(String nome, boolean aperto) {
        super(nome);
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
