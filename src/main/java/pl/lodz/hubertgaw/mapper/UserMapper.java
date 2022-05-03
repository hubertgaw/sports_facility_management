package pl.lodz.hubertgaw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.repository.RoleRepository;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RoleEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;

@Mapper(componentModel = "cdi")
public interface UserMapper {

    UserEntity toEntity(User domain);

    User toDomain(UserEntity entity);

    default RoleEntity map(String roleName) {
        RoleRepository roleRepository = new RoleRepository();

        return roleRepository.findByName(roleName);
    }

    default String map (RoleEntity roleEntity) {
        return roleEntity.getRoleName().toString();
    }

}
