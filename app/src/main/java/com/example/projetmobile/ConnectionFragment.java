package com.example.projetmobile;

import static android.content.ContentValues.TAG;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetmobile.Model.GameManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionFragment extends Fragment {
    public static boolean verification = false;

    private boolean affCreate = false;
    private long animation_create_account_duration = 500;

    private boolean et_mail_empty = false;
    private boolean et_pseudo_empty = false;
    private boolean et_psw_empty = false;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseDatabase database;
    private final static String USER = "user";
    private final static String TAG = "ConnectionFragment";
    private User user;

    public ConnectionFragment() {
        // Required empty public constructor
    }

    public static ConnectionFragment newInstance() {
        ConnectionFragment fragment = new ConnectionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        /** Initialize Firebase Auth **/
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance("https://mobile-a37ba-default-rtdb.europe-west1.firebasedatabase.app");
        mDatabase = database.getReference();

        /** ============ GET ALL VIEWs ============ **/
        Button connectButton = view.findViewById(R.id.connectButton);
        Button createAccoutButton = view.findViewById(R.id.createAccountButton);
        Button backButton = view.findViewById(R.id.backButton);

        TextView tv_pseudo = view.findViewById(R.id.textViewPseudo);
        tv_pseudo.setAlpha(0.0f);
        EditText et_pseudo = view.findViewById(R.id.editTextPseudo);
        et_pseudo.setAlpha(0.0f);

        EditText et_email = view.findViewById(R.id.editTextEmail);




        EditText et_psw = view.findViewById(R.id.editTextPassword);

        ViewGroup ll_form_container = view.findViewById(R.id.container_form);
        LinearLayout ll_questions_container = view.findViewById(R.id.container_questions);
        LinearLayout ll_options = view.findViewById(R.id.container_options);
        View v_bottom = view.findViewById(R.id.view_bottom);

        TextView tv_mdp_forget = view.findViewById(R.id.backupAccount);

        boolean land = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        /** ============ EDIT TEXT WATCHER ============ **/
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_mail_empty && et_email.getText().toString().equals("")) {
                    set_missing_editText(et_email, view.getContext());
                } else {
                    set_normal_editText(et_email, view.getContext());
                }
            }
        });
        et_pseudo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_pseudo_empty && et_pseudo.getText().toString().equals("")) {
                    set_missing_editText(et_pseudo, view.getContext());
                } else {
                    set_normal_editText(et_pseudo, view.getContext());
                }
            }
        });
        et_psw.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_psw_empty && et_psw.getText().toString().equals("")) {
                    set_missing_editText(et_psw, view.getContext());
                } else {
                    set_normal_editText(et_psw, view.getContext());
                }
            }
        });


        /** ============ BUTTON ONCLICK ============ **/
        connectButton.setOnClickListener(v -> {
            if (affCreate) {
                affCreate = false;
                /**Reset UI edit text missing statement**/
                set_normal_editText(et_email, view.getContext());
                set_normal_editText(et_pseudo, view.getContext());
                set_normal_editText(et_psw, view.getContext());
                et_mail_empty = false;
                et_pseudo_empty = false;
                et_psw_empty = false;

                /**Animation to make options appeared**/
                ll_options.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(animation_create_account_duration)
                        /*.setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);


                            }
                        });*/;

                /**Start the animation**/
                ValueAnimator slideAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(animation_create_account_duration);

                LinearLayout.LayoutParams params_form_container = (LinearLayout.LayoutParams) ll_form_container.getLayoutParams();
                float weight_start_form_container = params_form_container.weight;
                float weight_end_form_container = 6.0f;

                float weightSum_start_question_container = ll_questions_container.getWeightSum();
                float weightSum_end_question_container = 10.0f;

                LinearLayout.LayoutParams params_view_bottom = (LinearLayout.LayoutParams) v_bottom.getLayoutParams();
                float weight_start_bottom_view = params_view_bottom.weight;
                float weight_end_bottom_view = 1.0f;

                //For button margin
                float margin_min = view.getResources().getDimension(R.dimen.common_margin);
                float margin_max = view.getResources().getDimension(R.dimen.big_margin);

                float textSize_min = view.getResources().getDimension(R.dimen.txt_size_small_sub_btn_menu);
                float textSize_max = view.getResources().getDimension(R.dimen.txt_size_sub_btn_menu);

                LinearLayout.LayoutParams ll_opt_param = (LinearLayout.LayoutParams) ll_options.getLayoutParams();
                float w_option_start = ll_opt_param.weight;
                float w_option_end = 2.0f;

                //Animation function
                slideAnimator.addUpdateListener(a -> {
                    float value = (Float) a.getAnimatedValue();

                    //Layout general of all the form
                    LinearLayout.LayoutParams f_params = (LinearLayout.LayoutParams) ll_form_container.getLayoutParams();
                    f_params.weight = weight_start_form_container + ((weight_end_form_container - weight_start_form_container) * value);
                    ll_form_container.setLayoutParams(f_params);

                    if (!land) {
                        //weight sum question container
                        ll_questions_container.setWeightSum(weightSum_start_question_container + (weightSum_end_question_container - weightSum_start_question_container) * value);

                        //Bottom
                        LinearLayout.LayoutParams params_bt = (LinearLayout.LayoutParams) v_bottom.getLayoutParams();
                        params_bt.weight = weight_start_bottom_view + ((weight_end_bottom_view - weight_start_bottom_view) * value);
                        v_bottom.setLayoutParams(params_bt);
                    }


                    //BUTTONS maj size
                    LinearLayout.LayoutParams btn_min_params = (LinearLayout.LayoutParams) createAccoutButton.getLayoutParams();
                    float m_min_maj = btn_min_params.leftMargin + (margin_max - btn_min_params.leftMargin) * value;
                    btn_min_params.setMarginStart((int) m_min_maj);
                    btn_min_params.setMarginEnd((int) m_min_maj);
                    createAccoutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, createAccoutButton.getTextSize() + (textSize_min - createAccoutButton.getTextSize()) * value);

                    LinearLayout.LayoutParams btn_max_params = (LinearLayout.LayoutParams) connectButton.getLayoutParams();
                    float m_max_maj = btn_max_params.leftMargin + (margin_min - btn_max_params.leftMargin) * value;
                    btn_max_params.setMarginStart((int) m_max_maj);
                    btn_max_params.setMarginEnd((int) m_max_maj);
                    connectButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, connectButton.getTextSize() + (textSize_max - connectButton.getTextSize()) * value);

                    LinearLayout.LayoutParams opt_param = (LinearLayout.LayoutParams) ll_options.getLayoutParams();
                    opt_param.weight = w_option_start + ((w_option_end - w_option_start) * value);
                    ll_options.setLayoutParams(opt_param);

                    tv_pseudo.setAlpha(1.0f - value);
                    et_pseudo.setAlpha(1.0f - value);

                    v_bottom.requestLayout();
                    ll_questions_container.requestLayout();
                    ll_form_container.requestLayout();
                    ll_options.requestLayout();
                });

                //Launch the animation
                AnimatorSet animationSet = new AnimatorSet();
                animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animationSet.play(slideAnimator);
                animationSet.start();
            } else {
                //Check if all the datas are mentionned
                if (!et_email.getText().toString().isEmpty() && !et_psw.getText().toString().isEmpty()) {
                    connection_next(et_email.getText().toString(), et_psw.getText().toString());
                } else {
                    Toast.makeText(view.getContext(), getString(R.string.missing_informations), Toast.LENGTH_SHORT).show();

                    if (et_email.getText().toString().equals("")) {
                        et_mail_empty = true;
                        set_missing_editText(et_email, v.getContext());
                    } else {
                        et_mail_empty = false;
                    }

                    if (et_psw.getText().toString().equals("")) {
                        et_psw_empty = true;
                        set_missing_editText(et_psw, v.getContext());
                    } else {
                        et_psw_empty = false;
                    }
                }
            }
        });
        createAccoutButton.setOnClickListener(v -> {
            if (!affCreate) {
                affCreate = true;
                /**Reset UI edit text missing statement**/
                set_normal_editText(et_email, view.getContext());
                set_normal_editText(et_pseudo, view.getContext());
                set_normal_editText(et_psw, view.getContext());
                et_mail_empty = false;
                et_pseudo_empty = false;
                et_psw_empty = false;

                /**Animation to make options disappear**/
                ll_options.animate()
                        .translationY(ll_options.getHeight())
                        .alpha(0.0f)
                        .setDuration(animation_create_account_duration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                //ll_options.setVisibility(View.GONE);
                            }
                        });

                tv_pseudo.animate()
                        .alpha(1.0f)
                        .setDuration(animation_create_account_duration);

                et_pseudo.animate()
                        .alpha(1.0f)
                        .setDuration(animation_create_account_duration);

                /**Start the animation**/
                ValueAnimator slideAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(animation_create_account_duration);

                LinearLayout.LayoutParams params_form_container = (LinearLayout.LayoutParams) ll_form_container.getLayoutParams();
                float weight_start_form_container = params_form_container.weight;
                float weight_end_form_container = 7.0f;

                float weightSum_start_question_container = ll_questions_container.getWeightSum();
                float weightSum_end_question_container = 6.0f;

                LinearLayout.LayoutParams params_view_bottom = (LinearLayout.LayoutParams) v_bottom.getLayoutParams();
                float weight_start_bottom_view = params_view_bottom.weight;
                float weight_end_bottom_view = 0.5f;

                //For button margin
                float margin_min = view.getResources().getDimension(R.dimen.common_margin);
                float margin_max = view.getResources().getDimension(R.dimen.big_margin);

                float textSize_min = view.getResources().getDimension(R.dimen.txt_size_small_sub_btn_menu);
                float textSize_max = view.getResources().getDimension(R.dimen.txt_size_sub_btn_menu);

                LinearLayout.LayoutParams ll_opt_param = (LinearLayout.LayoutParams) ll_options.getLayoutParams();
                float w_option_start = ll_opt_param.weight;
                float w_option_end = 4.0f;

                //Animation function
                slideAnimator.addUpdateListener(a -> {
                    float value = (Float) a.getAnimatedValue();

                    //Layout general of all the form
                    LinearLayout.LayoutParams f_params = (LinearLayout.LayoutParams) ll_form_container.getLayoutParams();
                    f_params.weight = weight_start_form_container + ((weight_end_form_container - weight_start_form_container) * value);
                    ll_form_container.setLayoutParams(f_params);
                    if (!land) {

                        //weight sum question container
                        ll_questions_container.setWeightSum(weightSum_start_question_container + (weightSum_end_question_container - weightSum_start_question_container) * value);

                        //Bottom
                        LinearLayout.LayoutParams params_bt = (LinearLayout.LayoutParams) v_bottom.getLayoutParams();
                        params_bt.weight = weight_start_bottom_view + ((weight_end_bottom_view - weight_start_bottom_view) * value);
                        v_bottom.setLayoutParams(params_bt);
                    }

                    //BUTTONS maj size
                    LinearLayout.LayoutParams btn_min_params = (LinearLayout.LayoutParams) connectButton.getLayoutParams();
                    float m_min_maj = btn_min_params.leftMargin + (margin_max - btn_min_params.leftMargin) * value;
                    btn_min_params.setMarginStart((int) m_min_maj);
                    btn_min_params.setMarginEnd((int) m_min_maj);
                    connectButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, connectButton.getTextSize() + (textSize_min - connectButton.getTextSize()) * value);

                    LinearLayout.LayoutParams btn_max_params = (LinearLayout.LayoutParams) createAccoutButton.getLayoutParams();
                    float m_max_maj = btn_max_params.leftMargin + (margin_min - btn_max_params.leftMargin) * value;
                    btn_max_params.setMarginStart((int) m_max_maj);
                    btn_max_params.setMarginEnd((int) m_max_maj);
                    createAccoutButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, createAccoutButton.getTextSize() + (textSize_max - createAccoutButton.getTextSize()) * value);

                    LinearLayout.LayoutParams opt_param = (LinearLayout.LayoutParams) ll_options.getLayoutParams();
                    opt_param.weight = w_option_start + ((w_option_end - w_option_start) * value);
                    ll_options.setLayoutParams(opt_param);

                    v_bottom.requestLayout();
                    ll_questions_container.requestLayout();
                    ll_form_container.requestLayout();
                    ll_options.requestLayout();
                });

                //Launch the animation
                AnimatorSet animationSet = new AnimatorSet();
                animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
                animationSet.play(slideAnimator);
                animationSet.start();
            } else {
                if (!et_pseudo.getText().toString().isEmpty() && !et_psw.getText().toString().isEmpty() && !et_email.getText().toString().isEmpty()) {
                    create_account_next(et_email.getText().toString(), et_pseudo.getText().toString(), et_psw.getText().toString(), view.getContext());
                } else {
                    Toast.makeText(view.getContext(), getString(R.string.missing_informations), Toast.LENGTH_SHORT).show();
                    if (et_pseudo.getText().toString().equals("")) {
                        et_pseudo_empty = true;
                        set_missing_editText(et_pseudo, v.getContext());
                    } else {
                        et_pseudo_empty = false;
                    }
                    if (et_psw.getText().toString().equals("")) {
                        et_psw_empty = true;
                        set_missing_editText(et_psw, v.getContext());
                    } else {
                        et_psw_empty = false;
                    }
                    if (et_email.getText().toString().equals("")) {
                        et_mail_empty = true;
                        set_missing_editText(et_email, v.getContext());
                    } else {
                        et_mail_empty = false;
                    }
                }
            }
        });

        //MDP FORGET
        tv_mdp_forget.setOnClickListener(v -> {
            String email = et_email.getText().toString();

            if (!email_checking(email)) {
                Toast.makeText(view.getContext(), getString(R.string.unaivailable_email), Toast.LENGTH_SHORT).show();
                et_mail_empty = true;
                set_missing_editText(et_email, view.getContext());
            } else {
                mdp_forget_next(view.getContext(), email);
            }

        });

        backButton.setOnClickListener(v -> {
            getActivity().finish();
        });
        return view;
    }

    /**
     * UI changing functions on missing informations
     **/
    private void set_missing_editText(EditText et, Context ct) {
        et.setBackground(ContextCompat.getDrawable(ct, R.drawable.menu_edit_text_missing));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            InsetDrawable insetDrawable = null;
            insetDrawable = (InsetDrawable) et.getTextCursorDrawable();
            insetDrawable.setColorFilter(ContextCompat.getColor(ct, R.color.red), PorterDuff.Mode.SRC_ATOP);
            et.setTextCursorDrawable(insetDrawable);
        }
    }

    private void set_normal_editText(EditText et, Context ct) {
        et.setBackground(ContextCompat.getDrawable(ct, R.drawable.menu_edit_text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            InsetDrawable insetDrawable = null;
            insetDrawable = (InsetDrawable) et.getTextCursorDrawable();
            insetDrawable.setColorFilter(GameManager.getAttributeColor(ct, R.attr.colorSecondary), PorterDuff.Mode.SRC_ATOP);
            et.setTextCursorDrawable(insetDrawable);
        }
    }

    /**
     * Next mecanism after verifications
     **/
    private void connection_next(String email, String psw) {
        //TODO upload and save data

        mAuth.signInWithEmailAndPassword(email, psw).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "loginUser:success");
                            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getActivity(), R.string.wrong_pasword, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void create_account_next(String email, String pseudo, String psw, Context ct) {
        //TODO upload and save data
        mAuth.createUserWithEmailAndPassword(email, psw).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user, email, pseudo, psw);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    String message = getString(R.string.authentication_failed);
                    if (psw.length() < 6) {
                        message += getString(R.string.pasword_too_short);
                    }
                    else {
                        message += getString(R.string.unaivailable_email);
                    }
                    Toast.makeText(ct, message,
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mdp_forget_next(Context c, String email) {
        //TODO générer lien et envoyer mail
        Toast.makeText(c, "Not yet implemented", Toast.LENGTH_SHORT).show();

        //Toast.makeText(c, getString(R.string.forgot_psw_msgtoast), Toast.LENGTH_SHORT).show();
    }


    /**
     * EMAIL VERIFICATION parsing
     **/
    private static int email_nb_carac_max = 64;
    private static int email_nb_carac_min = 1;
    private static int email_nb_carac_max_domain = 64;
    private static int email_nb_carac_min_domain = 1;
    private static boolean email_maj_carac = false;
    private static boolean email_digit_carac = false;
    private static boolean email_dashes_carac = false;
    private static boolean email_maj_carac_domain_name = false;

    private boolean email_checking(String email) {
        /*
        String elem_post = "[" + ((email_maj_carac) ? "A-Za-z" : "a-z") + ((email_digit_carac) ? "0-9" : "") + ((email_dashes_carac) ? "_-" : "") + "]";
        String elem_pre = "[" + ((email_maj_carac) ? "A-Za-z" : "a-z") + ((email_digit_carac) ? "0-9" : "") + ((email_dashes_carac) ? "-" : "") + "]";
        String domain_name = "[" + ((email_maj_carac_domain_name) ? "A-Za-z" : "a-z") + "]";

        String EMAIL_PATTERN =
                "^(?=.{"
                        + email_nb_carac_min
                        + ","
                        + ((email_nb_carac_max < 0) ? "" : email_nb_carac_max + "")
                        + "}@)"
                        + elem_post
                        + "+(\\."
                        + elem_post
                        + "+)*@"
                        + "[^-]"
                        + elem_pre
                        + "+(\\."
                        + elem_pre
                        + "+)*(\\."
                        + domain_name
                        + "{"
                        + email_nb_carac_min_domain
                        + ","
                        + email_nb_carac_max_domain//((email_nb_carac_max_domain < 0) ? "" : email_maj_carac_domain_name + "")
                        + "})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
        */
        String[] name_adress = email.split("@");
        if (name_adress.length != 2) {
            return false;
        }
        if (name_adress[0].length() < 1) {
            return false;
        }
        if (name_adress[1].length() < 2) {
            return false;
        }
        String[] domain_ext = name_adress[1].split("\\.");
        if (domain_ext.length < 2) {
            return false;
        }
        for (String s : domain_ext) {
            if (s.length() < 1) {
                return false;
            }
        }
        return true;
    }

    private void updateUI(FirebaseUser currUser, String email, String pseudo, String psw) {
        user = new User(pseudo, psw, email);
        String keyId = currUser.getUid();
        mDatabase.child("users").child(keyId).setValue(user);
        AuthCredential credential = EmailAuthProvider.getCredential(email, psw);
        // put the credential into a bundle

        // put the bundle into an intent
        Intent intent = new Intent(getActivity(), MainMenuActivity.class);
        startActivity(intent);
    }

}