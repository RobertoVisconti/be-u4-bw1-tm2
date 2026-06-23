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
    @Column(name = "data_scadenza")
    private LocalDateTime dataScadenza;
    @ManyToOne
    @JoinColumn(name = "id_tessera", nullable = false)
    private Tessera tessera;

    protected Abbonamento() {
    }

    public Abbonamento(LocalDateTime dataEmissione, PuntoDiEmissione puntoDiEmissione, UUID codiceUnivoco, LocalDateTime dataScadenza, TipoAbbonamento tipoAbbonamento) {
        super(dataEmissione, puntoDiEmissione, codiceUnivoco);
        this.dataScadenza = dataScadenza;
        switch (tipoAbbonamento) {
            case SETTIMANALE -> this.dataScadenza = LocalDateTime.now().plusDays(7);

            case MENSILE -> this.dataScadenza = LocalDateTime.now().plusMonths(1);

            case ANNUALE -> this.dataScadenza = LocalDateTime.now().plusYears(1);
        }
    }

    public TipoAbbonamento getTipoAbbonamento() {
        return tipoAbbonamento;
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

   public void setDataScadenza(LocalDateTime dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    @Override
    public String toString() {
        return "Abbonamento{" +
                super.toString() + 
                "tipoAbbonamento=" + tipoAbbonamento  +
                ", dataScadenza=" + dataScadenza +
                ", tessera=" + tessera +
                '}';
    }
}
    