package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponAlreadyDeletedException;
import br.com.onebrain.couponapi.application.domain.exception.CouponNotFoundException;
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
@DisplayName("DeleteCouponUseCase - Testes de Orquestração")
public class DeleteCouponUseCaseTest {

    private DeleteCouponUseCase useCase;

    @Mock
    private CouponDomainService domainService;

    @Mock
    private CouponRepositoryPort repository;

    @BeforeEach
    public void setUp() {
        useCase = new DeleteCouponUseCase(domainService, repository);
    }

    @Test
    @DisplayName("Deve deletar cupom com sucesso")
    public void testDeleteCouponSuccess() {
        // Arrange
        String code = "ABC123";
        CouponDomain existingCoupon = new CouponDomain(
            code,
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            true
        );

        when(repository.findByCode(code)).thenReturn(Optional.of(existingCoupon));
        doNothing().when(domainService).deleteCoupon(existingCoupon);
        when(repository.save(any())).thenReturn(existingCoupon);

        // Act
        useCase.execute(code);

        // Assert - Verify chamadas
        verify(repository, times(1)).findByCode(code);
        verify(domainService, times(1)).deleteCoupon(existingCoupon);
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando cupom não existe")
    public void testDeleteNonExistentCoupon() {
        // Arrange
        String code = "NONEXISTENT";
        when(repository.findByCode(code)).thenReturn(Optional.empty());

        // Act & Assert
        CouponNotFoundException exception = assertThrows(
            CouponNotFoundException.class,
            () -> useCase.execute(code)
        );

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(domainService, never()).deleteCoupon(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar cupom já deletado")
    public void testDeleteAlreadyDeletedCoupon() {
        // Arrange
        String code = "ABC123";
        CouponDomain existingCoupon = new CouponDomain(
            code,
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            true
        );

        when(repository.findByCode(code)).thenReturn(Optional.of(existingCoupon));
        doThrow(new CouponAlreadyDeletedException())
            .when(domainService).deleteCoupon(existingCoupon);

        // Act & Assert
        CouponAlreadyDeletedException exception = assertThrows(
            CouponAlreadyDeletedException.class,
            () -> useCase.execute(code)
        );

        assertTrue(exception.getMessage().contains("já foi excluído"));
        verify(repository, never()).save(any());
    }
}
