package com.theodoremeras.dissertation.user;

import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private UserService userService;

    private RoleService roleService;

    private DepartmentService departmentService;

    private UserMapper userMapper;

    public UserController(
            UserService userService, RoleService roleService,
            DepartmentService departmentService, UserMapper userMapper
    ) {
        this.userService = userService;
        this.roleService = roleService;
        this.departmentService = departmentService;
        this.userMapper = userMapper;
    }

    @GetMapping(path = "/users")
    public List<UserDto> getAllUsers(
            @RequestParam(value = "ids", required = false) List<Integer> ids,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value="departmentId", required = false) Integer departmentId
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
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Integer id) {
        Optional<UserEntity> foundUser = userService.findOneById(id);

        return foundUser.map(userEntity -> {
            UserDto userDto = userMapper.mapToDto(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
