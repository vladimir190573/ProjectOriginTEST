package com.projectorigin;

import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

class Text {
    private final Activity act;
    String id;
    final String text;
    final Map<String, String> attrs;
    Text(Activity act, Map<String, String> attrs, String text){
        //this.text = text != null ? "\t" + text.replace("<br/>", "<br/>\t") : null;
        this.text = text != null ? text.replace("<br/>", "<br/>") : null;
        this.attrs = attrs;
        this.act = act;
    }
    @Override
    public String toString(){
        return text + attrs.toString();
    }

    public void setter(int id){
        TextView view = act.findViewById(id);
        if (text != null){
            view.setText(Html.fromHtml(text));
            if(attrs.containsKey("a")) {
                switch (attrs.get("a")) {
                    case "c":
                        view.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        break;
                    case "r":
                        view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                        break;
                    default:
                        view.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        break;
                }
            }
        }  else {
            view.setVisibility(View.GONE);
        }
    }
}
