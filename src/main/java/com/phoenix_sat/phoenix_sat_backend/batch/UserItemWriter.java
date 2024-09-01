package com.phoenix_sat.phoenix_sat_backend.batch;

import com.phoenix_sat.phoenix_sat_backend.config.CustomEventPublisher;
import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.event.RegistrationVerificationEvent;
import com.phoenix_sat.phoenix_sat_backend.repository.OrganizationRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import com.phoenix_sat.phoenix_sat_backend.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class UserItemWriter implements ItemWriter<User> {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final CustomEventPublisher customEventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void write(Chunk<? extends User> items) throws Exception {
        if (items.isEmpty())
            return;

        String organizationId = items.getItems().get(0).getOrganizationId();
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found OrgId " + organizationId));

        List<User> users = checkUsersIsRegisteredBeforeAndRetrieveNotRegisteredOnesAsList(items, organization.getId());

        items.forEach(item -> item.setOrganization(organization));
        List<String> tempPasswords = setTemporaryPasswordForAllUsersAndRetrieve(items.getItems());
        userRepository.saveAll(users);
        log.info("Users {} from csv file is saved to the database.Organization id {}", users, organization.getId());
        customEventPublisher.publish(new RegistrationVerificationEvent(users, tempPasswords));
        log.info("User import Job's ended successfully. Organization id-" + organization.getId());
    }

    private List<User> checkUsersIsRegisteredBeforeAndRetrieveNotRegisteredOnesAsList(Chunk<? extends User> items, String orgId) {
        List<User> registeredUsers = userRepository.findAllByOrganizationId(orgId);

        Set<String> registeredUserEmails = registeredUsers.stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());

        return items.getItems().stream()
                .filter(user -> !registeredUserEmails.contains(user.getEmail()))
                .collect(Collectors.toList());
    }


    private List<String> setTemporaryPasswordForAllUsersAndRetrieve(List<? extends User> users) {
        List<String> temporaryPasswords = new ArrayList<>();
        for (User user : users) {
            String tempPass = UserUtil.getTemporaryPasswordForUser();
            temporaryPasswords.add(tempPass);
            user.setPassword(passwordEncoder.encode(tempPass));
        }
        return temporaryPasswords;
    }


}
