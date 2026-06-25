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

    @Column(name = "targa", unique = true, nullable = false)
    private String targa;


    protected MezzoDiTrasporto() {
    }

    public MezzoDiTrasporto(TipoMezzo tipoMezzo, Integer capienza, StatoMezzo statoMezzo, String targa) {
        this.tipoMezzo = tipoMezzo;
        this.capienza = capienza;
        this.statoMezzo = statoMezzo;
        this.targa = targa;
    }

    public UUID getId() {
        return id;
    }

    public String getTarga() {
        return targa;
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

    public void setTipoMezzo(TipoMezzo tipoMezzo) {
        this.tipoMezzo = tipoMezzo;
    }

    public void setCapienza(int capienza) {
        this.capienza = capienza;
    }

    public void setStatoMezzo(StatoMezzo statoMezzo) {
        this.statoMezzo = statoMezzo;
    }

    @Override
    public String toString() {
        return "MezzoDiTrasporto{" +
                "id=" + id +
                ", tipoMezzo=" + tipoMezzo +
                ", capienza=" + capienza +
                ", statoMezzo=" + statoMezzo +
                ", targa='" + targa + '\'' +
                '}';
    }
}
