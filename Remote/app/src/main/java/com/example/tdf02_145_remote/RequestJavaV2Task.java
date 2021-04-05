package com.example.tdf02_145_remote;

import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.tdf02_145_remote.main.ConversationFragment;
import com.google.cloud.dialogflow.v2beta1.DetectIntentRequest;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;


public class RequestJavaV2Task extends AsyncTask<Void, Void, DetectIntentResponse> {

    Fragment fragment;
    private SessionName session;
    private SessionsClient sessionsClient;
    private QueryInput queryInput;

    String TAG = "RequestJavaV2Task";

    public RequestJavaV2Task(Fragment fragment, SessionName session, SessionsClient sessionsClient, QueryInput queryInput) {
        this.fragment = fragment;
        this.session = session;
        this.sessionsClient = sessionsClient;
        this.queryInput = queryInput;
    }

    @Override
    protected DetectIntentResponse doInBackground(Void... voids) {
        try{
            DetectIntentRequest detectIntentRequest =
                    DetectIntentRequest.newBuilder()
                            .setSession(session.toString())
                            .setQueryInput(queryInput)
                            .build();
            Log.d(TAG, "Detect intent passed");
            return sessionsClient.detectIntent(detectIntentRequest);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Detect intent failed");
        }
        return null;
    }

    @Override
    protected void onPostExecute(DetectIntentResponse response) {
        ((ConversationFragment) fragment).callbackV2(response);
    }
}
