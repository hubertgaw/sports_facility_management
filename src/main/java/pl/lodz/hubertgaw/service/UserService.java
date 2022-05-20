package pl.lodz.hubertgaw.service;

import org.slf4j.Logger;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.dto.User;
import pl.lodz.hubertgaw.mapper.UserMapper;
import pl.lodz.hubertgaw.repository.RoleRepository;
import pl.lodz.hubertgaw.repository.UserRepository;
import pl.lodz.hubertgaw.repository.entity.*;
import pl.lodz.hubertgaw.repository.entity.UserEntity;
import pl.lodz.hubertgaw.security.PasswordEncoder;
import pl.lodz.hubertgaw.service.exception.UserException;
import pl.lodz.hubertgaw.service.exception.RoleException;
import pl.lodz.hubertgaw.service.exception.UserException;
import pl.lodz.hubertgaw.service.utils.ServiceUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final Logger logger;
    private final ServiceUtils serviceUtils;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       Logger logger,
                       ServiceUtils serviceUtils,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.logger = logger;
        this.serviceUtils = serviceUtils;
        this.passwordEncoder = passwordEncoder;

        logger.info("Constructor UserService called");
    }

    public List<User> findAll() {
        logger.info("Method findAll() called");

        List<UserEntity> allUsersEntities = userRepository.listAll();

        logger.info("All users as entities taken from repository: {}", allUsersEntities);

        List<User> allUsersDto = allUsersEntities
                .stream()
                .map(userMapper::toDomain)
                .collect(Collectors.toList());

        logger.info("All users found (after mapping from entity to DTO): {}", allUsersDto);

        return allUsersDto;
    }

    public User findById(Integer userId) {
        logger.info("Method findById() called with argument:{}", userId);

        UserEntity entity = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> {

                    logger.warn("Exception", UserException.userNotFoundException());

                    return UserException.userNotFoundException();
                });

        logger.info("UserEntity by id: {} found in database:{}", userId, entity);

        User userDto = userMapper.toDomain(entity);

        logger.info("User by id: {} found after mapping to DTO:{}", userId, userDto);

        return userDto;
    }

    public User findByEmail(String email) {
        logger.info("Method findByEmail() called with argument: {}", email);

        UserEntity entity = userRepository.findByEmail(email);
        if (entity == null) {
            logger.warn("Exception", UserException.userForEmailNotFoundException());
            throw UserException.userForEmailNotFoundException();
        }

        logger.info("UserEntity by email: {} found in database:{}", email, entity);

        User userDto = userMapper.toDomain(entity);

        logger.info("User by email: {} found after mapping to DTO:{}", email, userDto);

        return userDto;
    }

    public List<User> findByRole(String roleNameString) {
        logger.info("Method findByRole called with argument {}", roleNameString);

        RoleName roleName = RoleName.getByName(roleNameString);

        logger.info("RoleName converted from String to RoleName type: {}", roleName);

        RoleEntity role = roleRepository.findByName(roleName);

        logger.info("RoleEntity for roleName:{} found in database:{}", roleName, role);

        List<UserEntity> userEntitiesByRole = userRepository.findByRole(role);

        logger.info("Users by role :{} as entities found in repository:{}", role, userEntitiesByRole);

        List<User> usersByType = userEntitiesByRole
                .stream()
                .map(userMapper::toDomain)
                .map(User.class::cast)
                .collect(Collectors.toList());
        if (usersByType.isEmpty()) {

            logger.warn("Exception", UserException.userForRoleNotFoundException());

            throw UserException.userForRoleNotFoundException();
        }

        logger.info("Users by type: {} found (after mapping from entity to DTO): {}",
                roleNameString, usersByType);


        return usersByType;
    }

    @Transactional
    public User save(User user) {
        logger.info("Method save() called with argument:{}", user);

        if (serviceUtils.compareUserEmailWithExisting(user.getEmail())) {

            logger.warn("Exception", UserException.userDuplicateEmailException());

            throw UserException.userDuplicateEmailException();
        }
        user.setRoles(Collections.singleton((RoleName.USER)));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        UserEntity entity = userMapper.toEntity(user);
        userRepository.persist(entity);

        logger.info("User persisted in repository: {}", entity);

        User userDto = userMapper.toDomain(entity);

        logger.info("User mapped from entity to DTO: {}", userDto);

        return userDto;
    }


    @Transactional
    public User update(User user) {
        logger.info("Method update() called with argument: {}", user);

        if (user.getId() == null) {

            logger.warn("Exception", UserException.userEmptyIdException());

            throw UserException.userEmptyIdException();
        }
        UserEntity entity = userRepository.findByIdOptional(user.getId())
                .orElseThrow(() -> {

                    logger.warn("Exception", UserException.userNotFoundException());

                    return UserException.userNotFoundException();
                });

        logger.info("User before update: {}", entity);

        entity.setEmail(user.getEmail());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setPhoneNumber(user.getPhoneNumber());
        userRepository.persist(entity);

        logger.info("User updated and persisted: {}", entity);

        User userDto = userMapper.toDomain(entity);

        logger.info("Updated user mapped from entity to DTO: {}", userDto);

        return userDto;
    }

    @Transactional
    public void deleteUserById(Integer userId) {
        logger.info("Method deleteSportObjectById() called with argument: {}", userId);

        UserEntity userToDelete = userRepository.findById(userId);
        if (userToDelete == null) {
            logger.warn("Exception", UserException.userNotFoundException());

            throw UserException.userNotFoundException();
        }

        logger.info("UserEntity by id: {} found in database:{}", userId, userToDelete);

//        Set<SportObjectEntity> sportObjectsToDelete = userToDelete.getSportObjects();
//        for (SportObjectEntity sportObject : sportObjectsToDelete) {
//            sportObject.removeUser(userToDelete);
//        }
        userRepository.delete(userToDelete);

        logger.info("User with id: {} deleted successfully", userId);

    }

}
