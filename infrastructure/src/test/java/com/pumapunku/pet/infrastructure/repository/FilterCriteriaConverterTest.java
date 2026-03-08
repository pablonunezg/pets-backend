package com.pumapunku.pet.infrastructure.repository;

import com.pumapunku.pet.domain.filters.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FilterCriteriaConverter}.
 * All JPA Criteria API is mocked to avoid requiring a persistence context.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FilterCriteriaConverter")
@SuppressWarnings({"unchecked", "rawtypes"})
class FilterCriteriaConverterTest
{
    private FilterCriteriaConverter converter;

    @Mock
    private CriteriaBuilder cb;
    @Mock
    private Root root;
    @Mock
    private Path path;
    @Mock
    private Predicate predicate;

    @BeforeEach
    void setUp()
    {
        converter = new FilterCriteriaConverter();
        lenient().when(root.get(anyString())).thenReturn(path);
        lenient().when(path.get(anyString())).thenReturn(path);
    }

    // ── Simple conditions ──────────────────────────────────────────────

    @Test
    @DisplayName("EQ - debe invocar cb.equal()")
    void eq_invocaCbEqual()
    {
        when(cb.equal(path, "Luna")).thenReturn(predicate);
        Filter f = new ConditionFilter<>("name", RelationalOperator.EQ, "Luna");

        Predicate result = converter.toPredicate(f, root, cb);

        assertSame(predicate, result);
        verify(cb).equal(path, "Luna");
    }

    @Test
    @DisplayName("NEQ - debe invocar cb.notEqual()")
    void neq_invocaCbNotEqual()
    {
        when(cb.notEqual(path, "Luna")).thenReturn(predicate);
        Filter f = new ConditionFilter<>("name", RelationalOperator.NEQ, "Luna");

        converter.toPredicate(f, root, cb);

        verify(cb).notEqual(path, "Luna");
    }

    @Test
    @DisplayName("GT - debe invocar cb.greaterThan()")
    void gt_invocaCbGreaterThan()
    {
        when(cb.greaterThan(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("age", RelationalOperator.GT, 3);

        converter.toPredicate(f, root, cb);

        verify(cb).greaterThan(any(Path.class), any(Comparable.class));
    }

    @Test
    @DisplayName("GTE - debe invocar cb.greaterThanOrEqualTo()")
    void gte_invocaCbGreaterThanOrEqualTo()
    {
        when(cb.greaterThanOrEqualTo(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("age", RelationalOperator.GTE, 3);

        converter.toPredicate(f, root, cb);

        verify(cb).greaterThanOrEqualTo(any(Path.class), any(Comparable.class));
    }

    @Test
    @DisplayName("LT - debe invocar cb.lessThan()")
    void lt_invocaCbLessThan()
    {
        when(cb.lessThan(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("age", RelationalOperator.LT, 10);

        converter.toPredicate(f, root, cb);

        verify(cb).lessThan(any(Path.class), any(Comparable.class));
    }

    @Test
    @DisplayName("LTE - debe invocar cb.lessThanOrEqualTo()")
    void lte_invocaCbLessThanOrEqualTo()
    {
        when(cb.lessThanOrEqualTo(any(Path.class), any(Comparable.class))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("age", RelationalOperator.LTE, 10);

        converter.toPredicate(f, root, cb);

        verify(cb).lessThanOrEqualTo(any(Path.class), any(Comparable.class));
    }

    @Test
    @DisplayName("BETWEEN - debe invocar cb.between() con dos valores")
    void between_invocaCbBetween()
    {
        when(cb.between(any(Path.class), any(Comparable.class), any(Comparable.class))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("age", RelationalOperator.BETWEEN, 1, 5);

        converter.toPredicate(f, root, cb);

        verify(cb).between(any(Path.class), any(Comparable.class), any(Comparable.class));
    }

    @Test
    @DisplayName("IN - debe invocar path.in()")
    void in_invocaPathIn()
    {
        when(path.in(any(List.class))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("size", RelationalOperator.IN, List.of("S", "M"));

        converter.toPredicate(f, root, cb);

        verify(path).in(any(List.class));
    }

    @Test
    @DisplayName("NOT_IN - debe invocar cb.not(path.in())")
    void notIn_invocaCbNotPathIn()
    {
        Predicate inPred = mock(Predicate.class);
        when(path.in(any(List.class))).thenReturn(inPred);
        when(cb.not(inPred)).thenReturn(predicate);
        Filter f = new ConditionFilter<>("size", RelationalOperator.NOT_IN, List.of("XL"));

        converter.toPredicate(f, root, cb);

        verify(cb).not(inPred);
    }

    @Test
    @DisplayName("LIKE - debe invocar cb.like() con % en ambos extremos")
    void like_invocaCbLikeConPorcentajes()
    {
        when(cb.like(any(Path.class), eq("%Lu%"))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("name", RelationalOperator.LIKE, "Lu");

        converter.toPredicate(f, root, cb);

        verify(cb).like(any(Path.class), eq("%Lu%"));
    }

    @Test
    @DisplayName("STARTS_WITH - debe invocar cb.like() con % al final")
    void startsWith_invocaCbLikeConPorcentajeAlFinal()
    {
        when(cb.like(any(Path.class), eq("Lu%"))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("name", RelationalOperator.STARTS_WITH, "Lu");

        converter.toPredicate(f, root, cb);

        verify(cb).like(any(Path.class), eq("Lu%"));
    }

    @Test
    @DisplayName("ENDS_WITH - debe invocar cb.like() con % al principio")
    void endsWith_invocaCbLikeConPorcentajeAlPrincipio()
    {
        when(cb.like(any(Path.class), eq("%na"))).thenReturn(predicate);
        Filter f = new ConditionFilter<>("name", RelationalOperator.ENDS_WITH, "na");

        converter.toPredicate(f, root, cb);

        verify(cb).like(any(Path.class), eq("%na"));
    }

    @Test
    @DisplayName("IS_NULL - debe invocar cb.isNull()")
    void isNull_invocaCbIsNull()
    {
        when(cb.isNull(path)).thenReturn(predicate);
        Filter f = new ConditionFilter<>("picture", RelationalOperator.IS_NULL, null);

        converter.toPredicate(f, root, cb);

        verify(cb).isNull(path);
    }

    @Test
    @DisplayName("IS_NOT_NULL - debe invocar cb.isNotNull()")
    void isNotNull_callsCbIsNotNull()
    {
        when(cb.isNotNull(path)).thenReturn(predicate);
        Filter f = new ConditionFilter<>("picture", RelationalOperator.IS_NOT_NULL, null);

        converter.toPredicate(f, root, cb);

        verify(cb).isNotNull(path);
    }

    // ── CompositeFilter ──────────────────────────────────────────────────

    @Test
    @DisplayName("CompositeFilter AND - debe invocar cb.and()")
    void compositeAnd_invocaCbAnd()
    {
        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        when(cb.equal(path, "x")).thenReturn(p1);
        when(cb.equal(path, "y")).thenReturn(p2);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Filter f = new CompositeFilter(LogicalOperator.AND, List.of(
                new ConditionFilter<>("a", RelationalOperator.EQ, "x"),
                new ConditionFilter<>("b", RelationalOperator.EQ, "y")
        ));

        Predicate result = converter.toPredicate(f, root, cb);

        assertSame(predicate, result);
        verify(cb).and(any(Predicate[].class));
    }

    @Test
    @DisplayName("CompositeFilter OR - debe invocar cb.or()")
    void compositeOr_invocaCbOr()
    {
        Predicate p1 = mock(Predicate.class);
        Predicate p2 = mock(Predicate.class);
        when(cb.equal(path, "S")).thenReturn(p1);
        when(cb.equal(path, "M")).thenReturn(p2);
        when(cb.or(any(Predicate[].class))).thenReturn(predicate);

        Filter f = new CompositeFilter(LogicalOperator.OR, List.of(
                new ConditionFilter<>("size", RelationalOperator.EQ, "S"),
                new ConditionFilter<>("size", RelationalOperator.EQ, "M")
        ));

        converter.toPredicate(f, root, cb);

        verify(cb).or(any(Predicate[].class));
    }

    // ── NotFilter ────────────────────────────────────────────────────────

    @Test
    @DisplayName("NotFilter - debe invocar cb.not() con el predicado interno")
    void notFilter_invocaCbNot()
    {
        Predicate inner = mock(Predicate.class);
        when(cb.equal(path, "active")).thenReturn(inner);
        when(cb.not(inner)).thenReturn(predicate);

        Filter f = new NotFilter(new ConditionFilter<>("status", RelationalOperator.EQ, "active"));

        Predicate result = converter.toPredicate(f, root, cb);

        assertSame(predicate, result);
        verify(cb).not(inner);
    }

    // ── Nested path ─────────────────────────────────────────────────────

    @Test
    @DisplayName("Campo anidado con punto debe resolver la ruta correctamente")
    void campoAnidado_resuelveRutaConPunto()
    {
        Path nestedPath = mock(Path.class);
        when(root.get("refuge")).thenReturn(path);
        when(path.get("id")).thenReturn(nestedPath);
        when(cb.equal(nestedPath, "uuid-123")).thenReturn(predicate);

        Filter f = new ConditionFilter<>("refuge.id", RelationalOperator.EQ, "uuid-123");

        converter.toPredicate(f, root, cb);

        verify(root).get("refuge");
        verify(path).get("id");
        verify(cb).equal(nestedPath, "uuid-123");
    }
}
