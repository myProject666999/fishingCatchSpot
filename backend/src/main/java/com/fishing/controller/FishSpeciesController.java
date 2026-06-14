package com.fishing.controller;

import com.fishing.common.Result;
import com.fishing.entity.FishSpecies;
import com.fishing.service.FishSpeciesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping
@Validated
public class FishSpeciesController {

    @Autowired
    private FishSpeciesService fishSpeciesService;

    @GetMapping("/api/species/list")
    public Result<List<FishSpecies>> listAll() {
        log.debug("listAll invoked");
        List<FishSpecies> list = fishSpeciesService.listAll();
        return Result.success(list);
    }

    @GetMapping("/api/species/{id}")
    public Result<FishSpecies> getById(@PathVariable Long id) {
        log.debug("getById invoked, id={}", id);
        FishSpecies fishSpecies = fishSpeciesService.getById(id);
        return Result.success(fishSpecies);
    }
}
