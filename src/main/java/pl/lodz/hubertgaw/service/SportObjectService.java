package pl.lodz.hubertgaw.service;

import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import pl.lodz.hubertgaw.service.exception.SportObjectException;


@ApplicationScoped
public class SportObjectService {
    private final SportObjectRepository sportObjectRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public SportObjectService(SportObjectRepository sportObjectRepository,
                              SportObjectMapper sportObjectMapper,
                              Logger logger,
                              RentEquipmentRepository rentEquipmentRepository) {
        this.sportObjectRepository = sportObjectRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<SportObject> findAll() {
        return sportObjectRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(SportObject.class::cast)
                .collect(Collectors.toList());
    }

    public SportObject findById(Integer sportObjectId) {
        SportObjectEntity entity = sportObjectRepository.findByIdOptional(sportObjectId)
                .orElseThrow(SportObjectException::sportObjectNotFoundException);

        return sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public SportObject putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportObjectEntity sportObjectToUpdate = sportObjectRepository.findById(sportObjectId);
        if (sportObjectToUpdate == null) {
            throw SportObjectException.sportObjectNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        sportObjectToUpdate.addRentEquipment(rentEquipmentToAdd);
        sportObjectRepository.persistAndFlush(sportObjectToUpdate);
        return sportObjectMapper.toDomain(sportObjectToUpdate);
    }

    @Transactional
    public void deleteSportObjectById(Integer sportObjectId) {
        SportObjectEntity sportObjectToDelete = sportObjectRepository.findById(sportObjectId);
        if (sportObjectToDelete == null) {
            throw SportObjectException.sportObjectNotFoundException();
        }
        Set<RentEquipmentEntity> rentEquipmentToDelete = sportObjectToDelete.getRentEquipment();
        for (RentEquipmentEntity rentEquipment : rentEquipmentToDelete) {
            rentEquipment.removeSportObject(sportObjectToDelete);
        }
        sportObjectRepository.delete(sportObjectToDelete);
    }
}
