package com.example.projetmobile;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SelectorDialogActivity extends AppCompatActivity {
    public static final String titleName = "TITLE";
    public static final String choicesName = "CHOICES";
    public static final String checkedIdName = "CHECKEDID";
    public static final String resultName = "RESULT";

    private RadioButton createRadioButton(String text, int id, @ColorInt int color) {
        RadioButton radioButton = new RadioButton(SelectorDialogActivity.this);
        radioButton.setPadding(16, 0, 0, 0);
        radioButton.setButtonTintList(ColorStateList.valueOf(color));
        radioButton.setTextColor(ColorStateList.valueOf(color));
        radioButton.setText(text);
        radioButton.setId(id);
        return radioButton;
    }

    private void populate(RadioGroup choiceRadioGroup, ArrayList<String> choices, int checkedId) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorSecondaryVariant, typedValue, true);
        @ColorInt int color = typedValue.data;
        for (int i = 0; i < choices.size(); i++) {
            String choice = choices.get(i);
            choiceRadioGroup.addView(createRadioButton(choice, i, color));
        }
        choiceRadioGroup.check(checkedId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_dialog);

        Intent intent = getIntent();

        String title = intent.getStringExtra(titleName);

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(title);

        RadioGroup choiceRadioGroup = findViewById(R.id.choiceRadioGroup);
        choiceRadioGroup.removeAllViews();
        populate(choiceRadioGroup, intent.getStringArrayListExtra(choicesName), intent.getIntExtra(checkedIdName, 0));

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent resultIntent = new Intent();
            setResult(RESULT_CANCELED, resultIntent);
            finish();
        });

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(view -> {
            int result = choiceRadioGroup.getCheckedRadioButtonId();
            Intent resultIntent = new Intent();
            resultIntent.putExtra(resultName, result);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}