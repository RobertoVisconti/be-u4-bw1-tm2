package robertovisconti.entities;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="tratte")
public class Tratta {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(name="punto_di_partenza", nullable = false)
    private String puntoDiPartenza;
    @Column(nullable = false)
    private String capolinea;
    @Column(name="tempo_percorrenza_stimato", nullable = false)
    private int tempoPercorrenzaStimato;

    public Tratta() {
    }

    public Tratta(String puntoDiPartenza, String capolinea, int tempoPercorrenzaStimato) {
        this.puntoDiPartenza = puntoDiPartenza;
        this.capolinea = capolinea;
        this.tempoPercorrenzaStimato = tempoPercorrenzaStimato;
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
}
