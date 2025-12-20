package com.cibertec.sistema_facturacion_g4.application.usecases;

import com.cibertec.sistema_facturacion_g4.application.dto.UserDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.auth.LoginRequest;
import com.cibertec.sistema_facturacion_g4.application.dto.auth.LoginResponse;
import com.cibertec.sistema_facturacion_g4.application.ports.UserService;
import com.cibertec.sistema_facturacion_g4.application.ports.PermissionService;
import com.cibertec.sistema_facturacion_g4.application.mapper.UserMapper;
import com.cibertec.sistema_facturacion_g4.domain.entities.Company;
import com.cibertec.sistema_facturacion_g4.domain.repositories.CompanyRepository;
import com.cibertec.sistema_facturacion_g4.domain.repositories.UserRepository;
import com.cibertec.sistema_facturacion_g4.domain.entities.User;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.JwtUtil;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.UserPrincipal;
import com.cibertec.sistema_facturacion_g4.infrastructure.security.SecurityUtils;
import com.cibertec.sistema_facturacion_g4.shared.constants.UserRoles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userPrincipal);
        String companyName = null;
        if (userPrincipal.getCompanyId() != null) {
            companyName = companyRepository.findById(userPrincipal.getCompanyId())
                .map(Company::getBusinessName)
                .orElse(null);
        }

        List<String> permissions = permissionService.getUserPermissions(userPrincipal.getRole());

        return LoginResponse.builder()
            .token(token)
            .userId(userPrincipal.getId())
            .username(userPrincipal.getUsername())
            .role(userPrincipal.getRole())
            .companyId(userPrincipal.getCompanyId())
            .companyName(companyName)
            .firstName(userPrincipal.getFirstName())
            .lastName(userPrincipal.getLastName())
            .permissions(permissions)
            .build();
    }

    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) {
        User user;
        
        if (userDTO.getId() != null) {
            user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            if (userDTO.getFirstName() != null) user.setFirstName(userDTO.getFirstName());
            if (userDTO.getLastName() != null) user.setLastName(userDTO.getLastName());
            if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
            if (userDTO.getActive() != null) user.setActive(userDTO.getActive());
            
            if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
        } else {
            user = userMapper.toEntity(userDTO);
            if (userDTO.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            if (user.getCompanyId() == null && userDTO.getCompanyId() != null) {
                user.setCompanyId(userDTO.getCompanyId());
            }
        }
        
        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Override
    public Optional<UserDTO> findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal currentUser = (UserPrincipal) authentication.getPrincipal();
            
            if (currentUser.getId().equals(id) || "ADMIN".equals(currentUser.getRole())) {
                return userRepository.findById(id).map(userMapper::toDTO);
            }
            
            throw new RuntimeException("No tienes permisos para realizar esta acción");
        }
        
        throw new RuntimeException("Usuario no autenticado");
    }

    @Override
    public List<UserDTO> findAll() {
        String currentRole = SecurityUtils.getCurrentUserRole();
        Long currentCompanyId = SecurityUtils.getCurrentCompanyId();
        
        if ("ADMIN".equals(currentRole)) {
            return userRepository.findAll().stream()
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
        }
        
        if (currentCompanyId != null) {
            List<UserDTO> result = userRepository.findAll().stream()
                    .filter(user -> user.getCompanyId().equals(currentCompanyId))
                    .filter(user -> user.getRole() != UserRoles.ADMIN)
                    .map(userMapper::toDTO)
                    .collect(Collectors.toList());
            
            return result;
        }
        
        return List.of();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
