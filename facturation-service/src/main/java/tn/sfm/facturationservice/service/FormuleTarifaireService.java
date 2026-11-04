package tn.sfm.facturationservice.service;

import tn.sfm.facturationservice.entity.FormuleTarifaire;
import tn.sfm.facturationservice.repository.FormuleTarifaireRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FormuleTarifaireService {

    private final FormuleTarifaireRepository repository;

    public List<FormuleTarifaire> getAll() {
        return repository.findAll();
    }

    public FormuleTarifaire getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formule non trouvée"));
    }

    public FormuleTarifaire save(FormuleTarifaire formule) {
        return repository.save(formule);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public FormuleTarifaire getByServiceAndPays(String service, String pays) {
        return repository.findByServiceAndPaysAndActifTrue(service, pays)
                .orElseThrow(() -> new RuntimeException("Aucune formule active pour ce service et pays"));
    }


    public double calculerMontant(String service, String pays,
                                  double puissance, double frequence, int nbAppareils) {
        log.info("Calcul du montant pour service={}, pays={}, puissance={}, fréquence={}, nbAppareils={}",
                service, pays, puissance, frequence, nbAppareils);

        FormuleTarifaire formule = getByServiceAndPays(service, pays);

        double base = formule.getMontantBase() != null ? formule.getMontantBase() : 0.0;
        double coeffP = formule.getCoeffPuissance() != null ? formule.getCoeffPuissance() : 0.0;
        double coeffF = formule.getCoeffFrequence() != null ? formule.getCoeffFrequence() : 0.0;
        double coeffA = formule.getCoeffAppareils() != null ? formule.getCoeffAppareils() : 0.0;
        double tva = formule.getTva() != null ? formule.getTva() : 0.0;

        double ht = base + (coeffP * puissance) + (coeffF * frequence) + (coeffA * nbAppareils);
        double ttc = ht * (1 + tva / 100.0);

        log.info("Montant calculé : HT={}, TVA={}%, TTC={}", ht, tva, ttc);
        return Math.round(ttc * 100.0) / 100.0; // Arrondi à 2 décimales
    }
}