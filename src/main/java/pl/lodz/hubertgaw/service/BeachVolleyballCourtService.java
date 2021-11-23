package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.AthleticsTrack;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.mapper.AthleticsTrackMapper;
import pl.lodz.hubertgaw.mapper.BeachVolleyballCourtMapper;
import pl.lodz.hubertgaw.repository.AthleticsTrackRepository;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.AthleticsTrackEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.service.exception.ServiceException;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class BeachVolleyballCourtService {
    private final BeachVolleyballCourtRepository beachVolleyballCourtRepository;
    private final BeachVolleyballCourtMapper beachVolleyballCourtMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;

    public BeachVolleyballCourtService(BeachVolleyballCourtRepository beachVolleyballCourtRepository,
                                 BeachVolleyballCourtMapper beachVolleyballCourtMapper,
                                 Logger logger,
                                 RentEquipmentRepository rentEquipmentRepository) {
        this.beachVolleyballCourtRepository = beachVolleyballCourtRepository;
        this.beachVolleyballCourtMapper = beachVolleyballCourtMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
    }

    public List<BeachVolleyballCourt> findAll() {
        return beachVolleyballCourtRepository.findAll().stream()
                .map(beachVolleyballCourtMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Optional<BeachVolleyballCourt> findById(Integer courtId) {
        return beachVolleyballCourtRepository.findByIdOptional(courtId).map(beachVolleyballCourtMapper::toDomain);
    }

    @Transactional
    public BeachVolleyballCourt save(BeachVolleyballCourt beachVolleyballCourt) {
        BeachVolleyballCourtEntity entity = beachVolleyballCourtMapper.toEntity(beachVolleyballCourt);
        beachVolleyballCourtRepository.persist(entity);
        return beachVolleyballCourtMapper.toDomain(entity);
    }

    @Transactional
    public BeachVolleyballCourt update(BeachVolleyballCourt beachVolleyballCourt) {
        if (beachVolleyballCourt.getId() == null) {
            throw new ServiceException("Customer does not have a customerId");
        }
        Optional<BeachVolleyballCourtEntity> optional = beachVolleyballCourtRepository.findByIdOptional(beachVolleyballCourt.getId());
        if (optional.isEmpty()) {
            throw new ServiceException(String.format("No Court found for Id[%s]", beachVolleyballCourt.getId()));
        }
        BeachVolleyballCourtEntity entity = optional.get();
        entity.setFullPrice(beachVolleyballCourt.getFullPrice());
        entity.setName(beachVolleyballCourt.getName());
//        entity.set(beachVolleyballCourt.getFullPrice());
//        entity.setRentEquipment(beachVolleyballCourt.getRentEquipments());
        beachVolleyballCourtRepository.persist(entity);
        return beachVolleyballCourtMapper.toDomain(entity);
    }

    public BeachVolleyballCourt putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        BeachVolleyballCourtEntity beachVolleyballCourtToUpdate = beachVolleyballCourtRepository.findById(sportObjectId);
        beachVolleyballCourtToUpdate.addRentEquipment(rentEquipmentRepository.findById(rentEquipmentId));
        beachVolleyballCourtRepository.persist(beachVolleyballCourtToUpdate);
        return beachVolleyballCourtMapper.toDomain(beachVolleyballCourtToUpdate);
    }

}
