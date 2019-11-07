package com.cordova.plugin.jitsi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.modules.core.PermissionListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import androidx.fragment.app.FragmentActivity;


public class JitsiCordovaView extends FragmentActivity implements JitsiMeetActivityInterface {
    private JitsiMeetView view;

    public static CallbackContext callbackContext = null;

    public static void setCallbackContext(CallbackContext callbackContext) {
        JitsiCordovaView.callbackContext = callbackContext;
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FragmentActivity activity = this;

        super.onCreate(savedInstanceState);

        view = new JitsiMeetView(this);
        Intent intent = this.getIntent();
        String room = intent.getStringExtra("room");
        String host = intent.getStringExtra("host");
        String jwt = intent.getStringExtra("jwt");
        JitsiMeetConferenceOptions options = null;
        try {
            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(host))
                    .setRoom(room)
                    .setWelcomePageEnabled(false);

            if (jwt != null) {
                builder = builder.setToken(jwt);
            }
            options = builder.build();
            setJitsiListener(view);
            view.join(options);

            setContentView(view);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            activity.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view.dispose();
        view = null;

        JitsiMeetActivityDelegate.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        JitsiMeetActivityDelegate.onNewIntent(intent);

        String action = intent.getStringExtra("action");

        if (action.equals("close")) {
            this.finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        JitsiMeetActivityDelegate.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        JitsiMeetActivityDelegate.onHostPause(this);
    }

    @Override
    public void requestPermissions(String[] strings, int i, PermissionListener permissionListener) {

    }

    private void setJitsiListener(JitsiMeetView view/* , final CallbackContext callbackContext */) {
        FragmentActivity activity = this;

        view.setListener(new JitsiMeetViewListener() {
            PluginResult pluginResult;

            private void on(String name, Map<String, Object> data) {
                Log.d("ReactNative", JitsiMeetViewListener.class.getSimpleName() + " " + name + " " + data);
            }

            @Override
            public void onConferenceTerminated(Map<String, Object> data) {
                on("CONFERENCE_TERMINATED", data);
                pluginResult = new PluginResult(PluginResult.Status.OK, "CONFERENCE_TERMINATED");
                pluginResult.setKeepCallback(true);
                JitsiCordovaView.callbackContext.sendPluginResult(pluginResult);
                activity.finish();
            }

            @Override
            public void onConferenceJoined(Map<String, Object> data) {
                on("CONFERENCE_JOINED", data);
                pluginResult = new PluginResult(PluginResult.Status.OK, "CONFERENCE_JOINED");
                pluginResult.setKeepCallback(true);
                JitsiCordovaView.callbackContext.sendPluginResult(pluginResult);
            }

            @Override
            public void onConferenceWillJoin(Map<String, Object> data) {
                on("CONFERENCE_WILL_JOIN", data);
                pluginResult = new PluginResult(PluginResult.Status.OK, "CONFERENCE_WILL_JOIN");
                pluginResult.setKeepCallback(true);
                JitsiCordovaView.callbackContext.sendPluginResult(pluginResult);
            }
        });
    }
}
