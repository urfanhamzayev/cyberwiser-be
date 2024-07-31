package com.phoenix_sat.phoenix_sat_backend.batch;

import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.repository.OrganizationRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor
@Slf4j
public class UserItemWriter implements ItemWriter<User> {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    @Override
    public void write(Chunk<? extends User> items) throws Exception {
        if (items.isEmpty())
            return;

        String organizationId = items.getItems().get(0).getOrganizationId();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        items.forEach(item -> item.setOrganization(organization));
        userRepository.saveAll(items);
        log.info("All user from csv file is saved to the database.");
        log.info("Job's ended successfully");
    }
}
