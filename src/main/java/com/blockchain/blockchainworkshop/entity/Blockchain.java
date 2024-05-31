package com.blockchain.blockchainworkshop.entity;

import lombok.Getter;
import lombok.Setter;

import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Blockchain {
    private List<Block> chain;
    private TransactionPool transactionPool;
    private int difficulty;
    private final int adjustmentInterval;
    public Blockchain(int difficulty, int adjustmentInterval) {
        this.chain = new ArrayList<>();
        this.transactionPool = new TransactionPool();
        this.difficulty = difficulty;
        this.adjustmentInterval = adjustmentInterval;
        Block genesisBlock = createGenesisBlock();
        chain.add(genesisBlock);
    }
    private Block createGenesisBlock() {
        List<Transaction> transactions = new ArrayList<>();
        return new Block(0, "0", transactions, 0);
    }
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }
    public Block addBlock(Block block) {
        if (isValidBlock(block)) {
            chain.add(block);
            transactionPool.removeTransactions(block.getTransactions());
            adjustDifficulty();
            return block;
        }
        throw new InvalidParameterException("Invalid block");
    }

    public boolean isValidBlock(Block block) {
        Block previousBlock = getLatestBlock();

        if (block.getIndex() != previousBlock.getIndex() + 1) {
            return false;
        }

        if (!block.getPreviousHash().equals(previousBlock.getCurrentHash())) {
            return false;
        }

        return block.getCurrentHash().startsWith(getDifficultyPrefix(difficulty));
    }

    public Block mineBlock() {
        Block newBlock = new Block(
                chain.size(),
                getLatestBlock().getCurrentHash(),
                transactionPool.getPendingTransactions(),
                0
        );

        mineBlock(newBlock, difficulty);
        return addBlock(newBlock);
    }

    public void mineBlock(Block block, int difficulty) {
        String prefix = getDifficultyPrefix(difficulty);
        String hash;
        do {
            block.incrementNonce();
            hash = block.calculateHash();
        } while (!hash.startsWith(prefix));
        block.setCurrentHash(hash);
    }

    private String getDifficultyPrefix(int difficulty) {
        return "0".repeat(difficulty);
    }

    public boolean validateChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);

            if (!currentBlock.validateBlock(difficulty, previousBlock)) {
                return false;
            }
        }
        return true;
    }

    public void addTransaction(Transaction transaction) {
        transactionPool.addTransaction(transaction);
    }

    public Block getBlockByIndex(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new InvalidParameterException("Block index out of bounds");
        }
        return chain.get(index);
    }

    private void adjustDifficulty() {
        if (chain.size() % adjustmentInterval == 0 && chain.size() > 0) {
            Block lastAdjustedBlock = chain.get(chain.size() - adjustmentInterval);
            Block latestBlock = getLatestBlock();
            long timeExpected = adjustmentInterval * 10 * 60;
            long timeTaken = Duration.between(lastAdjustedBlock.getTimestamp(), latestBlock.getTimestamp()).getSeconds();

            if (timeTaken < timeExpected / 2) {
                difficulty++;
            } else if (timeTaken > timeExpected * 2) {
                difficulty--;
            }
        }
    }
}