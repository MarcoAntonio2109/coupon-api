package br.com.onebrain.couponapi.application.usecase;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;

import java.util.List;

/**
 * CASO DE USO: ListCouponUseCase
 * 
 * Responsabilidade: ORQUESTRAR a listagem de cupons.
 * 
 * Fluxo simples:
 * 1. Recebe parâmetros de paginação
 * 2. Busca via porta
 * 3. Retorna lista e metadados
 */
public class ListCouponUseCase {
    private final CouponRepositoryPort repository;

    public ListCouponUseCase(CouponRepositoryPort repository) {
        this.repository = repository;
    }

    public ListCouponOutput execute(int page, int size) {
        List<CouponDomain> content = repository.findAllByDeletedFalse(page, size);
        Long totalElements = repository.countByDeletedFalse();

        return new ListCouponOutput(content, totalElements, page, size);
    }

    /**
     * Output do caso de uso (DTO de domínio - sem dependências externas)
     */
    public static class ListCouponOutput {
        private final List<CouponDomain> content;
        private final Long totalElements;
        private final int page;
        private final int size;

        public ListCouponOutput(List<CouponDomain> content, Long totalElements, int page, int size) {
            this.content = content;
            this.totalElements = totalElements;
            this.page = page;
            this.size = size;
        }

        public List<CouponDomain> getContent() {
            return content;
        }

        public Long getTotalElements() {
            return totalElements;
        }

        public int getPage() {
            return page;
        }

        public int getSize() {
            return size;
        }
    }
}
