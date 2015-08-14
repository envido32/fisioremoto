package ar.edu.frba.utn.gibio.fisioremoto;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    BluetoothSPP bt;
    TextView textRead;
    EditText etMessage;
    GraphView graph;
    LineGraphSeries<DataPoint> series;
    private double graphLastXValue = 1d;
    Menu menu;
    ArrayList<String> PatientArray;
    private ListView mDrawerList;
    private ArrayAdapter<String> mDrawerAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;

    private SharedPreferences prefSettings;
    private SharedPreferences.Editor prefEditor;
    Set<String> pacientesSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.wtf("Init", "WFT log visible");
        Log.d("Init", "DEBUG log visible");
        Log.e("Init", "ERROR log visible");
        Log.i("Init", "INFO log visible");
        Log.v("Init", "VERBOSE log visible");
        Log.w("Init", "WARNING log visible");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textRead = (TextView) findViewById(R.id.textRead);
        etMessage = (EditText) findViewById(R.id.etMessage);
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        graph.addSeries(series);

        prefSettings = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = prefSettings.edit();
        pacientesSet = new HashSet<>();

        setupDrawer();
        addDrawerItems();
        startBluetooth();
        Log.v("Init", "Init finished");
    }


    @Override
    protected void onResume() {
        super.onResume();
        pacientesSet = prefSettings.getStringSet("saved_name", null);
        if (pacientesSet != null) {
            PatientArray.clear();
            PatientArray.addAll(pacientesSet);
            Log.v("onResume", "pacientesSet: " + pacientesSet);
            mDrawerAdapter.notifyDataSetChanged();
        }
        else{
            Log.i("onResume", "pasientesSet is NULL");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void setupDrawer() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mActivityTitle = getTitle().toString();
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Pacientes");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void addDrawerItems() {
        PatientArray = new ArrayList<>();

        mDrawerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, PatientArray);
        mDrawerList.setAdapter(mDrawerAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Drawer", "Click! Pos: " + position + " - ID: " + id + " - " + PatientArray.get(position));
            }
        });

        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Drawer", "Long! Pos: " + position + " - ID: " + id + " - " + PatientArray.get(position));
                pacientesSet = prefSettings.getStringSet("saved_name", null);
                pacientesSet.remove(PatientArray.get(position));
                prefEditor.putStringSet("saved_name", pacientesSet).apply();
                PatientArray.remove(position);
                mDrawerAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Deleted: " + PatientArray.get(position), Toast.LENGTH_SHORT).show();
                Log.i("Drawer", "Deleted: " + PatientArray.get(position));
                return false;
            }
        });

    }

    private void startBluetooth() {

        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            Log.e("Bluetooth", "Not avaliable");
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                textRead.append(message + "\n");
                int myNum = 0;
                try {
                    myNum = Integer.parseInt(message);
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                    Log.e("Bluetooth", "Could not parse " + nfe);
                }

                //myNum = ProcessData(myNum);

                graphLastXValue += 1d;
                series.appendData(new DataPoint(graphLastXValue, myNum), true, 40);
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                Toast.makeText(getApplicationContext(), "Disconnected!", Toast.LENGTH_SHORT).show();
                Log.i("Bluetooth", "Disconnected!");
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);
            }

            public void onDeviceConnectionFailed() {
                Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
                Log.i("Bluetooth", "Connection failed!");
            }

            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext(), "Connected to " + name, Toast.LENGTH_SHORT).show();
                Log.i("Bluetooth", "Connected to " + name);
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_disconnection, menu);
            }
        });
    }

    /*
        double ProcessData(byte[] data)
        {
            byte aux = 0;

            aux = (byte)(data[2] << 7);
            data[3] = (byte)(data[3] | aux);
            data[2] = (byte)(data[2] >> 1);

            aux = (byte)(data[1] << 6);
            data[2] = (byte)(data[2] | aux);
            data[1] = (byte)(data[1] >> 2);

            aux = (byte)(data[0] << 5);
            data[1] = (byte)(data[1] | aux);

            double dato = (int)(data[1] << 16) + (int)(data[2] << 8) + (int)(data[3]);

            return dato;
        }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        Log.v("OptionsMenu", "Created");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        switch (id) {
            case R.id.menu_device_connect: {
                bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                }
                Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
            break;

            case R.id.menu_disconnect: {
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
                    bt.disconnect();
                }
            }
            break;

            case R.id.action_settings: {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
            break;

            case R.id.menu_new_person: {
                Intent intent = new Intent(getApplicationContext(), NewPersonActivity.class);
                startActivity(intent);
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
        Log.v("Main", "Closed");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setupBluetooth();
            }
        }
    }

    public void setupBluetooth() {
        ImageButton btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (etMessage.getText().length() != 0) {
                    bt.send(etMessage.getText().toString(), true);
                    etMessage.setText("");
                    Log.v("Bluetooth", "Send: " + etMessage.getText().toString());
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case BluetoothState.REQUEST_CONNECT_DEVICE: {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    bt.connect(data);
                }
            }
            break;

            case BluetoothState.REQUEST_ENABLE_BT: {
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_ANDROID);
                    setupBluetooth();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
                    Log.e("Bluetooth", "Was not enabled");
                    finish();
                }
            }
            break;
        }
    }
}
