package robertovisconti.entities;


import jakarta.persistence.*;
import robertovisconti.enums.StatoMezzo;
import robertovisconti.enums.TipoMezzo;

import java.util.UUID;

@Entity
@Table(name = "mezzo_di_trasporto")
public class MezzoDiTrasporto {

    @Id
    @GeneratedValue
    @Column(name = "id_mezzo")
    private UUID id;

    @Column(name = "tipo_mezzo", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoMezzo tipoMezzo;

    @Column(name = "capienza_massima", nullable = false)
    private int capienza;

    @Column(name = "stato_mezzo", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatoMezzo statoMezzo;


    protected MezzoDiTrasporto() {
    }

    public MezzoDiTrasporto(TipoMezzo tipoMezzo, Integer capienza, StatoMezzo statoMezzo) {
        this.tipoMezzo = tipoMezzo;
        this.capienza = capienza;
        this.statoMezzo = statoMezzo;
    }

    public UUID getId() {
        return id;
    }

    public TipoMezzo getTipoMezzo() {
        return tipoMezzo;
    }

    public int getCapienza() {
        return capienza;
    }

    public StatoMezzo getStatoMezzo() {
        return statoMezzo;
    }

    @Override
    public String toString() {
        return "MezzoDiTrasporto{" +
                "id=" + id +
                ", tipoMezzo=" + tipoMezzo +
                ", capienza=" + capienza +
                ", statoMezzo=" + statoMezzo +
                '}';
    }
}
