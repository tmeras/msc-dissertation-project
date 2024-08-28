package com.theodoremeras.dissertation.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    private final JwtDecoder jwtDecoder;

    private final JavaMailSender emailSender;

    public UserController(
            UserService userService, UserMapper userMapper,
            JwtDecoder jwtDecoder, JavaMailSender emailSender
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtDecoder = jwtDecoder;
        this.emailSender = emailSender;
    }

    @GetMapping(path = "/users")
    public List<UserDto> getAllUsers(
            @RequestParam(value = "ids", required = false) List<Integer> ids,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "departmentId", required = false) Integer departmentId
    ) {
        List<UserEntity> userEntities;

        // Fetch all users whose id is in the provided list
        if (ids != null)
            userEntities = userService.findAllByIdIn(ids);

        // Fetch the user who has the specified email
        else if (email != null)
            userEntities = userService.findAllByEmail(email);

        // Fetch all users that have the specified role and department id
        else if (roleId != null && departmentId != null)
            userEntities = userService.findAllByDepartmentIdAndRoleId(departmentId, roleId);

        // Otherwise, fetch all users
        else
            userEntities = userService.findAll();

        return userEntities.stream()
                .map(userMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/users/{id}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable("id") Integer id,
            @RequestHeader(name = "Authorization") String token
    ) {
        // Extract the user's id and role from the token
        Jwt jwt = jwtDecoder.decode(token.split(" ")[1]);
        Long userId = jwt.getClaim("userId");
        String userRole = jwt.getClaim("roles");

        Optional<UserEntity> foundUser = userService.findOneById(id);
        if (foundUser.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Students are only allowed to view their own information
        if (userRole.equals("Student") && userId.intValue() != foundUser.get().getId())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        UserDto userDto = userMapper.mapToDto(foundUser.get());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(path = "/users/{id}/mail")
    public ResponseEntity<String> emailUser(
            @PathVariable("id") Integer id,
            @Valid @RequestBody EmailDto emailDto
    ) {
        Optional<UserEntity> foundUser = userService.findOneById(id);

        if (foundUser.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        // Send email to the specified user
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("ecfportal@gmail.com");
        message.setTo(foundUser.get().getEmail());
        message.setSubject(emailDto.getSubject());
        message.setText(emailDto.getBody());
        emailSender.send(message);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping(path = "/users/{id}")
    public ResponseEntity<UserDto> partialUpdateUser(
            @PathVariable("id") Integer id, @RequestBody UserDto userDto
    ) {
        if (!userService.exists(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        userDto.setId(id);
        UserEntity userEntity = userMapper.mapFromDto(userDto);
        UserEntity updatedUserEntity = userService.partialUpdate(id, userEntity);
        return new ResponseEntity<>(userMapper.mapToDto(updatedUserEntity), HttpStatus.OK);
    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Integer id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
