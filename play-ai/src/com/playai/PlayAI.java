package com.playai;

import com.playai.helpers.PlayAIVoice;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.common.*;

import java.io.*;
import java.net.*;
import android.os.*;
import android.media.MediaPlayer;
import org.json.*;

@DesignerComponent(
    version = 1,
    versionName = "1.0",
    description = "PlayAI TTS Extension to generate and play audio using PlayAI models running 10x faster on Groq. <br> <a href=\"https://ai2.sarthakdev.in/extensions\" target=\"_blank\">My other extensions</a> <br> Built by <a href=\"https://www.sarthakdev.in/\" target=\"_blank\">Sarthak Gupta",
    iconName = "icon.png",
    category = ComponentCategory.EXTENSION,
    nonVisible = true
)
public class PlayAI extends AndroidNonvisibleComponent {

    private final String model = "playai-tts";
    private String voice = PlayAIVoice.Arista.toUnderlyingValue();
    private String apiKey = "";
    private final Handler handler;
    private MediaPlayer mediaPlayer;

    public PlayAI(ComponentContainer container) {
        super(container.$form());
        handler = new Handler(Looper.getMainLooper());
    }

    @SimpleProperty(description = "Gets the current voice")
    public @Options(PlayAIVoice.class) String Voice() {
        return voice;
    }

    @SimpleProperty(description = "Sets the voice for TTS")
    public void Voice(@Options(PlayAIVoice.class) String voice) {
        this.voice = voice;
    }

    @SimpleProperty(description = "Sets the API key for Groq")
    public void ApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @SimpleFunction(description = "Generates TTS audio from the input text using the Groq API")
    public void GenerateAudio(String text) {
        final String currentVoice = voice;
        final String currentApiKey = apiKey;

        new Thread(() -> {
            if (currentApiKey.isEmpty()) {
                handler.post(() -> ErrorOccurred("API key is empty"));
                return;
            }

            try {
                JSONObject json = new JSONObject();
                json.put("model", model);
                json.put("voice", currentVoice);
                json.put("input", text);
                json.put("response_format", "mp3");

                URL url = new URL("https://api.groq.com/openai/v1/audio/speech");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + currentApiKey);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    InputStream is = conn.getInputStream();
                    java.io.File cacheDir = form.getCacheDir();
                    java.io.File audioFile = java.io.File.createTempFile("tts", ".mp3", cacheDir);
                    FileOutputStream fos = new FileOutputStream(audioFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    fos.close();
                    is.close();

                    handler.post(() -> AudioGenerated(audioFile.getAbsolutePath()));
                } else {
                    InputStream es = conn.getErrorStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(es));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    handler.post(() -> ErrorOccurred("HTTP " + responseCode + ": " + sb));
                }
                conn.disconnect();
            } catch (Exception e) {
                handler.post(() -> ErrorOccurred("Error: " + e.getMessage()));
            }
        }).start();
    }

    @SimpleFunction(description = "Plays the TTS audio file at the specified file path")
    public void PlayTTS(String filePath) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                mediaPlayer.release();
                mediaPlayer = null;
            });
        } catch (Exception e) {
            ErrorOccurred("Playback error: " + e.getMessage());
        }
    }

    @SimpleEvent(description = "Triggered when audio is successfully generated. Returns the file path to the mp3 file")
    public void AudioGenerated(String filePath) {
        EventDispatcher.dispatchEvent(this, "AudioGenerated", filePath);
    }

    @SimpleEvent(description = "Triggered when an error occurs during audio generation or playback")
    public void ErrorOccurred(String errorMessage) {
        EventDispatcher.dispatchEvent(this, "ErrorOccurred", errorMessage);
    }
}
