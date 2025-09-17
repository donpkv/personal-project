package com.careeros.service;

import com.careeros.entity.DigitalCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Blockchain Service for immutable certificate verification
 * This is a simplified implementation - in production, you would integrate with
 * actual blockchain networks like Ethereum, Hyperledger, or custom blockchain
 */
@Service
public class BlockchainService {

    private static final Logger logger = LoggerFactory.getLogger(BlockchainService.class);

    @Value("${app.blockchain.enabled:false}")
    private boolean blockchainEnabled;

    @Value("${app.blockchain.network:ethereum-testnet}")
    private String blockchainNetwork;

    // In-memory storage for demo purposes - replace with actual blockchain integration
    private final Map<String, BlockchainRecord> blockchainRecords = new HashMap<>();

    /**
     * Record certificate on blockchain for immutable verification
     */
    public String recordCertificate(DigitalCertificate certificate) {
        logger.info("Recording certificate {} on blockchain", certificate.getCertificateId());

        try {
            if (!blockchainEnabled) {
                logger.info("Blockchain integration disabled, storing locally");
                return recordLocally(certificate);
            }

            // In a real implementation, this would:
            // 1. Connect to blockchain network
            // 2. Create a transaction with certificate data
            // 3. Submit transaction to blockchain
            // 4. Wait for confirmation
            // 5. Return transaction hash

            String transactionHash = generateTransactionHash(certificate);
            
            BlockchainRecord record = new BlockchainRecord();
            record.setCertificateId(certificate.getCertificateId());
            record.setTransactionHash(transactionHash);
            record.setBlockNumber(generateBlockNumber());
            record.setTimestamp(LocalDateTime.now());
            record.setNetwork(blockchainNetwork);
            record.setDataHash(generateDataHash(certificate));

            blockchainRecords.put(certificate.getCertificateId(), record);

            logger.info("Certificate recorded on blockchain with transaction hash: {}", transactionHash);
            return transactionHash;

        } catch (Exception e) {
            logger.error("Error recording certificate on blockchain", e);
            // Fallback to local storage
            return recordLocally(certificate);
        }
    }

    /**
     * Verify certificate against blockchain record
     */
    public boolean verifyCertificate(String certificateId, String expectedHash) {
        logger.info("Verifying certificate {} against blockchain", certificateId);

        BlockchainRecord record = blockchainRecords.get(certificateId);
        if (record == null) {
            logger.warn("No blockchain record found for certificate {}", certificateId);
            return false;
        }

        boolean isValid = record.getDataHash().equals(expectedHash);
        logger.info("Certificate {} verification result: {}", certificateId, isValid);
        
        return isValid;
    }

    /**
     * Get blockchain record for certificate
     */
    public BlockchainRecord getBlockchainRecord(String certificateId) {
        return blockchainRecords.get(certificateId);
    }

    /**
     * Record certificate revocation on blockchain
     */
    public String recordRevocation(DigitalCertificate certificate) {
        logger.info("Recording certificate revocation {} on blockchain", certificate.getCertificateId());

        try {
            String revocationHash = generateRevocationHash(certificate);
            
            BlockchainRecord record = blockchainRecords.get(certificate.getCertificateId());
            if (record != null) {
                record.setRevoked(true);
                record.setRevokedAt(LocalDateTime.now());
                record.setRevocationHash(revocationHash);
            }

            return revocationHash;

        } catch (Exception e) {
            logger.error("Error recording revocation on blockchain", e);
            return null;
        }
    }

    private String recordLocally(DigitalCertificate certificate) {
        String localHash = "local_" + generateTransactionHash(certificate);
        
        BlockchainRecord record = new BlockchainRecord();
        record.setCertificateId(certificate.getCertificateId());
        record.setTransactionHash(localHash);
        record.setBlockNumber(0L); // Local storage
        record.setTimestamp(LocalDateTime.now());
        record.setNetwork("local");
        record.setDataHash(generateDataHash(certificate));

        blockchainRecords.put(certificate.getCertificateId(), record);
        
        return localHash;
    }

    private String generateTransactionHash(DigitalCertificate certificate) {
        try {
            String data = certificate.getCertificateId() + 
                         certificate.getRecipient().getId() + 
                         certificate.getSkillName() + 
                         certificate.getIssuedAt();
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return "0x" + hexString.toString();
        } catch (Exception e) {
            logger.error("Error generating transaction hash", e);
            return "0x" + System.currentTimeMillis();
        }
    }

    private String generateDataHash(DigitalCertificate certificate) {
        return generateTransactionHash(certificate);
    }

    private String generateRevocationHash(DigitalCertificate certificate) {
        return "revoke_" + generateTransactionHash(certificate);
    }

    private Long generateBlockNumber() {
        // Simulate block number - in real blockchain, this would be actual block number
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Blockchain Record data structure
     */
    public static class BlockchainRecord {
        private String certificateId;
        private String transactionHash;
        private Long blockNumber;
        private LocalDateTime timestamp;
        private String network;
        private String dataHash;
        private boolean revoked = false;
        private LocalDateTime revokedAt;
        private String revocationHash;

        // Getters and setters
        public String getCertificateId() { return certificateId; }
        public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

        public String getTransactionHash() { return transactionHash; }
        public void setTransactionHash(String transactionHash) { this.transactionHash = transactionHash; }

        public Long getBlockNumber() { return blockNumber; }
        public void setBlockNumber(Long blockNumber) { this.blockNumber = blockNumber; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public String getNetwork() { return network; }
        public void setNetwork(String network) { this.network = network; }

        public String getDataHash() { return dataHash; }
        public void setDataHash(String dataHash) { this.dataHash = dataHash; }

        public boolean isRevoked() { return revoked; }
        public void setRevoked(boolean revoked) { this.revoked = revoked; }

        public LocalDateTime getRevokedAt() { return revokedAt; }
        public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }

        public String getRevocationHash() { return revocationHash; }
        public void setRevocationHash(String revocationHash) { this.revocationHash = revocationHash; }
    }
}
