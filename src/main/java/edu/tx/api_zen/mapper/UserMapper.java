package edu.tx.api_zen.mapper;

import edu.tx.api_zen.dto.UserCreateDto;
import edu.tx.api_zen.dto.UserDto;
import edu.tx.api_zen.model.Role;
import edu.tx.api_zen.model.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setNom(user.getNom());
        dto.setPrenom(user.getPrenom());
        dto.setEmail(user.getEmail());
        dto.setActive(user.getActive());
        dto.setCreationDate(user.getCreationDate());
        if (user.getRole() != null) {
            dto.setRoleId(user.getRole().getId());
            dto.setRoleLibelle(user.getRole().getLibelle());
        }
        return dto;
    }

    public static User toEntity(UserCreateDto dto, Role role) {
        if (dto == null) return null;
        User u = new User();
        u.setNom(dto.getNom());
        u.setPrenom(dto.getPrenom());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword());
        u.setRole(role);
        return u;
    }
}

