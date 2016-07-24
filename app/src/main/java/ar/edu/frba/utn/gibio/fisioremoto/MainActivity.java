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

    GraphView graphA;
    LineGraphSeries<DataPoint> seriesA;
    private double graphALastXValue = 1d;

    GraphView graphB;
    LineGraphSeries<DataPoint> seriesB;
    private double graphBLastXValue = 1d;


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


        graphA = (GraphView) findViewById(R.id.graphA);
        seriesA = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        graphA.addSeries(seriesA);
        graphA.getViewport().setScrollable(true);      // enable scrolling
        graphA.getViewport().setScalable(true);        // enable scaling

        graphB = (GraphView) findViewById(R.id.graphB);
        seriesB = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
        });
        graphB.addSeries(seriesB);

        graphB.getViewport().setScrollable(true);      // enable scrolling
        graphB.getViewport().setScalable(true);        // enable scaling

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
                Toast.makeText(getApplicationContext(), "Click! Pos: " + position + " - ID: " + id + " - " + PatientArray.get(position), Toast.LENGTH_SHORT).show();
                Log.v("Drawer", "Click! Pos: " + position + " - ID: " + id + " - " + PatientArray.get(position));
            }
        });

        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("Drawer", "Long! Pos: " + position + " - ID: " + id + " - " + PatientArray.get(position));
                pacientesSet = prefSettings.getStringSet("saved_name", null);
                if (pacientesSet == null) {
                    Log.e("NewPerson", "pacientesSet Null");
                    pacientesSet = new HashSet<>();
                } else {
                    Log.i("NewPerson", "pacientesSet not Null");
                    pacientesSet.remove(PatientArray.get(position));
                    prefEditor.putStringSet("saved_name", pacientesSet).apply();
                }
                PatientArray.remove(position);
                mDrawerAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Deleted: " + PatientArray.get(position), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    // Transformar en tarea
    private void graphApp (String message){
        int datos_canal[] = {0,0,0,0};
        int aux = 0;
        int dato_final = 0;
        int myNumA = 0;
        int myNumB = 0;

        if (message.length() > 3) {
            try {
                myNumA = message.charAt(0);
                myNumB = message.charAt(1);
            } catch (NumberFormatException nfe) {
                System.out.println("Could not parse " + nfe);
                Log.e("Bluetooth", "Could not parse " + nfe);
            }

            /* bits del DATO;

            sdptool records 5C:51:88:2C:2B:CF
            sudo gedit /etc/bluetooth/rfcomm.conf
                	rfcomm0 {
                        bind no;
                        device 5c:51:88:2c:2b:cf;
                        channel 8;
                        comment "SPP";
                        }
            sudo rfcomm connect 0
            sudo cutecom

            1 0     CH2   CH1     CH0   bit23 bit22 bit21
            0 bit20 bit19 bit18   bit17 bit16 bit15 bit14
            0 bit13 bit12 bit11   bit10 bit09 bit08 bit07
            0 bit06 bit05 bit04   bit03 bit02 bit01 bit00
            */
            datos_canal[0] = message.charAt(0);
            datos_canal[0] &= 0x07;  // 0000 0111
            datos_canal[1] = message.charAt(1);
            datos_canal[1] &= 0x7F;  // 0111 1111
            datos_canal[2] = message.charAt(2);
            datos_canal[2] &= 0x7F;
            datos_canal[3] = message.charAt(3);
            datos_canal[3] &= 0x7F;

            dato_final = datos_canal[3]
                ^ (datos_canal[2] << 7)
                ^ (datos_canal[1] << 14)
                ^ (datos_canal[0] << 21);
            Toast.makeText(getApplicationContext(), "dato_final: " + dato_final, Toast.LENGTH_SHORT).show();

            aux  = message.charAt(0);
            aux &= 0xC0 ;               // 1100 0000
            if (aux == 0x80) {
                aux  = message.charAt(1) & 0x80 ;           // 1000 0000
                if (aux == 0x00) {
                    aux  = message.charAt(2) & 0x80 ;
                    if (aux == 0x00) {
                        aux  = message.charAt(3) & 0x80 ;
                        if (aux == 0x00) {
                            aux = message.charAt(0) & 0x38;     // 0011 1000
                            aux >>= 3;

                            switch (aux) {
                                case 1: {
                                    graphALastXValue += 1d;
                                    seriesA.appendData(new DataPoint(graphALastXValue, dato_final), true, 40);
                                } break;

                                case 2: {
                                    graphBLastXValue += 1d;
                                    seriesB.appendData(new DataPoint(graphBLastXValue, dato_final), true, 40);
                                }break;

                                default: {
                                    Toast.makeText(getApplicationContext(), "Canal invalido! " + aux, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Mala Trama! (3) " + aux, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Mala Trama! (2) " + aux, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Mala Trama! (1) " + aux, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Mala Trama! (0) " + aux, Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "messege too short!", Toast.LENGTH_SHORT).show();
        }
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
                graphApp(message);
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
