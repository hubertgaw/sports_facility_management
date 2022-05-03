package pl.lodz.hubertgaw.service.utils;

import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.UserRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceUtils {

    private final SportObjectRepository sportObjectRepository;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final UserRepository userRepository;

    public ServiceUtils(SportObjectRepository sportObjectRepository,
                        RentEquipmentRepository rentEquipmentRepository,
                        UserRepository userRepository) {
       this.sportObjectRepository = sportObjectRepository;
       this.rentEquipmentRepository = rentEquipmentRepository;
       this.userRepository = userRepository;
    }

    public boolean compareSportObjectNameWithExisting(String name) {
        return sportObjectRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(SportObjectEntity::getName)
                .collect(Collectors.toList())
                .contains(name);
    }

    public boolean compareRentEquipmentNameWithExisting(String name) {
        return rentEquipmentRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(RentEquipmentEntity::getName)
                .collect(Collectors.toList())
                .contains(name);
    }

    public boolean compareUserEmailWithExisting(String email){
        return userRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(UserEntity::getEmail)
                .collect(Collectors.toList())
                .contains(email);
    }

    public LocalDateTime convertTime (LocalDateTime time) {
        return time.truncatedTo(ChronoUnit.MINUTES);
    }
}
