package pl.lodz.hubertgaw.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "User")
@Table(name = "user")
@Getter
@Setter
public class UserEntity {

    @Column(name = "user_id")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "email")
    @NotEmpty
    private String email;

    @Column(name = "password")
    @NotEmpty
    private String password;

    @Column(name = "first_name")
    @NotEmpty
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "role_id")})
    private Set<RoleEntity> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<BookingEntity> bookings = new HashSet<>();
}
