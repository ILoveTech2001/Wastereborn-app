package com.example.wastereborn.chatbot;

import android.content.Context;
import com.example.wastereborn.utils.SessionManager;
import java.util.*;
import java.util.regex.Pattern;

public class WasteBotEngine {
    
    private Context context;
    private SessionManager sessionManager;
    private Map<String, List<String>> knowledgeBase;
    private Map<String, String> contextMemory;
    private List<String> conversationHistory;
    
    public WasteBotEngine(Context context) {
        this.context = context;
        this.sessionManager = new SessionManager(context);
        this.knowledgeBase = new HashMap<>();
        this.contextMemory = new HashMap<>();
        this.conversationHistory = new ArrayList<>();
        initializeKnowledgeBase();
    }
    
    private void initializeKnowledgeBase() {
        // Pickup & Scheduling
        knowledgeBase.put("pickup_schedule", Arrays.asList(
            "To schedule a waste pickup:\n1. Go to the Pickup tab\n2. Select your waste type\n3. Choose your street\n4. Pick an available time slot\n5. Confirm your request\n\nYou'll earn 5 points for each completed pickup! 🚛",
            "Our pickup service operates Monday to Saturday, 7 AM to 6 PM. We cover all major areas in the city.",
            "You can schedule pickups up to 7 days in advance. Emergency pickups are available for an additional fee."
        ));
        
        // Points & Rewards
        knowledgeBase.put("points_rewards", Arrays.asList(
            "Our points system works like this:\n• Earn 5 points per completed pickup\n• Earn 2 points per kg of waste recycled\n• Redeem points for eco-friendly products\n• Check your balance in the Rewards tab\n• 100 points = Premium rewards! 🎁",
            "You can redeem points for:\n• Eco-friendly products (50-200 points)\n• Discount vouchers (25-100 points)\n• Free pickup services (30 points)\n• Plant seedlings (15 points)",
            "Bonus points events happen monthly! Follow our notifications to earn double points."
        ));
        
        // Products & Marketplace
        knowledgeBase.put("products_shop", Arrays.asList(
            "Browse our marketplace for amazing recycled products!\n• Eco-friendly bags (₦2,000)\n• Recycled furniture (₦15,000-50,000)\n• Solar chargers (₦8,000)\n• Compost bins (₦5,000)\n• And much more!\n\nYou can pay with mobile money or use your points. 🛒",
            "All our products are made from recycled materials by local artisans. Every purchase supports the circular economy!",
            "New products are added weekly. Enable notifications to be the first to know about new arrivals!"
        ));
        
        // Recycling & Waste Tips
        knowledgeBase.put("recycling_tips", Arrays.asList(
            "Great recycling tips:\n• Separate plastic, paper, and glass\n• Clean containers before disposal\n• Remove labels when possible\n• Electronics need special handling\n• Organic waste can be composted\n\nEvery small action helps our planet! 🌱",
            "Waste separation guide:\n🟢 Green bin: Organic waste\n🔵 Blue bin: Paper & cardboard\n🟡 Yellow bin: Plastic & metal\n⚪ White bin: Glass\n🔴 Red bin: Hazardous materials",
            "Did you know? Recycling 1 ton of paper saves 17 trees, 7,000 gallons of water, and 3.3 cubic yards of landfill space!"
        ));
        
        // App Usage Help
        knowledgeBase.put("app_help", Arrays.asList(
            "I'm here to help! You can:\n• Book waste pickups (Pickup tab)\n• Shop recycled products (Marketplace tab)\n• Track your orders (Orders section)\n• Earn and redeem points (Rewards tab)\n• View your recycling stats (Profile)\n\nWhat would you like to know more about? 📱",
            "Navigation tips:\n• Use the bottom tabs to switch between sections\n• Pull down to refresh any screen\n• Tap your profile picture for account settings\n• Use the search function in Marketplace",
            "Having trouble? Try:\n• Checking your internet connection\n• Updating the app\n• Restarting the app\n• Contacting support through the Profile tab"
        ));
        
        // Greetings & Social
        knowledgeBase.put("greetings", Arrays.asList(
            "Hello there! 👋 Welcome to WasteReborn! I'm WasteBot, your eco-friendly assistant. I'm excited to help you on your green journey!",
            "Hi! Great to see you again! How can I help you make a positive environmental impact today? 🌍",
            "Welcome back to WasteReborn! Ready to turn waste into something wonderful? Let's get started! ♻️"
        ));
        
        // Thanks & Appreciation
        knowledgeBase.put("thanks", Arrays.asList(
            "You're very welcome! 😊 Thank you for choosing WasteReborn and helping make our planet greener!",
            "My pleasure! Every action you take helps build a more sustainable future. Keep up the great work! 🌟",
            "Happy to help! Together, we're making a real difference for our environment. What else can I assist you with?"
        ));
        
        // Environmental Facts
        knowledgeBase.put("environment_facts", Arrays.asList(
            "🌍 Environmental fact: The average person generates 4.5 pounds of waste per day. By recycling with WasteReborn, you're helping reduce this impact!",
            "♻️ Did you know? Recycling aluminum cans uses 95% less energy than making new ones from raw materials!",
            "🌱 Fun fact: Composting organic waste can reduce your household waste by up to 30%!"
        ));
    }
    
    public String generateResponse(String userMessage) {
        String normalizedMessage = userMessage.toLowerCase().trim();
        conversationHistory.add(userMessage);
        
        // Context-aware responses
        String response = analyzeAndRespond(normalizedMessage);
        
        // Add personality and context
        response = addPersonalityToResponse(response, normalizedMessage);
        
        return response;
    }
    
    private String analyzeAndRespond(String message) {
        // Intent detection with improved pattern matching
        if (matchesPattern(message, "pickup|schedule|book|collect")) {
            return getRandomResponse("pickup_schedule");
        } else if (matchesPattern(message, "points|reward|redeem|earn")) {
            return getRandomResponse("points_rewards");
        } else if (matchesPattern(message, "product|shop|buy|marketplace|purchase")) {
            return getRandomResponse("products_shop");
        } else if (matchesPattern(message, "recycle|waste|tip|separate|bin")) {
            return getRandomResponse("recycling_tips");
        } else if (matchesPattern(message, "help|how|use|navigate|guide")) {
            return getRandomResponse("app_help");
        } else if (matchesPattern(message, "hello|hi|hey|good morning|good afternoon")) {
            return getRandomResponse("greetings");
        } else if (matchesPattern(message, "thank|thanks|appreciate")) {
            return getRandomResponse("thanks");
        } else if (matchesPattern(message, "fact|environment|planet|earth|green")) {
            return getRandomResponse("environment_facts");
        } else if (matchesPattern(message, "price|cost|money|payment")) {
            return "Our services are very affordable:\n• Basic pickup: Free with points\n• Premium pickup: ₦500-1,500\n• Products: Starting from ₦500\n• Payment: Mobile money, bank transfer, or points\n\nCheck the app for current pricing! 💰";
        } else if (matchesPattern(message, "time|when|hours|available")) {
            return "Our service hours:\n🕐 Pickup: Monday-Saturday, 7 AM - 6 PM\n🛒 Marketplace: 24/7 online shopping\n📞 Support: Monday-Friday, 8 AM - 8 PM\n\nWe're here when you need us! ⏰";
        } else if (matchesPattern(message, "location|area|where|address")) {
            return "We currently serve:\n📍 Major cities and suburbs\n📍 University areas\n📍 Business districts\n📍 Residential neighborhoods\n\nEnter your address in the app to check availability! 📍";
        } else {
            return generateSmartFallback(message);
        }
    }
    
    private boolean matchesPattern(String message, String patterns) {
        String[] patternArray = patterns.split("\\|");
        for (String pattern : patternArray) {
            if (message.contains(pattern.trim())) {
                return true;
            }
        }
        return false;
    }
    
    private String getRandomResponse(String category) {
        List<String> responses = knowledgeBase.get(category);
        if (responses != null && !responses.isEmpty()) {
            Random random = new Random();
            return responses.get(random.nextInt(responses.size()));
        }
        return "I'm here to help! What would you like to know about WasteReborn?";
    }
    
    private String addPersonalityToResponse(String response, String userMessage) {
        // Add user's name if available
        String userName = sessionManager.getUserEmail();
        if (userName != null && !userName.isEmpty() && !response.contains("Hello")) {
            // Personalize occasionally
            Random random = new Random();
            if (random.nextInt(4) == 0) { // 25% chance
                String name = userName.split("@")[0]; // Use part before @
                response = "Hey " + name + "! " + response;
            }
        }
        
        // Add encouraging messages
        if (userMessage.contains("recycle") || userMessage.contains("environment")) {
            response += "\n\n🌟 You're making a real difference for our planet!";
        }
        
        return response;
    }
    
    private String generateSmartFallback(String message) {
        // Analyze the message for keywords and provide helpful suggestions
        String[] keywords = message.split("\\s+");
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("I understand you're asking about: \"").append(message).append("\"\n\n");
        suggestions.append("I'm still learning! Here's what I can help with:\n");
        suggestions.append("🚛 Waste pickup scheduling\n");
        suggestions.append("🎁 Points and rewards system\n");
        suggestions.append("🛒 Recycled product marketplace\n");
        suggestions.append("♻️ Recycling tips and guides\n");
        suggestions.append("📱 App usage help\n\n");
        suggestions.append("Try asking: 'How do I schedule a pickup?' or 'Tell me about points!'");
        
        return suggestions.toString();
    }
    
    public void clearContext() {
        contextMemory.clear();
        conversationHistory.clear();
    }
}
