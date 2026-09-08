package com.ats.entity;
import java.util.List;

public class Candidate {
    private String fullName;
    private String email;
    private List<String> technicalSkills;
    private List<Project> projects;

    public Candidate(String fullName, String email, List<String> technicalSkills, List<Project> projects) {
        this.fullName = fullName;
        this.email = email;
        this.technicalSkills = technicalSkills;
        this.projects = projects;
    }
    // Ghi chú: Team tự dùng IDE generate Getters và Setters.
}