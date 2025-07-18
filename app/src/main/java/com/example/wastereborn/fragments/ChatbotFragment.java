package com.example.wastereborn.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wastereborn.R;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {

    private RecyclerView recyclerChat;
    private EditText editMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    public ChatbotFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        // Initialize views
        recyclerChat = view.findViewById(R.id.recycler_chat);
        editMessage = view.findViewById(R.id.edit_message);
        btnSend = view.findViewById(R.id.btn_send);

        // Setup RecyclerView
        recyclerChat.setLayoutManager(new LinearLayoutManager(getContext()));
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerChat.setAdapter(chatAdapter);

        // Add welcome message
        addBotMessage("Hello! I'm WasteBot, your eco-friendly assistant. How can I help you today?");
        addBotMessage("You can ask me about:\n• Waste pickup schedules\n• Recycling tips\n• Product information\n• Points system\n• How to use the app");

        // Setup send button
        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String message = editMessage.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        // Add user message
        addUserMessage(message);
        editMessage.setText("");

        // Generate bot response
        String response = generateBotResponse(message.toLowerCase());
        addBotMessage(response);

        // Scroll to bottom
        recyclerChat.scrollToPosition(chatMessages.size() - 1);
    }

    private String generateBotResponse(String userMessage) {
        if (userMessage.contains("pickup") || userMessage.contains("schedule")) {
            return "To schedule a waste pickup:\n1. Go to the Pickup tab\n2. Select your waste type\n3. Choose your street\n4. Pick an available time slot\n5. Confirm your request\n\nYou'll earn 5 points for each completed pickup!";
        } else if (userMessage.contains("points") || userMessage.contains("reward")) {
            return "Our points system works like this:\n• Earn 5 points per completed pickup\n• Redeem points for eco-friendly products\n• Check your balance in the Rewards tab\n• 100 points = Premium rewards!";
        } else if (userMessage.contains("product") || userMessage.contains("shop") || userMessage.contains("buy")) {
            return "Browse our marketplace for amazing recycled products!\n• Eco-friendly bags\n• Recycled furniture\n• Solar chargers\n• And much more!\n\nYou can pay with mobile money or use your points.";
        } else if (userMessage.contains("recycle") || userMessage.contains("waste")) {
            return "Great recycling tips:\n• Separate plastic, paper, and glass\n• Clean containers before disposal\n• Remove labels when possible\n• Electronics need special handling\n\nEvery small action helps our planet! 🌱";
        } else if (userMessage.contains("help") || userMessage.contains("how")) {
            return "I'm here to help! You can:\n• Book waste pickups\n• Shop recycled products\n• Track your orders\n• Earn and redeem points\n• View your recycling stats\n\nWhat would you like to know more about?";
        } else if (userMessage.contains("hello") || userMessage.contains("hi")) {
            return "Hello there! 👋 Welcome to WasteReborn! I'm excited to help you on your eco-friendly journey. What can I assist you with today?";
        } else if (userMessage.contains("thank")) {
            return "You're very welcome! 😊 Thank you for choosing WasteReborn and helping make our planet greener. Is there anything else I can help you with?";
        } else {
            return "I understand you're asking about: \"" + userMessage + "\"\n\nI'm still learning! For now, I can help with:\n• Waste pickup scheduling\n• Points and rewards\n• Product information\n• Recycling tips\n\nTry asking about one of these topics!";
        }
    }

    private void addUserMessage(String message) {
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    private void addBotMessage(String message) {
        chatMessages.add(new ChatMessage(message, false));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
    }

    // Simple ChatMessage class
    public static class ChatMessage {
        public String message;
        public boolean isUser;

        public ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
        }
    }

    // Simple ChatAdapter class
    public static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> messages;

        public ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage message = messages.get(position);
            String prefix = message.isUser ? "You: " : "WasteBot: ";
            holder.textMessage.setText(prefix + message.message);

            // Style differently for user vs bot
            if (message.isUser) {
                holder.textMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                holder.textMessage.setBackgroundColor(0xFFE8F5E8);
            } else {
                holder.textMessage.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                holder.textMessage.setBackgroundColor(0xFFF0F8FF);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ChatViewHolder extends RecyclerView.ViewHolder {
            TextView textMessage;

            ChatViewHolder(View itemView) {
                super(itemView);
                textMessage = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
