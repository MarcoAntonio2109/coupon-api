package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponNotFoundException;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindCouponUseCase - Testes de Orquestração")
public class FindCouponUseCaseTest {

    private FindCouponUseCase useCase;

    @Mock
    private CouponRepositoryPort repository;

    @BeforeEach
    public void setUp() {
        useCase = new FindCouponUseCase(repository);
    }

    @Test
    @DisplayName("Deve encontrar cupom pelo código")
    public void testFindCouponByCodeSuccess() {
        // Arrange
        String code = "ABC123";
        CouponDomain expectedCoupon = new CouponDomain(
            code,
            "Desconto 10%",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            true
        );

        when(repository.findByCode(code)).thenReturn(Optional.of(expectedCoupon));

        // Act
        CouponDomain result = useCase.execute(code);

        // Assert
        assertNotNull(result);
        assertEquals(code, result.getCode());
        verify(repository, times(1)).findByCode(code);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cupom não é encontrado")
    public void testFindCouponNotFound() {
        // Arrange
        String code = "NONEXISTENT";
        when(repository.findByCode(code)).thenReturn(Optional.empty());

        // Act & Assert
        CouponNotFoundException exception = assertThrows(
            CouponNotFoundException.class,
            () -> useCase.execute(code)
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(repository, times(1)).findByCode(code);
    }
}
