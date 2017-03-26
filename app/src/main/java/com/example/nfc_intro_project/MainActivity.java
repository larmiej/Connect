package com.example.nfc_intro_project;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback, CardListAdapter.ViewHolder.ClickListener, CardListAdapter.OnCompleteListener {
    //The array lists to hold our messages
    private ArrayList<String> messagesToSendArray = new ArrayList<>();
    private ArrayList<String> messagesReceivedArray = new ArrayList<>();
    private  static ArrayList<Card> cards = new ArrayList<Card>();

    private RecyclerView cardListView;
    private CardListAdapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //Text boxes to add and display our messages
    private EditText txtBoxAddMessage;
    private TextView txtReceivedMessages;
    private TextView txtMessagesToSend;

    private NfcAdapter mNfcAdapter;

//    public void addMessage(View view) {
//        String newMessage = txtBoxAddMessage.getText().toString();
//        messagesToSendArray.add(newMessage);
//
//        txtBoxAddMessage.setText(null);
//        updateTextViews();
//
//        Toast.makeText(this, "Added Message", Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //messagesToSendArray.clear();
        //This is called when the system detects that our NdefMessage was
        //Successfully sent
    }

    public NdefRecord[] createRecords() {
        NdefRecord[] records = new NdefRecord[messagesToSendArray.size() + 1];
        //To Create Messages Manually if API is less than
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            for (int i = 0; i < messagesToSendArray.size(); i++){
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));
                NdefRecord record = new NdefRecord(
                        NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                        NdefRecord.RTD_TEXT,            //Description of our payload
                        new byte[0],                    //The optional id for our Record
                        payload);                       //Our payload for the Record

                records[i] = record;
            }
        }
        //Api is high enough that we can use createMime, which is preferred.
        else {
            for (int i = 0; i < messagesToSendArray.size(); i++){
                byte[] payload = messagesToSendArray.get(i).
                        getBytes(Charset.forName("UTF-8"));

                NdefRecord record = NdefRecord.createMime("text/plain",payload);
                records[i] = record;
            }
        }
        records[messagesToSendArray.size()] = NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.navigation_Personalize) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //This will be called when another NFC capable device is detected.
        if (messagesToSendArray.size() == 0) {
            return null;
        }
        //We'll write the createRecords() method in just a moment
        NdefRecord[] recordsToAttach = createRecords();
        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }

    private void handleNfcIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if(receivedArray != null) {
                messagesReceivedArray.clear();
                NdefMessage receivedMessage = (NdefMessage) receivedArray[0];
                NdefRecord[] attachedRecords = receivedMessage.getRecords();

                for (NdefRecord record:attachedRecords) {
                    String string = new String(record.getPayload());
                    //Make sure we don't pass along our AAR (Android Applicatoin Record)
                    if (string.equals(getPackageName())) { continue; }
                    JSONObject json = new JSONObject();
                    try {
                        System.out.println(string.substring(3));
                        json = new JSONObject(string.substring(3));
                        //messagesReceivedArray.add(json.getString("name"));
                        Card newData = new Card(json.getString("name"), json.getString("company"), json.getString("email"), json.getString("number"));
                        boolean setCard = true;
                        for (int i = 0; i < cards.size(); i++)
                        {
                            if (cards.get(i).getName().equals(newData.getName()))
                            {
                                setCard = false;
                                break;
                            }
                        }

                        if (setCard) { cards.add(newData); }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                Toast.makeText(this, "Received " + 1 +
                        " Business Card", Toast.LENGTH_LONG).show();
                updateTextViews();
            }
            else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }


        }
    }


    @Override
    public void onNewIntent(Intent intent) {
            //handleNfcIntent(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateTextViews();
        //handleNfcIntent(getIntent());
    }

    private void setUp(){
        Card card1 = new Card("John Doe", "MHacks", "john.doe@mhacks.com", "123-456-789");
        boolean setCard = true;
        for (int i = 0; i < cards.size(); i++)
        {
            if (cards.get(i).getName().equals(card1.getName()))
            {
                setCard = false;
                break;
            }
        }

        if (setCard) { cards.add(card1); }
    }



    private  void updateTextViews() {
//        txtMessagesToSend.setText("Messages To Send:\n");
//        //Populate Our list of messages we want to send
//        if(messagesToSendArray.size() > 0) {
//            for (int i = 0; i < messagesToSendArray.size(); i++) {
//                txtMessagesToSend.append(messagesToSendArray.get(i));
//                txtMessagesToSend.append("\n");
//            }
//        }
//
//        txtReceivedMessages.setText("Messages Received:\n");
//        //Populate our list of messages we have received
//        if (messagesReceivedArray.size() > 0) {
//            for (int i = 0; i < messagesReceivedArray.size(); i++) {
//                txtReceivedMessages.append(messagesReceivedArray.get(i));
//                txtReceivedMessages.append("\n");
//            }
//        }
        cardListView = (RecyclerView) findViewById(R.id.card_list_view);

        layoutManager = new LinearLayoutManager(this);
        cardListView.setLayoutManager(layoutManager);

        listAdapter = new CardListAdapter(cards, this, this);
        cardListView.setAdapter(listAdapter);

        cardListView.setItemAnimator(new DefaultItemAnimator());
    }

    //Save our Array Lists of Messages for if the user navigates away
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putStringArrayList("messagesToSend", messagesToSendArray);
        savedInstanceState.putStringArrayList("lastMessagesReceived",messagesReceivedArray);
    }

    //Load our Array Lists of Messages for when the user navigates back
    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messagesToSendArray = savedInstanceState.getStringArrayList("messagesToSend");
        messagesReceivedArray = savedInstanceState.getStringArrayList("lastMessagesReceived");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUp();

        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            //This will refer back to createNdefMessage for what it will send
            //mNfcAdapter.setNdefPushMessageCallback(this, this);
            try {
                mNfcAdapter.setNdefPushMessage(createNdefMessageFromPhone(), this);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }


//        txtBoxAddMessage = (EditText) findViewById(R.id.txtBoxAddMessage);
//        txtMessagesToSend = (TextView) findViewById(R.id.txtMessageToSend);
//        txtReceivedMessages = (TextView) findViewById(R.id.txtMessagesReceived);
//        Button btnAddMessage = (Button) findViewById(R.id.buttonAddMessage);

//        btnAddMessage.setText("Add Message");
        updateTextViews();

        if (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            handleNfcIntent(getIntent());
        }
    }

    private NdefMessage createNdefMessageFromPhone() throws JSONException {

        //get values from system, manually input them right now
        JSONObject obj = new JSONObject();

        obj.put("Full name", "Jane Doe");
        obj.put("Email", "jane.doe@mhacks.com");
        obj.put("Phone Number", "123456789");


        String placeholder = obj.toString();

        byte[] payload = placeholder.getBytes(Charset.forName("UTF-8"));

        NdefRecord[] records = new NdefRecord[2];

        NdefRecord record = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                NdefRecord.RTD_TEXT,            //Description of our payload
                new byte[0],                    //The optional id for our Record
                payload);

        records[0] = record;
        records[1] = NdefRecord.createApplicationRecord(getPackageName());

        NdefMessage message = new NdefMessage(records);

        return message;
    }

    @Override
    public void onItemClicked(int position) {

    }

    @Override
    public boolean onItemLongClicked(int position) {
        return false;
    }

    @Override
    public void onComplete(int dataSetSize) {

    }
}