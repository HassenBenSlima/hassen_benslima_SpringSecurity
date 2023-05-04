package org.sid.sec.entities;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
public class Enseignant extends Personne {

    @OneToMany(mappedBy = "enseignant")
    List<Classe> classeList;
}
