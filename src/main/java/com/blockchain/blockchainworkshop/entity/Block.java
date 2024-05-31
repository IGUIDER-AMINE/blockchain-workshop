package com.blockchain.blockchainworkshop.entity;

import com.blockchain.blockchainworkshop.helper.HashUtil;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class Block {

    private int index;
    private Instant timestamp;
    private String previousHash;
    private String currentHash;
    private List<Transaction> transactions;
    private int nonce;

    public Block(int index, String previousHash, List<Transaction> transactions, int nonce) {
        this.index = index;
        this.timestamp = Instant.now();
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.nonce = nonce;
        this.currentHash = calculateHash();
    }

    public void incrementNonce() {
        nonce++;
    }

    public String calculateHash() {
        String data = index + timestamp.toString() + previousHash + transactions.toString() + nonce;
        return HashUtil.calculateSHA256(data);
    }

    public boolean validateBlock(int difficulty, Block previousBlock) {
        String prefix = "0".repeat(difficulty);
        String calculatedHash = calculateHash();
        // Check if the calculated hash satisfies the difficulty requirement
        if (!calculatedHash.startsWith(prefix)) {
            return false;
        }
        // Check if the calculated hash matches the stored hash
        if (!calculatedHash.equals(currentHash)) {
            return false;
        }
        // Check if the block's index is correct
        if (index != previousBlock.getIndex() + 1) {
            return false;
        }
        // Check if the previous hash matches
        if (!previousHash.equals(previousBlock.getCurrentHash())) {
            return false;
        }
        // Check if the timestamp is valid (not in the future)
        if (timestamp.isAfter(Instant.now())) {
            return false;
        }
        return true;
    }
}