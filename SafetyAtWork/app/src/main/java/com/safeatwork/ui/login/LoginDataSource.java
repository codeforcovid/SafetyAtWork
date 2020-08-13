package com.safeatwork.ui.login;

import android.content.Context;
import android.util.Log;

import com.safeatwork.data.LocalDataBaseHelper;
import com.safeatwork.model.EmployeeModel;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    Context applicationContext;

    public LoginDataSource(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Result<LoggedInUser> login(String username, String password) {
        Log.e("login method", "login");
        try {
            LocalDataBaseHelper localDataBaseHelper = new LocalDataBaseHelper(applicationContext);
            localDataBaseHelper.dbInit(localDataBaseHelper);
            /*if(localDataBaseHelper.isTableExists(LocalDataBaseHelper.EMPLOYEE_TABLE_NAME)){
                for (EmployeeModel employeeModel: employeeModelList) {
                    localDataBaseHelper.insertEmployeeData(employeeModel);
                }
            }*/
            EmployeeModel employeeModel = localDataBaseHelper.getUserDetails(username, password);
            localDataBaseHelper.dbDeInit(localDataBaseHelper);

            /*DummyUserDataProvider dummyUserDataProvider = new DummyUserDataProvider();
            EmployeeModel employeeModel = dummyUserDataProvider.validateCredentials(username, password);*/
            Log.e("employeeModel ", "" + employeeModel.getEmp_name());
            if (employeeModel == null) {
                return new Result.Error(new IOException("Error logging in", null));
            } else {
                LoggedInUser fakeUser =
                        new LoggedInUser(
                                java.util.UUID.randomUUID().toString(),
                                employeeModel.getEmp_name(), employeeModel.getEmp_email(),
                                employeeModel.getEmp_password(),employeeModel.getAdmin_rights());
                return new Result.Success<>(fakeUser);
            }
            // TODO: handle loggedInUser authentication
           /* LoggedInUser fakeUser =
                    new LoggedInUser(
                            java.util.UUID.randomUUID().toString(),
                            "Jane Doe");
            return new Result.Success<>(fakeUser);*/
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
