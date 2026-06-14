package com.fishing.service;

import com.fishing.entity.FishSpecies;

import java.util.List;

public interface FishSpeciesService {

    List<FishSpecies> listAll();

    FishSpecies getById(Long id);
}
