package com.atypon.project.chain;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * Represents the proof of work system
 *
 * @author Osama Abuhamdan
 */
final class ProofOfWork {

    // noninstantiable
    private ProofOfWork() {
    }

    private static int _miningDifficulty;
    private static int nonce;

    /**
     * @return nonce calculated by proof of work system.
     */
    static int getNonce() {
        return nonce;
    }

    /**
     * Created a hash code with specific number of zeroes in the head of it
     * by increasing the nonce value then recalculate the hash till the number of zeroes
     * wanted is created
     *
     * @param data             to be hashed with nonce
     * @param miningDifficulty number of zeroes to be added in front of the hash
     * @return hash value with header of zeroes .
     */
    static String calculateProofOfWork(String data, int miningDifficulty) {
        _miningDifficulty = miningDifficulty;
        String difficultyString = DifficultyString();

        while (true) {
            String hashedData = DigestUtils.sha256Hex(data + nonce);
            if (hashedData.substring(0, miningDifficulty).equals(difficultyString)) {
                return hashedData;
            }

            nonce++;
        }
    }

    /**
     * @return zeroes asked by the mining difficulty in string form
     */
    private static String DifficultyString() {
        StringBuilder difficultyString = new StringBuilder();

        for (int i = 0; i < _miningDifficulty; i++) {
            difficultyString.append("0");
        }

        return difficultyString.toString();
    }
}