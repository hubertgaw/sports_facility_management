package pl.lodz.hubertgaw.service.utils;

import pl.lodz.hubertgaw.repository.SportObjectRepository;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.SportObjectException;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Collectors;

@ApplicationScoped
public class ServiceUtils {

    private final SportObjectRepository sportObjectRepository;

    public ServiceUtils(SportObjectRepository sportObjectRepository) {
       this.sportObjectRepository = sportObjectRepository;
    }

    public boolean compareSportObjectNameWithExisted(String name) {
        return sportObjectRepository
                .findAll()
                .stream()
                .collect(Collectors.toList())
                .stream()
                .map(SportObjectEntity::getName)
                .collect(Collectors.toList())
                .contains(name);
    }
}
