package com.gundam.gundam_web.entity;
import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    
    // === 【新增】 ===
    private String role; 

    // === 【新增 Getter 和 Setter】 ===
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // ... (其他原有的 Getter/Setter 保持不变) ...
}