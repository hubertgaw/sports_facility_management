package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.ClimbingWall;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.ClimbingWallRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.ClimbingWallEntity;
import pl.lodz.hubertgaw.service.exception.ClimbingWallException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClimbingWallService {
    private final ClimbingWallRepository climbingWallRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public ClimbingWallService(ClimbingWallRepository climbingWallRepository,
                               SportObjectMapper sportObjectMapper,
                               Logger logger,
                               RentEquipmentRepository rentEquipmentRepository,
                               ServiceUtils serviceUtils) {
        this.climbingWallRepository = climbingWallRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<ClimbingWall> findAll() {
        return climbingWallRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(ClimbingWall.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<ClimbingWall> findById(Integer wallId) {
        return climbingWallRepository.findByIdOptional(wallId)
                .map(sportObjectMapper::toDomain)
                .map(ClimbingWall.class::cast);
    }

    @Transactional
    public ClimbingWall save(ClimbingWall climbingWall) {
        if (serviceUtils.compareSportObjectNameWithExisting(climbingWall.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        ClimbingWallEntity entity = (ClimbingWallEntity) sportObjectMapper.toEntity(climbingWall);
        climbingWallRepository.persist(entity);
        return (ClimbingWall) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public ClimbingWall update(ClimbingWall climbingWall) {
        if (climbingWall.getId() == null) {
            throw ClimbingWallException.climbingWallEmptyIdException();
        }
        ClimbingWallEntity entity = climbingWallRepository.findByIdOptional(climbingWall.getId())
                .orElseThrow(ClimbingWallException::climbingWallNotFoundException);
        if (serviceUtils.compareSportObjectNameWithExisting(entity.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        entity.setFullPrice(climbingWall.getFullPrice());
        entity.setName(climbingWall.getName());
        entity.setCapacity(climbingWall.getCapacity());
        entity.setSinglePrice(climbingWall.getSinglePrice());
        climbingWallRepository.persist(entity);
        return (ClimbingWall) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public ClimbingWall putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        ClimbingWallEntity climbingWallToUpdate = climbingWallRepository.findById(sportObjectId);
        if (climbingWallToUpdate == null) {
            throw ClimbingWallException.climbingWallNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }
        climbingWallToUpdate.addRentEquipment(rentEquipmentToAdd);
        climbingWallRepository.persistAndFlush(climbingWallToUpdate);
        return (ClimbingWall) sportObjectMapper.toDomain(climbingWallToUpdate);
    }

}
