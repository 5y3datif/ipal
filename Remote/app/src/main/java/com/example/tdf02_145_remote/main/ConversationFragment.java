package com.example.tdf02_145_remote.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tdf02_145_remote.MainActivity;
import com.example.tdf02_145_remote.PermissionHandler;
import com.example.tdf02_145_remote.R;
import com.example.tdf02_145_remote.RequestJavaV2Task;
import com.example.tdf02_145_remote.SpeechRecognizerManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @file SpeakingFragment.java
 * @brief Extends Fragment
 * @details
 * @li A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity.
 * @li This one deals with the Speaking aspect
 */


/**
 * @brief Conversation Fragment
 * @details
 * @li A Fragment is a piece of an application's user interface or behavior that can be placed in an Activity.
 * @li This one deals with the Conversation with the robot
 */
public class ConversationFragment extends Fragment {

    /**
     * @var PITCH_FACTOR
     * @brief Fixed value
     * @details Dividing the actual speed value
     *
     * @var DEFAULT_PITCH
     * @brief Default Speed
     * @details Default Speed as a float
     *
     * @var mySpeed
     * @brief Float to hold current speed
     * @details Holds the current speed so tp display in the slider
     *
     * @var defaultPercentage
     * @brief Speed is displayed as a percentage
     * @details DEFAULT_PITCH/PITCH_FACTOR is how the default speed percentage is calculated
     *
     */
    private float DEFAULT_SPEED = 0.75f;
    private double mySpeed;
    int defaultPercentage;

    /**
     * Debugging tool
     */
    private static String TAG = "ConversationFragment";

    private String uuid = UUID.randomUUID().toString();
    private SessionsClient sessionsClient;
    private SessionName session;

    private String SPEAK_OUT_CLASSIFIER;
    private EditText editText;
    private SpeechRecognizerManager mSpeechManager;
    private String resultString;
    private ArrayList<Word> words;
    private ConversationWordAdapter adapter;
    private FloatingActionButton fab;

    // Command String variables for expressions //
    private String FORWARD_MOVEMENT_CLASSIFIER;
    private String BACKWARD_MOVEMENT_CLASSIFIER;
    private String LEFT_MOVEMENT_CLASSIFIER;
    private String RIGHT_MOVEMENT_CLASSIFIER;
    private String STOP_MOVEMENT_CLASSIFIER;
    private String MODIFIER;
    private String distance;

    // Command String variables for expressions //
    private String EMOTION_CLASSIFIER;
    private String TAIL_STRING;
    private String VERBALIZE_EMOTION;



    /**
     * @brief Constructor
     * @details Required empty public constructor
     */
    public ConversationFragment() {
    }

    /**
     * @brief Called to have the fragment instantiate its user interface view
     * @details
     * @li This fragment has a View inflated from the speaking_view.xml file
     * @li This View has a GridView with the resourceID "grid"; an EditText; a few buttons; a TextView for percentage display; and a seekBar
     * @li This GridView is associated with an Adapter which is an ExpressionsWordAdapter object
     * @li The ExpressionsWordAdapter object returns custom itemView object to populate the GridView
     * @li The EditView works with the speakNowButton to send custom text to be spoken
     * @li The SeekBar works with two buttons to handle speed settings
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container          This is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflating speaking_view.xml (modified grid_view.xml) //
        View rootView = inflater.inflate(R.layout.conversation_view, container, false);

        // Entering custom text for speaking option //
//        editText = rootView.findViewById(R.id.editText);
//        ImageButton speakNowButton = rootView.findViewById(R.id.speak_now);

        // A SeekBar for changing speed//
        final SeekBar speedBar = rootView.findViewById(R.id.speed_bar);
        final TextView speedPercentage = rootView.findViewById(R.id.speed_percentage);

        // Resetting and saving speed settings //
        ImageButton resetSpeed = rootView.findViewById(R.id.reset_speed);
        ImageButton setDefaultSpeed = rootView.findViewById(R.id.set_default_speed);

        // Java V2
        initV2Chatbot();

        fab = rootView.findViewById(R.id.conversation_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordAudioMessage();
            }
        });


        // A list of Word objects //
        words = new ArrayList<Word>();

        words.add(new Word(""));
        words.add(new Word(""));
        words.add(new Word(""));
        words.add(new Word(""));
        words.add(new Word(""));

        // Command String variables and calculations for expressions //
        mySpeed = DEFAULT_SPEED    ;
//        defaultPercentage = (int) (DEFAULT_SPEED    /PITCH_FACTOR);
        defaultPercentage = (int) (DEFAULT_SPEED * 100);
        speedBar.setProgress(defaultPercentage);
        speedPercentage.setText(getString(R.string.SPEED_SLIDER_INFO) + ": " + defaultPercentage + "%");
        SPEAK_OUT_CLASSIFIER = getString(R.string.SPEAK_OUT_CLASSIFIER);

        // Command String variables for expressions //
        FORWARD_MOVEMENT_CLASSIFIER = getString(R.string.FORWARD_MOVEMENT_CLASSIFIER);
        BACKWARD_MOVEMENT_CLASSIFIER = getString(R.string.BACKWARD_MOVEMENT_CLASSIFIER);
        LEFT_MOVEMENT_CLASSIFIER = getString(R.string.LEFT_MOVEMENT_CLASSIFIER);
        RIGHT_MOVEMENT_CLASSIFIER = getString(R.string.RIGHT_MOVEMENT_CLASSIFIER);
        STOP_MOVEMENT_CLASSIFIER = getString(R.string.STOP_MOVEMENT_CLASSIFIER);
        MODIFIER = "_000_000";
        distance = "1000";

        // Command String variables for expressions //
        EMOTION_CLASSIFIER = getString(R.string.EMOTION_CLASSIFIER);
        TAIL_STRING = "000";
        VERBALIZE_EMOTION = "V";


        // An ExpressionsWordAdapter whose data source is a list of Words //
        // The adapter knows how to create custom itemViews for each item in the list //
        adapter = new ConversationWordAdapter(getActivity(), words, R.color.category_color);

        // Find the GridView view hierarchy of the Activity with the ID "grid" //
        GridView gridView = rootView.findViewById(R.id.grid);

        // Number of columns in the gridView //
        gridView.setNumColumns(1);

        // Make gridView use the SpeakingWordAdapter object we created above, so that the gridView will display items for each Word in the list.
        gridView.setAdapter(adapter);

        // Set a click listener to send command String when the list item is clicked //
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Word word = words.get(position);
                sendMessageToDialogflow(word.getStringResource());
            }
        });

//        // Set a click listener to send command String when the speakNowButton is clicked //
//        speakNowButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recordAudioMessage();
//            }
//        });

        // Set a click listener to reset speed //
        resetSpeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speedBar.setProgress(defaultPercentage);
                Toast.makeText(getContext(), "Speed reset",Toast.LENGTH_SHORT).show();
            }
        });

        // Set a long click listener to reset speed to default if updated //
        resetSpeed.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                defaultPercentage = (int) (DEFAULT_SPEED * 100);
                speedBar.setProgress(defaultPercentage);
                Toast.makeText(getContext(), "Default speed set to "+ defaultPercentage + "%",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Set a long click listener to update default speed //
        setDefaultSpeed.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                defaultPercentage = speedBar.getProgress();
                Toast.makeText(getContext(), "Default speed set to "+ defaultPercentage + "%", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Set a listener to update when seekBar updated by user //
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) progress = 1;
                mySpeed = progress / 100.0;
                speedPercentage.setText(getString(R.string.SPEED_SLIDER_INFO) + ": " + progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return rootView;
    }


    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.application_credentials);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);
            Log.d(TAG, "SESSION STARTED!!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "SESSION NOT STARTED!!");
        }
    }

    private void sendMessageToDialogflow(String userUtterance) {
        if (userUtterance.trim().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.empty_message_error), Toast.LENGTH_LONG).show();
        } else {
            // Java V2
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(userUtterance).setLanguageCode("en-US")).build();
            new RequestJavaV2Task(ConversationFragment.this, session, sessionsClient, queryInput).execute();
        }
    }

    public void callbackV2(DetectIntentResponse response) {
        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            if(botReply.contains(":")){
                String[] brokenBotReply = botReply.toUpperCase().split(":");
                Log.d(TAG, brokenBotReply[0]);
                Log.d(TAG, brokenBotReply[1]);
                Log.d(TAG, brokenBotReply[2]);
                switch(brokenBotReply[1].replaceAll("\\s+","") /*removing all white space*/ ){
                    case "DIRECTION":
//                        Log.d(TAG, "Direction detected");
                        MainActivity.writeToBluetooth(SPEAK_OUT_CLASSIFIER, brokenBotReply[0], String.valueOf(mySpeed));
                        MainActivity.writeToBluetooth(Character.toString(brokenBotReply[2].replaceAll("\\s+","").charAt(0)), distance, MODIFIER);
                        break;
                    case "EMOTION":
                        MainActivity.writeToBluetooth(EMOTION_CLASSIFIER, brokenBotReply[2].replaceAll("\\s+",""), TAIL_STRING);
                        MainActivity.writeToBluetooth(SPEAK_OUT_CLASSIFIER, brokenBotReply[0], String.valueOf(mySpeed));
                        break;
                }
            }
            else
                MainActivity.writeToBluetooth(SPEAK_OUT_CLASSIFIER, botReply, String.valueOf(mySpeed));
            Log.d(TAG, "V2 Bot Reply: " + botReply);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            Toast.makeText(getContext(), getString(R.string.communication_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void recordAudioMessage() {
        if (PermissionHandler.checkPermission(getActivity(), PermissionHandler.RECORD_AUDIO)) {
            Toast.makeText(getContext(), "SPEAK NOW", Toast.LENGTH_SHORT).show();
            if (mSpeechManager == null) {
                SetSpeechListener();
            } else if (!mSpeechManager.ismIsListening()) {
                mSpeechManager.destroy();
                SetSpeechListener();
            }
        }
        else {
            PermissionHandler.askForPermission(PermissionHandler.RECORD_AUDIO, getActivity());
        }
    }

    private void SetSpeechListener() {
        mSpeechManager = new SpeechRecognizerManager(getContext(), new SpeechRecognizerManager.onResultsReady() {
            @Override
            public void onResults(ArrayList<String> results) {
                if (results != null && results.size() > 0) {
                    if (results.size() == 1) {
                        mSpeechManager.destroy();
                        mSpeechManager = null;
                        resultString = results.get(0);
                        //queryEditText.setText(resultString);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        if (results.size() > 5) {
                            results = (ArrayList<String>) results.subList(0, 5);
                        }
                        for (String result : results) {
                            sb.append(result).append("\n");
                        }
                        //resultString = sb.toString();
                        resultString = results.get(0);
                        words.set(0, new Word(results.get(0)));
                        words.set(1, new Word(""));
                        words.set(2, new Word(""));
                        words.set(3, new Word(""));
                        words.set(4, new Word(""));
                        adapter.notifyDataSetChanged ();

                        if(mSpeechManager!=null) {
                            mSpeechManager.destroy();
                            mSpeechManager = null;
                        }
                        if (resultString.trim().isEmpty()) {
                            Toast.makeText(getContext(), getString(R.string.empty_message_error), Toast.LENGTH_LONG).show();
                        } else {
                            words.set(0, new Word(results.get(0)));
                            words.set(1, new Word(results.get(1)));
                            words.set(2, new Word(results.get(2)));
                            words.set(3, new Word(results.get(2)));
                            words.set(4, new Word(results.get(2)));
                            adapter.notifyDataSetChanged ();

                            // Java V2
                            sendMessageToDialogflow(resultString);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.empty_message_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
