package com.theodoremeras.dissertation.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

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

    public List<UserEntity> findAllByEmail(String email) {
        return userRepository.findAllByEmail(email);
}

    public List<UserEntity> findAllByDepartmentIdAndRoleId(Integer departmentId, Integer roleId) {
        return userRepository.findAllByDepartmentIdAndRoleId(departmentId, roleId);
    }

    public List<UserEntity> findAllByIdIn(List<Integer> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    public Optional<UserEntity> findOneById(Integer id) {
        return userRepository.findById(id);
    }

    public Optional<UserEntity> findOneByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Boolean exists(Integer id) {
        return userRepository.existsById(id);
    }

    public UserEntity partialUpdate(Integer id, UserEntity userEntity) {
        userEntity.setId(id);

        return userRepository.findById(id).map(existingUser -> {
            Optional.ofNullable(userEntity.getName()).ifPresent(existingUser::setName);
            Optional.ofNullable(userEntity.getEmail()).ifPresent(existingUser::setEmail);
            Optional.ofNullable(userEntity.getIsApproved()).ifPresent(existingUser::setIsApproved);
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("Could not find user with id: " + id));
    }

    public void delete(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository
                .findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

    }
}
