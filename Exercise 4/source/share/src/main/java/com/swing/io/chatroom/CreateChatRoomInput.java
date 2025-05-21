package com.swing.io.chatroom;

import com.swing.types.Result;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;


@Getter
public class CreateChatRoomInput {
    private String name;
    private List<String> otherUserIds;
    private boolean isGroup;
    private CreateChatRoomInput() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CreateChatRoomInput input;

        public Builder() {
            input = new CreateChatRoomInput();
        }

        public Builder name(String name) {
            input.name = name;
            return this;
        }
        public Builder otherUserIds(String ...otherUserIds) {
            input.otherUserIds = Arrays.asList(otherUserIds);
            return this;
        }

        public Builder isGroup(boolean isGroup) {
            input.isGroup = isGroup;
            return this;
        }

        public Result<CreateChatRoomInput> build() {
            if (input.getOtherUserIds() == null || input.getOtherUserIds().isEmpty()) {
                return Result.failure(new IllegalArgumentException("Must be at least one user id"));
            }
            return Result.success(input);
        }
    }
}
