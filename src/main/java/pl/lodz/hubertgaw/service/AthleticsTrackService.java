package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.DartRoom;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.AthleticsTrackRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.AthleticsTrackException;
import pl.lodz.hubertgaw.service.exception.ServiceException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class AthleticsTrackService {

    private final AthleticsTrackRepository athleticsTrackRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public AthleticsTrackService(AthleticsTrackRepository athleticsTrackRepository,
                                 SportObjectMapper sportObjectMapper,
                                 Logger logger,
                                 RentEquipmentRepository rentEquipmentRepository,
                                 ServiceUtils serviceUtils) {
        this.athleticsTrackRepository = athleticsTrackRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<AthleticsTrack> findAll() {
        return athleticsTrackRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(AthleticsTrack.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<AthleticsTrack> findById(Integer trackId) {
        return athleticsTrackRepository.findByIdOptional(trackId)
                .map(sportObjectMapper::toDomain)
                .map(AthleticsTrack.class::cast);
//    return null;
    }

    @Transactional
    public AthleticsTrack save(AthleticsTrack athleticsTrack) {
        AthleticsTrackEntity entity = (AthleticsTrackEntity) sportObjectMapper.toEntity(athleticsTrack);
        athleticsTrackRepository.persist(entity);
        return (AthleticsTrack) sportObjectMapper.toDomain(entity);
//    return null;
    }

    @Transactional
    public AthleticsTrack update(AthleticsTrack athleticsTrack) {
        if (athleticsTrack.getId() == null) {
            throw AthleticsTrackException.athleticsTrackEmptyIdException();
        }
        AthleticsTrackEntity entity = athleticsTrackRepository.findByIdOptional(athleticsTrack.getId())
                .orElseThrow(AthleticsTrackException::athleticsTrackNotFoundException);

        if (serviceUtils.compareSportObjectNameWithExisted(entity.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }

        entity.setName(athleticsTrack.getName());
        entity.setCapacity(athleticsTrack.getCapacity());
        entity.setSingleTrackPrice(athleticsTrack.getSingleTrackPrice());
        entity.setFullPrice(athleticsTrack.getFullPrice());
//        entity.setRentEquipment(athleticsTrack.getRentEquipments());
        athleticsTrackRepository.persist(entity);
        return (AthleticsTrack) sportObjectMapper.toDomain(entity);
//        return null;
    }

    @Transactional
    public AthleticsTrack putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        AthleticsTrackEntity athleticsTrackToUpdate = athleticsTrackRepository.findById(sportObjectId);
        athleticsTrackToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        athleticsTrackRepository.persistAndFlush(athleticsTrackToUpdate);
        return (AthleticsTrack) sportObjectMapper.toDomain(athleticsTrackToUpdate);
//        return null;
    }
}
