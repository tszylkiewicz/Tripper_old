package com.example.tripper;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

public class PropertiesActivity extends Activity implements PropertiesActivityContract.View {

    public PropertiesActivityContract.Presenter presenter;
    public EditText days;
    public Spinner type;
    public EditText maxDistance;

    NumberPicker np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        type = findViewById(R.id.type);
        String[] items = new String[]{"Car", "Bike", "Walk"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        type.setAdapter(adapter);

        np = findViewById(R.id.numberPicker);

        np.setMinValue(1);
        np.setMaxValue(30);

        np.setOnValueChangedListener(onValueChangeListener);

        presenter = new PropertiesActivityPresenter(this);
    }

    @Override
    public void onStop() {

            MapFragmentPresenter.days = np.getValue();


        MapFragmentPresenter.type = type.getSelectedItemPosition();
        //Log.d("Properties", String.valueOf(type.getSelectedItemPosition()));
        super.onStop();
    }

    NumberPicker.OnValueChangeListener onValueChangeListener =
            new 	NumberPicker.OnValueChangeListener(){
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    Log.d("Properties", "selected number "+numberPicker.getValue());
                }
            };
}
