package org.sid.sec.web;


import org.sid.sec.entities.Etudiant;
import org.sid.sec.repo.EtudiantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EtudiantController {


    private EtudiantRepository etudiantRepository;

    public EtudiantController(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    @GetMapping(path = "/students")
    @PostAuthorize("hasAuthority('ADMIN')")
    public List<Etudiant> getAllStudents() {
        return etudiantRepository.findAll();
    }

    @GetMapping(path = "/students-page")
    @PostAuthorize("hasAuthority('ADMIN')")
    public Page<Etudiant> getAllStudentsWithPage(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "3") int size) {
        Pageable paging = PageRequest.of(page, size);
        return etudiantRepository.findAll(paging);
    }

    @GetMapping(path = "/studentsByName")
    @PostAuthorize("hasAuthority('ADMIN')")
    public List<Etudiant> getAllStudentsByName( @RequestParam(name = "name", defaultValue = "") String name) {
        return etudiantRepository.getEtudiantsByNom(name);
    }



}
