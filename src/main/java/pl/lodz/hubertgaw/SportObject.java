package pl.lodz.hubertgaw;

import lombok.Data;

import javax.persistence.*;

//@Table(name = "object")
@Entity
@Data
public class SportObject {

//    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
