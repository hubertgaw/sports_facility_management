package pl.lodz.hubertgaw.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import pl.lodz.hubertgaw.repository.entity.RoleEntity;
import pl.lodz.hubertgaw.repository.entity.RoleName;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<RoleEntity, Integer> {

    public RoleEntity findByName(RoleName name) {
//        RoleName name = RoleName.valueOf(nameString);
        String roleNameString = name.toString();
        return find("role_name", roleNameString).firstResult();
    }
}
