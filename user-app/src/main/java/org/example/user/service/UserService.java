package org.example.user.service;

import lombok.RequiredArgsConstructor;
import org.example.common.dto.UserDto;
import org.example.common.enums.RoleEnum;
import org.example.common.security.JwtUserData;
import org.example.common.security.SecurityConfig;
import org.example.user.model.dto.TokenDto;
import org.example.user.model.entity.UserEntity;
import org.example.common.exceptions.*;
import org.example.user.model.requestBody.UserCreateRequestBody;
import org.example.user.model.requestBody.UserCredentialsRequestBody;
import org.example.user.model.requestBody.UserUpdateRequestBody;
import org.example.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final SecurityConfig securityConfig;

    public TokenDto register(UserCreateRequestBody userRequestBody) throws UserAlreadyExistException, UnauthorizedException, BadRequestException {
        if (userRepository.findByLogin(userRequestBody.getLogin()) != null) {
            throw new UserAlreadyExistException("There is an account with that login: " + userRequestBody.getLogin());
        }

        UserEntity userEntity = userRepository.save(new UserEntity(
                UUID.randomUUID(),
                userRequestBody.getLogin(),
                userRequestBody.getName(),
                securityConfig.passwordEncoder().encode(userRequestBody.getPassword()),
                Instant.now(),
                RoleEnum.USER
        ));

        return login(new UserCredentialsRequestBody(userRequestBody.getLogin(), userRequestBody.getPassword()));
    }

    public TokenDto login(UserCredentialsRequestBody user) throws UserAlreadyExistException, BadRequestException, UnauthorizedException {
        UserEntity userEntity = userRepository.findByLogin(user.getLogin());
        if(userEntity == null || !securityConfig.passwordEncoder().matches(user.getPassword(), userEntity.getPassword())) {
            throw new BadRequestException("Bad login or password");
        }

        String token = authenticationService.generateJwt(userEntity);

        return new TokenDto(token);
    }

    public Void updateUser(UUID userId, UserUpdateRequestBody userUpdateRequestBody, Authentication authentication) throws UserNotFoundException, ForbiddenException {
        UserEntity userEntity = findUserEntity(authentication);

        if (userEntity.getId() != userId) {
            throw new ForbiddenException("You can't change profile with user id: " + userId);
        }

        if(userUpdateRequestBody.getLogin() != null) {
            userEntity.setLogin(userUpdateRequestBody.getLogin());
        }
        if(userUpdateRequestBody.getName() != null) {
            userEntity.setName(userUpdateRequestBody.getName());
        }
        if(userUpdateRequestBody.getPassword() != null) {
            userEntity.setPassword(securityConfig.passwordEncoder().encode(userUpdateRequestBody.getPassword()));
        }

        userRepository.save(userEntity);
        return null;
    }

    public Void deleteUser(UUID userId, Authentication authentication) throws UserNotFoundException, ForbiddenException {
        UserEntity userEntity = findUserEntity(authentication);

        if(!userEntity.getId().equals(userId)) {
            throw new ForbiddenException("You can't delete user with id: " + userId + " " + userEntity.getId());
        }

        userRepository.delete(userEntity);

        return null;
    }

    public Void logout(Authentication authentication) throws UserNotFoundException, ForbiddenException {
        //TODO
        return null;
    }

    public UserDto getProfile(UUID userId) throws UserNotFoundException, ForbiddenException {
        var userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            throw new UserNotFoundException("user not found with id: " + userId);
        }
        return new UserDto(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getName(),
                userEntity.getRole()
        );
    }

    private UserEntity findUserEntity(Authentication authentication) throws UserNotFoundException {
        var userData = (JwtUserData) authentication.getPrincipal();
        var optionalUserEntity = userRepository.findById(userData.getId());
        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException("user not found with id: " + userData.getId());
        }
        return optionalUserEntity.get();
    }

    public Boolean isUserExists(UUID id) {
        return userRepository.existsById(id);
    }
}
