import React, { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const Chat = ({ token, username }) => {
    const [messages, setMessages] = useState([]);
    const [receiver, setReceiver] = useState('');
    const [content, setContent] = useState('');
    const stompRef = useRef(null);

    useEffect(() => {
        const socket = new SockJS(`http://localhost:8080/ws?token=${token}`);
        const stompClient = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`
            },
            onConnect: () => {
                console.log("Connected via WebSocket");
                stompClient.subscribe('/topic/messages', msg => {
                    const parsed = JSON.parse(msg.body);
                    setMessages(prev => [...prev, parsed]);
                });
            },
            onStompError: frame => {
                console.error("WebSocket error", frame);
            }
        });

        stompClient.activate();
        stompRef.current = stompClient;

        return () => stompClient.deactivate();
    }, [token]);

    const send = () => {
        if (!content.trim()) {
            alert('Message content cannot be empty.');
            return;
        }

        if (stompRef.current && stompRef.current.connected) {
            const msg = {
                sender: username,
                receiver,
                content
            };
            stompRef.current.publish({ destination: "/app/send", body: JSON.stringify(msg) });
            setContent('');
        } else {
            console.error('No WebSocket connection. Message could not be sent.');
            alert('WebSocket connection could not be established. Please try again.');
        }
    };

    return (
        <div>
            <h2>WebSocket Chat</h2>
            <input placeholder="Receiver" value={receiver} onChange={e => setReceiver(e.target.value)} />
            <input placeholder="Message" value={content} onChange={e => setContent(e.target.value)} />
            <button onClick={send}>Send</button>

            <ul>
                {messages.map((m, idx) => (
                    <li key={idx}>
                        <strong>{m.sender} → {m.receiver}:</strong> {m.content}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default Chat;