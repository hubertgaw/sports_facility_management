package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.RoleEntity;
import pl.lodz.hubertgaw.repository.entity.RoleName;
import pl.lodz.hubertgaw.repository.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, Integer> {

    public UserEntity findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public List<UserEntity> findByRole(RoleEntity role) {
        RoleName roleName = RoleName.getByName(role.getRoleName().name());
        return list("select u from User u inner join u.roles r where r.roleName in ?1", roleName);
//        return find("roles", role).firstResult();
    }



}
