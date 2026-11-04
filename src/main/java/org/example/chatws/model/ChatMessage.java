package org.example.chatws.model;

// lo que viaja por el socket, los nombres tienen que coincidir con el json que arma script.js
public record ChatMessage(String usuario, String contenido) {
}
