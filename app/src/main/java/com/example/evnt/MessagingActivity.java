package com.example.evnt;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evnt.adapters.MessageAdapter;
import com.example.evnt.fragments.PickEvntFragment;
import com.example.evnt.networking.ServerRequestModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    List<Message> messages;
    Context context;
    IdentProvider ident;
    ImageButton sendMsg;
    EditText contentMsg;

    MessageAdapter msgAdapter;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

//        Toolbar toolbar = findViewById(R.id.message_toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        messages = new ArrayList<>();
        messages.add(new Message("aa", "aaa", "hey"));

        sendMsg = findViewById(R.id.msg_send_button);
        contentMsg = findViewById(R.id.msg_text_field);
        recyclerView = findViewById(R.id.messages_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        context = this.getApplicationContext();
        ident = new IdentProvider(context);
//        mServerRequestModule = ServerRequestModule.getInstance(context, ident);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = contentMsg.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(ident.getValue(getString(R.string.user_id)), "you", msg);
                    messages.add(new Message(ident.getValue(context.getString(R.string.user_id)), "aaa", msg));
                    readMessages("a", "a");

                } else {
                    Toast.makeText(MessagingActivity.this, "cant do empty", Toast.LENGTH_LONG).show();
                }
                contentMsg.setText("");
            }
        });

    }

    private void sendMessage(String sender, String receiver, String message) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);


    }

    private void readMessages (String myId, String senderId) {



        msgAdapter = new MessageAdapter(MessagingActivity.this, messages);
        recyclerView.setAdapter(msgAdapter);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
