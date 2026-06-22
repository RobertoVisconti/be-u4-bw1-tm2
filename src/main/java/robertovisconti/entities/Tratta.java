package robertovisconti.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name="tratte")
public class Tratta {
    @Id
    @GeneratedValue
    private UUID id;

}
