package robertovisconti.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "titolo_viaggio")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TitoloViaggio {
    @Id
    @GeneratedValue
    @Column(name = "titolo_viaggio_id")
    private UUID id;

    @Column(name = "data_emissione")
    private LocalDate dataEmissione;

    @ManyToOne
    @JoinColumn(name = "punto_di_emissione_id", nullable = false)
    private PuntoDiEmissione puntoDiEmissione;

    @Column(name = "codice_univoco", unique = true, nullable = false)
    private UUID codiceUnivoco;

    protected TitoloViaggio(){}

    public TitoloViaggio(UUID codiceUnivoco, PuntoDiEmissione puntoDiEmissione, LocalDate dataEmissione) {
        this.codiceUnivoco = codiceUnivoco;
        this.puntoDiEmissione = puntoDiEmissione;
        this.dataEmissione = dataEmissione;
    }

    public UUID getId() {
        return id;
    }

    public LocalDate getDataEmissione() {
        return dataEmissione;
    }

    public PuntoDiEmissione getPuntoDiEmissione() {
        return puntoDiEmissione;
    }

    public UUID getCodiceUnivoco() {
        return codiceUnivoco;
    }

    @Override
    public String toString() {
        return "TitoloViaggio{" +
                "id=" + id +
                ", dataEmissione=" + dataEmissione +
                ", puntoDiEmissione=" + puntoDiEmissione +
                ", codiceUnivoco=" + codiceUnivoco +
                '}';
    }
}
