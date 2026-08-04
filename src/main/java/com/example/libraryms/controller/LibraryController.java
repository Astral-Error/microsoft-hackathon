package com.example.libraryms.controller;

import com.example.libraryms.dto.BorrowRequestForm;
import com.example.libraryms.service.LibraryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @ModelAttribute("requestForm")
    public BorrowRequestForm requestForm() {
        return new BorrowRequestForm();
    }

    @ModelAttribute("currentUser")
    public CurrentUser currentUser() {
        return new CurrentUser("Demo Librarian", "Admin");
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("dashboard", libraryService.getDashboard());
        return "index";
    }

    @PostMapping("/requests")
    public String createRequest(@Valid @ModelAttribute("requestForm") BorrowRequestForm form,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("dashboard", libraryService.getDashboard());
            return "index";
        }
        try {
            libraryService.submitBorrowingRequest(form);
            redirectAttributes.addFlashAttribute("successMessage", "Borrowing request submitted successfully.");
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("borrowRequest", ex.getMessage());
            model.addAttribute("dashboard", libraryService.getDashboard());
            return "index";
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.approveRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Request approved as a confirmed reservation.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.rejectRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Request rejected and the copy released.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{id}/issue")
    public String issue(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.issueRequest(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation issued as a loan.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.cancelReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reservation cancelled and the copy released.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{id}/return")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.returnBook(id, false, null);
            redirectAttributes.addFlashAttribute("successMessage", "Book returned and copy released.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/requests/{id}/return-unavailable")
    public String returnUnavailable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.returnBook(id, true, "Marked unavailable during return.");
            redirectAttributes.addFlashAttribute("successMessage", "Book returned but marked unavailable for maintenance.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/dashboard";
    }

    public record CurrentUser(String displayName, String role) {
    }
}