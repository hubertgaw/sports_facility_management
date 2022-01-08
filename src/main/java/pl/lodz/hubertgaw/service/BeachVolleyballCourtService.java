package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.BeachVolleyballCourt;
import pl.lodz.hubertgaw.mapper.SportObjectMapper;
import pl.lodz.hubertgaw.repository.BeachVolleyballCourtRepository;
import pl.lodz.hubertgaw.repository.RentEquipmentRepository;
import pl.lodz.hubertgaw.repository.entity.RentEquipmentEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.BeachVolleyballCourtEntity;
import pl.lodz.hubertgaw.service.exception.BeachVolleyballCourtException;
import pl.lodz.hubertgaw.service.exception.RentEquipmentException;
import pl.lodz.hubertgaw.service.exception.SportObjectException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class BeachVolleyballCourtService {
    private final BeachVolleyballCourtRepository beachVolleyballCourtRepository;
    private final SportObjectMapper sportObjectMapper;
    private final Logger logger;
    private final RentEquipmentRepository rentEquipmentRepository;
    private final ServiceUtils serviceUtils;

    public BeachVolleyballCourtService(BeachVolleyballCourtRepository beachVolleyballCourtRepository,
                                 SportObjectMapper sportObjectMapper,
                                 Logger logger,
                                 RentEquipmentRepository rentEquipmentRepository,
                                       ServiceUtils serviceUtils) {
        this.beachVolleyballCourtRepository = beachVolleyballCourtRepository;
        this.sportObjectMapper = sportObjectMapper;
        this.logger = logger;
        this.rentEquipmentRepository = rentEquipmentRepository;
        this.serviceUtils = serviceUtils;
    }

    public List<BeachVolleyballCourt> findAll() {
        return beachVolleyballCourtRepository.listAll()
                .stream()
                .map(sportObjectMapper::toDomain)
                .map(BeachVolleyballCourt.class::cast)
                .collect(Collectors.toList());
    }

    public Optional<BeachVolleyballCourt> findById(Integer courtId) {
        return beachVolleyballCourtRepository.findByIdOptional(courtId)
                .map(sportObjectMapper::toDomain)
                .map(BeachVolleyballCourt.class::cast);
    }

    @Transactional
    public BeachVolleyballCourt save(BeachVolleyballCourt beachVolleyballCourt) {
        if (serviceUtils.compareSportObjectNameWithExisting(beachVolleyballCourt.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        BeachVolleyballCourtEntity entity = (BeachVolleyballCourtEntity) sportObjectMapper.toEntity(beachVolleyballCourt);
        beachVolleyballCourtRepository.persist(entity);
        return (BeachVolleyballCourt) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public BeachVolleyballCourt update(BeachVolleyballCourt beachVolleyballCourt) {
        if (beachVolleyballCourt.getId() == null) {
            throw BeachVolleyballCourtException.beachVolleyballCourtEmptyIdException();
        }
        BeachVolleyballCourtEntity entity = beachVolleyballCourtRepository.findByIdOptional(beachVolleyballCourt.getId())
                .orElseThrow(BeachVolleyballCourtException::beachVolleyballCourtNotFoundException);
        if (serviceUtils.compareSportObjectNameWithExisting(entity.getName())) {
            throw SportObjectException.sportObjectDuplicateNameException();
        }
        entity.setFullPrice(beachVolleyballCourt.getFullPrice());
        entity.setName(beachVolleyballCourt.getName());
        beachVolleyballCourtRepository.persist(entity);
        return (BeachVolleyballCourt) sportObjectMapper.toDomain(entity);
    }

    @Transactional
    public BeachVolleyballCourt putEquipmentToObject(Integer sportObjectId, Integer rentEquipmentId) {
        BeachVolleyballCourtEntity beachVolleyballCourtToUpdate = beachVolleyballCourtRepository.findById(sportObjectId);
        if (beachVolleyballCourtToUpdate == null) {
            throw BeachVolleyballCourtException.beachVolleyballCourtNotFoundException();
        }
        RentEquipmentEntity rentEquipmentToAdd = rentEquipmentRepository.findById(rentEquipmentId);
        if (rentEquipmentToAdd == null) {
            throw RentEquipmentException.rentEquipmentNotFoundException();
        }

        beachVolleyballCourtToUpdate.addRentEquipment(rentEquipmentToAdd);
        beachVolleyballCourtRepository.persistAndFlush(beachVolleyballCourtToUpdate);
        return (BeachVolleyballCourt) sportObjectMapper.toDomain(beachVolleyballCourtToUpdate);
    }

}
