package fr.neamar.kiss.result;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.neamar.kiss.R;
import fr.neamar.kiss.pojo.MathPojo;

public class MathResult extends Result{

    private final MathPojo mathPojo;

    public MathResult(MathPojo mathPojo) {
        super();
        this.pojo = this.mathPojo = mathPojo;

    }

    @Override
    public View display(Context context, int position, View v) {
        if (v == null)
            v = inflateFromId(context, R.layout.item_math);
        TextView appName = (TextView) v.findViewById(R.id.item_math_text);
        String text = context.getString(R.string.ui_item_search);
        appName.setText(enrichText(String.format(text, this.pojo.name, "{" + mathPojo.result + "}")));

        ((ImageView) v.findViewById(R.id.item_math_icon)).setColorFilter(getThemeFillColor(context), PorterDuff.Mode.SRC_IN);
        return v;
    }

    /**
     * TODO open the calculator on tap
     */
    @Override
    protected void doLaunch(Context context, View v) {
        return;
    }
}
