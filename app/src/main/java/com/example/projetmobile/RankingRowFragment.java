package com.example.projetmobile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RankingRowFragment extends Fragment {

    private static final String RANKING_PARAM = "RANKING_PARAM";
    private static final String PSEUDO_PARAM = "PSEUDO_PARAM";

    private String ranking;
    private String pseudo;

    public RankingRowFragment() {
        // Required empty public constructor
    }


    public static RankingRowFragment newInstance(String ranking, String pseudo) {
        RankingRowFragment fragment = new RankingRowFragment();
        Bundle args = new Bundle();
        args.putString(RANKING_PARAM, ranking);
        args.putString(PSEUDO_PARAM, pseudo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ranking = getArguments().getString(RANKING_PARAM);
            pseudo = getArguments().getString(PSEUDO_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ranking_row, container, false);

        TextView rankingTextView = view.findViewById(R.id.rankingTextView);
        rankingTextView.setText(ranking);

        TextView pseudoTextView = view.findViewById(R.id.pseudoTextView);
        pseudoTextView.setText(pseudo);

        return view;
    }
}