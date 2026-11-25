
package com.zaax.booster;

import android.os.Process;
import android.util.Log;

public class ZaaxUserService extends IUserService.Stub {

    private static final String TAG = "ZAAX_USER_SERVICE";

    public ZaaxUserService() {
        super();
        Log.i(TAG, "ZAAX UserService started");
    }

    @Override
    public void destroy() {
        Log.i(TAG, "ZAAX UserService destroyed");
        System.exit(0);
    }

    @Override
    public void exit() {
        destroy();
    }

    @Override
    public String getServiceInfo() {
        return "ZAAX USER SERVICE READY"
                + " | UID: " + Process.myUid()
                + " | PID: " + Process.myPid();
    }
}
