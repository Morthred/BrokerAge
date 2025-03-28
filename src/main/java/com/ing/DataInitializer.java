package com.ing;

import com.ing.model.Asset;
import com.ing.model.Customer;
import com.ing.repository.AssetRepository;
import com.ing.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public void run(String... args) throws Exception {
        Customer customer = new Customer();
        customer.setUsername("sampleUser");
        customer.setPassword("password");
        customerRepository.save(customer);

        Asset assetTRY = Asset.builder()
                .customer(customer)
                .assetName("TRY")
                .size(800)
                .usableSize(800)
                .build();
        assetRepository.save(assetTRY);

        Asset assetBTC = Asset.builder()
                .customer(customer)
                .assetName("BTC")
                .size(5)
                .usableSize(5)
                .build();
        assetRepository.save(assetBTC);
    }
}