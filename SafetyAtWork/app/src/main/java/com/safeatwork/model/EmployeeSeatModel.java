package com.safeatwork.model;

public class EmployeeSeatModel {

    String emp_name;
    String emp_id;
    String rm_email;
    String rm_name;

    public EmployeeSeatModel(String emp_name, String emp_id, String rm_name, String rm_email) {
        this.emp_name = emp_name;
        this.emp_id = emp_id;
        this.rm_email = rm_email;
        this.rm_name = rm_name;
    }

    public EmployeeSeatModel(String emp_name, String emp_id, String rm_email) {
        this.emp_name = emp_name;
        this.emp_id = emp_id;
        this.rm_email = rm_email;
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

}