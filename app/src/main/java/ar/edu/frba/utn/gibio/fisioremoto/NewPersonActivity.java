package ar.edu.frba.utn.gibio.fisioremoto;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class NewPersonActivity extends AppCompatActivity {
    Menu menu;
    SharedPreferences sharedPref;
    SharedPreferences.Editor prefEditor;
    String new_profile_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = sharedPref.edit();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new NewPersonPreferenceFragment()).commit();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public static class NewPersonPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private static final String KEY_EDIT_TEXT_PREFERENCE = "new_height";

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.new_person);
            addPreferencesFromResource(R.xml.new_medical);
        }

        @Override
        public void onResume(){
            super.onResume();
            getPreferenceScreen().getSharedPreferences().
                    registerOnSharedPreferenceChangeListener(this);
            updatePreference(KEY_EDIT_TEXT_PREFERENCE);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().
                    unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(key);
        }

        private void updatePreference(String key){
            if (key.equals(KEY_EDIT_TEXT_PREFERENCE)){
                Preference preference = findPreference(key);
                if (preference instanceof EditTextPreference){
                    EditTextPreference editTextPreference = (EditTextPreference)preference;
                    if (editTextPreference.getText().trim().length() > 0){
                        editTextPreference.setSummary("Entered name is: " + editTextPreference.getText());
                    }else{
                        editTextPreference.setSummary("Enter patient name.");
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_new_person, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home: {
            } break;

            case R.id.menu_save: {
                new_profile_name = sharedPref.getString("new_name", "No Name");
                Toast.makeText(getApplicationContext()
                        , "New profile created: " + new_profile_name
                        , Toast.LENGTH_SHORT).show();
            } break;
        }
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        prefEditor.putString("new_name", "");
        prefEditor.putString("new_email", "");
        prefEditor.putInt("new_sex", 0);
        prefEditor.putString("new_birthdate", "");
        prefEditor.putString("new_birth_place", "");
        prefEditor.putString("new_birth_place2", "");
        prefEditor.putString("new_birth_city", "");
        prefEditor.putInt("new_job", 0);
        prefEditor.putString("new_height", "");
        prefEditor.putString("new_weight", "");
        prefEditor.putString("new_pres_max", "");
        prefEditor.putString("new_pres_min", "");
        prefEditor.putString("new_deltax", "");
        prefEditor.putBoolean("colesterol", false);
        prefEditor.putBoolean("presion", false);
        prefEditor.putBoolean("diabetes", false);
        prefEditor.putBoolean("artritis", false);
        prefEditor.putBoolean("rinon", false);
        prefEditor.putBoolean("fibrilacion", false);
        prefEditor.putBoolean("familiar", false);

        prefEditor.apply();
    }
}
