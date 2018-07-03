package com.projectorigin;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private State cState;
    private String quest;
    private Map<String, State> states;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<State> history = new ArrayList<>();
    private final Helper hlp = new Helper(this);
    private int position = 0;
    private SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("debug", "start");
        hlp.sleep(1000);
        //getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(getSupportActionBar() != null)   getSupportActionBar().hide();

        states = hlp.getStates(this, "ce");
        cState = states.get("1");
        history.add(cState);
        setState(cState);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void setState(State state){
        if (state == null) {
            states = hlp.getStates(this, "ce");
            state = states.get("1");
        }
        if (state.id.equals("exit")){
            exit();
            return;
        }
        if (state.id.equals("menu"))
            quest = null;
        cState = state;
        saveState(state.id);
        cState.update();
    }

    public void onButtonClick(View view){
        if (view instanceof Button){
            for (Text butt : cState.buttons){
                if (butt.text.contentEquals(((Button) view).getText())){
                    position++;
                    if (butt.attrs.get("inputType") != null && !butt.attrs.get("inputType").isEmpty()){
                        input(butt.attrs.get("header"), butt.attrs.get("inputType"), butt.attrs.get("next"), butt.attrs.get("error"), butt.attrs.get("val"));
                        return;
                    }
                    String newStateId = hlp.getValue(butt.attrs.get("next"));
                    if (butt.id != null && butt.id.equals("reset")){
                        askAdvice(R.string.ask_reset, newStateId);
                        return;
                    }
                    String prevStateId = null;
                    if (newStateId != null && newStateId.contains("-")){
                        quest = newStateId.split("-")[0];
                        states.putAll(hlp.getStates(this, quest));
                        newStateId = newStateId.split("-")[1];
                        prevStateId = loadStateId(quest);
                    }
                    State st = prevStateId != null && !prevStateId.isEmpty() && !prevStateId.equals(newStateId) ? resume(newStateId, prevStateId) : states.get(newStateId);
                    if (st != null) {
                        history.add(st);
                        setState(st);
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), "TODO: Ошибка, не описан state " + butt.attrs.get("next"), Toast.LENGTH_SHORT);
                        Log.d("debug", "Ошибка, не описан state " + butt.attrs.get("next"));
                        toast.show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (cState.id.equals("menu"))
            exit();
        else
            askAdvice(R.string.ad_menu_title, "menu");
        /*
        if (cState.id.equals("menu")){
            setState(states.get("exit"));
        } else {
            history.remove(position);
            position--;
            setState(history.get(position));
        }
        */
    }

    private void exit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builder.setTitle(getText(R.string.ad_exit_title));
        builder.setCancelable(false);
        builder.setNegativeButton(getText(R.string.ad_exit_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(getText(R.string.ad_exit_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Log.d("debug", "exit");
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void askAdvice(int textId, final String nextState){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builder.setTitle(getText(textId));
        builder.setCancelable(false);
        builder.setNegativeButton(getText(R.string.ad_exit_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.setPositiveButton(getText(R.string.ad_exit_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        State state = states.get(nextState);
                        Log.d("debug", "go to " + (state != null ? state.id : "null"));
                        if (nextState.equals("menu"))
                            quest = null;
                        setState(state);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void input(String header, String format, final String nextStateYes, final String nextStateNo, final String val){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        builder.setTitle(header);
        final EditText edit = new EditText(this);
        switch (format) {
            case "number":  edit.setInputType(InputType.TYPE_CLASS_NUMBER); break;
            case "string":  edit.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS); break;
        }
        edit.setGravity(Gravity.CENTER_HORIZONTAL);
        edit.setAllCaps(true);
        builder.setView(edit);
        builder.setCancelable(false);
        builder.setPositiveButton(getText(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (val != null && edit.getText() != null && !edit.getText().toString().isEmpty()) {
                            dialog.cancel();
                            String nextStateId = val.toUpperCase().equals(edit.getText().toString().toUpperCase()) ? nextStateYes : nextStateNo;
                            State state = states.get(hlp.getValue(nextStateId));
                            Log.d("debug", "go to " + state.id);
                            setState(state);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private State resume(String newStateId, String prevStateId){
        Log.d("debug", "prevStateId: " + prevStateId);
        State state = new State(this);
        state.attrs.put("bg", "launch");
        state.id = "ask";
        Text b1 = new Text(this, new HashMap<String, String>(), getText(R.string.ask_continue).toString());
        Text b2 = new Text(this, new HashMap<String, String>(), getText(R.string.ask_start).toString());
        Text b3 = new Text(this, new HashMap<String, String>(), getText(R.string.ask_menu).toString());
        b1.attrs.put("next", prevStateId);
        b2.attrs.put("next", newStateId);   b2.id = "reset";
        b3.attrs.put("next","menu");
        state.buttons.add(b1);
        state.buttons.add(b2);
        state.buttons.add(b3);
        return state;
    }

    private void saveState(String stateId) {
        Log.d("debug", "save. quest: " + quest + "; stateId: " + stateId);
        if (quest != null && states.containsKey(stateId)) {
            sPref = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putString(quest, stateId);
            ed.apply();
            Log.d("debug", "stored...");
        }
    }

    private String loadStateId(String qStore) {
        sPref = getPreferences(MODE_PRIVATE);
        String val = sPref.getString(qStore, "");
        Log.d("debug", "load. qStore: " + quest + "; val: " + val);
        return val;
    }

}