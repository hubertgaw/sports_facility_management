package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.DartRoomRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.DartRoomEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class DartRoomService {
    private final DartRoomRepository dartRoomRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public DartRoomService(DartRoomRepository dartRoomRepository,
                                       SportObjectMapper sportObjectMapper,
                                       Logger logger,
                                       RentEquipmentRepository rentEquipmentRepository) {
        this.dartRoomRepository = dartRoomRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<DartRoom> findAll() {
        return dartRoomRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(DartRoom.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<DartRoom> findById(Integer roomId) {
        return dartRoomRepository.findByIdOptional(roomId)
                .map(sportObjectMapper::toDomain)
                .map(DartRoom.class::cast);
    }

    @Transactional
    public DartRoom save(DartRoom dartRoom) {
        DartRoomEntity entity = (DartRoomEntity) sportObjectMapper.toEntity(dartRoom);
        dartRoomRepository.persist(entity);
        return (DartRoom) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public DartRoom update(DartRoom dartRoom) {
        if (dartRoom.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<DartRoomEntity> optional = dartRoomRepository.findByIdOptional(dartRoom.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", dartRoom.getId()));
        }
        DartRoomEntity entity = optional.get();
        entity.setFullPrice(dartRoom.getFullPrice());
        entity.setName(dartRoom.getName());
        entity.setStandPrice(dartRoom.getStandPrice());
        entity.setStandsNumber(dartRoom.getStandsNumber());
        dartRoomRepository.persist(entity);
        return (DartRoom) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public DartRoom putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        DartRoomEntity dartRoomToUpdate = dartRoomRepository.findById(sportObjectId);
        dartRoomToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        dartRoomRepository.persistAndFlush(dartRoomToUpdate);
        return (DartRoom) sportObjectMapper.toDomain(dartRoomToUpdate);
    }

}
