package com.theodoremeras.dissertation.authentication;

import com.theodoremeras.dissertation.role.RoleRepository;
import com.theodoremeras.dissertation.user.UserDto;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserMapper;
import com.theodoremeras.dissertation.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AuthenticationService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    private PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    private TokenService tokenService;


    public AuthenticationService(
            UserRepository userRepository, UserMapper userMapper,
            PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public UserEntity registerUser(UserEntity userEntity) {
        String encodedPassword = passwordEncoder.encode(userEntity.getPassword());
        userEntity.setPassword(encodedPassword);
        return userRepository.save(userEntity);
    }

    public UserLoginResponseDto loginUser(UserLoginRequestDto userLoginRequestDto) {
        try{
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLoginRequestDto.getEmail(),
                            userLoginRequestDto.getPassword()
                    )
            );

            String token = tokenService.generateJwt(auth);

            UserDto userDto =
                    userMapper.mapToDto(userRepository.findByEmail(userLoginRequestDto.getEmail()).get());

            return new UserLoginResponseDto(
                    userDto,
                    token
            );

        } catch (AuthenticationException e){
            return null;
        }
    }

}
