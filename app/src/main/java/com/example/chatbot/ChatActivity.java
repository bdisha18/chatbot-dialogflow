package com.example.chatbot;

import static android.content.ContentValues.TAG;
import static com.google.cloud.dialogflow.v2.SessionsClient.create;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbot.helpers.SendMessage;
import com.example.chatbot.models.Message;
import com.google.api.client.util.Lists;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity implements BotReply {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private final String uuid = UUID.randomUUID().toString();
    RecyclerView chatView;
    ChatAdapter chatAdapter;
    List<Message> messageList = new ArrayList<>();
    EditText editMessage;
    Button btnSend;
    ImageView voiceAssistanceBtn;
    TextView usernameText;
    //    private final String TAG = "mainactivity";
    String botReply;
    //    SpeechRecognizer speechRecognizer;
    int result;
    //dialogFlow
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Full Screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.edit_message);
        usernameText = findViewById(R.id.usernameText);

        String username = getIntent().getExtras().getString("name");
        usernameText.setText(username);

        chatAdapter = new ChatAdapter(messageList, ChatActivity.this);
        chatView.setAdapter(chatAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatView.setLayoutManager(layoutManager);

        //Send Message Button
        btnSend = findViewById(R.id.send_btn);
        btnSend.setOnClickListener(view -> {
            String message = editMessage.getText().toString();
            if (!message.isEmpty()) {
                messageList.add(new Message(message, false));
                editMessage.setText("");
                sendMessageToBot(message);
                Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager())
                        .scrollToPosition(messageList.size() - 1);
            } else {
                Toast.makeText(ChatActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();
            }
        });

        setUpBot();

        //Text to Speech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                result = tts.setLanguage(Locale.ENGLISH);
            }
        });

        // Mic Button to speak
        voiceAssistanceBtn = findViewById(R.id.voice_assistance_btn);
        voiceAssistanceBtn.setOnClickListener(view -> {
            Intent intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            } catch (Exception e) {
                Toast.makeText(ChatActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Speech to Text Output
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                editMessage.setText(Objects.requireNonNull(result).get(0));
            }
        }
    }

    //Back Button
    @Override
    public void onClick(View v) {
        super.onBackPressed(); // or super.finish();
    }

    // Connect Dialogflow to project
    private void setUpBot() {
        try {
            InputStream stream = this.getResources().openRawResource(R.raw.dialogflow_credential);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList(Collections.singleton("https://www.googleapis.com/auth/cloud-platform")));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);
        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    // Send Message to Bot
    private void sendMessageToBot(String message) {
        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        new SendMessage(this, sessionName, sessionsClient, input).execute();
    }


    // Response Message From Bot
    @Override
    public void callback(DetectIntentResponse returnResponse) {
        if (returnResponse != null) {
            botReply = returnResponse.getQueryResult().getFulfillmentText();
            if (!botReply.isEmpty()) {
                messageList.add(new Message(botReply, true));

                // Check Text to Speech is supported or not
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(ChatActivity.this, "Language not Supported", Toast.LENGTH_SHORT).show();
                } else {
                    tts.speak(botReply, TextToSpeech.QUEUE_FLUSH, null);
                }

                chatAdapter.notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);
            } else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "failed to connect!", Toast.LENGTH_SHORT).show();
        }
    }
}
