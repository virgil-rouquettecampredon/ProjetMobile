package com.example.projetmobile;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatDrawableManager;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryRowFragment extends Fragment {

    private static final String ISAWIN_PARAM = "ISAWIN_PARAM";
    private static final String ELOCHANGEAMOUT_PARAM = "ELOCHANGEAMOUT_PARAM";
    private static final String ADVERSARYPSEUDO_PARAM = "ADVERSARYPSEUDO_PARAM";
    private static final String GAMETURNCOUNT_PARAM = "GAMETURNCOUNT_PARAM";
    private static final String WINTYPE_PARAM = "WINTYPE_PARAM";
    private static final String ISOPEN_PARAM = "ISOPEN_PARAM";

    private boolean isAWin;
    private String eloChangeAmount;
    private String adversaryPseudo;
    private String gameTurnCount;
    private String winType;
    private boolean isOpen;

    public HistoryRowFragment() {
        // Required empty public constructor
    }

    public static HistoryRowFragment newInstance(HistoryDialogActivity.HistoryData data) {
        return newInstance(data.isAWin, data.eloChangeAmount, data.adversaryPseudo, data.gameTurnCount, data.winType);
    }

    public static HistoryRowFragment newInstance(
              boolean isAWin,
             String eloChangeAmount,
             String adversaryPseudo,
             String gameTurnCount,
             String winType
    ) {
        HistoryRowFragment fragment = new HistoryRowFragment();
        Bundle args = new Bundle();
        args.putBoolean(ISAWIN_PARAM, isAWin);
        args.putString(ELOCHANGEAMOUT_PARAM, eloChangeAmount);
        args.putString(ADVERSARYPSEUDO_PARAM, adversaryPseudo);
        args.putString(GAMETURNCOUNT_PARAM, gameTurnCount);
        args.putString(WINTYPE_PARAM, winType);
        args.putBoolean(ISOPEN_PARAM, false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAWin           = getArguments().getBoolean(ISAWIN_PARAM);
            eloChangeAmount  = getArguments().getString(ELOCHANGEAMOUT_PARAM);
            adversaryPseudo  = getArguments().getString(ADVERSARYPSEUDO_PARAM);
            gameTurnCount    = getArguments().getString(GAMETURNCOUNT_PARAM);
            winType          = getArguments().getString(WINTYPE_PARAM);
            isOpen           = getArguments().getBoolean(ISOPEN_PARAM);
        }
    }

    private void addTextView(String text, LinearLayout parent) {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(12);
        textView.setText(text);
        parent.addView(textView);
    }

    private void open(View arrowImageView, LinearLayout toOpenLinearLayout) {
        arrowImageView.setRotation(90);

        addTextView(getString(R.string.turn_number) + " : " + gameTurnCount, toOpenLinearLayout);
        if (isAWin) {
            addTextView(getString(R.string.win_type) + " : " + winType, toOpenLinearLayout);
        }
        else {
            addTextView(getString(R.string.lose_type) + " : " + winType, toOpenLinearLayout);
        }
        isOpen = true;
    }

    private void close(View arrowImageView, LinearLayout toOpenLinearLayout) {
        arrowImageView.setRotation(0);
        toOpenLinearLayout.removeAllViews();
        isOpen = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_row, container, false);

        String eloChangeAmountText;

        ImageView winLoseImageView = view.findViewById(R.id.winLoseImageView);
        if (isAWin) {
            winLoseImageView.setImageResource(R.drawable.star);
            winLoseImageView.setPadding(0, 0, 0, 0);
            eloChangeAmountText = "+";
        }
        else {
            winLoseImageView.setImageResource(R.drawable.cross);
            int crossPading = (int) getResources().getDimension(R.dimen.cross_padding);
            winLoseImageView.setPadding(crossPading, crossPading, crossPading, crossPading);
            eloChangeAmountText = "-";
        }
        eloChangeAmountText += eloChangeAmount;

        TextView eloChangeAmountTextView = view.findViewById(R.id.eloChangeAmountTextView);
        eloChangeAmountTextView.setText(eloChangeAmountText);

        TextView adversaryPseudoTextView = view.findViewById(R.id.adversaryPseudoTextView);
        adversaryPseudoTextView.setText(adversaryPseudo);

        LinearLayout toOpenLinearLayout = view.findViewById(R.id.toOpenLinearLayout);
        View arrowImageView = view.findViewById(R.id.arrowImageView);
        LinearLayout topLinearLayout = view.findViewById(R.id.topLinearLayout);
        topLinearLayout.setOnClickListener(v -> {
            if (isOpen) {
                close(arrowImageView, toOpenLinearLayout);
            }
            else {
                open(arrowImageView, toOpenLinearLayout);
            }
        });

        if (isOpen) {
            open(arrowImageView, toOpenLinearLayout);
        }
        else {
            close(arrowImageView, toOpenLinearLayout);
        }

        return view;
    }
}