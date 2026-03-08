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
 * Converts a domain {@link Filter} into a JPA Criteria API {@link Predicate}.
 *
 * <p>Acts as a bridge between the domain filter model and the JPA persistence layer,
 * preventing the domain from depending on Jakarta Persistence classes.
 * Supports the three filter types defined in the domain:</p>
 * <ul>
 *   <li>{@link ConditionFilter} — simple condition (field, operator, value).</li>
 *   <li>{@link CompositeFilter} — AND or OR combination of multiple filters.</li>
 *   <li>{@link NotFilter}       — negation of a filter.</li>
 * </ul>
 *
 * <p>Also resolves nested paths using dot notation, for example
 * {@code "address.city"} is translated to {@code root.get("address").get("city")}.</p>
 *
 * <p>Usage example from a custom repository:</p>
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
     * Converts the domain filter into a JPA {@link Predicate}.
     *
     * <p>Uses pattern matching on the concrete type of the filter to
     * delegate to the appropriate conversion method.</p>
     *
     * @param <T>    type of the query root entity.
     * @param filter domain filter to convert; must not be {@code null}.
     * @param root   Criteria query root.
     * @param cb     JPA predicate builder.
     * @return JPA predicate equivalent to the received filter.
     */
    public <T> Predicate toPredicate(Filter filter, Root<T> root, CriteriaBuilder cb)
    {
        return switch (filter)
        {
            case ConditionFilter<?> c -> fromCondition(c, root, cb);
            case CompositeFilter comp -> fromComposite(comp, root, cb);
            case NotFilter not -> cb.not(toPredicate(not.filter(), root, cb));
        };
    }

    // ── Simple condition ─────────────────────────────────────────────────────

    /**
     * Builds a predicate from a simple condition.
     *
     * @param <T>       type of the root entity.
     * @param condition condition with field, relational operator, and value(s).
     * @param root      Criteria query root.
     * @param cb        JPA predicate builder.
     * @return JPA predicate representing the condition.
     */
    private <T> Predicate fromCondition(ConditionFilter<?> condition,
                                        Root<T> root,
                                        CriteriaBuilder cb)
    {
        Path path = resolvePath(root, condition.field());
        Object value = condition.value();

        return switch (condition.operator())
        {
            case EQ -> cb.equal(path, value);
            case NEQ -> cb.notEqual(path, value);
            case GT -> cb.greaterThan(path, (Comparable) value);
            case GTE -> cb.greaterThanOrEqualTo(path, (Comparable) value);
            case LT -> cb.lessThan(path, (Comparable) value);
            case LTE -> cb.lessThanOrEqualTo(path, (Comparable) value);
            case BETWEEN -> cb.between(path,
                    (Comparable) value,
                    (Comparable) condition.secondValue());
            case IN -> path.in((Collection<?>) value);
            case NOT_IN -> cb.not(path.in((Collection<?>) value));
            case LIKE -> cb.like(path, "%" + value + "%");
            case STARTS_WITH -> cb.like(path, value + "%");
            case ENDS_WITH -> cb.like(path, "%" + value);
            case IS_NULL -> cb.isNull(path);
            case IS_NOT_NULL -> cb.isNotNull(path);
        };
    }

    // ── Composite AND / OR ───────────────────────────────────────────────────

    /**
     * Builds a composite predicate (AND or OR) from a {@link CompositeFilter}.
     *
     * @param <T>       type of the root entity.
     * @param composite composite filter with logical operator and list of sub-filters.
     * @param root      Criteria query root.
     * @param cb        JPA predicate builder.
     * @return JPA predicate representing the logical composition.
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
            case OR -> cb.or(array);
        };
    }

    // ── Nested field support (e.g. "address.city") ──────────────────────────

    /**
     * Resolves a field path (simple or dot-nested) against the query root.
     *
     * <p>For example, {@code "refuge.id"} is translated to {@code root.get("refuge").get("id")}.</p>
     *
     * @param root  Criteria query root.
     * @param field field name, with optional dot notation for nested fields.
     * @return {@link Path} pointing to the indicated attribute.
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
