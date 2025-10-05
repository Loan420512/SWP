package com.evswap.service;

import com.evswap.dto.inventory.*;
import java.util.List;

public interface InventoryService {
    List<InventoryItemResponse> listByStation(Integer stationId);
    InventoryItemResponse add(InventoryCreateRequest req);
    void updateStatus(Integer inventoryId, InventoryUpdateStatusRequest req);
    void transfer(InventoryTransferRequest req);
    List<StationStatusSummary> summarizeStatus(Integer stationId);
}
