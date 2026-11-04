package tn.sfm.clientservice.repository;

import tn.sfm.clientservice.entity.Client;
import tn.sfm.clientservice.entity.StatutClient;
import tn.sfm.clientservice.entity.TypeClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByCodeClient(String codeClient);

    Optional<Client> findByEmail(String email);

    // ✅ NOUVEAU
    Optional<Client> findByUserId(Long userId);

    List<Client> findByStatut(StatutClient statut);

    List<Client> findByType(TypeClient type);

    List<Client> findByNomContainingIgnoreCase(String nom);

    List<Client> findByVille(String ville);

    boolean existsByEmail(String email);

    boolean existsByCodeClient(String codeClient);

    // ✅ NOUVEAU
    boolean existsByUserId(Long userId);

    long countByStatut(StatutClient statut);

    long countByType(TypeClient type);
}