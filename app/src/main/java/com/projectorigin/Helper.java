package com.projectorigin;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class Helper {
    private final Activity act;
    Helper (Activity act){
        this.act = act;
    }
    Random random = new Random();

    public Drawable getDrawable(String title){
        if (title == null) {
            return act.getResources().getDrawable(R.drawable.black);
        }
        switch (title){
            case "black": return act.getResources().getDrawable(R.drawable.black);
            case "white": return act.getResources().getDrawable(R.drawable.white);
            case "launch": return act.getResources().getDrawable(R.drawable.launch_screen);
            default: return act.getResources().getDrawable(R.drawable.black);
        }
    }

    /*public static final Drawable getDrawable(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 21) {
            return context.getDrawable(id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }*/

    public int getImg(String title){
        switch (title){
            case "menu.gif": return R.mipmap.menu;
            case "Tony.jpg": return R.mipmap.tony;
            case "Archer.jpg": return R.mipmap.archer;
            case "Sam.jpg": return R.mipmap.sam;
            case "Shon.jpg": return R.mipmap.shon;
            case "Vins.jpg": return R.mipmap.vins;
            case "Dylan.jpg": return R.mipmap.dylan;
            case "img1": return R.mipmap.img1;
            case "img2": return R.mipmap.img2;
            case "img3": return R.mipmap.img3;
            case "img4": return R.mipmap.img4;
            case "img5": return R.mipmap.img5;
            case "img6": return R.mipmap.img6;
            case "img7": return R.mipmap.img7;
            case "img8": return R.mipmap.img8;
            //case "video": return R.mipmap.;
            case "q2_p3": return R.mipmap.q2_p3;
            case "q2_p4": return R.mipmap.q2_p4;
            default: return R.mipmap.img1;
        }
    }
    private int getXmlByName(String title){
        switch (title){
            case "ce": return R.xml.ce;
            case "menu": return R.xml.menu;
            case "q1": return R.xml.q1;
            case "q2": return R.xml.q2;
            case "archer": return R.xml.archer;
            default: return 0;
        }
    }
    private Map<String, String> getAttrs(XmlPullParser parser) {
        Map<String, String> attrs = new HashMap<>();
        for (int i = 0; i < parser.getAttributeCount(); i++) {
            attrs.put(parser.getAttributeName(i), parser.getAttributeValue(i));
        }
        //Log.d("debug", parser.getName() + "~" + parser.getAttributeCount() + "~" + attrs.toString());
        return attrs;
    }

    public void sleep(int mills){
        try{ TimeUnit.MILLISECONDS.sleep(mills); } catch (InterruptedException e){
            e.printStackTrace();
            Log.d("debug", "InterruptedException on sleep.");
        }
    }
    /* HELP ME */
    public Map<String, State> getStates(Activity act, String title){
        final Helper hlp = new Helper(act);
        int xmlId = hlp.getXmlByName(title);
        Map<String, State> states = new HashMap<>();
        try {
            XmlPullParser parser = act.getResources().getXml(xmlId);
            //DOM2XmlPullBuilder
            //Log.d("debug", parser.);
            int eventType = parser.getEventType();
            State state = new State(act);
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG){
                    if (parser.getName() != null){
                        switch (parser.getName()){
                            case "state":
                                state = new State(act, hlp.getAttrs(parser));
                                break;
                            case "image":
                                state.image = parser.nextText();
                                break;
                            case "text":
                                state.text = new Text(act, hlp.getAttrs(parser), parser.nextText());
                                break;
                            case "button":
                                Text button = new Text(act, hlp.getAttrs(parser), parser.nextText());
                                if (button.text != null && button.text.length() > 1) {
                                    state.buttons.add(button);
                                }
                                break;
                        }
                    }
                } else if (eventType == XmlPullParser.END_TAG && parser.getName().equals("state")){
                    state.id = state.attrs.get("id");
                    Log.d("debug", "state prepared:" + state.toString());
                    states.put(state.id, state);
                }
                eventType = parser.next();
            }
            Log.d("debug", "states prepared");
        } catch(Resources.NotFoundException x){
            Log.d("error", "Файл " + title + " не найден.");
        } catch(Throwable t) {
            Toast.makeText(act, "Ошибка при загрузке XML-документа: " + title, Toast.LENGTH_LONG).show();
            Log.d("error", "Ошибка при загрузке XML-документа: " + title + "\n" + t.toString());
        }
        return states;
    }

    public String getValue(String val){
        if (val != null && !val.isEmpty()){
            if (val.contains(";")){
                val = val.split(";")[random.nextInt(val.split(";").length)];
            }
            if (val.contains("_")){
                int from = Integer.valueOf(val.split("_")[0]);
                int to = Integer.valueOf(val.split("_")[1]);
                val = String.valueOf( from + random.nextInt(to - from) );
            }
        }
        return val;
    }

}
