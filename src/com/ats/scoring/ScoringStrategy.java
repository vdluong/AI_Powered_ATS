package com.ats.scoring;
import com.ats.entity.Candidate;
import com.ats.entity.JobProfile;

public interface ScoringStrategy {
    // Thuật toán so sánh ứng viên với mô tả công việc, trả về điểm số
    double calculateScore(Candidate candidate, JobProfile jobProfile);
}