package com.lessa.healthmonitoring.controller;


import com.lessa.healthmonitoring.domain.User;
import com.lessa.healthmonitoring.dto.UserDto;
import com.lessa.healthmonitoring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "API for user management")
public class UserController {

    private final UserService userService;

    @Operation( summary = "Create an user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User to be created")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid user supplied", content = @Content) })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        var userCreated = UserDto.fromDomain(userService.create(userDto.toDomain()));
        return ResponseEntity.ok(userCreated);
    }


    @Operation(summary = "Get all users with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page with users returned", useReturnTypeSchema = true,
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "400", description = "Invalid pageable supplied", content = @Content) })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDto>> getUsers(@ParameterObject @PageableDefault(size = 12) Pageable pageable) {
       var page = toPageDto(userService.getUsers(pageable));
       return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get an user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the user",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found with supplied id",
                    content = @Content) })
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(@Parameter(description = "id of user to be searched") @PathVariable Long id) {
        var user = userService.findById(id).map(UserDto::fromDomain);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation( summary = "Update an user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User to be updated")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid user supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found with supplied id", content = @Content) })
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto, @Parameter(description = "id of user to be updated") @PathVariable Long id) {
        var user = userService.update(id, userDto.toDomain()).map(UserDto::fromDomain);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @Operation(summary = "Delete an user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found with supplied id", content = @Content) })
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "id of user to be deleted") @PathVariable Long id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Page<UserDto> toPageDto(Page<User> pageDomain) {
        return pageDomain.map(UserDto::fromDomain);
    }
}
