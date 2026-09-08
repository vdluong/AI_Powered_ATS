package com.ats.entity;
import java.util.List;

public class Project {
    private String name;
    private List<String> technologies;
    private String description;

    public Project(String name, List<String> technologies, String description) {
        this.name = name;
        this.technologies = technologies;
        this.description = description;
    }
    // Ghi chú: Team tự dùng IDE generate các hàm Getters và Setters nhé.
}