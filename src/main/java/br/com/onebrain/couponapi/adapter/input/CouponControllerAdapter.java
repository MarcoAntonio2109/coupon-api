package br.com.onebrain.couponapi.adapter.input;

import br.com.onebrain.couponapi.application.domain.CouponDomain;
import br.com.onebrain.couponapi.application.domain.service.CouponDomainService;
import br.com.onebrain.couponapi.application.usecase.*;
import br.com.onebrain.couponapi.dto.CouponResponse;
import br.com.onebrain.couponapi.dto.CreateCouponRequest;
import br.com.onebrain.couponapi.dto.UpdateCouponRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ADAPTADOR DE ENTRADA: CouponControllerAdapter (REST)
 * 
 * Responsabilidade: Converter HTTP ↔ Domínio
 * 
 * ✓ Recebe DTO (JSON)
 * ✓ Valida anotações (@NotBlank, @NotNull, etc)
 * ✓ Invoca usecases
 * ✓ Converte resposta (CouponDomain → CouponResponse)
 * ✓ Trata exceções de domínio e mapeia para HTTP
 * 
 * APENAS adaptação de protocolo. Nenhuma lógica de negócio aqui.
 */
@RestController
@RequestMapping("/api/coupons")
public class CouponControllerAdapter {

    private final CreateCouponUseCase createUseCase;
    private final FindCouponUseCase findUseCase;
    private final DeleteCouponUseCase deleteUseCase;
    private final UpdateCouponUseCase updateUseCase;
    private final ListCouponUseCase listUseCase;
    private final CouponDomainService domainService;

    public CouponControllerAdapter(
            CreateCouponUseCase createUseCase,
            FindCouponUseCase findUseCase,
            DeleteCouponUseCase deleteUseCase,
            UpdateCouponUseCase updateUseCase,
            ListCouponUseCase listUseCase,
            CouponDomainService domainService) {
        this.createUseCase = createUseCase;
        this.findUseCase = findUseCase;
        this.deleteUseCase = deleteUseCase;
        this.updateUseCase = updateUseCase;
        this.listUseCase = listUseCase;
        this.domainService = domainService;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        try {
            CouponDomain result = createUseCase.execute(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.isPublished()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
        } catch (Exception e) {
            throw e; // GlobalExceptionHandler cuida do mapeamento
        }
    }

    @GetMapping
    public ResponseEntity<Page<CouponResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ListCouponUseCase.ListCouponOutput output = listUseCase.execute(page, size);
        List<CouponResponse> content = output.getContent()
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        Page<CouponResponse> result = new PageImpl<>(
            content,
            org.springframework.data.domain.PageRequest.of(page, size),
            output.getTotalElements()
        );

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{code}")
    public ResponseEntity<CouponResponse> update(
            @PathVariable String code,
            @Valid @RequestBody UpdateCouponRequest request) {

        try {
            CouponDomain result = updateUseCase.execute(
                code,
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.isPublished()
            );
            return ResponseEntity.ok(toResponse(result));
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        try {
            deleteUseCase.execute(code);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<CouponResponse> getByCode(@PathVariable String code) {
        CouponDomain result = findUseCase.execute(code);
        return ResponseEntity.ok(toResponse(result));
    }

    private CouponResponse toResponse(CouponDomain domain) {
        CouponResponse response = new CouponResponse();
        response.setId(domain.getId());
        response.setCode(domain.getCode());
        response.setDescription(domain.getDescription());
        response.setDiscountValue(domain.getDiscountValue());
        response.setExpirationDate(domain.getExpirationDate());
        response.setPublished(domain.isPublished());
        response.setDeleted(domain.isDeleted());
        response.setCreatedAt(domain.getCreatedAt());
        return response;
    }
}
