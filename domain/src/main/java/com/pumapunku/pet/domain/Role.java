package com.pumapunku.pet.domain;

/**
 * Enumeración de roles disponibles para los usuarios del sistema.
 *
 * <p>Define los perfiles de acceso que controlan las operaciones
 * que cada usuario puede realizar en la aplicación.</p>
 */
public enum Role
{
    /** Rol con permisos completos de administración del sistema. */
    ADMIN,

    /** Rol con permisos básicos de usuario regular. */
    USER
}
