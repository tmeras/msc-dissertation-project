package com.theodoremeras.dissertation.user;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public List<UserEntity> findAllByIdIn(List<Integer> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    public List<UserEntity> findAllByEmail(String email) {
        return userRepository.findAllByEmail(email);
}

    public Optional<UserEntity> findOneById(Integer id) {
        return userRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return userRepository.existsById(id);
    }

    public UserEntity partialUpdate(Integer id, UserEntity userEntity) {
        userEntity.setId(id);

        return userRepository.findById(id).map(existingUser -> {
            Optional.ofNullable(userEntity.getName()).ifPresent(existingUser::setName);
            Optional.ofNullable(userEntity.getEmail()).ifPresent(existingUser::setEmail);
            Optional.ofNullable(userEntity.getPassword()).ifPresent(existingUser::setPassword);
            Optional.ofNullable(userEntity.getIsApproved()).ifPresent(existingUser::setIsApproved);
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("Could not find user with id: " + id));
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

}
