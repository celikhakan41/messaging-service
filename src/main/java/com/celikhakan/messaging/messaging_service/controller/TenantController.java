package com.celikhakan.messaging.messaging_service.controller;

import com.celikhakan.messaging.messaging_service.config.AuthContext;
import com.celikhakan.messaging.messaging_service.dto.TenantDto;
import com.celikhakan.messaging.messaging_service.dto.UpdatePlanRequest;
import com.celikhakan.messaging.messaging_service.dto.CreateTenantRequest;
import com.celikhakan.messaging.messaging_service.model.Tenant;
import com.celikhakan.messaging.messaging_service.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for tenant configuration and management APIs.
 */
@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    /**
     * Retrieves the configuration of the current authenticated tenant.
     */
    @GetMapping
    public ResponseEntity<TenantDto> getCurrentTenant() {
        String tenantId = AuthContext.getTenantId()
                .orElseThrow(() -> new IllegalStateException("Tenant context not found"));
        Tenant tenant = tenantService.getTenant(tenantId);
        return ResponseEntity.ok(toDto(tenant));
    }

    /**
     * Updates the subscription plan of the current authenticated tenant.
     */
    @PutMapping("/plan")
    public ResponseEntity<TenantDto> updatePlan(@RequestBody UpdatePlanRequest request) {
        String tenantId = AuthContext.getTenantId()
                .orElseThrow(() -> new IllegalStateException("Tenant context not found"));
        Tenant updated = tenantService.updateTenantPlan(tenantId, request.getPlanType());
        return ResponseEntity.ok(toDto(updated));
    }

    private TenantDto toDto(Tenant tenant) {
        return TenantDto.builder()
                .id(tenant.getId())
                .planType(tenant.getPlanType())
                .createdAt(tenant.getCreatedAt())
        .build();
    }

    /**
     * Creates a new tenant with the specified subscription plan.
     */
    @PostMapping
    public ResponseEntity<TenantDto> createTenant(@RequestBody CreateTenantRequest request) {
        Tenant created = tenantService.createTenant(request.getPlanType());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    /**
     * Deletes the tenant with the given identifier.
     */
    @DeleteMapping("/{tenantId}")
    public ResponseEntity<Void> deleteTenant(@PathVariable String tenantId) {
        tenantService.deleteTenant(tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all tenants (admin use).
     */
    @GetMapping("/all")
    public ResponseEntity<List<TenantDto>> getAllTenants() {
        List<TenantDto> dtos = tenantService.listTenants().stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves a tenant by its identifier (admin use).
     */
    @GetMapping("/{tenantId}")
    public ResponseEntity<TenantDto> getTenantById(@PathVariable String tenantId) {
        Tenant tenant = tenantService.getTenant(tenantId);
        return ResponseEntity.ok(toDto(tenant));
    }
}
