package com.intouchapp.intouch.Register.Introduction;


import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rongi.rotate_layout.layout.RotateLayout;
import com.intouchapp.intouch.R;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapOrientedIntroFragment extends Fragment {

    private TextView mPunchLine;

    public MapOrientedIntroFragment() {
        // Required empty public constructor
    }


    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_oriented_intro, container, false);

        mPunchLine = (TextView) v.findViewById(R.id.tv_punchline);


        //setting textview in one line
        settingTextView();

        return v;
    }


    //--------------------------------------setting textview in one line ----------------------------------------------------------------
    private void settingTextView(){
        SpannableString span1 = new SpannableString(Html.fromHtml("<font color=" + "#000000 " + "face=dosis_semibold "  + ">" + getString(R.string.in_touch) + "</font>"));
        span1.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen._16ssp)), 0, span1.length(), SPAN_INCLUSIVE_INCLUSIVE);
        CharSequence finalText = TextUtils.concat(getString(R.string.keep_you)," ",span1," ",getString(R.string.with_your_knowers));
        mPunchLine.setText(finalText);
    }

}
