package com.cordova.plugin.jitsi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.json.JSONArray;
import org.json.JSONException;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;


public class JitsiPlugin extends CordovaPlugin {
    private JitsiMeetView view;
    private static final String TAG = "cordova-plugin-jitsi";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        JitsiCordovaView.setCallbackContext(callbackContext);

        if (action.equals("loadURL")) {
            String host = args.getString(0);
            String room = args.getString(1);
            String jwt = args.getString(2);
            this.openNewJitsiActivity(host, room, jwt);
            return true;
        } else if (action.equals("destroy")) {
            finishJitsiActivity();
            return true;
        } else if (action.equals("saveSettings")) {
            String json = args.getString(0);
            JitsiStorage storage = new JitsiStorage(cordova.getActivity().getApplicationContext());
            PluginResult pluginResult;

            if (storage.dumpSettings(json)) {
                pluginResult = new PluginResult(PluginResult.Status.OK, "SAVED");
            } else {
                pluginResult = new PluginResult(PluginResult.Status.ERROR, "FAILED");
            };

            pluginResult.setKeepCallback(true);
            JitsiCordovaView.callbackContext.sendPluginResult(pluginResult);
            return true;
        }
        return false;
    }

    private void openNewJitsiActivity(String host, final String room, String jwt) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Context context = cordova.getActivity().getApplicationContext();
                Intent intent = new Intent(context, JitsiCordovaView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("room", room);
                intent.putExtra("host", host);
                intent.putExtra("jwt", jwt);
                cordova.getActivity().startActivity(intent);
            }
        });
    }

    private void finishJitsiActivity() {
        Context context = cordova.getActivity().getApplicationContext();
        Intent intent = new Intent(context, JitsiCordovaView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("action", "close");
        cordova.getActivity().startActivity(intent);
    }
}

