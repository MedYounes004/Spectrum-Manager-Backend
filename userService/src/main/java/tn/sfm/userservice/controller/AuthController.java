package tn.sfm.userservice.controller;

import tn.sfm.userservice.entity.User;
import tn.sfm.userservice.entity.UserStatus;
import tn.sfm.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        try {
            User user = userService.getUserByUsername(username);

            if (!user.getPassword().equals(password)) {
                return ResponseEntity.status(401).body(Map.of(
                        "success", false,
                        "message", "Mot de passe incorrect"
                ));
            }

            if (user.getStatus() != UserStatus.ACTIVE) {
                return ResponseEntity.status(403).body(Map.of(
                        "success", false,
                        "message", "Compte désactivé. Contactez l'administrateur."
                ));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Utilisateur non trouvé"
            ));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}