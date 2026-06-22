package robertovisconti.entities;

public class Rivenditore extends PuntoDiEmissione {
    private boolean aperto;

    public Rivenditore(){
    }

    public Rivenditore(String nome, boolean aperto) {
        super(nome);
        this.aperto = aperto;
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
