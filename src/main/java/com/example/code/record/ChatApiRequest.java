package com.example.code.record;

import java.util.Arrays;
import java.util.List;

public record ChatApiRequest(
        String model,
        List<Message> messages
) {
    public ChatApiRequest(String model, String prompt) {
        this(model, Arrays.asList(new Message("user", prompt)));
    }
}
