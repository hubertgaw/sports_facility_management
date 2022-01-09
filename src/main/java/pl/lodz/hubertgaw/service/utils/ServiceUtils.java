package pl.lodz.hubertgaw.service.utils;

import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceUtils {

    private final SportObjectRepository sportObjectRepository;
    private final RentEquipmentRepository rentEquipmentRepository;

    public ServiceUtils(SportObjectRepository sportObjectRepository,
                        RentEquipmentRepository rentEquipmentRepository) {
       this.sportObjectRepository = sportObjectRepository;
       this.rentEquipmentRepository = rentEquipmentRepository;
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
}
