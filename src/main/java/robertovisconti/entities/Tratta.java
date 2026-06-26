package robertovisconti.entities;


import jakarta.persistence.*;
import robertovisconti.enums.StatoTratta;

import java.util.UUID;

@Entity
@Table(name = "tratte")
public class Tratta {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name = "punto_di_partenza", nullable = false)
    private String puntoDiPartenza;
    @Column(nullable = false)
    private String capolinea;
    @Column(name = "tempo_percorrenza_stimato", nullable = false)
    private int tempoPercorrenzaStimato;
    @EnumeratedValue
    private StatoTratta statoTratta;

    public Tratta() {
    }

    public Tratta(String puntoDiPartenza, String capolinea, int tempoPercorrenzaStimato, StatoTratta statoTratta) {
        this.puntoDiPartenza = puntoDiPartenza;
        this.capolinea = capolinea;
        this.tempoPercorrenzaStimato = tempoPercorrenzaStimato;
        this.statoTratta = statoTratta;
    }

    public String getPuntoDiPartenza() {
        return puntoDiPartenza;
    }

    public void setPuntoDiPartenza(String puntoDiPartenza) {
        this.puntoDiPartenza = puntoDiPartenza;
    }

    public String getCapolinea() {
        return capolinea;
    }

    public void setCapolinea(String capolinea) {
        this.capolinea = capolinea;
    }


    public int getTempoPercorrenzaStimato() {
        return tempoPercorrenzaStimato;
    }


    public void setTempoPercorrenzaStimato(int tempoPercorrenzaStimato) {
        this.tempoPercorrenzaStimato = tempoPercorrenzaStimato;
    }

    public StatoTratta getStatoTratta() {
        return statoTratta;
    }

    public void setStatoTratta(StatoTratta statoTratta) {
        this.statoTratta = statoTratta;
    }

    @Override
    public String toString() {
        return "Tratta{" +
                "id=" + id +
                ", puntoDiPartenza='" + puntoDiPartenza + '\'' +
                ", capolinea='" + capolinea + '\'' +
                ", tempoPercorrenzaStimato=" + tempoPercorrenzaStimato +
                '}';
    }

    public MezzoDiTrasporto getMezzoTrasporto() {
        return null;
    }
}
