package br.com.onebrain.couponapi.config;

import br.com.onebrain.couponapi.application.domain.service.CouponDomainService;
import br.com.onebrain.couponapi.application.port.CouponRepositoryPort;
import br.com.onebrain.couponapi.application.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CONFIGURAÇÃO DE DI (Injeção de Dependência)
 * 
 * Responsabilidade: Conectar adapters, portas e usecases.
 * 
 * Princípio DIP: O módulo de alto nível (usecases) não depende de módulos
 * de baixo nível (adapters). Ambos dependem de abstrações (portas/interfaces).
 * 
 * Fluxo aqui:
 * 1. Spring detecta CouponRepositoryAdapter implementando CouponRepositoryPort
 * 2. Injetar CouponRepositoryPort (interface) nos usecases
 * 3. Usecases nunca conhecem que é JPA, poderia ser MongoDB, Redis, etc
 * 4. Mudar implementação = mudar um bean, usecases intactos
 */
@Configuration
public class UseCaseConfig {

    /**
     * Domain Service: Concentra TODA lógica de negócio
     * Agnóstico de tecnologia (sem Spring, sem JPA)
     */
    @Bean
    public CouponDomainService couponDomainService() {
        return new CouponDomainService();
    }

    /**
     * UseCase: CreateCoupon
     * Orquestra domainService + repository
     */
    @Bean
    public CreateCouponUseCase createCouponUseCase(
            CouponDomainService domainService,
            CouponRepositoryPort repositoryPort) {
        return new CreateCouponUseCase(domainService, repositoryPort);
    }

    /**
     * UseCase: FindCoupon
     * Orquestra busca simples
     */
    @Bean
    public FindCouponUseCase findCouponUseCase(CouponRepositoryPort repositoryPort) {
        return new FindCouponUseCase(repositoryPort);
    }

    /**
     * UseCase: DeleteCoupon
     * Orquestra domainService + repository
     */
    @Bean
    public DeleteCouponUseCase deleteCouponUseCase(
            CouponDomainService domainService,
            CouponRepositoryPort repositoryPort) {
        return new DeleteCouponUseCase(domainService, repositoryPort);
    }

    /**
     * UseCase: UpdateCoupon
     * Orquestra domainService + repository
     */
    @Bean
    public UpdateCouponUseCase updateCouponUseCase(
            CouponDomainService domainService,
            CouponRepositoryPort repositoryPort) {
        return new UpdateCouponUseCase(domainService, repositoryPort);
    }

    /**
     * UseCase: ListCoupon
     * Orquestra busca com paginação
     */
    @Bean
    public ListCouponUseCase listCouponUseCase(CouponRepositoryPort repositoryPort) {
        return new ListCouponUseCase(repositoryPort);
    }
}
