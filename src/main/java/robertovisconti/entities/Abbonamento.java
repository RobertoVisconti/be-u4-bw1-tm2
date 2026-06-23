package robertovisconti.entities;

import jakarta.persistence.*;
import robertovisconti.enums.TipoAbbonamento;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "abbonamento")
public class Abbonamento extends TitoloViaggio {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_abbonamento")
    private TipoAbbonamento tipoAbbonamento;
    @Column(name = "data_emissione")
    private LocalDateTime dataEmissione;
    @Column(name = "data_scadenza")
    private LocalDateTime dataScadenza;
    @ManyToOne
    @JoinColumn(name = "id_tessera", nullable = false)
    private Tessera tessera;

    protected Abbonamento() {
    }

    public Abbonamento(UUID codiceUnivoco, PuntoDiEmissione puntoDiEmissione, TipoAbbonamento tipoAbbonamento, LocalDateTime dataEmissione, LocalDateTime dataScadenza, Tessera tessera) {
        super(dataEmissione, puntoDiEmissione, codiceUnivoco);
        switch (tipoAbbonamento) {
            case SETTIMANALE -> this.dataScadenza = LocalDateTime.now().plusDays(7);

            case MENSILE -> this.dataScadenza = LocalDateTime.now().plusMonths(1);

            case ANNUALE -> this.dataScadenza = LocalDateTime.now().plusYears(1);
        }
        this.dataEmissione = dataEmissione;
        this.dataScadenza = dataScadenza;
        this.tessera = tessera;
    }


    public TipoAbbonamento getTipoAbbonamento() {
        return tipoAbbonamento;
    }

    @Override
    public LocalDateTime getDataEmissione() {
        return dataEmissione;
    }

    public LocalDateTime getDataScadenza() {
        return dataScadenza;
    }

    public Tessera getTessera() {
        return tessera;
    }

    public void setTipoAbbonamento(TipoAbbonamento tipoAbbonamento) {
        this.tipoAbbonamento = tipoAbbonamento;
    }

    public void setDataEmissione(LocalDateTime dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public void setDataScadenza(LocalDateTime dataScadenza) {
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
    