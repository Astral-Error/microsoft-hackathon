package com.microsoft.libmanagement.controller;

import com.microsoft.libmanagement.dto.BorrowRequestCreateRequest;
import com.microsoft.libmanagement.dto.BorrowRequestResponse;
import com.microsoft.libmanagement.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
public class LibraryController {
    private final LibraryService libraryService = new LibraryService();

    @GetMapping("/requests")
    public List<BorrowRequestResponse> getRequests() {
        return libraryService.listRequests();
    }

    @PostMapping("/requests")
    public ResponseEntity<?> submitRequest(@RequestBody BorrowRequestCreateRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.submitBorrowRequest(request));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/requests/{id}/review")
    public BorrowRequestResponse reviewRequest(@PathVariable Long id, @RequestParam boolean approved, @RequestParam String comment) {
        return libraryService.reviewRequest(id, approved, comment);
    }

    @PostMapping("/requests/{id}/issue")
    public BorrowRequestResponse issueRequest(@PathVariable Long id) {
        return libraryService.issueRequest(id);
    }

    @PostMapping("/requests/{id}/return")
    public BorrowRequestResponse returnRequest(@PathVariable Long id, @RequestParam boolean markUnavailable) {
        return libraryService.returnRequest(id, markUnavailable);
    }
}
