package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.Booking;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.mapper.UserMapper;
import pl.lodz.hubertgaw.repository.RoleRepository;
import pl.lodz.hubertgaw.repository.UserRepository;
import pl.lodz.hubertgaw.repository.entity.BookingEntity;
import pl.lodz.hubertgaw.repository.entity.RoleEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.repository.entity.sports_objects.SportObjectEntity;
import pl.lodz.hubertgaw.service.exception.BookingException;
import pl.lodz.hubertgaw.service.exception.UserException;
import pl.lodz.hubertgaw.service.exception.UserException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final Logger logger;
    private final ServiceUtils serviceUtils;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       Logger logger,
                       ServiceUtils serviceUtils,
                       RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.logger = logger;
        this.serviceUtils = serviceUtils;
    }

    public List<User> findAll() {
        return userRepository.listAll()
                .stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());
    }

    public User findById(Integer userId) {
        UserEntity entity = userRepository.findByIdOptional(userId)
                .orElseThrow(UserException::userNotFoundException);

        return userMapper.toDomain(entity);
    }

    public User findByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email);
        if (entity == null) {
            throw UserException.userForEmailNotFoundException();
        }

        return userMapper.toDomain(entity);
    }

    public User findByRole(String roleName) {
        RoleEntity role = roleRepository.findByName(roleName);
        UserEntity entity = userRepository.findByRole(role);
        if (entity == null) {
            throw UserException.userForRoleNotFoundException();
        }

        return userMapper.toDomain(entity);
    }

    @Transactional
    public User save(User user) {
        if (serviceUtils.compareUserEmailWithExisting(user.getEmail())) {
            throw UserException.userDuplicateEmailException();
        }
        UserEntity entity = userMapper.toEntity(user);
        userRepository.persist(entity);
        return userMapper.toDomain(entity);
    }


    @Transactional
    public User update(User user) {
        if (user.getId() == null) {
            throw UserException.userEmptyIdException();
        }
        UserEntity entity = userRepository.findByIdOptional(user.getId())
                .orElseThrow(UserException::userNotFoundException);

        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setPhoneNumber(user.getPhoneNumber());
        return userMapper.toDomain(entity);
    }

    @Transactional
    public void deleteUserById(Integer userId) {
        UserEntity userToDelete = userRepository.findById(userId);
        if (userToDelete == null) {
            throw UserException.userNotFoundException();
        }
//        Set<SportObjectEntity> sportObjectsToDelete = userToDelete.getSportObjects();
//        for (SportObjectEntity sportObject : sportObjectsToDelete) {
//            sportObject.removeUser(userToDelete);
//        }
        userRepository.delete(userToDelete);
    }



}
