package org.sid.sec.repo;

import org.sid.sec.entities.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    @Query("SELECT p FROM Etudiant p WHERE p.nom = :nom")
    List<Etudiant> getEtudiantsByNom(@Param("nom") String nom);

}
