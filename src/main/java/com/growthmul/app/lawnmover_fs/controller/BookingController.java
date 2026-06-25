package com.growthmul.app.lawnmover_fs.controller;

import com.growthmul.app.lawnmover_fs.config.TenantContext;
import com.growthmul.app.lawnmover_fs.entity.BookingRequest;
import com.growthmul.app.lawnmover_fs.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookingController {

    @Autowired
    private BookingRepository bookingRepo;

    @Autowired
    private TenantContext tenantContext;

    @GetMapping("/booking")
    public String bookingPage(Model model) {
        model.addAttribute("company", tenantContext.getCompany());
        model.addAttribute("bookingRequest", new BookingRequest());
        return "booking";
    }

    @PostMapping("/booking")
    public String submitBooking(@ModelAttribute BookingRequest req, Model model) {
        req.setCompany(tenantContext.getCompany()); // tag this booking to the current tenant
        bookingRepo.save(req);
        model.addAttribute("company", tenantContext.getCompany());
        model.addAttribute("submitted", true);
        model.addAttribute("bookingRequest", new BookingRequest());
        return "booking";
    }
}
