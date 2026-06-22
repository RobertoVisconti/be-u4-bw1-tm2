package robertovisconti.entities;


import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tessera")
public class Tessera {

    @Id
    @GeneratedValue
    @Column
    private UUID id;


}
