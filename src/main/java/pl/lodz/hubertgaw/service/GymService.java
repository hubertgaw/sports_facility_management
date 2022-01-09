package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Gym;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.GymRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.GymEntity;
import pl.lodz.hubertgaw.service.exception.GymException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class GymService {
    private final GymRepository gymRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public GymService(GymRepository gymRepository,
                      SportObjectMapper sportObjectMapper,
                      Logger logger,
                      RentEquipmentRepository rentEquipmentRepository,
                      ServiceUtils serviceUtils) {
        this.gymRepository = gymRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<Gym> findAll() {
        return gymRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(Gym.class::cast)
                .collect(Collectors.toList());
    }

    public Gym findById(Integer courtId) {
        GymEntity entity = gymRepository.findByIdOptional(courtId)
                .orElseThrow(GymException::gymNotFoundException);
        return (Gym) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public Gym save(Gym gym) {
        if (serviceUtils.compareSportObjectNameWithExisting(gym.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        GymEntity entity = (GymEntity) sportObjectMapper.toEntity(gym);
        gymRepository.persist(entity);
        return (Gym) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public Gym update(Gym gym) {
        if (gym.getId() == null) {
            throw GymException.gymEmptyIdException();
        }
        GymEntity entity = gymRepository.findByIdOptional(gym.getId())
                .orElseThrow(GymException::gymNotFoundException);
        if (!gym.getName().equals(entity.getName())) {
            if (serviceUtils.compareSportObjectNameWithExisting(gym.getName())) {
                throw SportObjectException.sportObjectDuplicateNameException();
            }
        }
        entity.setFullPrice(gym.getFullPrice());
        entity.setName(gym.getName());
        entity.setCapacity(gym.getCapacity());
        entity.setSinglePrice(gym.getSinglePrice());
        gymRepository.persist(entity);
        return (Gym) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public Gym putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        GymEntity gymToUpdate = gymRepository.findById(sportObjectId);
        if (gymToUpdate == null) {
            throw GymException.gymNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        gymToUpdate.addRentEquipment(rentEquipmentToAdd);
        gymRepository.persistAndFlush(gymToUpdate);
        return (Gym) sportObjectMapper.toDomain(gymToUpdate);
    }

}
