package com.example.tripper;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class PropertiesActivity extends Activity implements PropertiesActivityContract.View {

    public PropertiesActivityContract.Presenter presenter;
    public EditText days;
    public Spinner type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .7), (int) (height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        days = findViewById(R.id.days);
        days.setFilters(new InputFilter[]{new InputFilterMinMax("1", "21")});
        days.setText("1");
        type = findViewById(R.id.type);
        String[] items = new String[]{"Walk", "Bike", "Car"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        type.setAdapter(adapter);

        presenter = new PropertiesActivityPresenter(this);
    }

    @Override
    public void onStop() {
        if (days.getText().toString() == "") {
            MapFragmentPresenter.days = 1;
        } else {
            MapFragmentPresenter.days = Integer.parseInt(days.getText().toString());
        }

        MapFragmentPresenter.type = type.getSelectedItemPosition();
        //Log.d("Properties", String.valueOf(type.getSelectedItemPosition()));
        super.onStop();
    }
}
