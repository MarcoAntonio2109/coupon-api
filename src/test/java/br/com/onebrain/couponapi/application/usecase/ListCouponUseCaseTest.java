package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListCouponUseCase - Testes de Orquestração")
public class ListCouponUseCaseTest {

    private ListCouponUseCase useCase;

    @Mock
    private CouponRepositoryPort repository;

    @BeforeEach
    public void setUp() {
        useCase = new ListCouponUseCase(repository);
    }

    @Test
    @DisplayName("Deve listar todos os cupons disponíveis com paginação")
    public void testListAllCouponsWithPagination() {
        // Arrange
        int page = 0;
        int size = 10;

        List<CouponDomain> mockCoupons = Arrays.asList(
            new CouponDomain("ABC123", "Desconto 10%", new BigDecimal("10.00"), LocalDate.now().plusDays(30), true),
            new CouponDomain("DEF456", "Desconto 20%", new BigDecimal("20.00"), LocalDate.now().plusDays(60), true)
        );

        when(repository.findAllByDeletedFalse(page, size)).thenReturn(mockCoupons);
        when(repository.countByDeletedFalse()).thenReturn(2L);

        // Act
        ListCouponUseCase.ListCouponOutput result = useCase.execute(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2L, result.getTotalElements());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());
        assertEquals("ABC123", result.getContent().get(0).getCode());
        assertEquals("DEF456", result.getContent().get(1).getCode());

        verify(repository, times(1)).findAllByDeletedFalse(page, size);
        verify(repository, times(1)).countByDeletedFalse();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há cupons")
    public void testListEmptyWhenNoCoupons() {
        // Arrange
        int page = 0;
        int size = 10;

        when(repository.findAllByDeletedFalse(page, size)).thenReturn(Collections.emptyList());
        when(repository.countByDeletedFalse()).thenReturn(0L);

        // Act
        ListCouponUseCase.ListCouponOutput result = useCase.execute(page, size);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        verify(repository, times(1)).findAllByDeletedFalse(page, size);
        verify(repository, times(1)).countByDeletedFalse();
    }

    @Test
    @DisplayName("Deve paginar corretamente cupons com diferentes páginas")
    public void testListCouponsPagination() {
        // Arrange
        int page = 1;
        int size = 5;

        List<CouponDomain> page1Coupons = Arrays.asList(
            new CouponDomain("GHI789", "Desconto 30%", new BigDecimal("30.00"), LocalDate.now().plusDays(90), true)
        );

        when(repository.findAllByDeletedFalse(page, size)).thenReturn(page1Coupons);
        when(repository.countByDeletedFalse()).thenReturn(6L);  // Total de 6 cupons

        // Act
        ListCouponUseCase.ListCouponOutput result = useCase.execute(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(6L, result.getTotalElements());
        assertEquals(page, result.getPage());
        assertEquals(size, result.getSize());

        verify(repository, times(1)).findAllByDeletedFalse(page, size);
        verify(repository, times(1)).countByDeletedFalse();
    }
}
