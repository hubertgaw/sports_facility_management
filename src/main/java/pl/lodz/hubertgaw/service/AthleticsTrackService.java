package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.AthleticsTrackRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

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

    public AthleticsTrackService(AthleticsTrackRepository athleticsTrackRepository,
                                 SportObjectMapper sportObjectMapper,
                                 Logger logger,
                                 RentEquipmentRepository rentEquipmentRepository) {
        this.athleticsTrackRepository = athleticsTrackRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<AthleticsTrack> findAll() {
        return athleticsTrackRepository.findAll().stream()
                .map(sportObjectMapper::map)
                .map(AthleticsTrack.class::cast)
                .collect(Collectors.toList());
//        return null;
    }

    public Optional<AthleticsTrack> findById(Integer trackId) {
        return athleticsTrackRepository.findByIdOptional(trackId)
                .map(sportObjectMapper::map)
                .map(AthleticsTrack.class::cast);
//    return null;
    }

    @Transactional
    public AthleticsTrack save(AthleticsTrack athleticsTrack) {
        AthleticsTrackEntity entity = (AthleticsTrackEntity) sportObjectMapper.map(athleticsTrack);
        athleticsTrackRepository.persist(entity);
        return (AthleticsTrack) sportObjectMapper.map(entity);
//    return null;
    }

    @Transactional
    public AthleticsTrack update(AthleticsTrack athleticsTrack) {
        if (athleticsTrack.getName() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<AthleticsTrackEntity> optional = athleticsTrackRepository.findByName(athleticsTrack.getName());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Customer found for customerId[%s]", athleticsTrack.getName()));
        }
        AthleticsTrackEntity entity = optional.get();
        entity.setCapacity(athleticsTrack.getCapacity());
        entity.setSingleTrackPrice(athleticsTrack.getSingleTrackPrice());
        entity.setFullPrice(athleticsTrack.getFullPrice());
//        entity.setRentEquipment(athleticsTrack.getRentEquipments());
        athleticsTrackRepository.persist(entity);
        return (AthleticsTrack) sportObjectMapper.map(entity);
//        return null;
    }

    @Transactional
    public AthleticsTrack putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        AthleticsTrackEntity athleticsTrackToUpdate = athleticsTrackRepository.findById(sportObjectId);
        athleticsTrackToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        athleticsTrackRepository.persistAndFlush(athleticsTrackToUpdate);
        return (AthleticsTrack) sportObjectMapper.map(athleticsTrackToUpdate);
//        return null;
    }
}
