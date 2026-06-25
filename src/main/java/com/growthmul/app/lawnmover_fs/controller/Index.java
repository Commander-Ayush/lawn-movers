package com.growthmul.app.lawnmover_fs.controller;

import com.growthmul.app.lawnmover_fs.config.TenantContext;
import com.growthmul.app.lawnmover_fs.entity.ServiceOffering;
import com.growthmul.app.lawnmover_fs.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class Index {

    @Autowired
    private ServiceRepository serviceRepo;

    @Autowired
    private TenantContext tenantContext;

    @GetMapping({"", "/", "/home"})
    public String index(Model model) {
        model.addAttribute("company", tenantContext.getCompany());
        return "index";
    }

    @GetMapping("/services")
    public String services(Model model) {
        Long tenantId = tenantContext.getTenantId();
        model.addAttribute("company", tenantContext.getCompany());

        List<ServiceOffering> services = serviceRepo.findByCompanyIdAndTypeOrderBySortOrder(tenantId, "service");
        List<ServiceOffering> plans = serviceRepo.findByCompanyIdAndTypeOrderBySortOrder(tenantId, "plan");
        List<ServiceOffering> addons = serviceRepo.findByCompanyIdAndTypeOrderBySortOrder(tenantId, "addon");

        // fallback to hardcoded demo content if this tenant hasn't added real data yet
        if (services.isEmpty()) services = getHardcodedServices();
        if (plans.isEmpty()) plans = getHardcodedPlans();
        if (addons.isEmpty()) addons = getHardcodedAddons();

        model.addAttribute("services", services);
        model.addAttribute("plans", plans);
        model.addAttribute("addons", addons);
        return "services";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        model.addAttribute("company", tenantContext.getCompany());
        return "reviews";
    }

    private List<ServiceOffering> getHardcodedServices() {
        return List.of(
                svc("🌿", "Lawn Mowing", "Weekly or bi-weekly mowing, edging along driveways and walkways, and full cleanup of all clippings.", "Starting at $45 / visit", "service", 1),
                svc("✂️", "Trimming & Edging", "Crisp edges along every fence, flower bed, and curb.", "Starting at $35 / visit", "service", 2),
                svc("🍂", "Seasonal Cleanup", "Spring and fall deep cleans — leaf removal, debris hauling, and bed prep.", "Starting at $120 / session", "service", 3),
                svc("💧", "Irrigation Check", "Sprinkler inspections, head adjustments, and timer programming.", "Starting at $80 / visit", "service", 4),
                svc("🌱", "Overseeding & Fertilizing", "Thicker, greener grass starts with the right soil treatment.", "Starting at $95 / treatment", "service", 5),
                svc("🏡", "Yard Debris Removal", "Branches, clippings, and organic debris — hauled away cleanly.", "Starting at $65 / visit", "service", 6)
        );
    }

    private List<ServiceOffering> getHardcodedPlans() {
        ServiceOffering basic = svc("", "Basic", "Perfect for small yards", "$79", "plan", 1);
        basic.setFeatures(List.of("Bi-weekly mowing", "Edging & blowing", "Clipping cleanup", "Email support"));

        ServiceOffering standard = svc("", "Standard", "Our most popular plan", "$139", "plan", 2);
        standard.setFeatured(true);
        standard.setFeatures(List.of("Weekly mowing", "Edging & trimming", "Monthly fertilizing", "Seasonal cleanup (x2)", "Priority scheduling", "Phone & email support"));

        ServiceOffering premium = svc("", "Premium", "Full-service yard care", "$199", "plan", 3);
        premium.setFeatures(List.of("Everything in Standard", "Irrigation check (monthly)", "Overseeding (seasonal)", "Debris removal included", "Dedicated account manager", "Same-day service calls"));

        return List.of(basic, standard, premium);
    }

    private List<ServiceOffering> getHardcodedAddons() {
        return List.of(
                svc("🌸", "Flower Bed Weeding", "Hand-pull weeds from all flower beds and mulched areas.", "From $40 / visit", "addon", 1),
                svc("🪵", "Fresh Mulching", "Refresh mulch in beds to retain moisture and improve curb appeal.", "From $85 / yard", "addon", 2),
                svc("🌳", "Tree & Shrub Trimming", "Shape hedges, shrubs, and small trees for a clean, manicured look.", "From $60 / session", "addon", 3),
                svc("🧹", "Driveway & Path Edging", "Define clean borders between lawn and hardscapes.", "From $30 / visit", "addon", 4)
        );
    }

    private ServiceOffering svc(String icon, String name, String desc, String price, String type, int order) {
        ServiceOffering s = new ServiceOffering();
        s.setIcon(icon); s.setName(name); s.setDescription(desc);
        s.setPrice(price); s.setType(type); s.setSortOrder(order);
        return s;
    }
}
