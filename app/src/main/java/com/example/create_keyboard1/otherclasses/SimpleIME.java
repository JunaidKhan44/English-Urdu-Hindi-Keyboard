package com.example.create_keyboard1.otherclasses;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.InputMethodService;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.MetaKeyKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.create_keyboard1.fragments.FlagsFrag;
import com.example.create_keyboard1.fragments.GradientFrag;
import com.example.create_keyboard1.fragments.ImageFrag;
import com.example.create_keyboard1.fragments.SportsFrag;
import com.example.create_keyboard1.R;
import com.example.create_keyboard1.database.DatabaseManager;
import com.example.create_keyboard1.util.Keyboard;
import com.example.create_keyboard1.util.KeyboardView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.create_keyboard1.adapter.KeyThemeAdapter.POSITION_AD1;
import static com.example.create_keyboard1.adapter.KeyThemeAdapter.SHARED_PREF_NAME1;
import static com.example.create_keyboard1.adapter.MyAdapter.GROUPSNAME_SHARED_PREF;
import static com.example.create_keyboard1.adapter.MyAdapter.POSITION_AD;
import static com.example.create_keyboard1.adapter.MyAdapter.SHARED_PREF_NAME;
import static com.example.create_keyboard1.otherclasses.CamGalleryBackground.CUSTOM_KEY;
import static com.example.create_keyboard1.otherclasses.CamGalleryBackground.CUSTOM_MODE;
import static com.example.create_keyboard1.otherclasses.SettingActivity.POSITION_AD_PREVIEW;
import static com.example.create_keyboard1.otherclasses.SettingActivity.PREVIEW_PREF_NAME;


public class SimpleIME extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {


    AnimationDrawable wifiAnimation;
    public ImageView hide, emojis, mic, color, setting;
    boolean sphenglish, sphurdu, sphhindu;
    public static InputConnection ic;
    public boolean flagforemoji = false;

    public KeyboardView obj;
    public View kv;
    public Keyboard keyboard;
    SharedPreferences sharedPreferences, sharedPreferencesforcustback;
    SharedPreferences forpreview;
    private boolean caps = false;
    public boolean isflagforurdu = false;
    private boolean flagforenglish = false;
    private SharedPreferences sharedPreferences2;
    private int mLastDisplayWidth;
    private InputMethodManager mInputMethodManager;
    private String mWordSeparators;
    private CandidateView mCandidateView;
    private long mMetaState;
    private DatabaseManager db;
    private StringBuilder mComposing = new StringBuilder();
    public static boolean mPredictionOn;
    private boolean mCompletionOn;
    private CompletionInfo[] mCompletions;
    private boolean mSound;
    private ArrayList<String> list;
    static final boolean PROCESS_HARD_KEYS = true;

    public static String mActiveKeyboard;
    private BitmapDrawable mBitmapDrawable;
    private File file;
    boolean flagformic = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateInputView() {

        kv = (LinearLayout) getLayoutInflater().inflate(R.layout.keyboardfortest, null);
        obj = kv.findViewById(R.id.keyboard);
        flagforemoji = false;
        initializeView();
        mic.setImageResource(R.drawable.ic_baseline_mic_24);

        forpreview = getSharedPreferences(PREVIEW_PREF_NAME, MODE_PRIVATE);
        int isval = forpreview.getInt(POSITION_AD_PREVIEW, 1);
        if (isval == 0) {

            Log.d("simpleime", "" + 0);
        }
        if (isval == 1) {
       
            Log.d("simpleime", "" + 1);
        }
            
        sharedPreferences2 = getSharedPreferences(SHARED_PREF_NAME1, MODE_PRIVATE);
        int pos1 = sharedPreferences2.getInt(POSITION_AD1, 0);
        if (pos1 == 0) {
            kv.findViewById(R.id.keyboard2).setVisibility(View.GONE);
            kv.findViewById(R.id.keyboard3).setVisibility(View.GONE);
            obj = kv.findViewById(R.id.keyboard);
            obj.setVisibility(View.VISIBLE);
            Log.d("simpleime", "Theme 0");

        } else if (pos1 == 1) {
            kv.findViewById(R.id.keyboard).setVisibility(View.GONE);
            kv.findViewById(R.id.keyboard3).setVisibility(View.GONE);
            obj = kv.findViewById(R.id.keyboard2);
            obj.setVisibility(View.VISIBLE);
            Log.d("simpleime", "Theme 1");
        } else if (pos1 == 2) {
            kv.findViewById(R.id.keyboard).setVisibility(View.GONE);
            kv.findViewById(R.id.keyboard2).setVisibility(View.GONE);
            obj = kv.findViewById(R.id.keyboard3);
            obj.setVisibility(View.VISIBLE);
            Log.d("simpleime", "Theme 2");
        }

        keyboard = new Keyboard(this, R.xml.custom_qwerty);
        obj.setKeyboard(keyboard);
        flagforenglish = true;
        isflagforurdu = false;
        sphenglish = true;




       int val= checkWhichGoToApply();
       if(val==5) {
           retriveBitmapAndSet();
       }else{
           callbackground();
       }

    
        obj.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //  kv.closing();
            }
            return false;
        });
        obj.setOnKeyboardActionListener(this);

        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), Background.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestHideSelf(0);
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        emojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flagforemoji = true;
                keyboard = new Keyboard(getBaseContext(), R.xml.emojis);
                obj.setKeyboard(keyboard);
                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            }
        });
        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                micFunForAll();


                mic.setBackground(getResources().getDrawable(R.drawable.background2));
                mic.setScaleX(1.3f);
                mic.setScaleY(1.3f);

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        mic.setBackground(getResources().getDrawable(R.drawable.background1));
                        mic.setScaleX(1f);
                        mic.setScaleY(1f);
                    }
                }, 5000);

            }
        });

        return kv;
    }

    @Override
    public void onInitializeInterface() {
        if (keyboard != null) {
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        keyboard = new Keyboard(this, R.xml.custom_qwerty);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);

        return mCandidateView;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        setInputView(onCreateInputView());
        mComposing.setLength(0);
        updateCandidates();
        if (!restarting) {
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;

      
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
             
                break;
            case InputType.TYPE_CLASS_DATETIME:
                //mCurKeyboard = mSymbolsKeyboard;
                break;

            case InputType.TYPE_CLASS_PHONE:
                //mCurKeyboard = mPhoneKeyboard;
                break;

            case InputType.TYPE_CLASS_TEXT:
                mPredictionOn = sharedPreferences.getBoolean("suggestion", true);
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    mPredictionOn = false;
                }

                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        || variation == InputType.TYPE_TEXT_VARIATION_URI
                        || variation == InputType.TYPE_TEXT_VARIATION_FILTER) {
                    mPredictionOn = false;
                }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }
                break;

            default:
        
        }
        if (mPredictionOn) db = new DatabaseManager(this);
        keyboard.setImeOptions(getResources(), attribute.imeOptions);

        mSound = sharedPreferences.getBoolean("sound", true);
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
        clearCandidateView();

        mComposing.setLength(0);
        updateCandidates();

        setCandidatesViewShown(false);
        if (db != null) db.close();
    }

    public void clearCandidateView() {
        if (list != null) list.clear();
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {

        int val= checkWhichGoToApply();
        if(val==5) {
            retriveBitmapAndSet();
        }else{
            callbackground();
        }
        forpreview = getSharedPreferences(PREVIEW_PREF_NAME, MODE_PRIVATE);
        int isval = forpreview.getInt(POSITION_AD_PREVIEW, 1);
        if (isval == 0) {
            //    kv.setPreviewEnabled(false);
        }
        if (isval == 1) {
            //  kv.setPreviewEnabled(true);
        }


        super.onStartInputView(info, restarting);
    }

    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
     
        String s = subtype.getLocale();
        switch (s) {
            case "ps_AF":
//                mActiveKeyboard = "ps_AF";
//                mCurKeyboard = mPashtoKeyboard;
//
                break;
            case "ps_latin_AF":
//                mActiveKeyboard = "ps_latin_AF";
//                mCurKeyboard = mPashtoLatinKeyboard;
//
                break;
            case "fa_AF":
//                mActiveKeyboard = "fa_AF";
//                mCurKeyboard = mFarsiKeyboard;
//
                break;
            default:
//                mActiveKeyboard = "en_US";
//                mCurKeyboard = mQwertyKeyboard;
//
        }
    }

    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<>();
            for (CompletionInfo ci : completions) {
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);
            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length() - 1);
            }
        }

        onKey(c, null);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0 && kv != null) {
                    if (obj.handleBack()) {
                        return true;
                    }
                }
                break;

            case KeyEvent.KEYCODE_DEL:
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                return false;

            default:
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE
                            && (event.getMetaState() & KeyEvent.META_ALT_ON) != 0) {
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A);
                            keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O);
                            keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            // And we consume this event.
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }

        return super.onKeyDown(keyCode, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {


        ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                keyboard.setShifted(caps);
                obj.invalidateAllKeys();
                break;
            case -16:
                keyboard = new Keyboard(this, R.xml.urdu);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);
                isflagforurdu = true;
                flagforenglish = false;
                sphenglish = false;
                sphhindu = false;
                sphurdu = true;
                break;

            default:

        }

        if (isWordSeparator(primaryCode)) {
            if (mComposing.length() > 0) {
                commitTyped(getCurrentInputConnection());
                Log.d("mytagfor", "world separator");
            }
            if (primaryCode == 32) {
                if (list != null) {
                    clearCandidateView();
                }

                try {
                    addUpdateWord();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            sendKey(primaryCode);
        } else if (primaryCode == android.inputmethodservice.Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == android.inputmethodservice.Keyboard.KEYCODE_SHIFT) {
            //handleShift();
        } else if (primaryCode == android.inputmethodservice.Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == -10000) {
            // Show Emoticons

        } else if (primaryCode == -10001) {
            mComposing.append("\u200C");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (primaryCode == -10002) {
            mComposing.append("ẋ");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (primaryCode == -10003) {
            mComposing.append("\u1E8A");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else if (primaryCode == 1567) {
            mComposing.append("\u061F");
            getCurrentInputConnection().setComposingText(mComposing, 1);
        } else {
            Log.d("mytagfor", "else handlecharater");
            if (primaryCode == -100002) {
                Log.d("mytagfor", "code execute");
                keyboard = new Keyboard(this, R.xml.hindi1);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);
                sphurdu = false;
                sphenglish = false;
                sphhindu = true;

            } else if (primaryCode == 0x2752) {
                keyboard = new Keyboard(this, R.xml.custom_qwerty);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);
                flagforenglish = true;
                isflagforurdu = false;
                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == -6) {
                keyboard = new Keyboard(this, R.xml.roman);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);
                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == -61) {

                keyboard = new Keyboard(this, R.xml.custom_qwerty);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);
                flagforenglish = true;
                isflagforurdu = false;

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == 0x2121) {

                keyboard = new Keyboard(this, R.xml.roman2);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == 0x221E) {
                keyboard = new Keyboard(this, R.xml.roman);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == -62) {
                keyboard = new Keyboard(this, R.xml.urdu_numeric);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);
                isflagforurdu = true;
                flagforenglish = false;


                sphurdu = true;
                sphenglish = false;
                sphhindu = false;

            } else if (primaryCode == -63) {

                keyboard = new Keyboard(this, R.xml.hindi2);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = false;
                sphhindu = true;

            } else if (primaryCode == -64) {

                keyboard = new Keyboard(this, R.xml.hindi3);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);


                sphurdu = false;
                sphenglish = false;
                sphhindu = true;

            } else if (primaryCode == -65) {

                keyboard = new Keyboard(this, R.xml.hindi1);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = false;
                sphhindu = true;

            } else if (primaryCode == -4) {

                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));

            } else if (primaryCode == 207) {
                keyboard = new Keyboard(this, R.xml.custom_qwerty);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);
                flagforenglish = true;
                isflagforurdu = false;

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            }
            else if (primaryCode == 0x0730) {

                keyboard = new Keyboard(this, R.xml.time_emoji);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == 0x0731) {

                keyboard = new Keyboard(this, R.xml.emojis);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == 0x0732) {

                keyboard = new Keyboard(this, R.xml.weather_emojis);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (primaryCode == 0x0733) {
                keyboard = new Keyboard(this, R.xml.heart_emojis);
                obj.setKeyboard(keyboard);
                obj.setOnKeyboardActionListener(this);

                sphurdu = false;
                sphenglish = true;
                sphhindu = false;

            } else if (flagforemoji) {
                Log.d("mytagfor", "default emojis");
                try {
                    ic.commitText(String.valueOf(Character.toChars(primaryCode)), 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                handleCharacter(primaryCode, keyCodes);
            }
        }

        if (mSound) playClick(primaryCode); 
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            updateCandidates();
        }
    }

   
    private boolean isAlphabet(int code) {
        return Character.isLetter(code);
    }

    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                    Log.d("mytagfor", "sendKey");
                }
                break;
        }
    }

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (am != null) {
            switch (keyCode) {
                case 32:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                    break;
                case android.inputmethodservice.Keyboard.KEYCODE_DONE:
                case 10:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                    break;
                case android.inputmethodservice.Keyboard.KEYCODE_DELETE:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                    break;
                default:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
            }
        }
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
    }

    private void updateCandidates() {
        if (!mCompletionOn && mPredictionOn) {
            if (mComposing.length() > 0) {
                SelectDataTask selectDataTask = new SelectDataTask();

                list = new ArrayList<>();
                list.add(mComposing.toString());

                selectDataTask.getSubtype(keyboard);
                selectDataTask.execute(mComposing.toString());
            } else {
                setSuggestions(null, false, false);
            }
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
                               boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
    }

    private void handleShift() {
        if (kv == null) {
            return;
        }

    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (obj.isShifted()) {
                primaryCode = Character.toUpperCase(primaryCode);
                Log.d("mytagfor", "isInputviewshow");
            }
        }
        if (isAlphabet(primaryCode) && mPredictionOn) {
            mComposing.append((char) primaryCode);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
            Log.d("mytagfor", "isAlphabet");
        } else {
            mComposing.append((char) primaryCode);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            Log.d("mytagfor", "else case");

        }

    }

    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        obj.closing();
    }

    private IBinder getToken() {
        final Dialog dialog = getWindow();
        if (dialog == null) {
            return null;
        }
        final Window window = dialog.getWindow();
        if (window == null) {
            return null;
        }
        return window.getAttributes().token;
    }

    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), true );
    }


    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char) code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0 && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
        } else if (mComposing.length() > 0) {

            mComposing.setLength(index);
            mComposing = new StringBuilder(list.get(index) + " ");
            commitTyped(getCurrentInputConnection());
        }
    }

    public void swipeRight() {
        if (mCompletionOn) {
            pickDefaultCandidate();
        }
    }

    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
        obj.setPreviewEnabled(true);
    }

    public void onRelease(int primaryCode) {
    }
                
    private class SelectDataTask extends AsyncTask<String, Void, ArrayList<String>> {

        private String subType;

        void getSubtype(Keyboard mCurKeyboard) {
            if (flagforenglish) {
                subType = "english";
            } else if (isflagforurdu) {
                subType = "pashto";
            }
        }

        @Override
        protected ArrayList<String> doInBackground(String... str) {
            list = db.getAllRow(str[0], subType);
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            list = result;
            setSuggestions(result, true, true);
        }
    }

    public void addUpdateWord() {

        if (!getLastWord().isEmpty()) {
            Integer freq = db.getWordFrequency(getLastWord(), mActiveKeyboard);
            if (freq > 0) {
                db.updateRecord(getLastWord(), freq, mActiveKeyboard);
            } else {
                db.insertNewRecord(getLastWord(), mActiveKeyboard);
            }
        }
    }

    public String getLastWord() {
        CharSequence inputChars = getCurrentInputConnection().getTextBeforeCursor(50, 0);
        String inputString = String.valueOf(inputChars);
        return inputString.substring(inputString.lastIndexOf(" ") + 1);
    }

    void speechToText() {
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.example.create_keyboard1");

        SpeechRecognizer recognizer = SpeechRecognizer
                .createSpeechRecognizer(this.getApplicationContext());
        RecognitionListener listener = new RecognitionListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults == null) {
                    System.out.println("No voice results");

                } else {
                    try {
                        System.out.println("Printing matches: ");
                        ic.commitText(voiceResults.get(0), 1);
                        Log.i("tag", voiceResults.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("Ready for speech");
                Log.i("tag", "Ready for speech");

            }

            @Override
            public void onError(int error) {
                System.err.println("Error listening for speech: " + error);
                Log.i("tag", "Error listening for speech: " + error);
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println("Speech starting");
                Log.i("tag", "Speech starting");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.i("tag", "on buffered received");

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onEndOfSpeech() {
                Log.i("tag", "onEndOfSpeech");
                mic.setImageResource(R.drawable.ic_baseline_mic_24);


            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.i("tag", "onEvent");

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.i("tag", "onPartialResults");

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.i("tag", "onRmsChanged");

            }
        };
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }

    void speechToTextMultipleHindi() {
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
        CharSequence language = "hi-IN";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS, language);


        SpeechRecognizer recognizer = SpeechRecognizer
                .createSpeechRecognizer(this.getApplicationContext());
        RecognitionListener listener = new RecognitionListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults == null) {
                    System.out.println("No voice results");
                } else {
                    try {
                        System.out.println("Printing matches: ");
                        ic.commitText(voiceResults.get(0), 1);
                        Log.i("tag", voiceResults.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("Ready for speech");
                Log.i("tag", "Ready for speech");

            }

            @Override
            public void onError(int error) {
                System.err.println("Error listening for speech: " + error);
                Log.i("tag", "Error listening for speech: " + error);
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println("Speech starting");
                Log.i("tag", "Speech starting");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.i("tag", "on buffered received");

            }

            @Override
            public void onEndOfSpeech() {
                Log.i("tag", "onEndOfSpeech");


            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.i("tag", "onEvent");

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.i("tag", "onPartialResults");

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.i("tag", "onRmsChanged");

            }
        };
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }

    void speechToTextMultipleUrdu() {
        Toast.makeText(this, "Listening...", Toast.LENGTH_SHORT).show();
        CharSequence language = "ur";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, language);
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, language);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, language);
        intent.putExtra(RecognizerIntent.EXTRA_RESULTS, language);


        SpeechRecognizer recognizer = SpeechRecognizer
                .createSpeechRecognizer(this.getApplicationContext());
        RecognitionListener listener = new RecognitionListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> voiceResults = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (voiceResults == null) {
                    System.out.println("No voice results");
                } else {
                    try {
                        System.out.println("Printing matches: ");
                        ic.commitText(voiceResults.get(0), 1);
                        Log.i("tag", voiceResults.get(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                System.out.println("Ready for speech");
                Log.i("tag", "Ready for speech");

            }

            @Override
            public void onError(int error) {
                System.err.println("Error listening for speech: " + error);
                Log.i("tag", "Error listening for speech: " + error);
            }

            @Override
            public void onBeginningOfSpeech() {
                System.out.println("Speech starting");
                Log.i("tag", "Speech starting");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                Log.i("tag", "on buffered received");

            }

            @Override
            public void onEndOfSpeech() {
                Log.i("tag", "onEndOfSpeech");


            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                Log.i("tag", "onEvent");

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.i("tag", "onPartialResults");

            }

            @Override
            public void onRmsChanged(float rmsdB) {
                Log.i("tag", "onRmsChanged");

            }
        };
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }




    public void callbackground() {
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String value = sharedPreferences.getString(GROUPSNAME_SHARED_PREF, "");
        int pos = sharedPreferences.getInt(POSITION_AD, 0);
        if (TextUtils.equals("gradient", value)) {

            if (GradientFrag.mygradient.size() > 0)
                obj.setBackgroundResource(GradientFrag.mygradient.get(pos).getTheme_image());
        } else if (TextUtils.equals("flag", value)) {

            if (FlagsFrag.myflag.size() > 0)
                obj.setBackgroundResource(FlagsFrag.myflag.get(pos).getTheme_image());
        } else if (TextUtils.equals("image", value)) {

            if (ImageFrag.myimg.size() > 0)
                obj.setBackgroundResource(ImageFrag.myimg.get(pos).getTheme_image());
        } else if (TextUtils.equals("sport", value)) {
            if (SportsFrag.mysport.size() > 0)
                obj.setBackgroundResource(SportsFrag.mysport.get(pos).getTheme_image());

        } else {

            obj.setBackgroundResource(R.drawable.gradient_0);
            Log.d("simpleime", "on stat default");

        }

    }


    public void initializeView() {

        hide = kv.findViewById(R.id.hidekeybaord);
        emojis = kv.findViewById(R.id.emojis);
        mic = kv.findViewById(R.id.mic);
        color = kv.findViewById(R.id.color);
        setting = kv.findViewById(R.id.setting);
    }

    public void micFunForAll() {

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            Log.i("tag", "permission granted");

                            if (sphurdu) {
                                speechToTextMultipleUrdu();
                                sphhindu = false;
                                sphenglish = false;

                            } else if (sphhindu) {
                                speechToTextMultipleHindi();
                                sphenglish = false;
                                sphurdu = false;
                            } else {
                                speechToText();
                                sphurdu = false;
                                sphhindu = false;
                            }
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Log.i("tag", "not permission granted");
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }


    public void retriveBitmapAndSet() {

        SharedPreferences mysharedPreferences = getSharedPreferences(CUSTOM_MODE, MODE_PRIVATE);
        String value = mysharedPreferences.getString(CUSTOM_KEY, "");
        if (value != "") {
            Bitmap bitmap = decodeBase64(value);
            Drawable d = new BitmapDrawable(getBaseContext().getResources(), bitmap);
            obj.setBackground(d);
        }
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public  int checkWhichGoToApply(){

        SharedPreferences prefs = getSharedPreferences("Mutual", MODE_PRIVATE);
        int idName = prefs.getInt("Mutual_no", 0); 
        return idName;
    }


}




