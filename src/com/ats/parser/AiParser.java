package com.ats.parser;
import com.ats.entity.Candidate;

public interface AiParser {
    // Đầu vào là chuỗi text thô, đầu ra là đối tượng Candidate đã được AI bóc tách
    Candidate parseToCandidate(String rawText);
}
