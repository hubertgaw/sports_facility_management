package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.mapper.DartRoomMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.DartRoomRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
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
    private final DartRoomMapper dartRoomMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public DartRoomService(DartRoomRepository dartRoomRepository,
                                       DartRoomMapper dartRoomMapper,
                                       Logger logger,
                                       RentEquipmentRepository rentEquipmentRepository) {
        this.dartRoomRepository = dartRoomRepository;
        this.dartRoomMapper = dartRoomMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<DartRoom> findAll() {
        return dartRoomRepository.findAll().stream()
                .map(dartRoomMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<DartRoom> findById(Integer roomId) {
        return dartRoomRepository.findByIdOptional(roomId).map(dartRoomMapper::toDomain);
    }

    @Transactional
    public DartRoom save(DartRoom dartRoom) {
        DartRoomEntity entity = dartRoomMapper.toEntity(dartRoom);
        dartRoomRepository.persist(entity);
        return dartRoomMapper.toDomain(entity);
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
//        entity.set(beachVolleyballCourt.getFullPrice());
//        entity.setRentEquipment(beachVolleyballCourt.getRentEquipments());
        dartRoomRepository.persist(entity);
        return dartRoomMapper.toDomain(entity);
    }

    public DartRoom putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        DartRoomEntity dartRoomToUpdate = dartRoomRepository.findById(sportObjectId);
        dartRoomToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        dartRoomRepository.persist(dartRoomToUpdate);
        return dartRoomMapper.toDomain(dartRoomToUpdate);
    }

}
