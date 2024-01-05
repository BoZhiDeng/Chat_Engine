'use strict';

// DOM elements
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var passwordForm = document.querySelector('#passwordForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

// WebSocket connection
var stompClient = null;
var username = null;

// Avatar colors
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0',
    '#6A1B9A', '#D500F9', '#2962FF', '#009688',
    '#827717', '#F57F17', '#FF1744', '#00E676',
    '#6200EA', '#C51162', '#FF3D00', '#64DD17',
    '#AA00FF', '#00B8D4', '#9E9D24', '#FF6D00'
];

// Connect to WebSocket
function connect() {
    username = document.querySelector('#username').getAttribute('data-username');
    if (username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
}

// Connection established
function onConnected() {
    console.log('onConnected Debug report function called');

    // Subscribe to the public topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Subscribe to a user-specific topic to receive chat history
    stompClient.subscribe('/topic/history', onHistoryReceived);

    // Sent username to the server
    stompClient.send("/app/chat.addUser", {}, JSON.stringify({ sender: username, type: 'JOIN' }));

    // Request the chat history from the server
    setTimeout(function () {
        //console.log('Requesting chat history...');
        stompClient.send("/app/chat.loadHistory", {}, {});
    }, 1000
    );

    // Hide the connecting message and set up the user's avatar
    connectingElement.classList.add('hidden');

    var avatarElement = document.querySelector('.user-avatar') || document.createElement('div');
    if (!avatarElement.parentNode) {
        document.querySelector('.chat-header').prepend(avatarElement);
    }
    avatarElement.className = 'user-avatar';
    avatarElement.textContent = username[0];
    avatarElement.style['background-color'] = getAvatarColor(username);
    avatarElement.onclick = function () {
        showUpdateOptions();
    };
}

// Connection error
function onError(error) {
    connectingElement.textContent = 'Could not connect to the server.';
    connectingElement.style.color = 'red';
}

// Display all history messages from the sever
function onHistoryReceived(payload) {
    var messages = JSON.parse(payload.body);
    //console.log('onHistoryReceived Debug report function called');
    //console.log('Received chat history:', messages);
    if (Array.isArray(messages)) {
        messages.forEach(message => {
            displayMessage(message);
        });
    } else {
        console.log('Error in onHistoryReceived, object "messages" should be an array:', messages);
    }
}

//Display input message
function displayMessage(message) {
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

// Send message
function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

// Message received
function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');
    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');
        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);
        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }
    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

// Get avatar color
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

// Show update options
function showUpdateOptions() {
    var avatarElement = document.querySelector('.user-avatar');
    var avatarRect = avatarElement.getBoundingClientRect();
    var updateOptionsMenu = document.getElementById('update-options-menu');
    updateOptionsMenu.style.display = 'block';
    updateOptionsMenu.style.left = avatarRect.left + 'px';
    updateOptionsMenu.style.top = (avatarRect.top + avatarRect.height) + 'px';
}

// Hide update options
function hideUpdateOptions() {
    var updateOptionsMenu = document.getElementById('update-options-menu');
    updateOptionsMenu.style.display = 'none';
}

// Show page
function showPage(pageId) {
    var pages = ['username-page', 'password-page', 'chat-page'];
    pages.forEach(function (page) {
        document.getElementById(page).classList.add('hidden');
    });
    document.getElementById(pageId).classList.remove('hidden');
}

// Close page
function closePage() {
    showPage('chat-page');
    location.reload();
}

// Handle click outside user avatar to hide options menu
document.onclick = function (event) {
    if (event.target.id !== 'user-avatar') {
        hideUpdateOptions();
    }
};

// Update username option click handler
document.getElementById('update-username-option').onclick = function () {
    showPage('username-page');
    hideUpdateOptions();
};

// Update password option click handler
document.getElementById('update-password-option').onclick = function () {
    showPage('password-page');
    hideUpdateOptions();
};

// Handle username form submission.
document.getElementById('usernameForm').onsubmit = function (event) {
    event.preventDefault(); // Prevent form from submitting the default way.

    var formData = new FormData(event.target); // Gather form data for submission.
    var username = formData.get('username'); // Retrieve the username from the form data.

    // Check if username meets length requirements.
    if (username.length < 3 || username.length > 12) {
        alert('Username must be 3-12 characters long.');
        return false; // Stop form submission if check fails.
    }

    // Append user's email to formData before sending.
    var email = document.getElementById('email').getAttribute('data-email');
    formData.append('email', email);

    // Submit the username form data.
    submitUsernameForm(formData);
};

// Function to submit username update request.
function submitUsernameForm(formData) {
    // Send update request to the server.
    fetch('/updateUsername', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(formData)
    })
        .then(response => {
            if (response.ok) {
                console.log('Username updated successfully');
                location.reload();
            } else if (response.status === 409) {
                // Handle username already exists
                return response.text().then(text => Promise.reject(text));
            } else {
                // Handle other errors generically
                return response.text().then(text => Promise.reject(text));
            }
        })
        .catch(errorMessage => {
            console.error('Error:', errorMessage);
            alert(errorMessage);
        });
}

// Handle password form submission.
document.getElementById('passwordForm').onsubmit = function (event) {
    event.preventDefault(); // Prevent form from submitting the default way.

    var formData = new FormData(event.target); // Gather form data for submission.
    var password = formData.get('password'); // Retrieve the password from the form data.

    // Check if password meets length requirements.
    if (password.length < 8 || password.length > 32) {
        alert('Password must be 8-32 characters long.');
        return false; // Stop form submission if check fails.
    }

    // Append user's email to formData before sending.
    var email = document.getElementById('email').getAttribute('data-email');
    formData.append('email', email);

    // Submit the password form data.
    submitPasswordForm(formData);
};

// Function to submit password update request.
function submitPasswordForm(formData) {
    // Send update request to the server.
    fetch('/updatePassword', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(formData)
    })
        .then(response => {
            if (response.ok) {
                console.log('Username updated successfully');
                location.reload();
            } else {
                return response.text().then(text => Promise.reject(text));
            }
        })
        .catch(error => {
            console.error('Error:', errorMessage);
            alert(errorMessage);
        });
}

// WebSocket connect event listener
usernameForm.addEventListener('submit', connect, true);

// Message send event listener
messageForm.addEventListener('submit', sendMessage, true);

// DOMContentLoaded event listener
document.addEventListener('DOMContentLoaded', function (event) {
    connect();
});
