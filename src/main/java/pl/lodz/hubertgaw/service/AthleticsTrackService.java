package pl.lodz.hubertgaw.service;

import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.mapper.AthleticsTrackMapper;
import pl.lodz.hubertgaw.repository.AthleticsTrackRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@ApplicationScoped
public class AthleticsTrackService {

    private final AthleticsTrackRepository athleticsTrackRepository;
    private final AthleticsTrackMapper athleticsTrackMapper;
    private final Logger logger;

    public AthleticsTrackService(AthleticsTrackRepository athleticsTrackRepository, AthleticsTrackMapper athleticsTrackMapper, Logger logger) {
        this.athleticsTrackRepository = athleticsTrackRepository;
        this.athleticsTrackMapper = athleticsTrackMapper;
        this.logger = logger;
    }

    public List<AthleticsTrack> findAll(){
        return athleticsTrackRepository.findAll().stream()
                .map(athleticsTrackMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<AthleticsTrack> findById(Integer trackId) {
        return athleticsTrackRepository.findByIdOptional(trackId).map(athleticsTrackMapper::toDomain);
    }

    @Transactional
    public AthleticsTrack save(AthleticsTrack athleticsTrack) {
        AthleticsTrackEntity entity = athleticsTrackMapper.toEntity(athleticsTrack);
        athleticsTrackRepository.persist(entity);
        return athleticsTrackMapper.toDomain(entity);
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
        return athleticsTrackMapper.toDomain(entity);
    }
}
