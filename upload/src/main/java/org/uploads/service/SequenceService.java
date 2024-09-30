package org.uploads.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SequenceService {

    private final JdbcTemplate jdbcTemplate;

    public SequenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generateFormattedSequenceValue(String sequenceName, String prefix, int length) {
        // Get the next value from the sequence
        String sql = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";
        Integer nextVal = jdbcTemplate.queryForObject(sql, Integer.class);

        if (nextVal == null) {
            throw new RuntimeException("Failed to get the next value from sequence: " + sequenceName);
        }

        // Format the sequence value with the prefix and pad with zeros to ensure a total length of 10
        String formattedValue = formatSequenceValue(nextVal, prefix, length);

        return formattedValue;
    }

    private String formatSequenceValue(int nextVal, String prefix, int length) {
        // Concatenate prefix and sequence value
        String result = prefix + nextVal;

        // Calculate the number of zeros needed to pad the total length to 10
        int zerosNeeded = length - result.length();

        if (zerosNeeded > 0) {
            // Pad with zeros between the prefix and the sequence number
            StringBuilder paddedResult = new StringBuilder(prefix);
            for (int i = 0; i < zerosNeeded; i++) {
                paddedResult.append('0');
            }
            paddedResult.append(nextVal);
            result = paddedResult.toString();
        }

        return result;
    }
}

