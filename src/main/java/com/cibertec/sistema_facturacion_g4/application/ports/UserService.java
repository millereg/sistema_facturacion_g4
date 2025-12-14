package com.cibertec.sistema_facturacion_g4.application.ports;

import com.cibertec.sistema_facturacion_g4.application.dto.UserDTO;
import com.cibertec.sistema_facturacion_g4.application.dto.auth.LoginRequest;
import com.cibertec.sistema_facturacion_g4.application.dto.auth.LoginResponse;
import java.util.List;
import java.util.Optional;

public interface UserService {
    LoginResponse login(LoginRequest request);
    
    UserDTO save(UserDTO user);

    Optional<UserDTO> findById(Long id);

    List<UserDTO> findAll();

    void deleteById(Long id);
}
