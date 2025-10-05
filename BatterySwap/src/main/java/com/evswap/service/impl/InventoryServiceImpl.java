package com.evswap.service.impl;

import com.evswap.dto.inventory.*;
import com.evswap.entity.*;
import com.evswap.repository.*;
import com.evswap.service.InventoryService;
import com.evswap.service.StationStatusSummary;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service @RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepo;
    private final StationRepository stationRepo;
    private final BatteryRepository batteryRepo;

    @Override
    public List<InventoryItemResponse> listByStation(Integer stationId) {
        return inventoryRepo.findByStationId(stationId).stream().map(i ->
                new InventoryItemResponse(i.getId(),
                        i.getStation().getId(), i.getStation().getName(),
                        i.getBattery().getId(), i.getBattery().getName(),
                        i.getBattery().getStatus(), i.getStatus())
        ).toList();
    }

    @Override @Transactional
    public InventoryItemResponse add(InventoryCreateRequest req) {
        if (inventoryRepo.existsByStationIdAndBatteryId(req.stationId(), req.batteryId()))
            throw new IllegalArgumentException("Battery đã tồn tại ở trạm này");
        Station st = stationRepo.findById(req.stationId())
                .orElseThrow(() -> new IllegalArgumentException("Station không tồn tại"));
        Battery bt = batteryRepo.findById(req.batteryId())
                .orElseThrow(() -> new IllegalArgumentException("Battery không tồn tại"));
        var inv = inventoryRepo.save(Inventory.builder().station(st).battery(bt).status(req.status()).build());
        return new InventoryItemResponse(inv.getId(), st.getId(), st.getName(), bt.getId(), bt.getName(), bt.getStatus(), inv.getStatus());
    }

    @Override @Transactional
    public void updateStatus(Integer inventoryId, InventoryUpdateStatusRequest req) {
        var inv = inventoryRepo.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory không tồn tại"));
        inv.setStatus(req.status());
    }

    @Override @Transactional
    public void transfer(InventoryTransferRequest req) {
        if (req.fromStationId().equals(req.toStationId()))
            throw new IllegalArgumentException("Trạm nguồn và đích không được trùng");
        var fromInv = inventoryRepo.findByStationId(req.fromStationId()).stream()
                .filter(i -> i.getBattery().getId().equals(req.batteryId()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Không tìm thấy pin ở trạm nguồn"));
        Station to = stationRepo.findById(req.toStationId())
                .orElseThrow(() -> new IllegalArgumentException("Trạm đích không tồn tại"));
        var moved = Inventory.builder().station(to).battery(fromInv.getBattery()).status("AVAILABLE").build();
        inventoryRepo.delete(fromInv);
        inventoryRepo.save(moved);
    }

    @Override
    public List<StationStatusSummary> summarizeStatus(Integer stationId) {
        return inventoryRepo.countByStatus(stationId).stream()
                .map(r -> new StationStatusSummary((String) r[0], ((Number) r[1]).longValue()))
                .toList();
    }
}
