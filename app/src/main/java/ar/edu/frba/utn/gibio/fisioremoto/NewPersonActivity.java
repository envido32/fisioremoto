package ar.edu.frba.utn.gibio.fisioremoto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class NewPersonActivity extends AppCompatActivity {
    Menu menu;
    String new_profile_name;

    private SharedPreferences prefSettings;
    private SharedPreferences.Editor prefEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v("NewPerson", "Opened");
        super.onCreate(savedInstanceState);

        prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = prefSettings.edit();

        deletePreferences();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new NewPersonPreferenceFragment()).commit();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void deletePreferences() {
        prefEditor.remove("new_name").apply();
        prefEditor.remove("new_email").apply();
        prefEditor.remove("new_sex").apply();
        prefEditor.remove("new_birthdate").apply();
        prefEditor.remove("new_birth_place").apply();
        prefEditor.remove("new_birth_place2").apply();
        prefEditor.remove("new_birth_city").apply();
        prefEditor.remove("new_job").apply();
        prefEditor.remove("new_height").apply();
        prefEditor.remove("new_weight").apply();
        prefEditor.remove("new_pres_max").apply();
        prefEditor.remove("new_pres_min").apply();
        prefEditor.remove("new_deltax").apply();
        prefEditor.remove("colesterol").apply();
        prefEditor.remove("presion").apply();
        prefEditor.remove("diabetes").apply();
        prefEditor.remove("artritis").apply();
        prefEditor.remove("rinon").apply();
        prefEditor.remove("fibrilacion").apply();
        prefEditor.remove("familiar");
        prefEditor.apply();
        Log.v("NewPerson", "Temp deleted");
    }

    public static class NewPersonPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.new_person);
            addPreferencesFromResource(R.xml.new_medical);
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
                Log.v("NewPerson", "Click back");
            }
            break;

            case R.id.menu_save: {
                Log.v("NewPerson", "Click save");
                Set<String> pacientesSet = new HashSet<>();
                pacientesSet = prefSettings.getStringSet("saved_name", null);
                if (pacientesSet != null) {
                    new_profile_name = prefSettings.getString("new_name", "No Name");
                    pacientesSet.add(new_profile_name);
                    prefEditor.putStringSet("saved_name", pacientesSet).apply();
                    Toast.makeText(getApplicationContext(), "Saved: " + new_profile_name, Toast.LENGTH_SHORT).show();
                    Log.i("NewPerson", "Saved: " + new_profile_name);
                }
            }
            break;
        }
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
