let stompClient = null;

function connect() {
    // si dejan la ip vacía probamos con localhost
    const serverIp = document.getElementById('serverIp').value.trim() || 'localhost';
    const socket = new SockJS('http://' + serverIp + ':8080/ws');

    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        setConnected(true);

        // nos colgamos de la sala pública, cada mensaje que llega se pinta
        stompClient.subscribe('/topic/public', function (payload) {
            showMessage(JSON.parse(payload.body));
        });
    }, function (error) {
        console.error('Error de conexion: ', error);
        setConnected(false);
    });
}

function disconnect() {
    // cerramos la sesión de verdad, antes solo cambiaba el cartelito
    if (stompClient !== null) {
        stompClient.disconnect(function () {
            console.log("Disconnected");
        });
        stompClient = null;
    }
    setConnected(false);
}

function setConnected(connected) {
    const status = document.getElementById('status');
    if (connected) {
        status.className = 'connected';
        status.textContent = 'Conectado';
    } else {
        status.className = 'disconnected';
        status.textContent = 'Desconectado';
    }
    document.getElementById('message').disabled = !connected;
}

function sendMessage() {
    // si no pusieron nombre sale como Anonimo
    const username = document.getElementById('username').value.trim() || 'Anonimo';
    const content = document.getElementById('message').value;

    if (content.trim() === '') return;

    if (stompClient === null || !stompClient.connected) {
        console.warn('No hay conexion con el servidor');
        return;
    }

    // va a /app/chat y el ChatController lo reparte a todos
    stompClient.send('/app/chat', {}, JSON.stringify({
        usuario: username,
        contenido: content
    }));

    document.getElementById('message').value = '';
}

function showMessage(message) {
    const messages = document.getElementById('messages');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message';

    // nada de innerHTML, si alguien manda un <script> se ejecutaba en todos los navegadores
    const user = document.createElement('strong');
    user.textContent = message.usuario + ':';
    messageDiv.appendChild(user);
    messageDiv.appendChild(document.createTextNode(' ' + message.contenido));

    messages.appendChild(messageDiv);
    messages.scrollTop = messages.scrollHeight;
}

// Allow sending message with Enter key
document.getElementById('message').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        sendMessage();
    }
});

// arrancamos desconectados, así el input queda bloqueado hasta conectar
setConnected(false);
