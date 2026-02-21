package com.pumapunku.pet.presentation.security;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.filters.Filter;
import com.pumapunku.pet.domain.filters.FilterBuilder;
import com.pumapunku.pet.domain.repository.UserDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link UserDetailsService} que carga los detalles del usuario
 * desde el repositorio de dominio.
 *
 * <p>Spring Security llama a este servicio durante el proceso de autenticación para
 * obtener un objeto {@link UserDetails} a partir del nombre de usuario proporcionado.</p>
 *
 * <p>Utiliza {@link UserDomainRepository} como puerta de entrada al dominio,
 * respetando así la arquitectura hexagonal y evitando dependencias directas
 * con la capa de infraestructura.</p>
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService
{
    /** Repositorio de dominio para buscar usuarios por nombre de usuario. */
    private final UserDomainRepository userDomainRepository;

    /**
     * Carga los detalles de seguridad del usuario identificado por {@code username}.
     *
     * @param username nombre de usuario a buscar.
     * @return objeto {@link UserDetails} listo para el proceso de autenticación.
     * @throws UsernameNotFoundException si no existe ningún usuario con ese nombre.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        // Ejemplo de uso del FilterBuilder para búsquedas dinámicas
        Filter filter = FilterBuilder.AND()
                .eq("username", username)
                .build();

        List<UserDomain> users = userDomainRepository.findByFilter(filter);

        Optional<User> ui = userDomainRepository
                .findByUserName(username)
                .map(u -> new User(u.getId(), u.getUsername(), u.getPassword(), u.getRole().toString()));

        return ui.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
