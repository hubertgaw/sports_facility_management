package pl.lodz.hubertgaw.repository.entity;

import pl.lodz.hubertgaw.service.exception.RoleException;

public enum RoleName {
    USER,
    ADMIN,
    GUEST;

    public static RoleName getByName(String name) {
        for (RoleName roleName : values()) {
            if (roleName.name().equals(name)) {
                return roleName;
            }
        }
        throw RoleException.roleNotFoundException();
    }
}
