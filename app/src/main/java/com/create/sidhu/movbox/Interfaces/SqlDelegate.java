package com.create.sidhu.movbox.Interfaces;

import com.create.sidhu.movbox.helpers.SqlHelper;

import org.json.JSONObject;

/**
 * Creates Response
 */

public interface SqlDelegate {
    void onResponse(SqlHelper sqlHelper);
}
