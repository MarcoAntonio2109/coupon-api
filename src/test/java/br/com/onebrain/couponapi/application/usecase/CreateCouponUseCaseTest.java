package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.CouponAlreadyExistsException;
import br.com.onebrain.couponapi.application.domain.exception.InvalidCouponCodeException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCouponUseCase - Testes de Orquestração")
public class CreateCouponUseCaseTest {

    private CreateCouponUseCase useCase;

    @Mock
    private CouponDomainService domainService;

    @Mock
    private CouponRepositoryPort repository;

    @BeforeEach
    public void setUp() {
        useCase = new CreateCouponUseCase(domainService, repository);
    }

    @Test
    @DisplayName("Deve criar cupom quando dados são válidos")
    public void testCreateCouponSuccess() {
        // Arrange
        String code = "ABC123";
        String description = "Desconto 10%";
        BigDecimal discount = new BigDecimal("10.00");
        LocalDate expirationDate = LocalDate.now().plusDays(30);
        boolean published = true;

        CouponDomain expectedCoupon = new CouponDomain(
            code,
            description,
            discount,
            expirationDate,
            published
        );

        when(domainService.createNewCoupon(code, description, discount, expirationDate, published))
            .thenReturn(expectedCoupon);

        when(repository.existsByCode(code)).thenReturn(false);
        when(repository.save(any())).thenReturn(expectedCoupon);

        // Act
        CouponDomain result = useCase.execute(code, description, discount, expirationDate, published);

        // Assert
        assertNotNull(result);
        assertEquals(code, result.getCode());
        assertEquals(description, result.getDescription());
        assertEquals(discount, result.getDiscountValue());

        // Verify chamadas
        verify(domainService, times(1)).createNewCoupon(code, description, discount, expirationDate, published);
        verify(repository, times(1)).existsByCode(code);
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar se cupom com mesmo código já existe")
    public void testRejectDuplicateCode() {
        // Arrange
        String code = "ABC123";
        String description = "Desconto 10%";
        BigDecimal discount = new BigDecimal("10.00");
        LocalDate expirationDate = LocalDate.now().plusDays(30);

        CouponDomain mockCoupon = new CouponDomain(
            code, description, discount, expirationDate, true
        );

        when(domainService.createNewCoupon(anyString(), anyString(), any(), any(), anyBoolean()))
            .thenReturn(mockCoupon);

        when(repository.existsByCode(code)).thenReturn(true);

        // Act & Assert
        CouponAlreadyExistsException exception = assertThrows(
            CouponAlreadyExistsException.class,
            () -> useCase.execute(code, description, discount, expirationDate, true)
        );

        assertTrue(exception.getMessage().contains("já existe"));

        // Não deve chamar save se código duplicado
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção de validação do serviço de domínio")
    public void testPropagateInvalidCouponCodeException() {
        // Arrange
        String invalidCode = "AB12";  // Menos de 6 caracteres
        String description = "Desconto";
        BigDecimal discount = new BigDecimal("10.00");
        LocalDate expirationDate = LocalDate.now().plusDays(30);

        when(domainService.createNewCoupon(
            invalidCode, description, discount, expirationDate, true
        )).thenThrow(new InvalidCouponCodeException(invalidCode));

        // Act & Assert
        assertThrows(
            InvalidCouponCodeException.class,
            () -> useCase.execute(invalidCode, description, discount, expirationDate, true)
        );

        // Não deve chamar repository
        verify(repository, never()).existsByCode(anyString());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve propagar exceção de data inválida")
    public void testPropagateInvalidExpirationDateException() {
        // Arrange
        String code = "ABC123";
        String description = "Desconto";
        BigDecimal discount = new BigDecimal("10.00");
        LocalDate pastDate = LocalDate.now().minusDays(1);

        when(domainService.createNewCoupon(
            code, description, discount, pastDate, true
        )).thenThrow(new InvalidExpirationDateException(pastDate));

        // Act & Assert
        assertThrows(
            InvalidExpirationDateException.class,
            () -> useCase.execute(code, description, discount, pastDate, true)
        );

        verify(repository, never()).existsByCode(anyString());
        verify(repository, never()).save(any());
    }
}
