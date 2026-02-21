package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.UserDomain;
import com.pumapunku.pet.domain.filters.Filter;
import com.pumapunku.pet.infrastructure.mapper.UserMapper;
import com.pumapunku.pet.infrastructure.repository.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

/**
 * Implementación personalizada del repositorio de usuarios.
 *
 * <p>Extiende la funcionalidad estándar de Spring Data JPA con consultas
 * dinámicas basadas en el modelo de filtros del dominio. Utiliza la
 * JPA Criteria API para construir consultas tipadas en tiempo de ejecución,
 * apoyándose en {@link FilterCriteriaConverter} para traducir los filtros
 * de dominio a predicados JPA.</p>
 *
 * <p>Spring Data JPA detecta esta clase automáticamente como implementación
 * del fragmento {@link UserCustomRepository} gracias al sufijo {@code Impl}.</p>
 */
public class UserRepositoryImpl implements UserCustomRepository
{
    /** EntityManager JPA inyectado por el contenedor de persistencia. */
    @PersistenceContext
    private EntityManager em;

    /**
     * Conversor de filtros de dominio a predicados JPA.
     * Se instancia como singleton de clase dado que es stateless.
     */
    private static final FilterCriteriaConverter converter = new FilterCriteriaConverter();

    /**
     * Busca usuarios que cumplan con los criterios definidos en el filtro de dominio.
     *
     * <p>Construye una consulta Criteria tipada sobre la entidad {@link User},
     * aplica el predicado generado por {@link FilterCriteriaConverter#toPredicate}
     * y mapea los resultados al modelo de dominio {@link UserDomain}.</p>
     *
     * @param filter filtro de dominio que define los criterios de búsqueda; no debe ser {@code null}.
     * @return lista de {@link UserDomain} que satisfacen el filtro; puede estar vacía.
     */
    @Override
    public List<UserDomain> findByFilter(Filter filter)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);

        Predicate predicate = converter.toPredicate(filter, root, cb);
        query.where(predicate);

        return em.createQuery(query)
                .getResultList()
                .stream()
                .map(UserMapper.INSTANCE::toUserDomain)
                .toList();
    }
}
