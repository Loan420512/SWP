package com.evswap.controller;

import com.evswap.dto.inventory.*;
import com.evswap.service.InventoryService;
import com.evswap.service.StationStatusSummary;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/inventory") @RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @PreAuthorize("hasAnyRole('Staff','Admin')")
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<InventoryItemResponse>> list(@PathVariable Integer stationId){
        return ResponseEntity.ok(service.listByStation(stationId));
    }

    @PreAuthorize("hasAnyRole('Staff','Admin')")
    @PostMapping
    public ResponseEntity<InventoryItemResponse> add(@Valid @RequestBody InventoryCreateRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.add(req));
    }

    @PreAuthorize("hasAnyRole('Staff','Admin')")
    @PutMapping("/{inventoryId}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Integer inventoryId,
                                             @Valid @RequestBody InventoryUpdateStatusRequest req){
        service.updateStatus(inventoryId, req);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('Staff','Admin')")
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody InventoryTransferRequest req){
        service.transfer(req);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('Staff','Admin')")
    @GetMapping("/station/{stationId}/summary")
    public ResponseEntity<List<StationStatusSummary>> summary(@PathVariable Integer stationId){
        return ResponseEntity.ok(service.summarizeStatus(stationId));
    }
}
