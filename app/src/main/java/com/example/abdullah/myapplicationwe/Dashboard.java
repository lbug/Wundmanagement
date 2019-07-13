package com.example.abdullah.myapplicationwe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ai.snips.hermes.IntentMessage;
import ai.snips.hermes.Slot;
import ai.snips.nlu.ontology.SlotValue;
import ai.snips.platform.SnipsPlatformClient;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = Dashboard.class.getSimpleName();
    private File assistantLocation;
    private SnipsPlatformClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_dashboard);

        // Initialisiere Snips
        assistantLocation = new File(getFilesDir(), "snips");
        extractAssistantIfNeeded(assistantLocation);
        startSnips(assistantLocation);
    }

    //snips kram
    private void extractAssistantIfNeeded(File assistantLocation) {
        File versionFile = new File(assistantLocation,
                "android_version_" + BuildConfig.VERSION_NAME);

        if (versionFile.exists()) {
            return;
        }

        try {
            assistantLocation.delete();
            Dashboard.unzip(getBaseContext().getAssets().open("assistant.zip"),
                    assistantLocation);
            versionFile.createNewFile();
        } catch (IOException e) {
            return;
        }
    }

    private static void unzip(InputStream zipFile, File targetDirectory)
            throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(zipFile));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    private SnipsPlatformClient createClient(File assistantLocation) {
        File assistantDir = new File(assistantLocation, "assistant");

        final SnipsPlatformClient client = new SnipsPlatformClient.Builder(assistantDir)
                .enableDialogue(true)
                .enableHotword(true)
                .enableSnipsWatchHtml(false)
                .enableLogs(true)
                .withHotwordSensitivity(0.5f)
                .enableStreaming(false)
                .enableInjection(false)
                .build();

        client.setOnPlatformReady(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                Log.d(TAG, "Snips is ready. Say the wake word!");
                return null;
            }
        });

        client.setOnPlatformError(
                new Function1<SnipsPlatformClient.SnipsPlatformError, Unit>() {
                    @Override
                    public Unit invoke(final SnipsPlatformClient.SnipsPlatformError
                                               snipsPlatformError) {
                        // Handle error
                        Log.d(TAG, "Error: " + snipsPlatformError.getMessage());
                        return null;
                    }
                });

        client.setOnHotwordDetectedListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                // Wake word detected, start a dialog session
                Log.d(TAG, "Wake word detected!");
                client.startSession(null, new ArrayList<String>(),
                        false, null);
                return null;
            }
        });

        client.setOnIntentDetectedListener(new Function1<IntentMessage, Unit>() {
            @Override
            public Unit invoke(final IntentMessage intentMessage) {
                // Intent detected, so the dialog session ends here
                client.endSession(intentMessage.getSessionId(), null);
                Log.d(TAG, "Intent detected: " +
                        intentMessage.getIntent().getIntentName());
                // hier werden die verschiedenen handler aufgerufen
                switch (intentMessage.getIntent().getIntentName()) {
                    case "lbug:TakePicture":
                        Log.d("Lukas", intentMessage.toString());
                        handleTakePicture(intentMessage);
                        break;

                    case "lbug:ConfirmAction":
                        Log.d("Lukas", intentMessage.toString());
                        handleConfirmAction(intentMessage);
                        break;
                    case "lbug:LocalizeWoundSector":
                        Log.d("Lukas", intentMessage.toString());
                        handleLocalizeWoundSector(intentMessage);
                        break;

                    case "lbug:WundverlaufAnzeigen":
                        Log.d("Lukas", intentMessage.toString());
                        handleWundverlaufAnzeigen(intentMessage);
                        break;

                    case "lbug:StartCamera":
                        Log.d("Lukas", intentMessage.toString());
                        handleStartCamera(intentMessage);
                        break;

                    case "lbug:Home":
                        Log.d("Lukas", intentMessage.toString());
                        handleHome(intentMessage);
                        break;

                    case "lbug:LastPicture":
                        Log.d("Lukas", intentMessage.toString());
                        handleLastPicture(intentMessage);
                        break;

                    case "lbug:NextPicture":
                        Log.d("Lukas", intentMessage.toString());
                        handleNextPicture(intentMessage);
                        break;

                    default:
                        Log.d("Lukas", "Default case");
                }
                return null;
            }
        });

        client.setOnSnipsWatchListener(new Function1<String, Unit>() {
            public Unit invoke(final String s) {
                Log.d(TAG, "Log: " + s);
                return null;
            }
        });

        return client;
    }

    private void startSnips(File snipsDir) {
        client = createClient(snipsDir);
        client.connect(this.getApplicationContext());
    }


    //voice commands

    // navigiere zu einem neueren Bild im Wundverlauf aus
    private void handleNextPicture(final IntentMessage intentMessage) {
    }

    // navigiere zu einem älteren Bild im Wundverlauf
    private void handleLastPicture(final IntentMessage intentMessage) {
    }

    // gehe zurück zum Dashboard
    private void handleHome(final IntentMessage intentMessage) {
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    // starte die Activity DetectorCamera
    private void handleStartCamera(final IntentMessage intentMessage) {
        showToast("StartCamera funktioniert!");
        Intent intent = new Intent(this, DetectorCamera.class);
        startActivity(intent);
    }

    // starte Activity WoundHistory
    private void handleWundverlaufAnzeigen(final IntentMessage intentMessage) {
        Intent intent = new Intent(this, WoundHistory.class);
        startActivity(intent);
    }

    // Bild wird aufgenommen und analysiert, anschließend wird Activity Result ausgeführt
    private void handleTakePicture(final IntentMessage intentMessage){
        DetectorCamera.getInstance().handleTakePicture();
    }

    // Eine Zahl zwischen 1-12 wird eingelesen, die Klasse Localization wird gestartet
    private void handleLocalizeWoundSector(final IntentMessage intentMessage) {
        List<Slot> slots = intentMessage.getSlots();
        int v1 = (int) ((SlotValue.NumberValue) slots.get(0).getValue()).getValue();
        Localization.woundSector = v1;
        String message = "Wundsektor: " + v1;
        Log.d("Lukas", message);
        makeText(this, message, LENGTH_LONG).show();
        Log.d("Lukas", "handleLocalizeWoundSector");
        Intent intent = new Intent(this, Localization.class);
        startActivity(intent);
    }

    // "ja" oder nein "nein" als intent.
    // falls "ja": starte Activity Localization
    // falls "nein": gehe zurück zu Activity DetectorCamera, um noch ein Bild aufzunehmen
    private void handleConfirmAction(final IntentMessage intentMessage) {

        List<Slot> slots = intentMessage.getSlots();
        String v1 = slots.get(0).component1();
        String answer = getIntent().getStringExtra("ConfirmAction");
        Log.d("Confirmation", v1);

        switch (v1) {
            case "ja":
                Intent localizationIntent = new Intent(this, Localization.class);
                makeText(this, "ConfirmAction", LENGTH_SHORT).show();
                startActivity(localizationIntent);
                break;

            case "nein":
                makeText(this, "ConfirmAction nein", LENGTH_SHORT).show();
                Result.getInstance().onBackPressed();
                break;

            default:
                break;
        }
    }



    public void next(View view) {

        Intent intent = new Intent(this, DetectorCamera.class);
        startActivity(intent);



    }


    private void showToast(String text) {
        runOnUiThread(() ->
                makeText(getApplicationContext(), text, LENGTH_SHORT).show()
        );
    }
}
