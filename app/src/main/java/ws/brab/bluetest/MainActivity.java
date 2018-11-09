package ws.brab.bluetest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "cws.brab.bluetest.MESSAGE";
    private static final String TAG = "BlueTest";

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "something happend");

            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String mac = device.getAddress(); // MAC address
                Log.i(TAG, "A discovery is made '" + name + "' " + mac);
            }
        }
    };


    void DumpBluetooth()
    {

        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba == null)
        {
            Log.i(TAG, "does not support bluetooth");
            return;
        }

        if (!ba.isEnabled())
        {
            Log.i(TAG, "bluetooth disabled");
            return;
        }

        Set<BluetoothDevice> pairedDevices = ba.getBondedDevices();

        Log.i(TAG, "ba is '" + ba + "' pairedDevices.size " + pairedDevices.size() + " ba.isEnabled() " + ba.isEnabled());

        BluetoothDevice hiper = null;

        if (pairedDevices.size() <= 0)
        {
            Log.i(TAG, "no paired devices");
            return;
        }

        // There are paired devices. Get the name and address of each paired device.
        for (BluetoothDevice device : pairedDevices)
        {
            String deviceName = device.getName();
            String mac = device.getAddress(); // MAC address

            Log.i(TAG, "dev name " + deviceName + " mac " + mac);

            if (mac.equals("00:07:80:36:02:C6"))
            {
                Log.i(TAG, "found Hiper");
                hiper = device;
                break;
            }
        }

        Log.i(TAG, "hiper is " + hiper);

        BluetoothSocket hiperSock = null;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            // hardcoded UUID for Serial port service
            // https://stackoverflow.com/questions/13964342/android-how-do-bluetooth-uuids-work
            hiperSock = hiper.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
        } catch (IOException e) {
            Log.i(TAG, "Socket's create() method failed", e);
            return;
        }


        Log.i(TAG, "hiperSock " + hiperSock);

        InputStream hiperIn = null;
        OutputStream hiperOut= null;

        try {
            hiperSock.connect();

            hiperIn = hiperSock.getInputStream();
            hiperOut = hiperSock.getOutputStream();

        } catch (IOException e) {
            Log.i(TAG, "hiperSock connect exception " + e);
        }

        Log.i(TAG, "no complains in " + hiperIn + " out " + hiperOut);
        try {
            hiperOut.write("em,/cur/term,/msg/nmea/GGA:.05\n\r".getBytes());

            BufferedReader br = new BufferedReader(new InputStreamReader(hiperIn));
            String line;
            while ((line = br.readLine()) != null)
            {
                Log.i(TAG, "line '" + line + "'");
            }
        } catch (IOException e) {
            Log.i(TAG, "IO exception " + e);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);


        DumpBluetooth();
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);


        startActivity(intent);
    }
}
