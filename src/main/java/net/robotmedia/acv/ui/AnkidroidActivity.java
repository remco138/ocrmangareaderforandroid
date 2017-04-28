package net.robotmedia.acv.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ichi2.anki.api.AddContentApi;

import java.util.Map;

/**
 * Created by rdbruin on 19-3-17.
 */

public class AnkidroidActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private AddContentApi api;
    private String word;
    private String definition;

    private final String[] CARD_NAME = {"Ocr manga vocab"};
    private final String MODEL_NAME = "remco.ocrmangareader";
    private final String[] FIELD_NAMES = {"word", "definition", "sanseido", "audio"};
    private final String[] QFMT = {"{{audio}}<br><span class=\"kanji\"> {{furigana:Word}} </span>"};
    private final String[] AFMT = {"{{FrontSide}}\n" +
            "\n" +
            "<hr id=answer>\n" +
            "\n" +
            "{{Back}}\n" +
            "\n" +
            "<br/>\n" +
            "<br/><div id=\"japanese_meaning\">\n" +
            "<span class= \"title\">Japanese:</span><br/> \n" +
            "<span id= \"japanese\" class=\"sanseido\">\n" +
            "{{furigana:Sanseido}}<br/><br>\n" +
            "</span></div>\n"};


    private final int NUMBER_OF_FIELDS = 4;


    public void init(Context context) {
        api = new AddContentApi(context);
    }

    public void setCardState(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // call the super class onCreate to complete the creation of activity like
        // the view hierarchy
        super.onCreate(savedInstanceState);
        Log.d("MyApp", "onCreate");
    }

    /*fields are in this order: word, definition, sanseido, audio (, more?/)*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("MyApp", "onrequestpermissionsresult");
        addCard();
    }

    public void addCard() {
        Log.d("MyApp", "onRequestPermissionsResult");
        long deckId = getOrCreateDeckId(CARD_NAME[0]);
        long modelId = getOrCreateModelId(MODEL_NAME);
        api.addNote(modelId, deckId, new String[]{this.word, this.definition, "sanseido", "audio"}, null);
    }

    public long getOrCreateModelId(String modelName) {
        Map<Long, String> modelList = api.getModelList(NUMBER_OF_FIELDS);
        for (Map.Entry<Long, String> entry : modelList.entrySet()) {
            if (entry.getValue().equals(modelName)) {
                return entry.getKey(); // first model wins
            }
        }
        //todo: may fail
        return api.addNewCustomModel(MODEL_NAME, FIELD_NAMES, CARD_NAME, QFMT, AFMT, null, null, null);
    }

    private Long getOrCreateDeckId(String deckName) {
        Map<Long, String> deckList = api.getDeckList();
        for (Map.Entry<Long, String> entry : deckList.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(deckName)) {
                return entry.getKey();
            }
        }
        return api.addNewDeck(deckName);
    }

}
