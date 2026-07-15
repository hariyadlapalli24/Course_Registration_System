package com.abc.registration.dto;

import com.abc.registration.model.Role;
import com.abc.registration.model.User;

public class UserResponse {
    private Long id;
    private String name;
    private String department;
    private String rollNumber;
    private String email;
    private Role role;

    public static UserResponse from(User user) {
        UserResponse dto = new UserResponse();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.department = user.getDepartment();
        dto.rollNumber = user.getRollNumber();
        dto.email = user.getEmail();
        dto.role = user.getRole();
        return dto;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getRollNumber() { return rollNumber; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}
