package ru.coolsoft.iris2go;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.coolsoft.iris2go.rest.Engine;
import ru.coolsoft.iris2go.rest.ResponseDto;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.View.NO_ID;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_LANG = "lang";
    private static final Lang DEFAULT_LANG_FROM = Lang.RUS;
    private static final Lang DEFAULT_LANG_PAIR = Lang.HEB_RUS;

    public static final Map<String, String> STYLING = new HashMap<String, String>() {{
        put("class=\"splitter\">", "style=\"" +
                "    height: 2px;" +
                "    background-color: #ff0000;" +
                "    text-align: center;" +
                "\">");
        put("class=\"cycle1\"", "style=\"" +
                "    background-color: #ffffff;" +
                "    padding: 5px;" +
                "\"");
        put("class=\"cycle-1\"", "style=\"" +
                "    background-color: #f1f1f1;" +
                "    padding: 5px;" +
                "\"");
        put("class=\"word\"", "style=\"" +
                "    font-weight: bold;" +
                "    color: #000;" +
                "\"");
        put("class=\"translit\"", "style=\"" +
                "    font-weight: bold;" +
                "    color: #009900;" +
                "\"");
        put("class=\"translitaccent\"", "style=\"" +
                "    color: #ff0000;" +
                "\"");
        put("class=\"rtl\"", "style=\"" +
                "    direction: rtl;" +
                "    text-align: right;" +
                "\"");
        put("class=\"wordaccent\"", "style=\"" +
                "    color: #ff0000;" +
                "\"");
    }};

    private final ArrayList<Pair<Lang, String>> mHistory = new ArrayList<>();
    private Lang mLangFrom = DEFAULT_LANG_FROM;
    private int mHistoryHeadIndex;

    private TextView mInput;
    private WebView mResult;
    private Switch mTranscript;
    private Switch mNekudot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mLangFrom = Lang.valueOf(savedInstanceState.getInt(KEY_LANG, DEFAULT_LANG_FROM.mLang));
        }

        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setIcon(R.drawable.logo);

        mInput = findViewById(R.id.input);
        mResult = findViewById(R.id.result);
        Objects.requireNonNull((View) findViewById(R.id.input)).setOnKeyListener(
                (v, keyCode, event) -> {
                    if (keyCode == 66 && event.getAction() == ACTION_UP) {
                        translate(true);
                        return true;
                    }
                    return false;
                }
        );

        mTranscript = findViewById(R.id.transcript);
        mNekudot = findViewById(R.id.nekudot);
        CompoundButton.OnCheckedChangeListener checkedChangeListener =
                (buttonView, isChecked) -> translate(true);
        mTranscript.setOnCheckedChangeListener(checkedChangeListener);
        mNekudot.setOnCheckedChangeListener(checkedChangeListener);

        updateLangs();
    }

    @Override
    public void onBackPressed() {
        if (mHistoryHeadIndex < 2) {
            super.onBackPressed();
            return;
        }

        Pair<Lang, String> polledItem = mHistory.get(--mHistoryHeadIndex - 1);
        mLangFrom = polledItem.first;
        updateLangs();
        mInput.setText(polledItem.second);

        translate(false);
    }

    private void translate(boolean updateHistory) {
        CharSequence text = mInput.getText();

        //ToDo: add char range to lang definition
        if (mLangFrom == Lang.RUS && text.charAt(0) > 'я') {
            onSwap(null);
        }
        if (updateHistory) {
            Pair<Lang, String> historyItem = new Pair<>(mLangFrom, text.toString());
            if (mHistoryHeadIndex == 0 || !historyItem.equals(mHistory.get(mHistoryHeadIndex - 1))) {
                mHistory.add(mHistoryHeadIndex++, historyItem);
            }
        }

        findViewById(R.id.loader).setVisibility(View.VISIBLE);
        Engine.translate(text.toString(),
                mLangFrom.mLang,
                text.charAt(0) >= 'А' && text.charAt(0) <= 'я',
                mTranscript.isChecked(),
                mNekudot.isChecked(),

                new Callback<ResponseDto>() {
                    private void doFinally() {
                        findViewById(R.id.loader).setVisibility(View.GONE);
                    }

                    @Override
                    public void onResponse(@NonNull Call<ResponseDto> call, @NonNull Response<ResponseDto> response) {
                        ResponseDto dto = response.body();
                        String text = Objects.requireNonNull(dto).cmd.get(1).text;
                        for (Map.Entry<String, String> pattern : STYLING.entrySet()) {
                            text = text.replace(pattern.getKey(), pattern.getValue());
                        }

                        mResult.loadDataWithBaseURL(null, text, "text/html; charset=utf-8", "UTF-8", null);
                        doFinally();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseDto> call, @NonNull Throwable t) {
                        t.printStackTrace();

                        mResult.loadDataWithBaseURL(null, t.getMessage(), "text/html", null, null);
                        doFinally();
                    }
                }
        );
    }

    private void updateLangs() {
        Lang langTo = get2ndParty();
        ((ImageView) Objects.requireNonNull((View) findViewById(R.id.flag_from))).setImageResource(mLangFrom.mIcon);
        ((ImageView) Objects.requireNonNull((View) findViewById(R.id.flag_to))).setImageResource(langTo.mIcon);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LANG, mLangFrom.mLang);
    }

    public void onSwap(View view) {
        mLangFrom = get2ndParty();
        updateLangs();
    }

    private Lang get2ndParty() {
        return Lang.valueOf(MainActivity.DEFAULT_LANG_PAIR.mLang - mLangFrom.mLang);
    }

    private enum Lang {
        HEB(1, R.drawable.flag_isr),
        RUS(2, R.drawable.flag_rus),
        HEB_RUS(3, NO_ID);
        /*ENG = 4*/
        /*ENG_HEB = 5*/
        /*ENG_RUS = 6*/

        Lang(int langId, @DrawableRes int iconResId) {
            mLang = langId;
            mIcon = iconResId;
        }

        static Lang valueOf(int langId) {
            return values()[langId - 1];
        }

        private final int mLang;
        private final int mIcon;
    }
}
