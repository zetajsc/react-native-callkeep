/*
 * Copyright (c) 2016-2019 The CallKeep Authors (see the AUTHORS file)
 * SPDX-License-Identifier: ISC, MIT
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package io.wazo.callkeep;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;


// @see https://github.com/kbagchiGWC/voice-quickstart-android/blob/9a2aff7fbe0d0a5ae9457b48e9ad408740dfb968/exampleConnectionService/src/main/java/com/twilio/voice/examples/connectionservice/VoiceConnectionServiceActivity.java
public class RNCallKeepModule extends ReactContextBaseJavaModule {
    public static final int REQUEST_READ_PHONE_STATE = 1337;
    public static final int REQUEST_REGISTER_CALL_PROVIDER = 394859;

    private static final String E_ACTIVITY_DOES_NOT_EXIST = "E_ACTIVITY_DOES_NOT_EXIST";
    private static final String REACT_NATIVE_MODULE_NAME = "RNCallKeep";
    private static String[] permissions = {
        Build.VERSION.SDK_INT < 30 ? Manifest.permission.READ_PHONE_STATE : Manifest.permission.READ_PHONE_NUMBERS,
        Manifest.permission.CALL_PHONE,
        Manifest.permission.RECORD_AUDIO
    };

    public static RNCallKeepModule instance = null;

    private static final String TAG = "RNCallKeep";
    private static Promise hasPhoneAccountPromise;
    private ReactApplicationContext reactContext;
    private boolean isReceiverRegistered = false;
//    private VoiceBroadcastReceiver voiceBroadcastReceiver;
    private ReadableMap _settings;

    public RNCallKeepModule(ReactApplicationContext reactContext) {
        super(reactContext);
        Log.d(TAG, "[VoiceConnection] constructor");

        this.reactContext = reactContext;
    }

    public static RNCallKeepModule getInstance(ReactApplicationContext reactContext, boolean realContext) {
        if (instance == null) {
            Log.d(TAG, "[RNCallKeepModule] getInstance : " + (reactContext == null ? "null" : "ok"));
            instance = new RNCallKeepModule(reactContext);
        }
        if (realContext) {
            instance.setContext(reactContext);
        }
        return instance;
    }
    public void setContext(ReactApplicationContext reactContext) {
        Log.d(TAG, "[RNCallKeepModule] updating react context");
        this.reactContext = reactContext;
    }
    public ReactApplicationContext getContext() {
        return this.reactContext;
    }
    
    private boolean isSelfManaged() {
        try { 
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && _settings.hasKey("selfManaged") && _settings.getBoolean("selfManaged");
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public String getName() {
        return REACT_NATIVE_MODULE_NAME;
    }

    @ReactMethod
    public void setup(ReadableMap options) {
        Log.d(TAG, "[VoiceConnection] setup");

    }

    @ReactMethod
    public void registerPhoneAccount() {

    }

    @ReactMethod
    public void registerEvents() {

    }

    @ReactMethod
    public void displayIncomingCall(String uuid, String number, String callerName) {

    }

    @ReactMethod
    public void answerIncomingCall(String uuid) {
        Log.d(TAG, "[VoiceConnection] answerIncomingCall, uuid: " + uuid);

    }

    @ReactMethod
    public void startCall(String uuid, String number, String callerName) {
        Log.d(TAG, "[VoiceConnection] startCall called, uuid: " + uuid + ", number: " + number + ", callerName: " + callerName);


    }

    @ReactMethod
    public void endCall(String uuid) {
        Log.d(TAG, "[VoiceConnection] endCall called, uuid: " + uuid);

        Log.d(TAG, "[VoiceConnection] endCall executed, uuid: " + uuid);
    }

    @ReactMethod
    public void endAllCalls() {
        Log.d(TAG, "[VoiceConnection] endAllCalls called");

        Log.d(TAG, "[VoiceConnection] endAllCalls executed");
    }

    @ReactMethod
    public void checkPhoneAccountPermission(ReadableArray optionalPermissions, Promise promise) {

        promise.resolve(!hasPhoneAccount());
    }

    @ReactMethod
    public void checkDefaultPhoneAccount(Promise promise) {


        promise.resolve(false);
    }

    @ReactMethod
    public void setOnHold(String uuid, boolean shouldHold) {
        Log.d(TAG, "[VoiceConnection] setOnHold, uuid: " + uuid + ", shouldHold: " + (shouldHold ? "true" : "false"));


    }

    @ReactMethod
    public void reportEndCallWithUUID(String uuid, int reason) {
        Log.d(TAG, "[VoiceConnection] reportEndCallWithUUID, uuid: " + uuid + ", reason: " + reason);

    }

    @ReactMethod
    public void rejectCall(String uuid) {
        Log.d(TAG, "[VoiceConnection] rejectCall, uuid: " + uuid);

    }

    @ReactMethod
    public void setMutedCall(String uuid, boolean shouldMute) {
        Log.d(TAG, "[VoiceConnection] setMutedCall, uuid: " + uuid + ", shouldMute: " + (shouldMute ? "true" : "false"));

    }
    /**
     * toggle audio route for speaker via connection service function
     * @param uuid
     * @param routeSpeaker
     */
    @ReactMethod
    public void toggleAudioRouteSpeaker(String uuid, boolean routeSpeaker) {
        Log.d(TAG, "[VoiceConnection] toggleAudioRouteSpeaker, uuid: " + uuid + ", routeSpeaker: " + (routeSpeaker ? "true" : "false"));

    }

    @ReactMethod
    public void setAudioRoute(String uuid, String audioRoute, Promise promise){

        promise.resolve(true);

    }

    @ReactMethod
    public void getAudioRoutes(Promise promise){

        promise.resolve(null);

    }

    private String getAudioRouteType(int type){
        switch (type){
            case(AudioDeviceInfo.TYPE_BLUETOOTH_A2DP):
            case(AudioDeviceInfo.TYPE_BLUETOOTH_SCO):
                return "Bluetooth";
            case(AudioDeviceInfo.TYPE_WIRED_HEADPHONES):
            case(AudioDeviceInfo.TYPE_WIRED_HEADSET):
                return "Headset";
            case(AudioDeviceInfo.TYPE_BUILTIN_MIC):
                return "Phone";
            case(AudioDeviceInfo.TYPE_BUILTIN_SPEAKER):
                return "Speaker";
            default:
                return null;
        }
    }

    @ReactMethod
    public void sendDTMF(String uuid, String key) {
        Log.d(TAG, "[VoiceConnection] sendDTMF, uuid: " + uuid + ", key: " + key);

    }

    @ReactMethod
    public void updateDisplay(String uuid, String displayName, String uri) {
        Log.d(TAG, "[VoiceConnection] updateDisplay, uuid: " + uuid + ", displayName: " + displayName+ ", uri: " + uri);

    }

    @ReactMethod
    public void hasPhoneAccount(Promise promise) {


        promise.resolve(hasPhoneAccount());
    }

    @ReactMethod
    public void hasOutgoingCall(Promise promise) {
        promise.resolve(false);
    }

    @ReactMethod
    public void hasPermissions(Promise promise) {
        promise.resolve(this.hasPermissions());
    }

    @ReactMethod
    public void setAvailable(Boolean active) {
    }

    @ReactMethod
    public void setForegroundServiceSettings(ReadableMap settings) {
    }

    @ReactMethod
    public void canMakeMultipleCalls(Boolean allow) {
    }

    @ReactMethod
    public void setReachable() {
    }

    @ReactMethod
    public void setCurrentCallActive(String uuid) {
        Log.d(TAG, "[VoiceConnection] setCurrentCallActive, uuid: " + uuid);
    }

    @ReactMethod
    public void openPhoneAccounts() {
        Log.d(TAG, "[VoiceConnection] openPhoneAccounts");
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] openPhoneAccounts ignored due to no ConnectionService");
            return;
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            intent.setComponent(new ComponentName("com.android.server.telecom",
                    "com.android.server.telecom.settings.EnableAccountPreferenceActivity"));

            this.getAppContext().startActivity(intent);
            return;
        }

        openPhoneAccountSettings();
    }

    @ReactMethod
    public void openPhoneAccountSettings() {
        Log.d(TAG, "[VoiceConnection] openPhoneAccountSettings");
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] openPhoneAccountSettings ignored due to no ConnectionService");
            return;
        }


    }

    public static Boolean isConnectionServiceAvailable() {
        // PhoneAccount is available since api level 23
        return Build.VERSION.SDK_INT >= 23;
    }

    @ReactMethod
    public void isConnectionServiceAvailable(Promise promise) {
        promise.resolve(isConnectionServiceAvailable());
    }

    @ReactMethod
    public void checkPhoneAccountEnabled(Promise promise) {
        promise.resolve(hasPhoneAccount());
    }

    @ReactMethod
    public void backToForeground() {

    }

    private void initializeTelecomManager() {

    }

    private void registerPhoneAccount(Context appContext) {
        if (!isConnectionServiceAvailable()) {
            Log.w(TAG, "[VoiceConnection] registerPhoneAccount ignored due to no ConnectionService");
            return;
        }


    }

    private void sendEventToJS(String eventName, @Nullable WritableMap params) {
        Log.v(TAG, "[VoiceConnection] sendEventToJS, eventName :" + eventName + ", args : " + (params != null ? params.toString() : "null"));
        this.reactContext.getJSModule(RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    private String getApplicationName(Context appContext) {
        ApplicationInfo applicationInfo = appContext.getApplicationInfo();
        int stringId = applicationInfo.labelRes;

        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : appContext.getString(stringId);
    }

    private Boolean hasPermissions() {

        boolean hasPermissions = true;

        return hasPermissions;
    }

    private static boolean hasPhoneAccount() {
//        return isConnectionServiceAvailable() && telecomManager != null
//            && telecomManager.getPhoneAccount(handle) != null && telecomManager.getPhoneAccount(handle).isEnabled();
        return false;
    }

    private void registerReceiver() {

    }

    private Context getAppContext() {
        return this.reactContext.getApplicationContext();
    }

    public static void onRequestPermissionsResult(int requestCode, String[] grantedPermissions, int[] grantResults) {
        int permissionsIndex = 0;
        List<String> permsList = Arrays.asList(permissions);
        for (int result : grantResults) {
            if (permsList.contains(grantedPermissions[permissionsIndex]) && result != PackageManager.PERMISSION_GRANTED) {
                hasPhoneAccountPromise.resolve(false);
                return;
            }
            permissionsIndex++;
        }
        hasPhoneAccountPromise.resolve(true);
    }


}