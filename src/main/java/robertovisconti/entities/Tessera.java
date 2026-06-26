package robertovisconti.entities;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tessera")
public class Tessera {

    @Id
    @GeneratedValue
    @Column(name = "id_tessera")
    private UUID id;

    @Column(name = "data_emissione", nullable = false)
    private LocalDate dataEmissione;

    @Column(name = "data_scadenza", nullable = false)
    private LocalDate dataScadenza;


    @Column(name = "codice_univoco", nullable = false, unique = true)
    private UUID codiceUnivoco;

    protected Tessera() {
    }

    public Tessera(UUID codiceUnivoco) {
        this.dataEmissione = LocalDate.now();
        this.dataScadenza = this.dataEmissione.plusYears(1);
        this.codiceUnivoco = codiceUnivoco;  // UUID.randomUUID(); alla creazione tessera
    }

    public void setCodiceUnivoco(UUID codiceUnivoco) {
        this.codiceUnivoco = codiceUnivoco;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(LocalDate dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public LocalDate getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(LocalDate dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public UUID getCodiceUnivoco() {
        return codiceUnivoco;
    }

    @Override
    public String toString() {
        return "Tessera{" +
                "id=" + id +
                ", dataEmissione=" + dataEmissione +
                ", dataScadenza=" + dataScadenza +
                ", codiceUnivoco=" + codiceUnivoco +
                '}';
    }
}
