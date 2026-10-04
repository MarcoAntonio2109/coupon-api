package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponNotFoundException;
import br.com.onebrain.couponapi.application.domain.exception.InvalidExpirationDateException;
import br.com.onebrain.couponapi.application.domain.service.CouponDomainService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateCouponUseCase - Testes de Orquestração")
public class UpdateCouponUseCaseTest {

    private UpdateCouponUseCase useCase;

    @Mock
    private CouponDomainService domainService;

    @Mock
    private CouponRepositoryPort repository;

    @BeforeEach
    public void setUp() {
        useCase = new UpdateCouponUseCase(domainService, repository);
    }

    @Test
    @DisplayName("Deve atualizar cupom com sucesso")
    public void testUpdateCouponSuccess() {
        // Arrange
        String code = "ABC123";
        String newDescription = "Desconto 20%";
        BigDecimal newDiscount = new BigDecimal("20.00");
        LocalDate newExpiration = LocalDate.now().plusDays(60);
        boolean published = true;

        CouponDomain existingCoupon = new CouponDomain(
            code,
            "Desconto 10%",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            false
        );

        CouponDomain updatedCoupon = new CouponDomain(
            code,
            newDescription,
            newDiscount,
            newExpiration,
            published
        );

        when(repository.findByCode(code)).thenReturn(Optional.of(existingCoupon));
        doNothing().when(domainService).updateCoupon(
            existingCoupon, newDescription, newDiscount, newExpiration, published
        );
        when(repository.save(any())).thenReturn(updatedCoupon);

        // Act
        CouponDomain result = useCase.execute(
            code, newDescription, newDiscount, newExpiration, published
        );

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).findByCode(code);
        verify(domainService, times(1)).updateCoupon(
            existingCoupon, newDescription, newDiscount, newExpiration, published
        );
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cupom não existe")
    public void testUpdateNonExistentCoupon() {
        // Arrange
        String code = "NONEXISTENT";
        when(repository.findByCode(code)).thenReturn(Optional.empty());

        // Act & Assert
        CouponNotFoundException exception = assertThrows(
            CouponNotFoundException.class,
            () -> useCase.execute(
                code,
                "Desconto",
                new BigDecimal("10.00"),
                LocalDate.now().plusDays(30),
                true
            )
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(domainService, never()).updateCoupon(any(), anyString(), any(), any(), anyBoolean());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção de validação de data")
    public void testPropagateInvalidExpirationDateException() {
        // Arrange
        String code = "ABC123";
        LocalDate pastDate = LocalDate.now().minusDays(1);

        CouponDomain existingCoupon = new CouponDomain(
            code,
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            false
        );

        when(repository.findByCode(code)).thenReturn(Optional.of(existingCoupon));
        doThrow(new InvalidExpirationDateException(pastDate))
            .when(domainService).updateCoupon(
                existingCoupon, "Desconto", new BigDecimal("10.00"), pastDate, false
            );

        // Act & Assert
        assertThrows(
            InvalidExpirationDateException.class,
            () -> useCase.execute(code, "Desconto", new BigDecimal("10.00"), pastDate, false)
        );

        verify(repository, never()).save(any());
    }
}
