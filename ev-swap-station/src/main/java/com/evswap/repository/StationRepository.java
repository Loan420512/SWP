package com.evswap.repository;

import com.evswap.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StationRepository extends JpaRepository<Station, Integer> {
    List<Station> findByStationStatus(String stationStatus);
    List<Station> findByAddressContaining(String address);
}
