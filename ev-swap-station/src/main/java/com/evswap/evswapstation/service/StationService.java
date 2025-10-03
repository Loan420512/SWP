package com.evswap.evswapstation.service;

import com.evswap.evswapstation.entity.Station;
import com.evswap.evswapstation.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationService {
    private final StationRepository stationRepository;

    public List<Station> getAll() {
        return stationRepository.findAll();
    }

    public Optional<Station> getById(Integer id) {
        return stationRepository.findById(id);
    }

    public Station create(Station station) {
        return stationRepository.save(station);
    }

    public Station update(Integer id, Station station) {
        return stationRepository.findById(id)
                .map(s -> {
                    s.setStationName(station.getStationName());
                    s.setAddress(station.getAddress());
                    s.setStationStatus(station.getStationStatus());
                    s.setContact(station.getContact());
                    return stationRepository.save(s);
                }).orElseThrow(() -> new RuntimeException("Station not found"));
    }

    public void delete(Integer id) {
        stationRepository.deleteById(id);
    }
}
