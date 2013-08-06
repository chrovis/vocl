var socket = new WebSocket("ws://localhost:8008");

socket.onmessage = function(event) {
    console.log(event.data);
};

function sendMessage(msg){
    try{
        socket.send(msg);
    } catch(exception){
        console.log(exception);
    }
}

socket.onopen = function(event) {
    (function() {
        var d = new Date();
        var name = "cli" + String(parseInt(d.getTime(), 10));

        sendMessage(name);
    })();
};

function hello() {
    sendMessage("hello");
}
