package robertovisconti.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "percorrenze")
public class Percorrenza {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_tratta", nullable = false)
    private Tratta tratta;

    @ManyToOne
    @JoinColumn(name = "id_mezzo", nullable = false)
    private MezzoDiTrasporto mezzo;

    @Column(name = "data_orario_inizio", nullable = false)
    private LocalDateTime dataInizio;

    @Column(name = "data_orario_fine", nullable = false)
    private LocalDateTime dataFine;

    protected Percorrenza() {
    }

    public Percorrenza(Tratta tratta, MezzoDiTrasporto mezzo, LocalDateTime dataInizio, LocalDateTime dataFine) {
        this.tratta = tratta;
        this.mezzo = mezzo;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }


    public LocalDateTime getDataInizio() {
        return dataInizio;
    }

    public LocalDateTime getDataFine() {
        return dataFine;
    }


    @Override
    public String toString() {
        return "Percorrenza{" +
                "id=" + id +
                ", tratta=" + tratta +
                ", mezzo=" + mezzo +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                '}';
    }
}
