package com.ancalutu.step_counter2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class Settings extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;
        EditText editText_age, editText_height_cm, editText_height_ft, editText_height_in, editText_weight_kg, editText_weight_lb;
        LinearLayout layout_ft_in;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            editor = sharedPref.edit();
            editText_age = (EditText) getActivity().findViewById(R.id.editText_age);
            editText_height_cm = (EditText) getActivity().findViewById(R.id.editText_height_cm);
            editText_height_ft = (EditText) getActivity().findViewById(R.id.editText_height_ft);
            editText_height_in = (EditText) getActivity().findViewById(R.id.editText_height_in);
            editText_weight_kg = (EditText) getActivity().findViewById(R.id.editText_weight_kg);
            editText_weight_lb = (EditText) getActivity().findViewById(R.id.editText_weight_lb);
            layout_ft_in = (LinearLayout) getActivity().findViewById(R.id.layout_ft_in);

        }

        @Override
        public void onResume() {
            super.onResume();
            setSpinnerUnits();
            editText_age.setText(String.valueOf(sharedPref.getInt(Constants.AGE, 20)));
            editText_weight_kg.setText(String.valueOf( sharedPref.getInt(Constants.WEIGHT_KG, 60)));
            editText_weight_lb.setText(String.valueOf( sharedPref.getInt(Constants.WEIGHT_LB, 140)));
            if(sharedPref.getInt(Constants.WEIGHT_POS,0)==0){
                editText_weight_lb.setVisibility(View.GONE);
                editText_weight_kg.setVisibility(View.VISIBLE);
            }else{
                editText_weight_kg.setVisibility(View.GONE);
                editText_weight_lb.setVisibility(View.VISIBLE);
            }

            editText_height_cm.setText(String.valueOf(sharedPref.getInt(Constants.HEIGHT_CM, 170)));
            editText_height_ft.setText(String.valueOf(sharedPref.getInt(Constants.HEIGHT_FT, 5)));
            editText_height_in.setText(String.valueOf(sharedPref.getInt(Constants.HEIGHT_IN, 10)));
            if(sharedPref.getInt(Constants.HEIGHT_POS,0)==0){
                layout_ft_in.setVisibility(View.GONE);
                editText_height_cm.setVisibility(View.VISIBLE);
            }else{
                editText_height_cm.setVisibility(View.GONE);
                layout_ft_in.setVisibility(View.VISIBLE);
            }
            setSpinnerGender();
            setSpinnerHeight();
            setSpinnerWeight();
        }

        private void setSpinnerUnits() {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.units_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner_units = (Spinner) getActivity().findViewById(R.id.spinner_units);
            spinner_units.setAdapter(adapter);
            spinner_units.setSelection(sharedPref.getInt(Constants.UNIT_POS, getResources().getInteger(R.integer.Imperial_Units)));                   //modif unitati initiale
            spinner_units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    editor.putInt(Constants.UNIT_POS, position);
                    editor.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        private void setSpinnerGender() {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.sex_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner_sex = (Spinner) getActivity().findViewById(R.id.spinner_sex);
            spinner_sex.setAdapter(adapter);
            spinner_sex.setSelection(sharedPref.getInt(Constants.SEX_POS, 0));
            spinner_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    editor.putInt(Constants.SEX_POS, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        private void setSpinnerHeight() {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.height_unit_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner_units = (Spinner) getActivity().findViewById(R.id.spinner_height_units);
            spinner_units.setAdapter(adapter);
            spinner_units.setSelection(sharedPref.getInt(Constants.HEIGHT_POS, getResources().getInteger(R.integer.Imperial_Units)));                      //modif unitati initiale
            spinner_units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 1) {  //cm
                        layout_ft_in.setVisibility(View.GONE);
                        editText_height_cm.setVisibility(View.VISIBLE);
                    } else {    // ft, in
                        editText_height_cm.setVisibility(View.GONE);
                        layout_ft_in.setVisibility(View.VISIBLE);
                    }
                    editor.putInt(Constants.HEIGHT_POS, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        private void setSpinnerWeight() {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                    R.array.weight_unit_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner_units = (Spinner) getActivity().findViewById(R.id.spinner_weight_units);
            spinner_units.setAdapter(adapter);
            spinner_units.setSelection(sharedPref.getInt(Constants.WEIGHT_POS, getResources().getInteger(R.integer.Imperial_Units)));                      //modif unitati initiale
            spinner_units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 1) {   //kg
                        editText_weight_lb.setVisibility(View.GONE);
                        editText_weight_kg.setVisibility(View.VISIBLE);
                    } else {  //lb
                        editText_weight_kg.setVisibility(View.GONE);
                        editText_weight_lb.setVisibility(View.VISIBLE);
                    }
                    editor.putInt(Constants.WEIGHT_POS, position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        @Override
        public void onStop() {
            super.onStop();
            editor.putInt(Constants.AGE, Integer.parseInt(editText_age.getText().toString()));

            editor.putInt(Constants.HEIGHT_CM, Integer.parseInt(editText_height_cm.getText().toString()));
            editor.putInt(Constants.HEIGHT_FT, Integer.parseInt(editText_height_ft.getText().toString()));
            editor.putInt(Constants.HEIGHT_IN, Integer.parseInt(editText_height_in.getText().toString()));

            editor.putInt(Constants.WEIGHT_KG, Integer.parseInt(editText_weight_kg.getText().toString()));
            editor.putInt(Constants.WEIGHT_LB, Integer.parseInt(editText_weight_lb.getText().toString()));
            editor.commit();
            getActivity().startService(new Intent(getActivity(), MainService.class).setAction(Constants.ACTION_SETTINGS_CHANGE));


        }




    }
}
