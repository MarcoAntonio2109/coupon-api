package br.com.onebrain.couponapi.application.domain.service;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TESTE PURO DO SERVIÇO DE DOMÍNIO
 * 
 * ✓ Sem Spring
 * ✓ Sem JPA
 * ✓ Sem banco de dados
 * ✓ Testando LÓGICA DE NEGÓCIO PURA
 * 
 * Observação: @ExtendWith não é necessário porque não usamos mocks
 * Apenas testamos a classe diretamente.
 */
@DisplayName("CouponDomainService - Testes de Lógica de Negócio")
public class CouponDomainServiceTest {

    private final CouponDomainService service = new CouponDomainService();

    // === TESTES DE CRIAÇÃO ===

    @Test
    @DisplayName("Deve criar cupom com código normalizado")
    public void testCreateNewCouponNormalizesCode() {
        CouponDomain coupon = service.createNewCoupon(
            "A-B-C-1-2-3",
            "Desconto 10%",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            true
        );

        assertEquals("ABC123", coupon.getCode());
        assertEquals("Desconto 10%", coupon.getDescription());
        assertTrue(coupon.isPublished());
        assertFalse(coupon.isDeleted());
        assertNotNull(coupon.getCreatedAt());
    }

    @Test
    @DisplayName("Deve rejeitar código com menos de 6 caracteres após normalização")
    public void testRejectCodeTooShort() {
        InvalidCouponCodeException exception = assertThrows(
            InvalidCouponCodeException.class,
            () -> service.createNewCoupon(
                "AB-12",  // Normaliza para "AB12" (4 caracteres)
                "Desconto",
                new BigDecimal("10.00"),
                LocalDate.now().plusDays(30),
                true
            )
        );

        assertTrue(exception.getMessage().contains("6 caracteres"));
    }

    @Test
    @DisplayName("Deve rejeitar código com mais de 6 caracteres após normalização")
    public void testRejectCodeTooLong() {
        InvalidCouponCodeException exception = assertThrows(
            InvalidCouponCodeException.class,
            () -> service.createNewCoupon(
                "A-B-C-D-E-F-G",  // Normaliza para "ABCDEFG" (7 caracteres)
                "Desconto",
                new BigDecimal("10.00"),
                LocalDate.now().plusDays(30),
                true
            )
        );

        assertTrue(exception.getMessage().contains("6 caracteres"));
    }

    @Test
    @DisplayName("Deve rejeitar data de expiração no passado")
    public void testRejectExpiredDate() {
        LocalDate yesterday = LocalDate.now().minusDays(1);

        InvalidExpirationDateException exception = assertThrows(
            InvalidExpirationDateException.class,
            () -> service.createNewCoupon(
                "ABC123",
                "Desconto",
                new BigDecimal("10.00"),
                yesterday,
                true
            )
        );

        assertTrue(exception.getMessage().contains("passado"));
    }

    @Test
    @DisplayName("Deve aceitar data de hoje como expiração válida")
    public void testAcceptTodayAsExpirationDate() {
        LocalDate today = LocalDate.now();

        CouponDomain coupon = service.createNewCoupon(
            "ABC123",
            "Desconto",
            new BigDecimal("10.00"),
            today,
            true
        );

        assertEquals(today, coupon.getExpirationDate());
    }

    // === TESTES DE ATUALIZAÇÃO ===

    @Test
    @DisplayName("Deve atualizar cupom com novos valores")
    public void testUpdateCouponWithNewValues() {
        CouponDomain coupon = new CouponDomain(
            "ABC123",
            "Desconto antigo",
            new BigDecimal("5.00"),
            LocalDate.now().plusDays(10),
            false
        );

        service.updateCoupon(
            coupon,
            "Desconto novo",
            new BigDecimal("15.00"),
            LocalDate.now().plusDays(30),
            true
        );

        assertEquals("Desconto novo", coupon.getDescription());
        assertEquals(new BigDecimal("15.00"), coupon.getDiscountValue());
        assertEquals(LocalDate.now().plusDays(30), coupon.getExpirationDate());
        assertTrue(coupon.isPublished());
    }

    @Test
    @DisplayName("Deve rejeitar atualização com data no passado")
    public void testRejectUpdateWithExpiredDate() {
        CouponDomain coupon = new CouponDomain(
            "ABC123",
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            false
        );

        LocalDate yesterday = LocalDate.now().minusDays(1);

        InvalidExpirationDateException exception = assertThrows(
            InvalidExpirationDateException.class,
            () -> service.updateCoupon(
                coupon,
                "Desconto",
                new BigDecimal("10.00"),
                yesterday,
                false
            )
        );

        assertTrue(exception.getMessage().contains("passado"));
        // Cupom não foi modificado
        assertEquals("Desconto", coupon.getDescription());
    }

    // === TESTES DE DELEÇÃO ===

    @Test
    @DisplayName("Deve marcar cupom como deletado")
    public void testDeleteCoupon() {
        CouponDomain coupon = new CouponDomain(
            "ABC123",
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            false
        );

        service.deleteCoupon(coupon);

        assertTrue(coupon.isDeleted());
        assertNotNull(coupon.getDeletedAt());
    }

    @Test
    @DisplayName("Deve rejeitar deleção de cupom já deletado")
    public void testRejectDeletingAlreadyDeletedCoupon() {
        CouponDomain coupon = new CouponDomain(
            "ABC123",
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            false
        );

        // Primeira deleção: OK
        service.deleteCoupon(coupon);

        // Segunda deleção: Deve lançar exceção
        CouponAlreadyDeletedException exception = assertThrows(
            CouponAlreadyDeletedException.class,
            () -> service.deleteCoupon(coupon)
        );

        assertTrue(exception.getMessage().contains("já foi excluído"));
    }

    // === TESTES DE NORMALIZAÇÃO ===

    @Test
    @DisplayName("Deve normalizar código removendo caracteres especiais")
    public void testCodeNormalization() {
        // Testando a normalização através da criação
        // já que normalizeCode é privado

        String[] testCodes = {
            "ABC123",      // Sem especiais
            "A-B-C-1-2-3", // Com hífens
            "A@B#C$1%2&3", // Caracteres especiais variados
            "abc123",      // Minúsculas
            "a-b-c-1-2-3"  // Minúsculas com hífens
        };

        for (String code : testCodes) {
            CouponDomain coupon = service.createNewCoupon(
                code,
                "Test",
                new BigDecimal("10.00"),
                LocalDate.now().plusDays(30),
                true
            );

            assertEquals("ABC123", coupon.getCode());
        }
    }

    // === TESTES DE ESTADO ===

    @Test
    @DisplayName("Cupom novo deve ter estado padrão correto")
    public void testNewCouponDefaultState() {
        CouponDomain coupon = service.createNewCoupon(
            "ABC123",
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            true
        );

        assertFalse(coupon.isDeleted());
        assertNull(coupon.getDeletedAt());
        assertTrue(coupon.isPublished());
        assertNotNull(coupon.getCreatedAt());
    }

    @Test
    @DisplayName("Cupom deletado deve ter deletedAt preenchido")
    public void testDeletedCouponHasDeletedAtTimestamp() {
        CouponDomain coupon = new CouponDomain(
            "ABC123",
            "Desconto",
            new BigDecimal("10.00"),
            LocalDate.now().plusDays(30),
            false
        );

        assertNull(coupon.getDeletedAt()); // Antes de deletar

        service.deleteCoupon(coupon);

        assertNotNull(coupon.getDeletedAt()); // Depois de deletar
        assertTrue(coupon.isDeleted());
    }
}
