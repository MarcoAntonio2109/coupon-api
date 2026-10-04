package br.com.onebrain.couponapi.application.domain.service;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.exception.*;
import java.time.LocalDate;
import java.math.BigDecimal;

/**
 * SERVIÇO DE DOMÍNIO: CouponFactory
 * 
 * Responsabilidade: Encapsular a LÓGICA DE CRIAÇÃO E VALIDAÇÃO de cupons.
 * 
 * O serviço de domínio organiza regras de negócio que não pertencem a uma
 * entidade específica, mas são essenciais para manter a integridade.
 * 
 * Vive na camada de domínio (application/) - agnóstica de tecnologia.
 * O UseCase apenas ORQUESTRA: chama factory e porta.
 */
public class CouponDomainService {

    /**
     * Cria e valida um novo cupom.
     * Centraliza TODA a lógica de negócio relacionada à criação.
     * 
     * @param code Código bruto (pode ter caracteres especiais)
     * @param description Descrição do cupom
     * @param discountValue Valor do desconto
     * @param expirationDate Data de expiração
     * @param published Se já está publicado
     * @return CouponDomain pronto para persistência
     * @throws InvalidCouponCodeException se código inválido
     * @throws InvalidExpirationDateException se data no passado
     */
    public CouponDomain createNewCoupon(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDate expirationDate,
            boolean published) {

        String normalizedCode = normalizeCode(code);
        validateCodeLength(normalizedCode);
        validateExpirationDate(expirationDate);

        return new CouponDomain(
            normalizedCode,
            description,
            discountValue,
            expirationDate,
            published
        );
    }

    /**
     * Atualiza um cupom existente com novas informações.
     * Valida as mudanças antes de atualizar.
     * 
     * @param coupon Cupom a atualizar (já existe no banco)
     * @param description Nova descrição
     * @param discountValue Novo valor de desconto
     * @param expirationDate Nova data de expiração
     * @param published Novo status de publicação
     * @throws InvalidExpirationDateException se data no passado
     */
    public void updateCoupon(
            CouponDomain coupon,
            String description,
            BigDecimal discountValue,
            LocalDate expirationDate,
            boolean published) {

        validateExpirationDate(expirationDate);

        coupon.setDescription(description);
        coupon.setDiscountValue(discountValue);
        coupon.setExpirationDate(expirationDate);
        coupon.setPublished(published);
    }

    /**
     * Marca cupom como deletado (soft delete).
     * Valida estado antes de deletar.
     * 
     * @param coupon Cupom a deletar
     * @throws CouponAlreadyDeletedException se já foi deletado
     */
    public void deleteCoupon(CouponDomain coupon) {
        if (coupon.isDeleted()) {
            throw new CouponAlreadyDeletedException();
        }
        coupon.markAsDeleted();
    }

    /**
     * REGRA: Normalizar código de cupom.
     * Remove caracteres especiais e converte para maiúsculas.
     */
    private String normalizeCode(String code) {
        return code.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    /**
     * REGRA: Validar comprimento do código normalizado.
     * Deve ter exatamente 6 caracteres.
     */
    private void validateCodeLength(String normalizedCode) {
        if (normalizedCode.length() != 6) {
            throw new InvalidCouponCodeException(normalizedCode);
        }
    }

    /**
     * REGRA: Validar data de expiração.
     * Não pode estar no passado.
     */
    private void validateExpirationDate(LocalDate expirationDate) {
        if (expirationDate.isBefore(LocalDate.now())) {
            throw new InvalidExpirationDateException(expirationDate);
        }
    }
}
