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
        prefEditor.remove("new_name");
        prefEditor.remove("new_email");
        prefEditor.remove("new_sex");
        prefEditor.remove("new_birthdate");
        prefEditor.remove("new_birth_place");
        prefEditor.remove("new_birth_place2");
        prefEditor.remove("new_birth_city");
        prefEditor.remove("new_job");
        prefEditor.remove("new_height");
        prefEditor.remove("new_weight");
        prefEditor.remove("new_pres_max");
        prefEditor.remove("new_pres_min");
        prefEditor.remove("new_deltax");
        prefEditor.remove("colesterol");
        prefEditor.remove("presion");
        prefEditor.remove("diabetes");
        prefEditor.remove("artritis");
        prefEditor.remove("rinon");
        prefEditor.remove("fibrilacion");
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
                new_profile_name = prefSettings.getString("new_name", "No Name");
                Set<String> pacientesSet = prefSettings.getStringSet("saved_name", null);
                if (pacientesSet == null) {
                    pacientesSet = new HashSet<>();
                    Log.i("NewPerson", "Saved: " + new_profile_name);
                } else {
                    Log.e("NewPerson", "pacientesSet Null");
                }
                pacientesSet.add(new_profile_name);
                prefEditor.putStringSet("saved_name", pacientesSet).apply();
                Toast.makeText(getApplicationContext(), "Saved: " + new_profile_name, Toast.LENGTH_SHORT).show();

            }
            break;
        }
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
