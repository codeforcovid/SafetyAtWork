package com.safeatwork.data;

import android.content.Context;

import com.safeatwork.model.EmployeeModel;

import java.util.ArrayList;
import java.util.List;

public class DummyUserDataProvider {

    public static String rm_email = "venkat@hcl.com";
    public static String rm_name = "Venkat";
/*
    Admin
    venkat@hcl.com venkat1 seat: T1-F1-ODC1-R2-C1-S4

    Users:
    anees@hcl.com anees1 seat: T1-F1-ODC1-R1-C1-S4
    vijesh@hcl.com vijesh1 seat: T1-F1-ODC1-R4-C2-S4
    subramanya@hcl.com subramanya1 seat: T1-F1-ODC1-R4-C4-S4
    divya@hcl.com divya1 seat: T1-F1-ODC1-R3-C3-S3

    */
    public List<EmployeeModel> createDummyUserData(Context context) {
        List<EmployeeModel> employeeModelList = new ArrayList<>();
        employeeModelList.add(new EmployeeModel("Venkat", "51648432", "venkat@hcl.com", "venkat1" , "Sundar", "sundar@hcl.com","1","T1-F1-ODC1-R2-C1-S4"));
       // employeeModelList.add(new EmployeeModel("Prajeesh", "51648911", "prajeesh@hcl.com", "prajeesh" , rm_name, rm_email,"0","T1-F1-ODC1-R3-C1-S4"));
        employeeModelList.add(new EmployeeModel("Anees", "51648954", "anees@hcl.com", "anees1" , rm_name, rm_email,"0","T1-F1-ODC1-R1-C1-S4"));
        employeeModelList.add(new EmployeeModel("Vijesh", "51783957", "vijesh@hcl.com", "vijesh1" , rm_name, rm_email,"0","T1-F1-ODC1-R4-C2-S4"));
        employeeModelList.add(new EmployeeModel("Subramanya", "51648977", "subramanya@hcl.com", "subramanya1" , rm_name, rm_email,"0","T1-F1-ODC1-R4-C4-S4"));
        //employeeModelList.add(new EmployeeModel("Vinay", "51648999", "vinay@hcl.com", "vinay1", rm_name, rm_email ,"0","T1-F1-ODC1-R1-C3-S4"));
        employeeModelList.add(new EmployeeModel("Divya", "51648933", "divya@hcl.com", "divya1" , rm_name, rm_email,"0","T1-F1-ODC1-R3-C3-S3"));
        employeeModelList.add(new EmployeeModel("Sarath", "52628390", "sarath@hcl.com", "sarath1" , rm_name, rm_email,"0","T1-F1-ODC1-R1-C2-S4"));
        employeeModelList.add(new EmployeeModel("Arun", "51327777", "arun@hcl.com", "arun11" , rm_name, rm_email,"0","T1-F1-ODC1-R2-C2-S4"));
        employeeModelList.add(new EmployeeModel("Babu", "63027744", "babu@hcl.com", "babu11" , rm_name, rm_email,"0","T1-F1-ODC1-R3-C1-S3"));
        employeeModelList.add(new EmployeeModel("Rahul", "51326280", "rahul@hcl.com", "rahul1" , rm_name, rm_email,"0","T1-F1-ODC1-R1-C3-S4"));
        LocalDataBaseHelper localDataBaseHelper = new LocalDataBaseHelper(context);
        localDataBaseHelper. dbInit(localDataBaseHelper);
        if(!localDataBaseHelper.isTableExists(LocalDataBaseHelper.EMPLOYEE_TABLE_NAME)){
            for (EmployeeModel employeeModel: employeeModelList) {
                localDataBaseHelper.insertEmployeeData(employeeModel);
            }
        }
        localDataBaseHelper. dbDeInit(localDataBaseHelper);

        return employeeModelList;
    }
}


