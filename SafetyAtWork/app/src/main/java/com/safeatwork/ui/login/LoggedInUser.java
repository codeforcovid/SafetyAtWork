package com.safeatwork.ui.login;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String userId;
    private String displayName;
    private String empEmail;
    private String empPassword;

    public String getAdminAccess() {
        return adminAccess;
    }

    public void setAdminAccess(String adminAccess) {
        this.adminAccess = adminAccess;
    }

    private String adminAccess;

    public LoggedInUser(String userId, String displayName,String empEmail,String empPassword,String adminAccess) {
        this.userId = userId;
        this.displayName = displayName;
        this.empEmail = empEmail;
        this.empPassword = empPassword;
        this.adminAccess=adminAccess;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
