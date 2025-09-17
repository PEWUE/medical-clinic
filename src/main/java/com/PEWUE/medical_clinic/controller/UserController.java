package com.PEWUE.medical_clinic.controller;

import com.PEWUE.medical_clinic.command.ChangePasswordCommand;
import com.PEWUE.medical_clinic.command.UserCreateCommand;
import com.PEWUE.medical_clinic.dto.ErrorMessageDto;
import com.PEWUE.medical_clinic.dto.PageDto;
import com.PEWUE.medical_clinic.dto.UserDto;
import com.PEWUE.medical_clinic.mapper.PageMapper;
import com.PEWUE.medical_clinic.mapper.UserMapper;
import com.PEWUE.medical_clinic.model.User;
import com.PEWUE.medical_clinic.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "Users operations")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final PageMapper pageMapper;

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of users returned",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class)))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping
    public PageDto<UserDto> find(@ParameterObject Pageable pageable) {
        Page<User> page = userService.find(pageable);
        return pageMapper.toDto(page, userMapper::toDto);
    }

    @Operation(summary = "Add user to collection")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fields should not be null",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Given email or username already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@RequestBody UserCreateCommand userCreateCommand) {
        User user = userMapper.toEntity(userCreateCommand);
        return userMapper.toDto(userService.add(user));
    }

    @Operation(summary = "Change user password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fields should not be null",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping("/{id}")
    public UserDto changePassword(@PathVariable Long id, @RequestBody ChangePasswordCommand command) {
        return userMapper.toDto(userService.changePassword(id, command.password()));
    }
}
