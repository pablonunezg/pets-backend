package com.pumapunku.pet.presentation.security;

import com.pumapunku.pet.domain.Role;
import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.repository.UserDomainRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the presentation layer security components.
 */
@DisplayName("Componentes de seguridad")
class SecurityComponentsTest
{
    // ── User (UserDetails) ───────────────────────────────────────────────

    @Nested
    @DisplayName("User (UserDetails)")
    class UserTest
    {
        @Test
        @DisplayName("getAuthorities() debe retornar autoridad con prefijo ROLE_")
        void getAuthorities_retornaRolConPrefijo()
        {
            User user = User.builder().username("alice").password("pwd").role("ADMIN").build();

            Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

            assertEquals(1, authorities.size());
            assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority());
        }

        @Test
        @DisplayName("todos los flags de cuenta deben retornar true cuando locked=false")
        void flagsCuenta_lockedFalse_retornanTrue()
        {
            User user = User.builder().username("alice").password("pwd").role("ADMIN")
                    .locked(false).build();
            assertTrue(user.isAccountNonExpired());
            assertTrue(user.isAccountNonLocked());
            assertTrue(user.isCredentialsNonExpired());
            assertTrue(user.isEnabled());
        }

        @Test
        @DisplayName("isAccountNonLocked() should return false when account is locked")
        void isAccountNonLocked_lockedTrue_retornaFalse()
        {
            User user = User.builder().username("alice").password("pwd").role("ADMIN")
                    .locked(true).build();

            assertFalse(user.isAccountNonLocked());
        }

        @Test
        @DisplayName("isAccountNonLocked() should return true when account is NOT locked")
        void isAccountNonLocked_lockedFalse_retornaTrue()
        {
            User user = User.builder().username("alice").password("pwd").role("ADMIN")
                    .locked(false).build();

            assertTrue(user.isAccountNonLocked());
        }

        @Test
        @DisplayName("getUsername() debe retornar el nombre de usuario")
        void getUsername_retornaUsername()
        {
            User user = User.builder().username("bob").password("pwd").role("NORMAL_USER").build();
            assertEquals("bob", user.getUsername());
        }

        @Test
        @DisplayName("builder sin argumentos debe funcionar con NoArgsConstructor")
        void noArgsConstructor_creaObjetoVacio()
        {
            User user = new User();
            assertNull(user.getUsername());
            assertNull(user.getRole());
            assertFalse(user.isLocked());
        }
    }

    // ── AuthRequest ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("AuthRequest")
    class AuthRequestTest
    {
        @Test
        @DisplayName("debe almacenar username y password")
        void record_almacenaCredenciales()
        {
            AuthRequest req = new AuthRequest("alice", "secret123");
            assertEquals("alice", req.username());
            assertEquals("secret123", req.password());
        }

        @Test
        @DisplayName("dos registros con mismos valores deben ser iguales")
        void equals_mismosValores_sonIguales()
        {
            assertEquals(new AuthRequest("u", "p"), new AuthRequest("u", "p"));
        }
    }

    // ── PasswordEncoderConfig ────────────────────────────────────────────

    @Nested
    @DisplayName("PasswordEncoderConfig")
    class PasswordEncoderConfigTest
    {
        @Test
        @DisplayName("passwordEncoder() debe retornar una instancia de BCryptPasswordEncoder")
        void passwordEncoder_retornaBCrypt()
        {
            PasswordEncoderConfig config = new PasswordEncoderConfig();
            PasswordEncoder encoder = config.passwordEncoder();

            assertNotNull(encoder);
            assertInstanceOf(BCryptPasswordEncoder.class, encoder);
        }

        @Test
        @DisplayName("BCryptPasswordEncoder should be able to encode and verify passwords")
        void encoder_codificaYVerifica()
        {
            PasswordEncoderConfig config = new PasswordEncoderConfig();
            PasswordEncoder encoder = config.passwordEncoder();

            String raw = "admin123";
            String encoded = encoder.encode(raw);

            assertNotEquals(raw, encoded);
            assertTrue(encoder.matches(raw, encoded));
        }
    }

    // ── UserDetailsServiceImpl ────────────────────────────────────────────

    @Nested
    @DisplayName("UserDetailsServiceImpl")
    @ExtendWith(MockitoExtension.class)
    class UserDetailsServiceImplTest
    {
        @Mock
        private UserDomainRepository userDomainRepository;

        @InjectMocks
        private UserDetailsServiceImpl service;

        @Test
        @DisplayName("loadUserByUsername() debe retornar UserDetails cuando el usuario existe")
        void loadUserByUsername_usuarioExiste_retornaUserDetails()
        {
            UserDomain domain = new UserDomain(UUID.randomUUID(), "alice", "hashed", Role.ADMIN, false);
            when(userDomainRepository.findByUserName("alice")).thenReturn(Optional.of(domain));
            when(userDomainRepository.findByFilter(any())).thenReturn(java.util.List.of(domain));

            var result = service.loadUserByUsername("alice");

            assertNotNull(result);
            assertEquals("alice", result.getUsername());
        }

        @Test
        @DisplayName("loadUserByUsername() debe propagar locked=true al User de seguridad")
        void loadUserByUsername_usuarioBloqueado_propagaLocked()
        {
            UserDomain domain = new UserDomain(UUID.randomUUID(), "alice", "hashed", Role.ADMIN, true);
            when(userDomainRepository.findByUserName("alice")).thenReturn(Optional.of(domain));
            when(userDomainRepository.findByFilter(any())).thenReturn(java.util.List.of(domain));

            var result = (User) service.loadUserByUsername("alice");

            assertTrue(result.isLocked());
            assertFalse(result.isAccountNonLocked());
        }

        @Test
        @DisplayName("loadUserByUsername() debe lanzar UsernameNotFoundException si no existe el usuario")
        void loadUserByUsername_usuarioNoExiste_lanzaExcepcion()
        {
            when(userDomainRepository.findByUserName("ghost")).thenReturn(Optional.empty());
            when(userDomainRepository.findByFilter(any())).thenReturn(java.util.List.of());

            assertThrows(UsernameNotFoundException.class,
                    () -> service.loadUserByUsername("ghost"));
        }
    }
}
