package com.lessa.healthmonitoring.controller;


import com.lessa.healthmonitoring.domain.User;
import com.lessa.healthmonitoring.dto.UserDto;
import com.lessa.healthmonitoring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        var userCreated = UserDto.fromDomain(userService.create(userDto.toDomain()));
        return ResponseEntity.ok(userCreated);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<UserDto>> getUsers(@PageableDefault(size = 12) Pageable pageable) {
       var page = toPageDto(userService.getUsers(pageable));
       return ResponseEntity.ok(page);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var user = userService.findById(id).map(UserDto::fromDomain);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDto> update(@RequestBody UserDto userDto, @PathVariable Long id) {
        var user = userService.update(id, userDto.toDomain()).map(UserDto::fromDomain);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Page<UserDto> toPageDto(Page<User> pageDomain) {
        return pageDomain.map(UserDto::fromDomain);
    }
}
