package ar.edu.frba.utn.gibio.fisioremoto;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class NewPersonActivity extends AppCompatActivity {
    Menu menu;
    String new_profile_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        deletePreferences();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new NewPersonPreferenceFragment()).commit();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void deletePreferences() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_name").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_email").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_sex").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_birthdate").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_birth_place").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_birth_place2").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_birth_city").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_job").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_height").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_weight").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_pres_max").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_pres_min").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("new_deltax").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("colesterol").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("presion").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("diabetes").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("artritis").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("rinon").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("fibrilacion").apply();
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove("familiar").apply();
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
            }
            break;

            case R.id.menu_save: {
                new_profile_name = PreferenceManager.getDefaultSharedPreferences(this).getString("new_name", "No Name");
                Toast.makeText(getApplicationContext()
                        , "New profile created: " + new_profile_name
                        , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("stuff", new_profile_name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            break;
        }
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
