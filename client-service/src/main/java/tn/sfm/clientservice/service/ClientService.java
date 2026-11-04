package tn.sfm.clientservice.service;

import tn.sfm.clientservice.entity.Client;
import tn.sfm.clientservice.entity.StatutClient;
import tn.sfm.clientservice.entity.TypeClient;
import tn.sfm.clientservice.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClientService {

    private final ClientRepository clientRepository;

    public Client createClient(Client client) {
        log.info("Création d'un nouveau client: {}", client.getNom());

        if (clientRepository.existsByEmail(client.getEmail())) {
            throw new RuntimeException("Email déjà utilisé: " + client.getEmail());
        }

        if (client.getCodeClient() == null) {
            client.setCodeClient("CLT-" + System.currentTimeMillis());
        }
        if (client.getStatut() == null) {
            client.setStatut(StatutClient.ACTIF);
        }
        if (client.getType() == null) {
            client.setType(TypeClient.ENTREPRISE);
        }
        if (client.getPays() == null) {
            client.setPays("Tunisie");
        }

        return clientRepository.save(client);
    }

    public List<Client> getAllClients() {
        log.info("Récupération de tous les clients");
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + id));
    }

    // ✅ NOUVEAU : Récupérer un client par userId
    public Client getClientByUserId(Long userId) {
        return clientRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Aucun client associé à cet utilisateur: " + userId));
    }

    public Client getClientByCode(String codeClient) {
        return clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec le code: " + codeClient));
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'email: " + email));
    }

    public List<Client> getClientsByStatut(StatutClient statut) {
        return clientRepository.findByStatut(statut);
    }

    public List<Client> getClientsByType(TypeClient type) {
        return clientRepository.findByType(type);
    }

    public List<Client> searchClientsByName(String nom) {
        return clientRepository.findByNomContainingIgnoreCase(nom);
    }

    public Client updateClient(Long id, Client clientDetails) {
        log.info("Mise à jour du client ID: {}", id);
        Client client = getClientById(id);

        if (clientDetails.getUserId() != null) {
            client.setUserId(clientDetails.getUserId());
        }
        if (clientDetails.getNom() != null) {
            client.setNom(clientDetails.getNom());
        }
        if (clientDetails.getPrenom() != null) {
            client.setPrenom(clientDetails.getPrenom());
        }
        if (clientDetails.getEmail() != null && !clientDetails.getEmail().equals(client.getEmail())) {
            if (clientRepository.existsByEmail(clientDetails.getEmail())) {
                throw new RuntimeException("Email déjà utilisé: " + clientDetails.getEmail());
            }
            client.setEmail(clientDetails.getEmail());
        }
        if (clientDetails.getTelephone() != null) {
            client.setTelephone(clientDetails.getTelephone());
        }
        if (clientDetails.getAdresse() != null) {
            client.setAdresse(clientDetails.getAdresse());
        }
        if (clientDetails.getVille() != null) {
            client.setVille(clientDetails.getVille());
        }
        if (clientDetails.getCodePostal() != null) {
            client.setCodePostal(clientDetails.getCodePostal());
        }
        if (clientDetails.getPays() != null) {
            client.setPays(clientDetails.getPays());
        }
        if (clientDetails.getType() != null) {
            client.setType(clientDetails.getType());
        }
        if (clientDetails.getStatut() != null) {
            client.setStatut(clientDetails.getStatut());
        }
        if (clientDetails.getSecteurActivite() != null) {
            client.setSecteurActivite(clientDetails.getSecteurActivite());
        }
        if (clientDetails.getNumeroFiscal() != null) {
            client.setNumeroFiscal(clientDetails.getNumeroFiscal());
        }
        if (clientDetails.getMatricule() != null) {
            client.setMatricule(clientDetails.getMatricule());
        }
        if (clientDetails.getNotes() != null) {
            client.setNotes(clientDetails.getNotes());
        }

        return clientRepository.save(client);
    }

    public Client updateClientStatus(Long id, StatutClient statut) {
        Client client = getClientById(id);
        client.setStatut(statut);
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        log.info("Suppression du client ID: {}", id);
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé avec l'ID: " + id);
        }
        clientRepository.deleteById(id);
    }

    // Statistiques
    public long countClients() {
        return clientRepository.count();
    }

    public long countActiveClients() {
        return clientRepository.countByStatut(StatutClient.ACTIF);
    }

    public long countByType(TypeClient type) {
        return clientRepository.countByType(type);
    }
}