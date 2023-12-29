package com.example.chatbot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    RecyclerView idRVChats;
    EditText idEdtMsg;
    FloatingActionButton idFABSend;
    private final String BOT_KEY = "bot";
    private final String USER_KEY = "user";
    ArrayList<ChatsModal> chatsModalArrayList;
    ChatRVAdapter chatRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        idRVChats = findViewById(R.id.idRVChats);
        idEdtMsg = findViewById(R.id.idEdtMsg);
        idFABSend = findViewById(R.id.idFABSend);
        chatsModalArrayList = new ArrayList<>();
        chatRVAdapter = new ChatRVAdapter(chatsModalArrayList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        idRVChats.setLayoutManager(linearLayoutManager);
        idRVChats.setAdapter(chatRVAdapter);

        idFABSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idEdtMsg.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(idEdtMsg.getText().toString());
                idEdtMsg.setText("");
            }
        });


    }
    private void getResponse(String message){
        chatsModalArrayList.add(new ChatsModal(message, USER_KEY));
        chatRVAdapter.notifyDataSetChanged();
        String url = "http://api.brainshop.ai/get?bid=167076&key=q6Dmd7uRxP6Dew3W&uid=[uid]&msg="+message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<MsgModal> call = retrofitAPI.getMsg(url);
//        Log.d("Response",call.toString());
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if (response.isSuccessful()){
//                    Log.d("Response",response.body().getCnt());
                    MsgModal modal = response.body();
                    chatsModalArrayList.add(new ChatsModal(modal.getCnt(), BOT_KEY));
                    chatRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
//                Log.d("Response","response.body().getCnt()");
                chatsModalArrayList.add(new ChatsModal("Please repeat your question.", BOT_KEY));
                chatRVAdapter.notifyDataSetChanged();
            }
        });
    }
}



