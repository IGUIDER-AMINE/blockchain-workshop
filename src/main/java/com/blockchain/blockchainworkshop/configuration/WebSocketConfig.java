package com.blockchain.blockchainworkshop.configuration;

import com.blockchain.blockchainworkshop.service.P2PService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final P2PService p2pService;

    public WebSocketConfig(P2PService p2pService) {
        this.p2pService = p2pService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(p2pService, "/ws").setAllowedOrigins("*");
    }
}