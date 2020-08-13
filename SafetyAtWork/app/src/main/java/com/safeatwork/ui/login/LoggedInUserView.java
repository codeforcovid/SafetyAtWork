package com.safeatwork.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {

    private String userId;
    private String displayName;
    private String empEmail;
    private String empPassword;
    private String adminAccess;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String userId,String displayName,String empEmail,String empPassword,String adminAccess) {
        this.userId = userId;
        this.displayName = displayName;
        this.empEmail = empEmail;
        this.empPassword = empPassword;
        this.adminAccess=adminAccess;
    }


    public String getAdminAccess() {
        return adminAccess;
    }

    public void setAdminAccess(String adminAccess) {
        this.adminAccess = adminAccess;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public String getEmpPassword() {
        return empPassword;
    }

    String getDisplayName() {
        return displayName;
    }
}
