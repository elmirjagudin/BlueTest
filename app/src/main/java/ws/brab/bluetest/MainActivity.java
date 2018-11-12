package ws.brab.bluetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "cws.brab.bluetest.MESSAGE";
    private static final String TAG = "BlueTest";


    public void SetNMEA(String str)
    {
        TextView textView = findViewById(R.id.NMEA);
        textView.setText(str);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new HiperReader(this);
        //DumpBluetooth();
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);


        startActivity(intent);
    }
}
