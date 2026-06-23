package robertovisconti.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "biglietto")
public class Biglietto extends TitoloViaggio {
    @ManyToOne
    @JoinColumn(name = "id_mezzo")
    private MezzoDiTrasporto mezzoDiTrasporto;

    @Column(name = "data_validazione")
    private LocalDateTime dataValidazione;

    protected Biglietto() {
    }

    public Biglietto(LocalDateTime dataEmissione, PuntoDiEmissione puntoDiEmissione, MezzoDiTrasporto mezzoDiTrasporto, LocalDateTime dataValidazione) {
        super(dataEmissione, puntoDiEmissione);
        this.mezzoDiTrasporto = mezzoDiTrasporto;
        this.dataValidazione = dataValidazione;
    }

    public MezzoDiTrasporto getMezzoDiTrasporto() {
        return mezzoDiTrasporto;
    }


    public LocalDateTime getDataValidazione() {
        return dataValidazione;
    }

    @Override
    public String toString() {
        return "Biglietto{" + super.toString() +
                "mezzoDiTrasporto=" + mezzoDiTrasporto +
                ", dataValidazione=" + dataValidazione +
                '}';
    }
}
