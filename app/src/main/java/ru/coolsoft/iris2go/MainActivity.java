package ru.coolsoft.iris2go;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import retrofit2.*;
import ru.coolsoft.iris2go.rest.Engine;
import ru.coolsoft.iris2go.rest.ResponseDto;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.View.NO_ID;

public class MainActivity extends AppCompatActivity {
    private static final String KEY_LANG = "lang";
    private static final Lang DEFAULT_LANG_FROM = Lang.RUS;
    private static final Lang DEFAULT_LANG_PAIR = Lang.HEB_RUS;

    private Lang mLangFrom = DEFAULT_LANG_FROM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            mLangFrom = Lang.valueOf(savedInstanceState.getInt(KEY_LANG, DEFAULT_LANG_FROM.mLang));
        }

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setIcon(R.drawable.logo);

        findViewById(R.id.input).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66 && event.getAction() == ACTION_UP) {
                    CharSequence text = ((TextView)v).getText();
                    Engine.translate(text.toString(),
                            mLangFrom.mLang,
                            text.charAt(0) > 'А' && text.charAt(0) < 'я',
                            findViewById(R.id.transcript).isSelected(),
                            findViewById(R.id.nekudot).isSelected(),

                            new Callback<ResponseDto>() {
                                @Override
                                public void onResponse(@NonNull Call<ResponseDto> call, @NonNull Response<ResponseDto> response) {
                                    ResponseDto dto = response.body();
                                    //format Html.fromHtml(dto.cmd[1].text);

                                    ((TextView)findViewById(R.id.result)).setText(Objects.requireNonNull(dto).cmd.get(1).text);
                                }

                                @Override
                                public void onFailure(@NonNull Call<ResponseDto> call, @NonNull Throwable t) {
                                    t.printStackTrace();

                                    ((TextView)findViewById(R.id.result)).setText(t.getMessage());
                                }
                            }
                    );
                    return true;
                }
                return false;
            }
        });

        updateLangs();
    }

    private void updateLangs() {
        Lang langTo = get2ndParty(DEFAULT_LANG_PAIR);
        ((ImageView)findViewById(R.id.flag_from)).setImageResource(mLangFrom.mIcon);
        ((ImageView)findViewById(R.id.flag_to)).setImageResource(langTo.mIcon);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LANG, mLangFrom.mLang);
    }

    public void onSwap(View view) {
        mLangFrom = get2ndParty(DEFAULT_LANG_PAIR);
        updateLangs();
    }

    private Lang get2ndParty(Lang pair){
        return Lang.valueOf(pair.mLang - mLangFrom.mLang);
    }

    private enum Lang{
        HEB (1, R.drawable.flag_isr),
        RUS (2, R.drawable.flag_rus),
        HEB_RUS (3, NO_ID);
        /*ENG = 4*/
        /*ENG_HEB = 5*/
        /*ENG_RUS = 6*/

        Lang(int langId, @DrawableRes int iconResId){
            mLang = langId;
            mIcon = iconResId;
        }

        static Lang valueOf(int langId){
            return values()[langId - 1];
        }

        private final int mLang;
        private final int mIcon;
    }
}
