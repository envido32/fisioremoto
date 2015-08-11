package ar.edu.frba.utn.gibio.fisioremoto;


import android.content.SharedPreferences;
import android.os.Bundle;
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

    public static class NewPersonPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
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
                new_profile_name = sharedPref.getString("new_name", "No Name");
                Toast.makeText(getApplicationContext()
                        , "New profile created: " + new_profile_name
                        , Toast.LENGTH_SHORT).show();
            }
            break;
        }
        prefEditor.putString("new_name","");
        prefEditor.apply();
        this.finish();
        return super.onOptionsItemSelected(item);
    }
}
