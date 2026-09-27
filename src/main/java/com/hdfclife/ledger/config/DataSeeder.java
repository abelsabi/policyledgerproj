package com.hdfclife.ledger.config;

import com.hdfclife.ledger.domain.Customer;
import com.hdfclife.ledger.domain.Policy;
import com.hdfclife.ledger.domain.Rider;
import com.hdfclife.ledger.repo.CustomerRepository;
import com.hdfclife.ledger.repo.PolicyRepository;
import com.hdfclife.ledger.repo.RiderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final PolicyRepository policyRepository;
    private final RiderRepository riderRepository;
    private final Environment environment;

    public DataSeeder(
            CustomerRepository customerRepository,
            PolicyRepository policyRepository,
            RiderRepository riderRepository,
            Environment environment
    ) {
        this.customerRepository = customerRepository;
        this.policyRepository = policyRepository;
        this.riderRepository = riderRepository;
        this.environment = environment;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (policyRepository.count() == 0) {
            seed();
        }
        printStartup();
    }

    private void seed() {
        Customer anita = customerRepository.save(new Customer("Anita Sharma", "anita.sharma@hdfclife.example"));
        Customer rahul = customerRepository.save(new Customer("Rahul Mehta", "rahul.mehta@hdfclife.example"));
        Customer priya = customerRepository.save(new Customer("Priya Nair", "priya.nair@hdfclife.example"));
        Customer vikram = customerRepository.save(new Customer("Vikram Singh", "vikram.singh@hdfclife.example"));
        Customer sneha = customerRepository.save(new Customer("Sneha Patel", "sneha.patel@hdfclife.example"));

        Policy p1001 = policyRepository.save(new Policy("HDFC-LIFE-1001", anita, "TERM", 18500, "Active"));
        policyRepository.save(new Policy("HDFC-LIFE-1002", rahul, "ULIP", 42000, "Active"));
        policyRepository.save(new Policy("HDFC-LIFE-1003", priya, "ENDOWMENT", 27000, "Lapsed"));
        policyRepository.save(new Policy("HDFC-LIFE-1004", vikram, "TERM", 15200, "Active"));
        policyRepository.save(new Policy("HDFC-LIFE-1005", sneha, "ULIP", 36000, "Active"));
        policyRepository.save(new Policy("HDFC-LIFE-1006", anita, "ENDOWMENT", 22000, "Pending"));

        Rider accident = riderRepository.findByCode("ACCIDENT_COVER").orElseThrow();
        Rider waiver = riderRepository.findByCode("WAIVER_OF_PREMIUM").orElseThrow();
        p1001.getRiders().add(accident);
        p1001.getRiders().add(waiver);
        policyRepository.save(p1001);
    }

    private void printStartup() {
        String profile = Arrays.stream(environment.getActiveProfiles()).findFirst()
                .orElse(Arrays.stream(environment.getDefaultProfiles()).findFirst().orElse("dev"));
        System.out.println("Active profile → " + profile);
        System.out.println("Policy count → " + policyRepository.count());
        System.out.println("Customer count → " + customerRepository.count());
        String lookupCustomer = policyRepository.findByPolicyNo("HDFC-LIFE-1004")
                .map(p -> p.getCustomer().getFullName())
                .orElse("missing");
        System.out.println("Lookup HDFC-LIFE-1004 customer → " + lookupCustomer);
        System.out.println("Active count → " + policyRepository.findByStatusOrderByPolicyNoAsc("Active").size());
        System.out.println("TERM count → " + policyRepository.findByProductTypeOrderByPolicyNoAsc("TERM").size());
        System.out.println("Anita Sharma policy count → "
                + policyRepository.findByCustomer_FullNameOrderByPolicyNoAsc("Anita Sharma").size());
        String minPremiumNos = policyRepository.findWithPremiumAtLeast(20000).stream()
                .map(Policy::getPolicyNo)
                .collect(Collectors.joining(", "));
        System.out.println("minPremium=20000 policy numbers → " + minPremiumNos);
        Policy p1001 = policyRepository.findByPolicyNo("HDFC-LIFE-1001").orElseThrow();
        String riderCodes = p1001.getRiders().stream()
                .map(Rider::getCode)
                .sorted()
                .collect(Collectors.joining(", "));
        System.out.println("HDFC-LIFE-1001 rider codes → " + riderCodes);
    }
}
