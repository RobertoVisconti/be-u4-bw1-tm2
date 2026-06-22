package robertovisconti.entities;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "manutenzione")
public class Manutenzione {

    @Id
    @GeneratedValue
    @Column(name = "id_manutenzione")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_mezzo", nullable = false)
    private MezzoDiTrasporto mezzo;

    @Column(name = "data_inizio", nullable = false)
    private LocalDate dataInizio;

    @Column(name = "data_fine", nullable = false)
    private LocalDate dataFine;

    @Column(name = "motivo", nullable = false, length = 200)
    private String motivo;

    protected Manutenzione() {
    }

    public Manutenzione(MezzoDiTrasporto mezzo, String motivo) {
        this.mezzo = mezzo;
        this.dataInizio = LocalDate.now();
        this.dataFine = null;
        this.motivo = motivo;
    }

    public UUID getId() {
        return id;
    }

    public MezzoDiTrasporto getMezzo() {
        return mezzo;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public String getMotivo() {
        return motivo;
    }

    @Override
    public String toString() {
        return "Manutenzione{" +
                "id=" + id +
                ", mezzo=" + mezzo +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                ", motivo='" + motivo + '\'' +
                '}';
    }
}
