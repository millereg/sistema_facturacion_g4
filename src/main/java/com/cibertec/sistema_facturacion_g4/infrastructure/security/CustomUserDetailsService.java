package com.cibertec.sistema_facturacion_g4.infrastructure.security;

import com.cibertec.sistema_facturacion_g4.domain.entities.User;
import com.cibertec.sistema_facturacion_g4.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

                return new UserPrincipal(
                                user.getId(),
                                user.getUsername(),
                                user.getPassword(),
                                user.getRole() != null ? user.getRole().name() : "USER",
                                user.getCompanyId(),
                                user.getActive(),
                                user.getFirstName(),
                                user.getLastName());
        }

        public UserPrincipal loadUserById(Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "Usuario no encontrado con ID: " + id));

                return new UserPrincipal(
                                user.getId(),
                                user.getUsername(),
                                user.getPassword(),
                                user.getRole() != null ? user.getRole().name() : "USER",
                                user.getCompanyId(),
                                user.getActive(),
                                user.getFirstName(),
                                user.getLastName());
        }
}
