package robertovisconti.entities;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name="percorrenze")
public class Percorrenza {
    @Id
    @GeneratedValue
    private UUID id;
    @ManyToOne
    @JoinColumn(name="id_tratta", nullable = false)
    private Tratta tratta;
    @ManyToOne
    @JoinColumn(name="id_mezzo", nullable = false)
    private MezzoDiTrasporto mezzo;
    @Column(name="tempo_effettivo")
    private double tempoEffettivo;
    @Column(name="data_percorrenza", nullable = false)
    private LocalDate dataPercorrenza;
    @Column(name="data_inizio", nullable = false)
    private LocalDate dataInizio;
    @Column(name="data_fine", nullable = false)
    private LocalDate dataFine;

    protected Percorrenza(){}

    public Percorrenza(Tratta tratta, MezzoDiTrasporto mezzo, LocalDate dataPercorrenza, LocalDate dataInizio) {
        this.tratta = tratta;
        this.mezzo = mezzo;
        this.dataPercorrenza = dataPercorrenza;
        this.dataInizio = dataInizio;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
        this.tempoEffettivo = Duration.between(dataInizio, dataFine).toMinutes();
    }
    

    @Override
    public String toString() {
        return "Percorrenza{" +
                "id=" + id +
                ", tratta=" + tratta +
                ", mezzo=" + mezzo +
                ", tempoEffettivo=" + tempoEffettivo +
                ", dataPercorrenza=" + dataPercorrenza +
                ", dataInizio=" + dataInizio +
                ", dataFine=" + dataFine +
                '}';
    }
}
