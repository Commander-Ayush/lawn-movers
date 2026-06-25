package com.growthmul.app.lawnmover_fs.config;

import com.growthmul.app.lawnmover_fs.entity.Company;
import com.growthmul.app.lawnmover_fs.repository.CompanyRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * One deployment of this app = one business.
 * Which business is decided entirely by the app.tenant.id property
 * (set via an environment variable per deployment).
 *
 * Every controller asks THIS class for the current company / tenant id
 * instead of hardcoding company details or guessing from the URL.
 */
@Component
public class TenantContext {

    private final CompanyRepo companyRepo;

    @Value("${app.tenant.id}")
    private Long tenantId;

    private Company cachedCompany;

    public TenantContext(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public Company getCompany() {
        if (cachedCompany == null) {
            cachedCompany = companyRepo.findById(tenantId)
                    .orElseThrow(() -> new IllegalStateException(
                            "No company row found for app.tenant.id=" + tenantId +
                            ". Did you insert the company row for this deployment yet?"));
        }
        return cachedCompany;
    }
}
