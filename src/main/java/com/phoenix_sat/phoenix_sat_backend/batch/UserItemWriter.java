package com.phoenix_sat.phoenix_sat_backend.batch;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@RequiredArgsConstructor

public class UserItemWriter implements ItemWriter<User> {
    private final UserRepository userRepository;

    @Override
    public void write(Chunk<? extends User> items) throws Exception {
        if (items.isEmpty())
            return;

        userRepository.saveAll(items);
    }
}
