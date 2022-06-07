package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.repository.RoleRepository;
import pl.lodz.hubertgaw.repository.entity.RoleEntity;
import pl.lodz.hubertgaw.repository.entity.RoleName;
import pl.lodz.hubertgaw.repository.entity.UserEntity;

@Mapper(componentModel = "cdi")
public interface UserMapper {

    UserEntity toEntity(User domain);

    User toDomain(UserEntity entity);

    default RoleEntity map(RoleName roleName) {
        RoleRepository roleRepository = new RoleRepository();

        return roleRepository.findByName(roleName);
    }

    default RoleName map (RoleEntity roleEntity) {
        return roleEntity.getRoleName();
    }

}
