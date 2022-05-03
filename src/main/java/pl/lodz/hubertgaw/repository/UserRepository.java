package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.RoleEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepositoryBase<UserEntity, Integer> {

    public UserEntity findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public UserEntity findByRole(RoleEntity role) {
        return find("role", role).firstResult();
    }



}
