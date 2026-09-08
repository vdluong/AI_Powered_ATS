package com.ats.entity;
import java.util.ArrayList;
import java.util.List;

public class JobProfile {
    private final String roleName;
    private final List<String> requiredSkills;
    private final boolean requireAiKnowledge;

    // Constructor để private, bắt buộc phải tạo qua Builder
    private JobProfile(Builder builder) {
        this.roleName = builder.roleName;
        this.requiredSkills = builder.requiredSkills;
        this.requireAiKnowledge = builder.requireAiKnowledge;
    }

    // Static Builder Class
    public static class Builder {
        private String roleName;
        private List<String> requiredSkills = new ArrayList<>();
        private boolean requireAiKnowledge = false;

        public Builder setRoleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public Builder addSkill(String skill) {
            this.requiredSkills.add(skill);
            return this;
        }

        public Builder requireAiKnowledge(boolean isRequired) {
            this.requireAiKnowledge = isRequired;
            return this;
        }

        public JobProfile build() {
            return new JobProfile(this);
        }
    }
    // Ghi chú: Team tự dùng IDE generate Getters.
}