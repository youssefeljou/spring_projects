package com.example.education.controller;

import com.example.education.dto.MasterDto;
import com.example.education.service.MasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/masters")
@RequiredArgsConstructor
public class MasterController {
    private final MasterService masterService;

    @PostMapping("/{userId}")
    public ResponseEntity<MasterDto> createMaster(@RequestBody MasterDto masterDto) {
        MasterDto createdMaster = masterService.createMaster(masterDto);
        return new ResponseEntity<>(createdMaster, HttpStatus.CREATED);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<MasterDto> getMasterById(@PathVariable long id) {
        MasterDto masterDto = masterService.findById(id);
        return ResponseEntity.ok(masterDto);
    }

    @GetMapping("/id/{id}/info")
    public ResponseEntity<?> getMasterWithAllInfo(@PathVariable long id) {
        return ResponseEntity.ok(masterService.findByIdWithAllInfo(id));
    }

    @GetMapping("all")
    public ResponseEntity<List<MasterDto>> getAllMasters() {
        List<MasterDto> masters = masterService.findAll();
        return ResponseEntity.ok(masters);
    }

    @GetMapping("/all/info")
    public ResponseEntity<List<?>> getAllMastersWithInfo() {
        return ResponseEntity.ok(masterService.findAllWithAllInfo());
    }

    @PostMapping("/{masterId}/course/{courseId}")
    public ResponseEntity<MasterDto> assignCourseToMaster(@PathVariable long masterId, @PathVariable long courseId) {
        MasterDto updatedMaster = masterService.assignCourse(courseId, masterId);
        return ResponseEntity.ok(updatedMaster);
    }

    @DeleteMapping("/{masterId}/course")
    public ResponseEntity<MasterDto> removeCourseFromMaster(@PathVariable long masterId) {
        MasterDto updatedMaster = masterService.removeCourse(masterId);
        return ResponseEntity.ok(updatedMaster);
    }
}