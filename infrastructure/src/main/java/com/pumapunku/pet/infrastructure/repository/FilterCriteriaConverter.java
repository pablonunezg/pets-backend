package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.filters.CompositeFilter;
import com.pumapunku.pet.domain.filters.ConditionFilter;
import com.pumapunku.pet.domain.filters.Filter;
import com.pumapunku.pet.domain.filters.NotFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;

/**
 * Convierte un {@link Filter} del dominio en un {@link Predicate} de JPA Criteria API.
 *
 * <p>Actúa como puente entre el modelo de filtros del dominio y la capa de
 * persistencia JPA, evitando que el dominio dependa de clases de Jakarta Persistence.
 * Soporta los tres tipos de filtro definidos en el dominio:</p>
 * <ul>
 *   <li>{@link ConditionFilter} — condición simple (campo, operador, valor).</li>
 *   <li>{@link CompositeFilter} — combinación AND u OR de varios filtros.</li>
 *   <li>{@link NotFilter}       — negación de un filtro.</li>
 * </ul>
 *
 * <p>También resuelve rutas anidadas usando la notación punto, por ejemplo
 * {@code "address.city"} se traduce en {@code root.get("address").get("city")}.</p>
 *
 * <p>Ejemplo de uso desde un repositorio personalizado:</p>
 * <pre>
 *   FilterCriteriaConverter converter = new FilterCriteriaConverter();
 *   Predicate predicate = converter.toPredicate(filter, root, cb);
 *   query.where(predicate);
 * </pre>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class FilterCriteriaConverter
{
    /**
     * Convierte el filtro de dominio en un {@link Predicate} JPA.
     *
     * <p>Utiliza pattern matching sobre el tipo concreto del filtro para
     * delegar en el método de conversión adecuado.</p>
     *
     * @param <T>    tipo de la entidad raíz de la consulta.
     * @param filter filtro de dominio a convertir; no debe ser {@code null}.
     * @param root   raíz de la consulta Criteria.
     * @param cb     constructor de predicados JPA.
     * @return predicado JPA equivalente al filtro recibido.
     */
    public <T> Predicate toPredicate(Filter filter, Root<T> root, CriteriaBuilder cb)
    {
        return switch (filter)
        {
            case ConditionFilter<?> c -> fromCondition(c, root, cb);
            case CompositeFilter comp  -> fromComposite(comp, root, cb);
            case NotFilter not         -> cb.not(toPredicate(not.filter(), root, cb));
        };
    }

    // ── Condición simple ─────────────────────────────────────────────────────

    /**
     * Construye un predicado a partir de una condición simple.
     *
     * @param <T>       tipo de la entidad raíz.
     * @param condition condición con campo, operador relacional y valor(es).
     * @param root      raíz de la consulta Criteria.
     * @param cb        constructor de predicados JPA.
     * @return predicado JPA que representa la condición.
     */
    private <T> Predicate fromCondition(ConditionFilter<?> condition,
                                        Root<T> root,
                                        CriteriaBuilder cb)
    {
        Path path  = resolvePath(root, condition.field());
        Object value = condition.value();

        return switch (condition.operator())
        {
            case EQ         -> cb.equal(path, value);
            case NEQ        -> cb.notEqual(path, value);
            case GT         -> cb.greaterThan(path, (Comparable) value);
            case GTE        -> cb.greaterThanOrEqualTo(path, (Comparable) value);
            case LT         -> cb.lessThan(path, (Comparable) value);
            case LTE        -> cb.lessThanOrEqualTo(path, (Comparable) value);
            case BETWEEN    -> cb.between(path,
                                    (Comparable) value,
                                    (Comparable) condition.secondValue());
            case IN         -> path.in((Collection<?>) value);
            case NOT_IN     -> cb.not(path.in((Collection<?>) value));
            case LIKE       -> cb.like(path, "%" + value + "%");
            case STARTS_WITH -> cb.like(path, value + "%");
            case ENDS_WITH  -> cb.like(path, "%" + value);
            case IS_NULL    -> cb.isNull(path);
            case IS_NOT_NULL -> cb.isNotNull(path);
        };
    }

    // ── Compuesto AND / OR ───────────────────────────────────────────────────

    /**
     * Construye un predicado compuesto (AND u OR) a partir de un {@link CompositeFilter}.
     *
     * @param <T>       tipo de la entidad raíz.
     * @param composite filtro compuesto con operador lógico y lista de sub-filtros.
     * @param root      raíz de la consulta Criteria.
     * @param cb        constructor de predicados JPA.
     * @return predicado JPA que representa la composición lógica.
     */
    private <T> Predicate fromComposite(CompositeFilter composite,
                                        Root<T> root,
                                        CriteriaBuilder cb)
    {
        List<Predicate> predicates = composite.filters().stream()
                .map(f -> toPredicate(f, root, cb))
                .toList();

        Predicate[] array = predicates.toArray(Predicate[]::new);

        return switch (composite.operator())
        {
            case AND -> cb.and(array);
            case OR  -> cb.or(array);
        };
    }

    // ── Soporte para campos anidados (ej: "address.city") ───────────────────

    /**
     * Resuelve una ruta de campo (simple o anidada con puntos) sobre la raíz de la consulta.
     *
     * <p>Por ejemplo, {@code "refuge.id"} se traduce en {@code root.get("refuge").get("id")}.</p>
     *
     * @param root  raíz de la consulta Criteria.
     * @param field nombre del campo, con posible notación punto para campos anidados.
     * @return {@link Path} que apunta al atributo indicado.
     */
    private Path<?> resolvePath(Root<?> root, String field)
    {
        String[] parts = field.split("\\.");
        Path<?> path = root;
        for (String part : parts)
        {
            path = path.get(part);
        }
        return path;
    }
}
