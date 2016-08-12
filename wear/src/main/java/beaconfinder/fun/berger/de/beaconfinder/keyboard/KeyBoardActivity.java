package beaconfinder.fun.berger.de.beaconfinder.keyboard;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.brunodles.simplepreferences.lib.DaoPreferences;

import java.util.ArrayList;

import beaconfinder.fun.berger.de.beaconfinder.R;

public class KeyBoardActivity extends Activity implements View.OnClickListener {
    /**
     * Wear-Keyboard, Activity-Based Keyboard for Android Wear.
     * Built by Ido Ideas, 2014.
     * This code is Open Source and free to use.
     */
    private TextView mTextView;
    static EditText editText;
    static Button del, space, num, cap, OK;
    RelativeLayout Scroll;
    static String letters = "abcdefghijklmnopqrstuvwxyz.:/-";
    static String capitalletters = ".:/ABCDEFGHIJKLMNOPQRSTUVWXYZ.:/-";
    static String numbers = "1234567890.:/-";
    ArrayList<Button> buttons = new ArrayList<Button>();
    private boolean Capital = false;
    static String textInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_keyboard);
        final Bundle bundle = getIntent().getExtras();
        Scroll = (RelativeLayout) findViewById(R.id.scroll);
        editText = (EditText) findViewById(R.id.textedit);
        OK = (Button) findViewById(R.id.send);

        if (bundle.getString("ip") != null)
            editText.setText(bundle.getString("ip"));
        else if (bundle.getString("uuid") != null)
            editText.setText(bundle.getString("uuid"));
        else if (bundle.getString("maj") != null)
            editText.setText(bundle.getString("maj"));
        else if (bundle.getString("min") != null)
            editText.setText(bundle.getString("min"));
        else if (bundle.getString("methode") != null)
            editText.setText(bundle.getString("methode"));
        else if (bundle.getString("dist") != null)
            editText.setText(bundle.getString("dist"));

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DaoInputObject daoInputObject = new DaoInputObject();
                DaoPreferences dao = new DaoPreferences(KeyBoardActivity.this);

                textInput = editText.getText().toString();

                daoInputObject.name = textInput;


                if (bundle.getString("ip") != null) {
                    dao.commit(daoInputObject, "ip");

                    setResult(Activity.RESULT_OK,
                            new Intent().putExtra("key", "ip"));
                    finish();
                } else if (bundle.getString("uuid") != null) {
                    dao.commit(daoInputObject, "uuid");

                    setResult(Activity.RESULT_OK,
                            new Intent().putExtra("key", "uuid"));
                    finish();
                } else if (bundle.getString("maj") != null) {
                    dao.commit(daoInputObject, "maj");

                    setResult(Activity.RESULT_OK,
                            new Intent().putExtra("key", "maj"));
                    finish();
                } else if (bundle.getString("min") != null) {
                    dao.commit(daoInputObject, "min");

                    setResult(Activity.RESULT_OK,
                            new Intent().putExtra("key", "min"));
                    finish();
                } else if (bundle.getString("methode") != null) {
                    dao.commit(daoInputObject, "methode");

                    setResult(Activity.RESULT_OK,
                            new Intent().putExtra("key", "methode"));
                    finish();
                } else if (bundle.getString("dist") != null) {
                    dao.commit(daoInputObject, "dist");
                    setResult(Activity.RESULT_OK,
                            new Intent().putExtra("key", "dist"));
                    finish();
                }

            }
        });
        setKeyboardCharacters(letters);
        del = (Button) findViewById(R.id.backspace);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() > 0) {
                    int cursorPosition = editText.getSelectionStart();
                    if (cursorPosition > 0) {
                        editText.setText(editText.getText().delete(cursorPosition - 1, cursorPosition));
                        editText.setSelection(cursorPosition - 1);
                    }
//                    editText.setText(editText.getText().toString().substring(editText.getSelectionEnd()-1, editText.getSelectionEnd()));
                }
            }
        });
        space = (Button) findViewById(R.id.space);
        space.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int start = Math.max(editText.getSelectionStart(), 0);
                int end = Math.max(editText.getSelectionEnd(), 0);
                editText.getText().replace(Math.min(start, end), Math.max(start, end),
                        " ", 0, 1);
//                editText.setText(editText.getText() + " ");
            }
        });
        num = (Button) findViewById(R.id.numbers);
        num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scroll.removeAllViews();
                if (num.getText().toString().equals("123")) {
                    num.setText("Eng");
                    setKeyboardCharacters(numbers);
                } else {
                    num.setText("123");
                    setKeyboardCharacters(letters);
                }
            }
        });
        cap = (Button) findViewById(R.id.capital);
        cap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num.getText().toString().equals("123")) {
                    Scroll.removeAllViews();
                    if (!Capital) {
                        Capital = true;
                        setKeyboardCharacters(capitalletters);
                    } else {
                        Capital = false;

                    }
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        Vibrator vi = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        vi.vibrate(50);
        Button button = (Button) v;
        int start = Math.max(editText.getSelectionStart(), 0);
        int end = Math.max(editText.getSelectionEnd(), 0);
        editText.getText().replace(Math.min(start, end), Math.max(start, end),
                button.getText(), 0, button.getText().length());
//        editText.setText(editText.getText() + "" + button.getText());
    }

    public void setKeyboardCharacters(String Characters) {
        for (int i = 0; i < Characters.length(); i++) {
            Button b = new Button(getApplicationContext());
            b.setId(i);
            buttons.add(b);
            Button currentButton = buttons.get(i);
            Resources r = getResources();
            float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, r.getDisplayMetrics());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) px, (int) px);
            if (i > 0) {
                if (i % 4 != 0) {
                    if (i < 4) {
                        lp.addRule(RelativeLayout.BELOW, R.id.textedit);
                    } else {
                        lp.addRule(RelativeLayout.BELOW, buttons.get(i - 4).getId());
                    }
                    if (i == 1) {
                        lp.setMargins((int) px, lp.topMargin, lp.rightMargin, lp.bottomMargin);
                    } else {
                        lp.addRule(RelativeLayout.RIGHT_OF, buttons.get(i - 1).getId());
                    }
                } else {
                    lp.addRule(RelativeLayout.BELOW, buttons.get(i - 3).getId());
                }
            } else {
                lp.addRule(RelativeLayout.BELOW, R.id.textedit);
            }
            currentButton.setLayoutParams(lp);
            currentButton.setText(Characters.charAt(i) + "");
            currentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Vibrator vi = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                    vi.vibrate(50);
                    Button button = (Button) v;
                    int start = Math.max(editText.getSelectionStart(), 0);
                    int end = Math.max(editText.getSelectionEnd(), 0);
                    editText.getText().replace(Math.min(start, end), Math.max(start, end),
                            button.getText(), 0, button.getText().length());
//                    editText.setText(editText.getText() + "" + button.getText());

                }
            });
            Scroll.addView(currentButton);
        }
    }
}
