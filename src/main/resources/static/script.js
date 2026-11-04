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
    const username = document.getElementById('username').value;
    const content = document.getElementById('message').value;
    
    if (content.trim() === '') return;
    
     // Conexion
    
    document.getElementById('message').value = '';
}

function showMessage(message) {
    const messages = document.getElementById('messages');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message';
    messageDiv.innerHTML = '<strong>' + message.usuario + ':</strong> ' + message.contenido;
    messages.appendChild(messageDiv);
    messages.scrollTop = messages.scrollHeight;
}

// Allow sending message with Enter key
document.getElementById('message').addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        sendMessage();
    }
});
