package com.safeatwork.model;

public class EmployeeModel {

    String emp_name;
    String emp_id;
    String emp_email;
    String emp_password;
    String rm_email;
    String rm_name;
    String seat_code;

    //1: admin_rights
    //0: not admin_rights
    String admin_rights = "0";

    public EmployeeModel(String emp_name, String emp_id, String emp_email, String emp_password, String rm_email, String rm_name, String admin_rights,String seat_code) {
        this.emp_name = emp_name;
        this.emp_id = emp_id;
        this.emp_email = emp_email;
        this.emp_password = emp_password;
        this.rm_email = rm_email;
        this.rm_name = rm_name;
        this.admin_rights = admin_rights;
        this.seat_code = seat_code;
    }

    public EmployeeModel() {

    }


    public String getSeat_code() {
        return seat_code;
    }

    public void setSeat_code(String seat_code) {
        this.seat_code = seat_code;
    }
    public String getAdmin_rights() {
        return admin_rights;
    }

    public void setAdmin_rights(String admin_rights) {
        this.admin_rights = admin_rights;
    }

    public String getRm_email() {
        return rm_email;
    }

    public void setRm_email(String rm_email) {
        this.rm_email = rm_email;
    }

    public String getRm_name() {
        return rm_name;
    }

    public void setRm_name(String rm_name) {
        this.rm_name = rm_name;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmp_email() {
        return emp_email;
    }

    public void setEmp_email(String emp_email) {
        this.emp_email = emp_email;
    }

    public String getEmp_password() {
        return emp_password;
    }

    public void setEmp_password(String emp_password) {
        this.emp_password = emp_password;
    }


}