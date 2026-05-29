package edu.tx.api_zen.service;

import edu.tx.api_zen.dao.LogActiviteDao;
import edu.tx.api_zen.dao.LogConnexionDao;
import edu.tx.api_zen.dao.RoleDao;
import edu.tx.api_zen.dao.UserDao;
import edu.tx.api_zen.dto.UserCreateDto;
import edu.tx.api_zen.dto.UserDto;
import edu.tx.api_zen.dto.UserUpdateDto;
import edu.tx.api_zen.mapper.UserMapper;
import edu.tx.api_zen.model.Role;
import edu.tx.api_zen.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final LogActiviteDao logActiviteDao;
    private final LogConnexionDao logConnexionDao;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> listAll() {
        return userDao.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    public Optional<UserDto> getById(Integer id) {
        return userDao.findById(id).map(UserMapper::toDto);
    }

    public UserDto create(UserCreateDto dto) {
        if (dto.getRoleId() == null) {
            throw new IllegalArgumentException("roleId is required to create a user");
        }
        Role role = roleDao.findById(dto.getRoleId()).orElseThrow(() -> new IllegalArgumentException("Role not found"));
        User u = UserMapper.toEntity(dto, role);
        userDao.save(u);
        return UserMapper.toDto(u);
    }

    public UserDto update(Integer id, UserUpdateDto dto) {
        Optional<User> opt = userDao.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("User not found");
        User user = opt.get();
        if (dto.getNom() != null) user.setNom(dto.getNom());
        if (dto.getPrenom() != null) user.setPrenom(dto.getPrenom());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        if (dto.getRoleId() != null) {
            Role role = roleDao.findById(dto.getRoleId()).orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRole(role);
        }
        userDao.save(user);
        return UserMapper.toDto(user);
    }

    /**
     * Anonymisation RGPD (droit à l'oubli).
     * Remplace toutes les données personnelles par des valeurs neutres et
     * désactive le compte. La ligne user est conservée pour maintenir
     * l'intégrité référentielle (consulter, exercer) — aucune PII ne subsiste.
     */
    @Transactional
    public void anonymize(Integer id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String realEmail = user.getEmail();
        String anonymEmail = "anonyme_" + id + "@supprime.invalid";

        // 1. Anonymiser le compte utilisateur
        user.setNom("Supprimé");
        user.setPrenom("Utilisateur");
        user.setEmail(anonymEmail);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setActive(false);
        userDao.save(user);

        // 2. Détacher les logs d'activité (conserver l'action, supprimer l'identité)
        logActiviteDao.detachUser(id);

        // 3. Anonymiser l'email dans les logs de connexion
        logConnexionDao.anonymizeEmail(realEmail, anonymEmail);
    }
}
