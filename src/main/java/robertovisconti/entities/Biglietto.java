package robertovisconti.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "biglietto")
public class Biglietto extends TitoloViaggio{
    @ManyToOne
    @JoinColumn(name = "id_mezzo")
    private MezzoDiTrasporto mezzoDiTrasporto;

    @Column(name = "vidimato")
    private boolean vidimato;

    @Column(name = "data_validazione")
    private LocalDate dataValidazione;

    protected Biglietto(){}

    public Biglietto(LocalDate dataEmissione, PuntoDiEmissione puntoDiEmissione, MezzoDiTrasporto mezzoDiTrasporto, boolean vidimato, LocalDate dataValidazione) {
        super(dataEmissione, puntoDiEmissione);
        this.mezzoDiTrasporto = mezzoDiTrasporto;
        this.vidimato = vidimato;
        this.dataValidazione = dataValidazione;
    }

    public MezzoDiTrasporto getMezzoDiTrasporto() {
        return mezzoDiTrasporto;
    }

    public boolean isVidimato() {
        return vidimato;
    }

    public LocalDate getDataValidazione() {
        return dataValidazione;
    }

    @Override
    public String toString() {
        return "Biglietto{" + super.toString() + 
                "mezzoDiTrasporto=" + mezzoDiTrasporto +
                ", vidimato=" + vidimato +
                ", dataValidazione=" + dataValidazione +
                '}';
    }
}
