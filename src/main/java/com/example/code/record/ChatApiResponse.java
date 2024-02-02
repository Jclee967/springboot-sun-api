package com.example.code.record;

import java.util.List;

public record ChatApiResponse(List<Choice> choices) {
    public record Choice(int index, Message message){}

}
