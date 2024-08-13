package com.theodoremeras.dissertation.unit_tests.services;

import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.authentication.AuthenticationService;
import com.theodoremeras.dissertation.authentication.TokenService;
import com.theodoremeras.dissertation.authentication.UserLoginRequestDto;
import com.theodoremeras.dissertation.authentication.UserLoginResponseDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.UserDto;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserMapper;
import com.theodoremeras.dissertation.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;



    @Test
    public void testRegisterUser() throws Exception {
        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(
                TestDataUtil.createTestRoleEntityA(),
                TestDataUtil.createTestDepartmentEntityA()
        );
        UserEntity testRegisteredUserEntity= TestDataUtil.createTestUserEntityA(
                TestDataUtil.createTestRoleEntityA(),
                TestDataUtil.createTestDepartmentEntityA()
        );
        String encodedPassword = "encodedPass";
        testRegisteredUserEntity.setPassword(encodedPassword);

        when(passwordEncoder.encode(testUserEntity.getPassword())).thenReturn(encodedPassword);
        when(userRepository.save(testUserEntity)).thenReturn(testRegisteredUserEntity);

        UserEntity result = authenticationService.registerUser(testUserEntity);

        assertEquals(testRegisteredUserEntity, result);
    }

    @Test
    public void testLoginUser() throws Exception {
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        UserLoginRequestDto testUserLoginRequestDto = TestDataUtil.createTestUserLoginRequestDto();
        UserDto testUserDto = TestDataUtil.createTestUserDtoA(testRoleEntity.getId(), testDepartmentEntity.getId());
        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
        String token = "token";
        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        when(tokenService.generateJwt(mockAuth)).thenReturn(token);
        when(userRepository.findByEmail(testUserLoginRequestDto.getEmail())).thenReturn(Optional.of(testUserEntity));
        when(userMapper.mapToDto(testUserEntity)).thenReturn(testUserDto);

        UserLoginResponseDto userLoginResponseDto = authenticationService.loginUser(testUserLoginRequestDto);

        assertEquals(userLoginResponseDto.getUser(), testUserDto);
        assertEquals(userLoginResponseDto.getJwt(), token);
    }

    @Test
    public void testLoginUserWhenLoginFails() throws Exception {
        UserLoginRequestDto testUserLoginRequestDto = TestDataUtil.createTestUserLoginRequestDto();

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid Credentials"));

        assertEquals(null, authenticationService.loginUser(testUserLoginRequestDto));
    }

}
