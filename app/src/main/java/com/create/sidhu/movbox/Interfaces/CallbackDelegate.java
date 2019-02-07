package com.create.sidhu.movbox.Interfaces;

import android.os.Bundle;

import java.util.HashMap;

public interface CallbackDelegate {
    void onResultReceived(String type, boolean resultCode, HashMap<String, String> extras);
    void onResultReceived(String type, boolean resultCode, Bundle extras);
}
