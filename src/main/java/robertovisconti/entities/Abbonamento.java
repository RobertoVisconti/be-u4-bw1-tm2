package robertovisconti.entities;

import jakarta.persistence.*;
import robertovisconti.enums.TipoAbbonamento;

import java.time.LocalDate;

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

    public Abbonamento(TipoAbbonamento tipoAbbonamento, LocalDate dataEmissione, LocalDate dataScadenza) {
        this.tipoAbbonamento = tipoAbbonamento;
        this.dataEmissione = LocalDate.now();
        switch (tipoAbbonamento) {
            case SETTIMANALE -> this.dataScadenza = LocalDate.now().plusDays(7);

            case MENSILE -> this.dataScadenza = LocalDate.now().plusMonths(1);

            case ANNUALE -> this.dataScadenza = LocalDate.now().plusYears(1);
        }
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
    