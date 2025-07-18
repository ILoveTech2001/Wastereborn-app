package com.example.wastereborn.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastereborn.R;
import com.example.wastereborn.adapter.ChatAdapter;
import com.example.wastereborn.chatbot.WasteBotEngine;
import com.example.wastereborn.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

    private RecyclerView recyclerChat;
    private EditText editMessage;
    private ImageView btnSend, btnClearChat;
    private Button btnQuickPickup, btnQuickPoints;
    private LinearLayout typingIndicator, quickActions;
    private TextView botStatus;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private WasteBotEngine wasteBotEngine;
    private Handler handler;

    public ChatbotFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        // Initialize components
        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();

        // Initialize bot engine
        wasteBotEngine = new WasteBotEngine(getContext());
        handler = new Handler(Looper.getMainLooper());

        // Add welcome messages
        addWelcomeMessages();

        return view;
    }

    private void initializeViews(View view) {
        recyclerChat = view.findViewById(R.id.recycler_chat);
        editMessage = view.findViewById(R.id.edit_message);
        btnSend = view.findViewById(R.id.btn_send);
        btnClearChat = view.findViewById(R.id.btn_clear_chat);
        btnQuickPickup = view.findViewById(R.id.btn_quick_pickup);
        btnQuickPoints = view.findViewById(R.id.btn_quick_points);
        typingIndicator = view.findViewById(R.id.typing_indicator);
        quickActions = view.findViewById(R.id.quick_actions);
        botStatus = view.findViewById(R.id.bot_status);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        recyclerChat.setLayoutManager(layoutManager);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        btnClearChat.setOnClickListener(v -> clearChat());
        btnQuickPickup.setOnClickListener(v -> sendQuickMessage("How do I schedule a pickup?"));
        btnQuickPoints.setOnClickListener(v -> sendQuickMessage("Tell me about the points system"));

        // Show quick actions when input is focused
        editMessage.setOnFocusChangeListener((v, hasFocus) -> {
            quickActions.setVisibility(hasFocus && chatMessages.size() > 2 ? View.VISIBLE : View.GONE);
        });
    }

    private void addWelcomeMessages() {
        addBotMessage("Hello! I'm WasteBot 🤖, your eco-friendly assistant!");

        handler.postDelayed(() -> {
            addBotMessage("I'm here to help you with:\n" +
                    "🚛 Waste pickup scheduling\n" +
                    "🎁 Points and rewards\n" +
                    "🛒 Recycled products\n" +
                    "♻️ Recycling tips\n" +
                    "📱 App guidance\n\n" +
                    "What would you like to know? 😊");
        }, 1000);
    }

    private void sendMessage() {
        String message = editMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        sendUserMessage(message);
    }

    private void sendQuickMessage(String message) {
        editMessage.setText(message);
        sendMessage();
    }

    private void sendUserMessage(String message) {
        // Add user message
        addUserMessage(message);
        editMessage.setText("");
        quickActions.setVisibility(View.GONE);

        // Show typing indicator
        showTypingIndicator();

        // Generate bot response with delay for realism
        handler.postDelayed(() -> {
            hideTypingIndicator();
            String response = wasteBotEngine.generateResponse(message);
            addBotMessage(response);
            scrollToBottom();
        }, 1000 + (int)(Math.random() * 1000)); // 1-2 second delay
    }

    private void showTypingIndicator() {
        typingIndicator.setVisibility(View.VISIBLE);
        botStatus.setText("Typing...");
        scrollToBottom();
    }

    private void hideTypingIndicator() {
        typingIndicator.setVisibility(View.GONE);
        botStatus.setText("Online • Ready to help");
    }

    private void scrollToBottom() {
        if (chatMessages.size() > 0) {
            recyclerChat.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }

    private void clearChat() {
        chatMessages.clear();
        chatAdapter.notifyDataSetChanged();
        wasteBotEngine.clearContext();

        // Add fresh welcome message
        handler.postDelayed(() -> {
            addBotMessage("Chat cleared! 🧹\nHow can I help you today?");
        }, 500);

        Toast.makeText(getContext(), "Chat cleared", Toast.LENGTH_SHORT).show();
    }

    private void addUserMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true);
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void addBotMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false);
        chatMessages.add(chatMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

}
