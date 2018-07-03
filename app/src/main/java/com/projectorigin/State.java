package com.projectorigin;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class State {
    private final Activity act;
    String id;
    final Map<String, String> attrs;
    String image;
    Text text;
    final List<Text> buttons = new ArrayList<>();
    State(Activity act){
        this.act = act;
        this.attrs = new HashMap<>();
        this.text = new Text(act, new HashMap<String, String>(), null);
    }
    State(Activity act, Map<String, String> attrs){
        this.act = act;
        this.attrs = attrs;
        this.text = new Text(act, new HashMap<String, String>(), null);
    }
    @Override
    public String toString(){
        return id + "(" + (attrs != null ? attrs.toString() : "null") + ")" +
                " t:" + (text != null ? text.toString() : "null") +
                " i:" + (image != null ? image : "null") +
                " b:" + buttons.toString();
    }

    public void update(){
        final Helper hlp = new Helper(act);
        act.setContentView(R.layout.state);
        View cl = act.findViewById(R.id.cl);
        Log.d("debug", "set state " + id);

        cl.setBackground(hlp.getDrawable(attrs.get("background")));
        ImageView img = act.findViewById(R.id.img);
        if (image != null && !image.isEmpty())    {
            img.setImageResource(hlp.getImg(image));
        } else {
            img.setVisibility(View.GONE);
    }

        text.setter(R.id.text);


        TextView footer_text = act.findViewById(R.id.footer_text);
        footer_text.setVisibility(View.GONE);
        LinearLayout footer = (LinearLayout)footer_text.getParent();
        for (Text txt : buttons){
            Button b = new Button(act);
            b.setText(txt.text);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity)act).onButtonClick(view);
                }
            });
            footer.addView(b);
        }

    }
}
