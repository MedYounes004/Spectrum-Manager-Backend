package tn.sfm.facturationservice.controller;

import tn.sfm.facturationservice.entity.FormuleTarifaire;
import tn.sfm.facturationservice.service.FormuleTarifaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formules")
@RequiredArgsConstructor
public class FormuleTarifaireController {

    // ✅ Renommage de l'instance pour éviter le conflit
    private final FormuleTarifaireService formuleService;

    @GetMapping
    public ResponseEntity<List<FormuleTarifaire>> getAll() {
        return ResponseEntity.ok(formuleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormuleTarifaire> getById(@PathVariable Long id) {
        return ResponseEntity.ok(formuleService.getById(id));
    }

    @GetMapping("/{service}/{pays}")
    public ResponseEntity<FormuleTarifaire> getByServiceAndPays(
            @PathVariable String service,
            @PathVariable String pays) {
        // ✅ Utilisation de l'instance 'formuleService'
        return ResponseEntity.ok(formuleService.getByServiceAndPays(service, pays));
    }

    @PostMapping
    public ResponseEntity<FormuleTarifaire> create(@RequestBody FormuleTarifaire formule) {
        return ResponseEntity.ok(formuleService.save(formule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormuleTarifaire> update(
            @PathVariable Long id,
            @RequestBody FormuleTarifaire formule) {
        formule.setId(id);
        return ResponseEntity.ok(formuleService.save(formule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        formuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calcul")
    public ResponseEntity<Double> calculerMontant(
            @RequestParam String service,
            @RequestParam String pays,
            @RequestParam double puissance,
            @RequestParam double frequence,
            @RequestParam int nbAppareils) {

        // ✅ Utilisation de l'instance 'formuleService'
        double montant = formuleService.calculerMontant(service, pays, puissance, frequence, nbAppareils);
        return ResponseEntity.ok(montant);
    }
}