package br.com.onebrain.couponapi.application.domain;

import br.com.onebrain.couponapi.application.domain.exception.CouponAlreadyDeletedException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.math.BigDecimal;

/**
 * ENTIDADE DE DOMÍNIO: CouponDomain
 * 
 * Responsabilidade: Representar o estado de um cupom.
 * Encapsula apenas as REGRAS que pertencem à entidade.
 * 
 * ✓ Sem dependências de frameworks (Spring, JPA)
 * ✓ Sem conhecimento de banco de dados
 * ✓ Regras complexas delegadas ao CouponDomainService
 * 
 * A entidade é um Value Object (apenas estado + comportamento de estado).
 */
public class CouponDomain {
    private Long id;
    private String code;
    private String description;
    private BigDecimal discountValue;
    private LocalDate expirationDate;
    private boolean published;
    private boolean deleted;
    private OffsetDateTime createdAt;
    private OffsetDateTime deletedAt;

    public CouponDomain() {}

    public CouponDomain(String code, String description, BigDecimal discountValue, 
                        LocalDate expirationDate, boolean published) {
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = published;
        this.deleted = false;
        this.createdAt = OffsetDateTime.now();
    }

    /**
     * Marca cupom como deletado (soft delete).
     * REGRA: Cupom já deletado não pode ser deletado novamente.
     */
    public void markAsDeleted() {
        if (this.deleted) {
            throw new CouponAlreadyDeletedException();
        }
        this.deleted = true;
        this.deletedAt = OffsetDateTime.now();
    }

    // Normalmente usaria Lombok para geração de getters e setters
    // e construtores automaticamente. Porém aqui estou escrevendo
    // manualmente para manter a camada de domínio agnóstica de frameworks.
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
