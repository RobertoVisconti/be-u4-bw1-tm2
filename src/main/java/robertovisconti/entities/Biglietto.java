package robertovisconti.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
    public Biglietto(UUID codiceUnivoco, PuntoDiEmissione puntoDiEmissione, LocalDate dataEmissione, MezzoDiTrasporto mezzoDiTrasporto, LocalDateTime dataValidazione) {
        super(codiceUnivoco, puntoDiEmissione, dataEmissione);
        this.mezzoDiTrasporto = mezzoDiTrasporto;
        this.dataValidazione = dataValidazione;
    }

    public MezzoDiTrasporto getMezzoDiTrasporto() {
        return mezzoDiTrasporto;
    }


    public LocalDateTime getDataValidazione() {
        return dataValidazione;
    }

    public void setDataValidazione(LocalDateTime dataValidazione) {
        this.dataValidazione = dataValidazione;
    }

    @Override
    public String toString() {
        return "Biglietto{" + super.toString() +
                "mezzoDiTrasporto=" + mezzoDiTrasporto +
                ", dataValidazione=" + dataValidazione +
                '}';
    }


}
