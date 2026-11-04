package tn.sfm.facturationservice.repository;

import tn.sfm.facturationservice.entity.FormuleTarifaire;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FormuleTarifaireRepository extends JpaRepository<FormuleTarifaire, Long> {
    Optional<FormuleTarifaire> findByServiceAndPaysAndActifTrue(String service, String pays);
    List<FormuleTarifaire> findByActifTrue();
}