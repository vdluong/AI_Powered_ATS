package com.ats.facade;

import com.ats.entity.Candidate;
import com.ats.entity.JobProfile;
import com.ats.parser.AiParser;
import com.ats.parser.TextExtractor;
import com.ats.scoring.ScoringStrategy;
import java.io.File;
import java.util.List;

public class AtsFacade {
    private TextExtractor textExtractor;
    private AiParser aiParser;
    private List<ScoringStrategy> scoringStrategies;

    // Inject các module thực tế vào đây
    public AtsFacade(TextExtractor textExtractor, AiParser aiParser, List<ScoringStrategy> scoringStrategies) {
        this.textExtractor = textExtractor;
        this.aiParser = aiParser;
        this.scoringStrategies = scoringStrategies;
    }

    public double processCv(File cvFile, JobProfile jobProfile) {
        // Bước 1: Rút trích văn bản
        String rawText = textExtractor.extractText(cvFile);

        // Bước 2: Dùng AI bóc tách đối tượng
        Candidate candidate = aiParser.parseToCandidate(rawText);

        // Bước 3: Chấm điểm
        double totalScore = 0;
        for (ScoringStrategy strategy : scoringStrategies) {
            totalScore += strategy.calculateScore(candidate, jobProfile);
        }

        return totalScore;
    }
}