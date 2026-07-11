package com.enterprise.docai.infrastructure.adapter.ai;

import com.enterprise.docai.domain.port.AIAgentPort;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringAiChatModelAdapter implements AIAgentPort {

    private final ChatModel chatModel;

    public SpringAiChatModelAdapter(@Autowired(required = false) ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String generateAnswer(String promptText) {
        if (chatModel != null) {
            try {
                return chatModel.call(promptText);
            } catch (Exception e) {
                // fall back to mock
            }
        }
        return "Simulated Answer based on the retrieved excerpts. The system is operating in fallback/offline simulation mode.";
    }
}
