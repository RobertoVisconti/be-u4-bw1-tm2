package robertovisconti.entities;

import jakarta.persistence.*;
import robertovisconti.enums.TipoAbbonamento;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "abbonamento")
public class Abbonamento extends TitoloViaggio {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_abbonamento")
    private TipoAbbonamento tipoAbbonamento;
    @Column(name = "data_emissione")
    private LocalDate dataEmissione;
    @Column(name = "data_scadenza")
    private LocalDate dataScadenza;
    @ManyToOne
    @JoinColumn(name = "id_tessera", nullable = false)
    private Tessera tessera;

    protected Abbonamento() {
    }

    public Abbonamento(UUID codiceUnivoco, PuntoDiEmissione puntoDiEmissione, TipoAbbonamento tipoAbbonamento, LocalDate dataEmissione, LocalDate dataScadenza, Tessera tessera) {
        super(codiceUnivoco, puntoDiEmissione);
        switch (tipoAbbonamento) {
            case SETTIMANALE -> this.dataScadenza = LocalDate.now().plusDays(7);

            case MENSILE -> this.dataScadenza = LocalDate.now().plusMonths(1);

            case ANNUALE -> this.dataScadenza = LocalDate.now().plusYears(1);
        }
        this.dataEmissione = dataEmissione;
        this.dataScadenza = dataScadenza;
        this.tessera = tessera;
    }


    public TipoAbbonamento getTipoAbbonamento() {
        return tipoAbbonamento;
    }

    @Override
    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public Tessera getTessera() {
        return tessera;
    }

    public void setTipoAbbonamento(TipoAbbonamento tipoAbbonamento) {
        this.tipoAbbonamento = tipoAbbonamento;
    }

    public void setDataEmissione(LocalDate dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    @Override
    public String toString() {
        return "Abbonamento{" +
                "tipoAbbonamento=" + tipoAbbonamento +
                ", dataEmissione=" + dataEmissione +
                ", dataScadenza=" + dataScadenza +
                ", tessera=" + tessera +
                '}';
    }
}
    