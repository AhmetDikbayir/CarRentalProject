package com.tpe.service;

import com.tpe.domain.User;
import com.tpe.enums.RoleType;
import com.tpe.exceptions.BadRequestException;
import com.tpe.exceptions.ResourceNotFoundException;
import com.tpe.payload.mappers.UserMapper;
import com.tpe.payload.messages.ErrorMessages;
import com.tpe.payload.messages.SuccessMessages;
import com.tpe.payload.request.*;
import com.tpe.payload.response.UserResponse;
import com.tpe.repository.UserRepository;
import com.tpe.repository.UserRoleRepository;
import com.tpe.service.helper.UserMethodHelper;
import com.tpe.service.helper.PageableHelper;
import com.tpe.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
/*
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
*/
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
  //  public final JwtUtils jwtUtils;
  //  public final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final PageableHelper pageableHelper;
    private final UserRoleService userRoleService;
    private final UserMethodHelper methodHelper;

/*
    public ResponseEntity<SigninResponse> authenticateUser(SigninRequest signInRequest) {
        String email = signInRequest.getEmail();
        String password = signInRequest.getPassword();

        Authentication authentication = authenticationManager.
                authenticate(new UsernamePasswordAuthenticationToken(email, password));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // GrantedAuthority turundeki role yapisini String turune ceviriliyor
        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());


        // AuthResponse nesnesi olusturuluyor ve gerekli alanlar setleniyor
        SigninResponse signinResponse = SigninResponse.builder()
                .email(userDetails.getEmail())
                .token(token.substring(7))
                .name(userDetails.getName())
                .roles(roles)
                .build();

        // SigninResponse nesnesi ResponseEntity ile donduruluyor
        return ResponseEntity.ok(signinResponse);
    }
*/
    public ResponseEntity<UserResponse> register(UserRequestForRegister userRequestForRegister){

        //!!! username - ssn- phoneNumber unique mi kontrolu ??
        uniquePropertyValidator.checkDuplicate(userRequestForRegister.getEmail(),
                userRequestForRegister.getPhone());
        //!!! DTO --> POJO
        User user = userMapper.mapUserRequestToUser(userRequestForRegister);
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

        user.setBuiltIn(false);
        // !!! Rol bilgisi setleniyor
        user.getRoles().add(userRoleService.getUserRole(RoleType.CUSTOMER));

        // !!! password encode ediliyor
        user.setPassword(passwordEncoder.encode(userRequestForRegister.getPassword()));

        user.setCreateDate(LocalDateTime.now()); // Automatically set on create

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(savedUser));
    }

    public ResponseEntity<UserResponse> getAuthenticatedUser(HttpServletRequest httpServletRequest) {

        String email = (String) httpServletRequest.getAttribute("username");

        User foundUser = userRepository.findByEmail(email);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(foundUser));
    }

    /*public ResponseEntity<Page<LoanResponse>> getAllLoansByUserByPage(HttpServletRequest httpServletRequest,
                                                                      int page, int size,
                                                                      String sort, String type) {

        String email = (String) httpServletRequest.getAttribute("username");

        User foundUser = userRepository.findByEmail(email);

        return loanService.getAllLoansByUserIdByPage(foundUser.getId(), page, size, sort, type);
    }*/

    public ResponseEntity<Page<UserResponse>> getAllUsersByPage(int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return ResponseEntity.ok(userRepository.findAll(pageable).map(userMapper::mapUserToUserResponse));
    }

    @Transactional      //todo sorulacak
    public ResponseEntity<UserResponse> getUserById(Long userId) {

        User user = methodHelper.isUserExist(userId);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(user));
    }

    public ResponseEntity<UserResponse> deleteUserById(Long userId){
        User user = methodHelper.isUserExist(userId);

        methodHelper.checkBuiltIn(user);

      /*  if (!user.getLoanList().isEmpty()) {
            throw new BadRequestException(ErrorMessages.USER_HAS_LOAN);
        }
      */
        if (!user.getReservationList().isEmpty()) {
            throw new BadRequestException(ErrorMessages.RESERVATION_NOT_EMPTY);
        }
        userRepository.delete(user);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(user));   //Aslında no content 204 kodu
        // döndürmek daha mantıklı olabilir
    }

    public ResponseEntity<UserResponse> createUser(UserRequestForCreateOrUpdate userRequestForCreateOrUpdate, HttpServletRequest httpServletRequest, String userRole){

        String email = (String) httpServletRequest.getAttribute("username");

        User foundUser = userRepository.findByEmail(email);

        //!!! email - phoneNumber unique mi kontrolu ??
        uniquePropertyValidator.checkDuplicate(userRequestForCreateOrUpdate.getEmail(),
                userRequestForCreateOrUpdate.getPhone());
        //!!! DTO --> POJO
        User userToCreate = userMapper.mapUserRequestToUser(userRequestForCreateOrUpdate);

        if (userToCreate.getRoles() == null) {
            userToCreate.setRoles(new ArrayList<>());
        }

        setRoleForNewUser(foundUser, userToCreate, userRole);

        userToCreate.setCreateDate(LocalDateTime.now());

        // !!! password encode ediliyor
        userToCreate.setPassword(passwordEncoder.encode(userRequestForCreateOrUpdate.getPassword()));

        User savedUser = userRepository.save(userToCreate);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(savedUser));
    }

    //create user methodunda role bilgisi setlemek için yazıldı, yardımcı
    private void setRoleForNewUser(User foundUser, User userToCreate, String userRole) {
        if (foundUser.getRoles().contains(userRoleService.getUserRole(RoleType.ADMIN))) {
            switch (userRole.toLowerCase()) {
                case "customer":
                    userToCreate.getRoles().add(userRoleService.getUserRole(RoleType.CUSTOMER));
                    break;
                case "admin":
                    userToCreate.getRoles().add(userRoleService.getUserRole(RoleType.ADMIN));
                    break;
                default:
                    throw new ResourceNotFoundException((ErrorMessages.ROLE_NOT_FOUND));
            }
        } else if (foundUser.getRoles().contains(userRoleService.getUserRole(RoleType.CUSTOMER))) {
            if (userRole.equalsIgnoreCase("Customer")) {
                userToCreate.getRoles().add(userRoleService.getUserRole(RoleType.CUSTOMER));
            } else {
                throw new BadRequestException(ErrorMessages.DONT_HAVE_AUTHORITY);
            }
        }
    }

    public ResponseEntity<UserResponse> updateUser(
            UserRequestForCreateOrUpdate userRequestForCreateOrUpdate,
            Long userId,
            HttpServletRequest httpServletRequest){
        String email = (String) httpServletRequest.getAttribute("username");
        // işlemi yapan user
        User foundUser = userRepository.findByEmail(email);

        // güncellenecek user
        User userToUpdate = methodHelper.isUserExist(userId);

        // Role based update permission ve built-in kontrolü
        checkUpdatePermission(foundUser, userToUpdate);

        // email - phoneNumber unique mi kontrolü
        uniquePropertyValidator.checkUniqueProperties(userToUpdate, userRequestForCreateOrUpdate);

        User updatedUser = userMapper.mapUserRequestToUpdatedUser(userRequestForCreateOrUpdate, userId);

        updatedUser.setPassword(passwordEncoder.encode(userRequestForCreateOrUpdate.getPassword()));
        updatedUser.setRoles(userToUpdate.getRoles());
        updatedUser.setCreateDate(foundUser.getCreateDate());

        User savedUser = userRepository.save(updatedUser);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(savedUser));
    }

    //updateUser için yazıldı controller bağlantısı yok , yardımcı method
    private void checkUpdatePermission(User foundUser, User userToUpdate) {
        methodHelper.checkBuiltIn(userToUpdate);

        if (foundUser.getRoles().contains(userRoleService.getUserRole(RoleType.ADMIN))) {
            if (userToUpdate.getBuiltIn()) {
                throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
            }
        } else if (foundUser.getRoles().contains(userRoleService.getUserRole(RoleType.CUSTOMER))) {
            if (userToUpdate.getRoles().contains(userRoleService.getUserRole(RoleType.CUSTOMER)) ||
                    userToUpdate.getRoles().contains(userRoleService.getUserRole(RoleType.ADMIN))) {
                throw new BadRequestException(ErrorMessages.CUSTOMER_CAN_ONLY_UPDATE_OWN_MESSAGE);
            }
        } else {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    // public long countAllAdmins() { return userRepository.countAdmin(RoleType.ADMIN);  }

    public ResponseEntity<UserResponse> saveUser(UserRequest adminRequest, String userRole) {
        //!!! email ve phoneNumber ile unique mi kontrolu yapıldı...
        uniquePropertyValidator.checkDuplicate(adminRequest.getPhone(), adminRequest.getEmail());
        //!!! DTO --> POJO
        User user = userMapper.mapUserRequestForAdminToUser(adminRequest);
        // Initialize roles list if null
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        // !!! Rol bilgisi setleniyor
        if (userRole.equalsIgnoreCase(RoleType.ADMIN.name())) {
            if (Objects.equals(adminRequest.getEmail(), "admin@admin.com")) {
                user.setBuiltIn(true);
            }
            user.getRoles().add(userRoleService.getUserRole(RoleType.ADMIN));
        }
        // !!! password encode ediliyor
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setCreateDate(LocalDateTime.now()); // Automatically set on create

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(userMapper.mapUserToUserResponse(savedUser));
    }
    public long countMembers(RoleType roleType) {return userRepository.countByRoleType(roleType.getName()); }

    public ResponseEntity<String> updateUserPassword(UserRequestForUpdatePassword userRequestForUpdatePassword, Long userId, HttpServletRequest httpServletRequest) {

        String email = (String) httpServletRequest.getAttribute("username");
        // işlemi yapan user
        User foundUser = userRepository.findByEmail(email);

        // güncellenecek user
        User userToUpdate = methodHelper.isUserExist(userId);

        // Role based update permission ve built-in kontrolü
        checkUpdatePermission(foundUser, userToUpdate);

        User updatedUser = userMapper.mapUserRequestToUserUpdatedPassword(userRequestForUpdatePassword, userId);

        updatedUser.setPassword(passwordEncoder.encode(userRequestForUpdatePassword.getPassword()));

        User savedUser = userRepository.save(updatedUser);

        return ResponseEntity.ok(SuccessMessages.PASSWORD_UPDATED);
    }
}
