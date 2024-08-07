package com.theodoremeras.dissertation.authentication;

import com.theodoremeras.dissertation.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLoginResponseDto {

    private UserDto userDto;

    private String jwt;

}
