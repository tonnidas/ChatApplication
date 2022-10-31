let ws;

function connect() {
    ws = new WebSocket('ws://localhost:8080/chat');

    ws.onmessage = function (evt) {
        console.log('received message: ' + evt.data);
        processMessage(JSON.parse(evt.data));
    }

    ws.onopen = function (evt) {
        console.log("Websocket connected");
        sendName()
    }

    $("#user").prop("disabled", true);
    $("#send").prop("disabled", false);
    $("#connect").prop("disabled", true);
    $("#disconnect").prop("disabled", false);
    $("#online").prop("disabled", true);
    $("#dnd").prop("disabled", false);

    console.log("Client initialized");
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }

    $("#user").prop("disabled", false);
    $("#send").prop("disabled", true);
    $("#connect").prop("disabled", false);
    $("#disconnect").prop("disabled", true);
    $("#online").prop("disabled", true);
    $("#dnd").prop("disabled", true);

    console.log("Websocket disconnected");
}

function setOnline() {
    let data = JSON.stringify({
        'type': 2,
        'sender': $("#user").val()
    })
    ws.send(data);
    $("#online").prop("disabled", true);
    $("#dnd").prop("disabled", false);
}

function setDND() {
    let data = JSON.stringify({
        'type': 3,
        'sender': $("#user").val()
    })
    ws.send(data);
    $("#online").prop("disabled", false);
    $("#dnd").prop("disabled", true);
}

function sendName() {
    let data = JSON.stringify({
        'type': 1,
        'sender': $("#user").val()
    })
    ws.send(data);
}

function sendMessage() {
    let data = JSON.stringify({
        'type': 0,
        'sender': $("#user").val(),
        'content': $("#message").val()
    })
    ws.send(data);
    $("#message").val("");
}

function processMessage(message) {
    if (message.type === 0) {
        $("#chatBody").append("<tr><td>" + message.sender + ": " + message.content + "</td></tr>");
    } else if (message.type === 4) {
        $("#clientsBody").children().remove();
        for (let i = 0; i < message.clients.length; i++) {
            $("#clientsBody").append("<tr><td>" + getClientStatus(message.clients[i]) + "</td></tr>");
        }
    }
}

function getClientStatus(client) {
    if (client.isOnline === true) return client.name + ": Online";
    else return client.name + ": Do Not Disturb"
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#online").click(function () {
        setOnline();
    });
    $("#dnd").click(function () {
        setDND();
    });
    $("#send").click(function () {
        sendMessage();
    });
});
