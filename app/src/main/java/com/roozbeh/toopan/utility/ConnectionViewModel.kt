package com.roozbeh.toopan.utility;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;



public class ConnectionViewModel extends ViewModel {

    private final MutableLiveData<ConnectionModel> mutableLiveData = new MutableLiveData<ConnectionModel>();

    private static final ConnectionViewModel connectionViewModel = new ConnectionViewModel();

    private ConnectionViewModel() {
    }

    public static ConnectionViewModel getInstance() {
        return connectionViewModel;
    }




    private final MutableLiveData<Boolean> booleanMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> getUpdateUser() {
        return booleanMutableLiveData;
    }



}
