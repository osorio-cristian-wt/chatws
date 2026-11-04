package org.example.chatws.controller;

import org.example.chatws.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    // llega por /app/chat y lo reboto tal cual a todos los que escuchan /topic/public
    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public ChatMessage enviarMensaje(ChatMessage mensaje) {
        return mensaje;
    }
}
