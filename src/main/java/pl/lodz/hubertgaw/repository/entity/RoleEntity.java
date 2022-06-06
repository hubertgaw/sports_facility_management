package pl.lodz.hubertgaw.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "Role")
@Table(name = "role")
@Getter
@Setter
public class RoleEntity {

    @Column(name = "role_id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "role_name")
    @Enumerated(EnumType.STRING)
    private RoleName roleName;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();
}
