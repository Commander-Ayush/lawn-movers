package com.growthmul.app.lawnmover_fs.controller;

import com.growthmul.app.lawnmover_fs.config.TenantContext;
import com.growthmul.app.lawnmover_fs.entity.*;
import com.growthmul.app.lawnmover_fs.repository.BookingRepository;
import com.growthmul.app.lawnmover_fs.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private TenantContext tenantContext;

    //==LOGIN==
    @GetMapping("/login")
    public String login() {
        return "admin/adminLogin";
    }

    // ── DASHBOARD ──
    @GetMapping
    public String dashboard(Model model) {
        Long tenantId = tenantContext.getTenantId();
        model.addAttribute("requests", bookingRepo.findByCompanyIdOrderBySubmittedAtDesc(tenantId));
        model.addAttribute("totalRequests", bookingRepo.countByCompanyId(tenantId));
        model.addAttribute("pending", bookingRepo.findByCompanyIdAndCompleted(tenantId, false).size());
        model.addAttribute("completed", bookingRepo.findByCompanyIdAndCompleted(tenantId, true).size());
        return "admin/dashboard";
    }

    // ── MARK COMPLETE ──
    @PostMapping("/requests/{id}/complete")
    public String markComplete(@PathVariable Long id) {
        BookingRequest req = ownedBookingOrThrow(id);
        req.setCompleted(true);
        bookingRepo.save(req);
        return "redirect:/admin";
    }

    // ── MARK INCOMPLETE ──
    @PostMapping("/requests/{id}/reopen")
    public String reopen(@PathVariable Long id) {
        BookingRequest req = ownedBookingOrThrow(id);
        req.setCompleted(false);
        bookingRepo.save(req);
        return "redirect:/admin";
    }

    // ── DELETE REQUEST ──
    @PostMapping("/requests/{id}/delete")
    public String deleteRequest(@PathVariable Long id) {
        BookingRequest req = ownedBookingOrThrow(id);
        bookingRepo.delete(req);
        return "redirect:/admin";
    }

    // ── SERVICES PAGE ──
    @GetMapping("/services")
    public String services(Model model) {
        Long tenantId = tenantContext.getTenantId();
        model.addAttribute("services", serviceRepo.findByCompanyIdAndTypeOrderBySortOrder(tenantId, "service"));
        model.addAttribute("plans", serviceRepo.findByCompanyIdAndTypeOrderBySortOrder(tenantId, "plan"));
        model.addAttribute("addons", serviceRepo.findByCompanyIdAndTypeOrderBySortOrder(tenantId, "addon"));
        model.addAttribute("newService", new ServiceOffering());
        return "admin/services";
    }

    // ── ADD SERVICE ──
    @PostMapping("/services/add")
    public String addService(@ModelAttribute ServiceOffering svc,
                             @RequestParam String type,
                             @RequestParam(required = false) List<String> features) {
        svc.setType(type);
        svc.setCompany(tenantContext.getCompany()); // tag to the current tenant
        if (features != null) svc.setFeatures(features);
        serviceRepo.save(svc);
        return "redirect:/admin/services";
    }

    // ── DELETE SERVICE ──
    @PostMapping("/services/{id}/delete")
    public String deleteService(@PathVariable Long id) {
        ServiceOffering svc = ownedServiceOrThrow(id);
        serviceRepo.delete(svc);
        return "redirect:/admin/services";
    }

    // ── EDIT SERVICE ──
    @PostMapping("/services/{id}/edit")
    public String editService(@PathVariable Long id,
                              @ModelAttribute ServiceOffering updated,
                              @RequestParam(required = false) List<String> features) {
        ServiceOffering existing = ownedServiceOrThrow(id);
        existing.setIcon(updated.getIcon());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setFeatured(updated.isFeatured());
        if (features != null) existing.setFeatures(features);
        serviceRepo.save(existing);
        return "redirect:/admin/services";
    }

    // ── ownership guards: prevent one tenant's admin from touching another tenant's rows ──

    private BookingRequest ownedBookingOrThrow(Long id) {
        BookingRequest req = bookingRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (!req.getCompany().getId().equals(tenantContext.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your booking request");
        }
        return req;
    }

    private ServiceOffering ownedServiceOrThrow(Long id) {
        ServiceOffering svc = serviceRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service not found"));
        if (!svc.getCompany().getId().equals(tenantContext.getTenantId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your service");
        }
        return svc;
    }
}
