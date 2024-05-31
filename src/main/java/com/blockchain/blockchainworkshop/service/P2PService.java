package com.blockchain.blockchainworkshop.service;

import com.blockchain.blockchainworkshop.entity.Block;
import com.blockchain.blockchainworkshop.entity.Blockchain;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class P2PService extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final Blockchain blockchain;

    public P2PService(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(blockchain.getChain())));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Block[] receivedBlocks = mapper.readValue(message.getPayload(), Block[].class);
        synchronizeBlockchain(receivedBlocks);
    }

    private void synchronizeBlockchain(Block[] receivedBlocks) {
        // Implement synchronization logic here
    }

    public void broadcastNewBlock(Block block) {
        sessions.forEach(session -> {
            try {
                session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(block)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}