package com.tahashaheen.chotu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class UnityPlayerActivity extends Activity {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BT_DISCOVERABILITY_ENABLE = 2;
    private static final int TIME_DURATION_FOR_DISCOVERABILITY = 60;
    private static final int ADMIN_INTENT = 15;
    private static final int REQUEST_COARSE_LOCATION_PERMISSION = 16;
    private static String SEPARATOR, TERMINATOR;
    private final int HAPPY = 0, SAD = 1, SURPRISED = 2, ANGRY = 3, IDLE = 4;
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    private BluetoothAdapter bluetoothAdapter;
    private String deviceOldName;
    private String concatenatedString = "";
    private MediaPlayer voiceYourEmotion;
    private String mainCameraObject, eyebrowsObject, mouthObject, tearObject, ouchZoneObject, eyelidsObject;
    private String changeBackgroundColorFunction, setEmotionFunction, setSpeakingFunction, setEyePokeEnabledStateFunction, goToSleepFunction;
    private String startSpeaking, stopSpeaking;
    private String trueString, falseString;
    private TextToSpeech textToSpeech;
    private AudioManager audioManager;
    private int savedVolume;
    private DevicePolicyManager mDevicePolicyManager;
    private String backgroundColor = "000000";
    private ComponentName mComponentName;
    private String deviceNameToSearch, myName;
    private ManageMyConnectedSocket manageMyConnectedSocketConnectThread;
    private boolean restartDiscoveryBoolean;
    private ProgressBar progressBar;

    boolean myDeviceDiscoverable = false; //change the way you do this when you trigger discovery and discoverability with buttons

    private BroadcastReceiver BT_BroadcastReceiver = new BroadcastReceiver() {

        ArrayList<BluetoothDevice> potentialFaceDevices = new ArrayList<>();

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "BT_BroadcastReceiver");
            String action = intent.getAction();

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_ON:
                            Log.d(TAG, "Bluetooth turned on!");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.d(TAG, "Bluetooth turned off!");
                            break;
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.d(TAG, "Bluetooth is turning on!");
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.d(TAG, "Bluetooth is turning off!");
                            break;
                    }
                    break;
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    int scanMode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                    switch (scanMode) {
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            Toast.makeText(context, "This device is now discoverable", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "The device is in discoverable mode.");
                            myDeviceDiscoverable = true;
                            break;
                        case (BluetoothAdapter.SCAN_MODE_CONNECTABLE):
                            Log.d(TAG, "The device isn't in discoverable mode but can still receive connections.");
                            myDeviceDiscoverable = true;
                            break;
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            Log.d(TAG, "The device cannot receive connections.");
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.d(TAG, "Bluetooth device connected");
                    Toast.makeText(context, "Bluetooth device connected", Toast.LENGTH_SHORT).show();
                    if(!myDeviceDiscoverable){
                        turnOnDiscoverability();
                    }
                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.d(TAG, "Bluetooth device disconnected");
                    Toast.makeText(context, "Bluetooth device disconnected", Toast.LENGTH_SHORT).show();
                    lookForIncomingConnections();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    if(deviceName != null){
                        deviceName = deviceName.toUpperCase();
                    }
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    Log.d(TAG, "Device found: " + deviceName + "; " + deviceHardwareAddress);
                    if (deviceName == null || deviceName.contains(deviceNameToSearch)) {
                        Log.d(TAG, "Found a device with name " + deviceNameToSearch);
                        potentialFaceDevices.add(device);
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "Discovery started");
                    progressBar.setVisibility(View.VISIBLE);
                    potentialFaceDevices.clear();
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "Discovery finished");
                    progressBar.setVisibility(View.GONE);
                    lookAtDevicesFound(potentialFaceDevices, false);
                    break;
            }
        }
    };

//    public static boolean sleepPermissionGranted;

    // Override this in your custom UnityPlayerActivity to tweak the command line arguments passed to the Unity Android Player
    // The command line arguments are passed as a string, separated by spaces
    // UnityPlayerActivity calls this from 'onCreate'
    // Supported: -force-gles20, -force-gles30, -force-gles31, -force-gles31aep, -force-gles32, -force-gles, -force-vulkan
    // See https://docs.unity3d.com/Manual/CommandLineArguments.html
    // @param cmdLine the current command line arguments, may be null
    // @return the modified command line string or null
    protected String updateUnityCommandLineArguments(String cmdLine) {
        return cmdLine;
    }

    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        String cmdLine = updateUnityCommandLineArguments(getIntent().getStringExtra("unity"));
        getIntent().putExtra("unity", cmdLine);

        mUnityPlayer = new UnityPlayer(this);

        progressBar = new ProgressBar(UnityPlayerActivity.this,null, android.R.attr.progressBarStyle);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        UnityPlayer.LayoutParams params = new UnityPlayer.LayoutParams(100,100);
        mUnityPlayer.addView(progressBar,params);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Save volume //
        savedVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // Set volume to max //
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        // String code //
        mainCameraObject = getString(R.string.MAIN_CAMERA_OBJECT);
        eyebrowsObject = getString(R.string.EYEBROWS_OBJECT);
        mouthObject = getString(R.string.MOUTH_OBJECT);
        tearObject = getString(R.string.TEAR_OBJECT);
        ouchZoneObject = getString(R.string.OUCH_ZONE);
        eyelidsObject = getString(R.string.EYELIDS_OBJECT);

        changeBackgroundColorFunction = getString(R.string.CHANGE_BACKGROUND_COLOR_FUNCTION);
        setEmotionFunction = getString(R.string.SET_EMOTION_FUNCTION);
        setSpeakingFunction = getString(R.string.SET_SPEAKING_FUNCTION);
        setEyePokeEnabledStateFunction = getString(R.string.SET_EYE_POKE_ENABLED_STATE_FUNCTION);
        goToSleepFunction = getString(R.string.GO_TO_SLEEP_FUNCTION);

        startSpeaking = getString(R.string.START_SPEAKING);
        stopSpeaking = getString(R.string.STOP_SPEAKING);
        trueString = getString(R.string.TRUE);
        falseString = getString(R.string.FALSE);
        SEPARATOR = getString(R.string.SEPARATOR);
        TERMINATOR = getString(R.string.TERMINATOR);

        deviceNameToSearch = getString(R.string.ROBOCHOTU_BODY);
        myName = getString(R.string.ROBOCHOTU_FACE);

        //Text to speech code //
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("en", "in"));
//                        Set<Voice> voice = textToSpeech.getVoices();
                }

                // This bit of code handles mouth animation during TTS //
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        UnityPlayer.UnitySendMessage(mouthObject, setSpeakingFunction, startSpeaking);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        UnityPlayer.UnitySendMessage(mouthObject, setSpeakingFunction, stopSpeaking);
                    }

                    @Override
                    public void onError(String utteranceId) {
                    }
                });
            }
        });

    // Screen Control code //
    mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);

    // Ask the user to add a new device administrator to the system. //
    mComponentName = new ComponentName(this, MyAdminReceiver.class);

    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
    startActivityForResult(intent, ADMIN_INTENT);

    setUpBluetooth();
    }

    private void setUpBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "This device does not support Bluetooth");
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            connectToBluetoothDevice();
        }
    }

    private void connectToBluetoothDevice() {

        Log.d(TAG, bluetoothAdapter.getName());
        deviceOldName = bluetoothAdapter.getName();
        bluetoothAdapter.setName(myName);

        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(BT_BroadcastReceiver, intentFilter);

        lookAtPairedDevices(deviceNameToSearch);
    }

    private void lookAtPairedDevices(String nameToSearch) {

        Log.d(TAG, "Currently paired devices:");
        ArrayList<BluetoothDevice> pairedDevicesWithName = new ArrayList<>();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (!pairedDevices.isEmpty()) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName().toUpperCase();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(TAG, deviceName + "; " + deviceHardwareAddress);

                if (deviceName.contains(nameToSearch)) {
                    pairedDevicesWithName.add(device);
                }
            }
        }

        if (!pairedDevicesWithName.isEmpty()) {
            Log.d(TAG, "Found some paired devices with the name you inserted:");
            for (int i = 0; i < pairedDevicesWithName.size(); i++) {
                String deviceName = pairedDevicesWithName.get(i).getName();
                String deviceHardwareAddress = pairedDevicesWithName.get(i).getAddress();
                Log.d(TAG, deviceName + "; " + deviceHardwareAddress);
            }
        }

        lookAtDevicesFound(pairedDevicesWithName, true);
    }

    private void lookAtDevicesFound(final ArrayList<BluetoothDevice> devicesToDisplay, boolean deviceListIsFromPairedDevices) {
        if (devicesToDisplay.isEmpty()) {
            Log.d(TAG, "No devices with name " + deviceNameToSearch + " found.");
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No " + ((deviceListIsFromPairedDevices) ? "paired" : "nearby") + " devices found");
            if (deviceListIsFromPairedDevices) {
                builder.setMessage("Would you like to look at nearby devices?");
            } else {
                builder.setMessage("Would you like to continue to look?");
            }
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkPermissionsAndLocateDevices();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    turnOnDiscoverability();
                }
            });
            builder.show();
        } else {
            final ArrayList<String> listOfNames = new ArrayList<>();
            final ArrayList<String> listOfMACAddresses = new ArrayList<>();
            for (int i = 0; i < devicesToDisplay.size(); i++) {

                if (devicesToDisplay.get(i).getName() == null) {
                    listOfNames.add(getString(R.string.NULL_NAME_ERROR));
                } else {
                    listOfNames.add(devicesToDisplay.get(i).getName());
                }

                listOfMACAddresses.add(devicesToDisplay.get(i).getAddress());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if ((deviceListIsFromPairedDevices)) {
                builder.setTitle("Paired devices found. Tap to connect to one.");
            } else {
                builder.setTitle("Devices found. Tap to connect.");
            }

            builder.setItems(listOfNames.toArray(new String[0]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int deviceSelected) {
                    Log.d(TAG, deviceSelected + " was clicked. It has name " + listOfNames.get(deviceSelected) + " and MAC Address " + listOfMACAddresses.get(deviceSelected));
                    ConnectThread connectThread = new ConnectThread(devicesToDisplay.get(deviceSelected));
                    connectThread.start();
                }
            });

            builder.setPositiveButton(((deviceListIsFromPairedDevices) ? "Look for devices nearby" : "Look again"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkPermissionsAndLocateDevices();
                }
            });
            builder.setNegativeButton(((deviceListIsFromPairedDevices) ? "Exit" : "Stop looking"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    turnOnDiscoverability();
                }
            });
            builder.show();
        }
    }

    private void checkPermissionsAndLocateDevices() {
        Log.d(TAG, "Going to start looking for devices");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Looks like we don't have permissions");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d(TAG, "Permission request was previously rejected, asking for permission again with an explanation of why it is needed.");
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Permissions previously rejected");
                alertDialog.setMessage("We need access to certain permissions to find the Robochotu face device. Please click ACCEPT on the dialog that appears after this.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(UnityPlayerActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSION);
                            }
                        });
                alertDialog.show();
            } else {
                Log.d(TAG, "Asking for permission");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSION);
            }
        } else {
            Log.d(TAG, "We have permissions");
            beginDiscovery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "permission was granted, yay!");
                    beginDiscovery();
                } else {
                    Log.d(TAG, "permission denied, boo!");
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void beginDiscovery() {
        if (bluetoothAdapter.isDiscovering()) {
            Log.d(TAG, "Already discovering. Cancelling.");
            Log.d(TAG, "Cancel discovery: " + bluetoothAdapter.cancelDiscovery());
        }
        Log.d(TAG, "Start discovery: " + bluetoothAdapter.startDiscovery());

        Toast.makeText(this, "Looking for devices nearby", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);
    }

    private void endDiscovery(){
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void beginDiscoveryAgain() {
        if(restartDiscoveryBoolean){
            beginDiscovery();
        }
    }

    private void turnOnDiscoverability() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, TIME_DURATION_FOR_DISCOVERABILITY); //turns on for 300 seconds
        startActivityForResult(discoverableIntent, REQUEST_BT_DISCOVERABILITY_ENABLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult, requestCode = " + requestCode);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "Bluetooth enabled");
                    connectToBluetoothDevice();
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d(TAG, "Unable to start Bluetooth");
                    Toast.makeText(this, "Unable to turn on Bluetooth. Cannot continue.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_BT_DISCOVERABILITY_ENABLE:
                if (resultCode == TIME_DURATION_FOR_DISCOVERABILITY) {
                    Log.d(TAG, "Discoverability enabled");
                } else if (resultCode == RESULT_CANCELED) {
                    Log.d(TAG, "Unable to begin discoverability");
                    myDeviceDiscoverable = true;
                    Toast.makeText(this, "This device can still receive connections", Toast.LENGTH_SHORT).show();
                }
                lookForIncomingConnections();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void lookForIncomingConnections() {
        AcceptThread acceptIncomingConnectionThread = new AcceptThread();
        acceptIncomingConnectionThread.start();
    }

    private void processInstruction(String incomingMessage) {
        concatenatedString = concatenatedString + incomingMessage;
        Log.d(TAG, "Concatenated String: " + concatenatedString);

        while (concatenatedString.contains("#")) {
            // send the String till # to be broken up //
            String[] messagePieces = concatenatedString.substring(0, concatenatedString.indexOf(TERMINATOR)).split(SEPARATOR);

            Log.d(TAG, "Instruction received:" + concatenatedString.substring(0, concatenatedString.indexOf(TERMINATOR)));

            // empty the concatenatedString //
            concatenatedString = concatenatedString.substring(concatenatedString.indexOf(TERMINATOR) + 1);
            Log.d(TAG, "Remaining concat string: " + concatenatedString);

            // send it to be processed //
            Log.d(TAG, messagePieces[0]);
            switch (messagePieces[0].trim()) {
                case "E":
                    setEmotion(messagePieces[1]);
                    break;
                case "G":
                    speakOut(messagePieces);
                    break;
                case "C":
                    settingsUpdate(messagePieces);
                    break;
                default:
                    sendToBody(messagePieces);
                    break;
            }
        }
    }

    private void setEmotion(String emotionString) {

        // Was an audio also requested //
        boolean voiceYourEmotionInstruction = false;
        if (emotionString.substring(emotionString.length() - 1).equals("V")) {
            voiceYourEmotionInstruction = true;
            emotionString = emotionString.substring(0, emotionString.length() - 1);
        }

        // If audio already playing, stop it //
        if (voiceYourEmotion != null) {
            voiceYourEmotion.stop();
            voiceYourEmotion.release();
        }
        voiceYourEmotion = null;

        // String to int conversion //
        int emotionInteger;
        switch (emotionString.toUpperCase()) {
            case "HAPPY":
                emotionInteger = HAPPY;
                if (voiceYourEmotionInstruction)
                    voiceYourEmotion = MediaPlayer.create(this, R.raw.happy);
                break;
            case "SAD":
                emotionInteger = SAD;
                if (voiceYourEmotionInstruction)
                    voiceYourEmotion = MediaPlayer.create(this, R.raw.sad);
                break;
            case "ANGRY":
                emotionInteger = ANGRY;
                if (voiceYourEmotionInstruction)
                    voiceYourEmotion = MediaPlayer.create(this, R.raw.angry);
                break;
            case "SURPRISED":
                emotionInteger = SURPRISED;
                break;
            case "IDLE":
                emotionInteger = IDLE;
                break;
            default:
                emotionInteger = 10;    //because 10 isn't a registered emotion, nothing happens //
        }

        // This part talks to UNITY. Using the emotionInteger //
        // The format is UnityPlayer.UnitySendMessage(unityObjectName, methodName, parameterToPass) //
        // All must be String values //
        String backgroundColor = emotionBackgroundColor(emotionInteger);
        UnityPlayer.UnitySendMessage(mainCameraObject, changeBackgroundColorFunction, backgroundColor);
        switch (emotionInteger) {
            case HAPPY:
                UnityPlayer.UnitySendMessage(eyebrowsObject, setEmotionFunction, "HAPPY");
                UnityPlayer.UnitySendMessage(mouthObject, setEmotionFunction, "HAPPY");
                UnityPlayer.UnitySendMessage(tearObject, setEmotionFunction, "HAPPY");
                break;
            case SAD:
                UnityPlayer.UnitySendMessage(eyebrowsObject, setEmotionFunction, "SAD");
                UnityPlayer.UnitySendMessage(mouthObject, setEmotionFunction, "SAD");
                UnityPlayer.UnitySendMessage(tearObject, setEmotionFunction, "SAD");
                break;
            case SURPRISED:
                UnityPlayer.UnitySendMessage(eyebrowsObject, setEmotionFunction, "SURPRISED");
                UnityPlayer.UnitySendMessage(mouthObject, setEmotionFunction, "SURPRISED");
                UnityPlayer.UnitySendMessage(tearObject, setEmotionFunction, "SURPRISED");
                break;
            case ANGRY:
                UnityPlayer.UnitySendMessage(eyebrowsObject, setEmotionFunction, "ANGRY");
                UnityPlayer.UnitySendMessage(mouthObject, setEmotionFunction, "ANGRY");
                UnityPlayer.UnitySendMessage(tearObject, setEmotionFunction, "ANGRY");
                break;
            case IDLE:
                UnityPlayer.UnitySendMessage(eyebrowsObject, setEmotionFunction, "IDLE");
                UnityPlayer.UnitySendMessage(mouthObject, setEmotionFunction, "IDLE");
                UnityPlayer.UnitySendMessage(tearObject, setEmotionFunction, "IDLE");
                break;
            default:
        }

        // This part makes the mouth move if speaking is happening //
        if (voiceYourEmotion != null) {
            voiceYourEmotion.start();
            UnityPlayer.UnitySendMessage(mouthObject, setSpeakingFunction, startSpeaking);
            voiceYourEmotion.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    // This bit gets called back when audio is finished and mouth needs to stop moving //
                    UnityPlayer.UnitySendMessage(mouthObject, setSpeakingFunction, stopSpeaking);
                }
            });
        }
    }

    private String emotionBackgroundColor(int emotion) {
        switch (emotion) {
            case HAPPY:
                backgroundColor = "FFB400";
                break;
            case SAD:
                backgroundColor = "058548";
                break;
            case SURPRISED:
                backgroundColor = "FFFFFF";
                break;
            case ANGRY:
                backgroundColor = "B60000";
                break;
            case IDLE:
                backgroundColor = "878787";
                break;
            default:
        }
        return (backgroundColor);
    }

    private void speakOut(String[] messagePieces) {

        float speedValue = Float.parseFloat(messagePieces[2]);

//        textToSpeech.setPitch(pitchValue);
        textToSpeech.setSpeechRate(speedValue);

//        Log.d(TAG + " speed:", String.valueOf(speedValue));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "texToSpeech");
        textToSpeech.speak(messagePieces[1], TextToSpeech.QUEUE_FLUSH, map);

    }

    private void settingsUpdate(String[] messagePieces) {

        // Will change these to different letters later //
        switch (messagePieces[1].toUpperCase()) {
            case "POKE":
                switch (messagePieces[2]) {
                    case "DISABLE":
                        UnityPlayer.UnitySendMessage(ouchZoneObject, setEyePokeEnabledStateFunction, falseString);
                        break;
                    case "ENABLE":
                        UnityPlayer.UnitySendMessage(ouchZoneObject, setEyePokeEnabledStateFunction, trueString);
                        break;
                }
                break;
            case "SLEEP":
                // run the sleepy animation //
                UnityPlayer.UnitySendMessage(eyelidsObject, goToSleepFunction, "");
                    Handler handler = new Handler();
                    // delay a little bit //
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // turn the device off //
                            if(mDevicePolicyManager != null && mDevicePolicyManager.isAdminActive(mComponentName)) {
                                mDevicePolicyManager.lockNow();
                            }
                            else{
                                Toast.makeText(UnityPlayerActivity.this, "I need admin permissions to go to sleep!", Toast.LENGTH_SHORT).show();
                                // Add feedback messages to send to remote to tell it what's happening with the robot
                            }
                        }
                    }, 2500);
                break;
            default:
                break;
        }

    }

    private void sendToBody(String[] messagePieces){
        String fullMessage = TextUtils.join("_", messagePieces);
        fullMessage += TERMINATOR;
        Log.d(TAG, "Sending to body: " + fullMessage);

        if(manageMyConnectedSocketConnectThread != null) {
            manageMyConnectedSocketConnectThread.sendMessageViaBluetooth(fullMessage);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
        mUnityPlayer.newIntent(intent);
    }

    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.destroy();
        super.onDestroy();
        unregisterReceiver(BT_BroadcastReceiver);
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        endDiscovery();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        beginDiscoveryAgain();
        mUnityPlayer.resume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUnityPlayer.stop();
        Log.d(TAG, bluetoothAdapter.getName());
        bluetoothAdapter.setName(deviceOldName);

        if (bluetoothAdapter.isDiscovering()) {
            restartDiscoveryBoolean = true;
            bluetoothAdapter.cancelDiscovery();
        }
        else{
            restartDiscoveryBoolean = false;
        }
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            Log.d(TAG, "Creating a thread for trying to connect to server devices");
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                UUID MY_UUID = UUID.fromString(getString(R.string.BODY_UUID));
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
                Log.d(TAG, "Trying to create a Bluetooth Client Socket");
            } catch (IOException e) {
                Log.d(TAG, "Socket's create() method failed");
            }
            mmSocket = tmp;
        }

        @Override
        public void run() {

            Log.d(TAG, "Cancel discovery because it otherwise slows down the connection");
            Log.d(TAG, "cancel discovery: " + bluetoothAdapter.cancelDiscovery());

            try {
                Log.d(TAG, "Attempting connection...");
                // Connect to the remote_launcher device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.d(TAG, "Unable to connect; closing the socket and returning");
                try {
                    mmSocket.close();
                    Log.d(TAG, "Socket's close() method successful");
                } catch (IOException closeException) {
                    Log.d(TAG, "Socket's close() method failed");
                }
                return;
            }

            Log.d(TAG, "Connection attempt successful!");
            manageMyConnectedSocketConnectThread = new ManageMyConnectedSocket(mmSocket) {
                @Override
                public void whenMessageReceived(String message) {
                }
            };
            manageMyConnectedSocketConnectThread.start();
            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            Log.d(TAG, "Closing the connect socket and causing the thread to finish.");
            try {
                mmSocket.close();
                Log.d(TAG, "Closed the client socket");
            } catch (IOException e) {
                Log.d(TAG, "Could not close the client socket");
            }
        }
    }

    private class AcceptThread extends Thread {

        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            Log.d(TAG, "Creating a thread for accepting incoming connections");

            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                String NAME = getString(R.string.app_name);
                UUID MY_UUID = UUID.fromString(getString(R.string.REMOTE_UUID));
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                Log.d(TAG, "Trying to create a Bluetooth Server Socket");
            } catch (IOException e) {
                Log.d(TAG, "Socket's listen() method failed");
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket mmSocket;
            Log.d(TAG, "Listening until exception occurs or a socket is returned...");
            while (true) {
                try {
                    mmSocket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.d(TAG, "Socket's accept() method failed");
                    break;
                }

                if (mmSocket != null) {
                    Log.d(TAG, "A connection was accepted!");
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    ManageMyConnectedSocket manageMyConnectedSocketAcceptThread = new ManageMyConnectedSocket(mmSocket) {
                        @Override
                        public void whenMessageReceived(String message) {
                            processInstruction(message);
                        }
                    };
                    manageMyConnectedSocketAcceptThread.start();
                    try {
                        mmServerSocket.close();
                        Log.d(TAG, "Socket's close() method successful");
                    } catch (IOException e) {
                        Log.d(TAG, "Socket's close() method failed");
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            Log.d(TAG, "Closing the connect socket and causing the thread to finish.");
            try {
                mmServerSocket.close();
                Log.d(TAG, "Closed the connect socket");
            } catch (IOException e) {
                Log.d(TAG, "Could not close the connect socket");
            }
        }
    }

    private abstract class ManageMyConnectedSocket extends Thread {

        private BluetoothSocket mmSocket;
        private MyBluetoothService myBluetoothService;

        public ManageMyConnectedSocket(BluetoothSocket socket) {
            mmSocket = socket;

            final int MESSAGE_READ = 0;
            final int MESSAGE_WRITE = 1;
            final int MESSAGE_TOAST = 2;

            Handler handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    switch (msg.what) {
                        case MESSAGE_READ:
                            byte[] readBuffer = (byte[]) msg.obj;
                            // construct a string from the valid bytes in the buffer
                            String readMessage = new String(readBuffer, 0, msg.arg1);
                            Log.d(TAG, "MESSAGE_READ");
                            Log.d(TAG, readMessage);
                            whenMessageReceived(readMessage);
                            break;
                        case MESSAGE_WRITE:
                            byte[] writeBuffer = (byte[]) msg.obj;
                            String writeMessage = new String(writeBuffer);
                            Log.d(TAG, "MESSAGE_WRITE");
                            Log.d(TAG, writeMessage);
                            break;
                        case MESSAGE_TOAST:
                            Log.d(TAG, "MESSAGE_TOAST");
                            break;
                    }
                    super.handleMessage(msg);
                }
            };

            myBluetoothService = new MyBluetoothService(mmSocket, handler);
        }

        public abstract void whenMessageReceived(String message);

        public void sendMessageViaBluetooth(String messageToSend) {
            myBluetoothService.write(messageToSend);
        }
    }
}
