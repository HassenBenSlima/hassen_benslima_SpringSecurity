package org.sid.sec.entities;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Data
public class Etudiant extends Personne {


    @ManyToOne
    @JoinColumn(name = "classe_id")
    private Classe classe;
}
