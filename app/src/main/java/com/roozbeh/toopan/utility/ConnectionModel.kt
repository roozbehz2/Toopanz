package com.roozbeh.toopan.utility;

public class ConnectionModel {

    private static final ConnectionModel connectionModel = new ConnectionModel();

    public static ConnectionModel getInstance() {
        return connectionModel;
    }

    private ConnectionModel() {
    }

    private boolean updateUser = true;

    public boolean isUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(boolean updateUser) {
        this.updateUser = updateUser;
    }

}
