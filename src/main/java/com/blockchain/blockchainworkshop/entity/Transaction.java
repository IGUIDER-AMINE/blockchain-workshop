package com.blockchain.blockchainworkshop.entity;

import lombok.Getter;
import lombok.Setter;

import java.security.InvalidKeyException;
import java.security.*;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

@Getter
@Setter
public class Transaction {
    private final String sender;
    private final String recipient;
    private final double amount;
    private String signature;
    public Transaction(String sender, String recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.signature = "";
    }
    @Override
    public String toString() {
        return "Transaction{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", amount=" + amount +
                ", signature='" + signature + '\'' +
                '}';
    }
    public boolean verifyTransaction() throws Exception {
        PublicKey publicKey = Wallet.getPublicKeyFromAddress(sender);
        return Wallet.verifyTransaction(this, publicKey);
    }
}