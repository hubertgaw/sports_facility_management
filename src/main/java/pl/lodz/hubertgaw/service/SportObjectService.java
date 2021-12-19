package pl.lodz.hubertgaw.service;

import pl.lodz.hubertgaw.dto.SmallPitch;
import pl.lodz.hubertgaw.dto.SportObject;
import pl.lodz.hubertgaw.dto.SportsHall;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SmallPitchEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<SportObject> findById(Integer sportObjectId) {
        return sportObjectRepository.findByIdOptional(sportObjectId)
                .map(sportObjectMapper::toDomain)
                .map(SportObject.class::cast);
    }

//    @Transactional
//    public SportObject update(SportObject sportObject) {
//        if (sportObject.getId() == null) {
//            throw new ServiceException("SportObject does not have a sportObjectId");
//        }
//        Optional<SportObjectEntity> optional = sportObjectRepository.findByIdOptional(sportObject.getId());
//        if (optional.isEmpty()) {
//            throw new ServiceException(String.format("No SportObject found for Id[%s]", sportObject.getId()));
//        }
//        SportObjectEntity entity = optional.get();
//        entity.setFullPrice(smallPitch.getFullPrice());
//        entity.setName(smallPitch.getName());
//        entity.setHalfPitchPrice(smallPitch.getHalfPitchPrice());
//        entity.setIsFullRented(smallPitch.getIsFullRented());
//        smallPitchRepository.persist(entity);
//        return (SmallPitch) sportObjectMapper.toDomain(entity);
//
//    }

    @Transactional
    public SportObject putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        SportObjectEntity sportObjectToUpdate = sportObjectRepository.findById(sportObjectId);
        sportObjectToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        sportObjectRepository.persistAndFlush(sportObjectToUpdate);
        return sportObjectMapper.toDomain(sportObjectToUpdate);
    }
}
