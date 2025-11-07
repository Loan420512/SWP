package com.evswap.service;

import com.evswap.entity.PackagePlan;
import com.evswap.repository.PackagePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackagePlanService {

    private final PackagePlanRepository packageRepo;

    public List<PackagePlan> getAllPackages() {
        return packageRepo.findAll();
    }

    public PackagePlan getById(Integer id) {
        return packageRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy gói dịch vụ"));
    }
}
