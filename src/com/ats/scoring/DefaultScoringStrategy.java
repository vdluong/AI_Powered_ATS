
package com.ats.scoring;

import com.ats.entity.Candidate;
import com.ats.entity.JobProfile;
import com.ats.entity.Project;
import java.util.List;

public class DefaultScoringStrategy implements ScoringStrategy {

    @Override
    public double calculateScore(Candidate candidate, JobProfile jobProfile) {
        if (candidate == null || jobProfile == null) {
            return 0.0;
        }

        double skillScore = scoreSkills(candidate, jobProfile);
        double projectScore = scoreProjects(candidate, jobProfile);

        // Trong phien ban hien tai, AI chi duoc tinh khi nha tuyen dung yeu cau
        double aiScore = scoreAI(candidate, jobProfile);

        // Trong so: ky nang 60%, du an 25%, AI 15%
        double total = skillScore * 0.60
                     + projectScore * 0.25
                     + aiScore * 0.15;

        return Math.round(total * 100.0) / 100.0;
    }

    private double scoreSkills(Candidate candidate, JobProfile job) {
        List<String> required = job.getRequiredSkills();
        List<String> skills = candidate.getTechnicalSkills();

        if (required == null || required.isEmpty()) {
            return 100.0;
        }

        if (skills == null || skills.isEmpty()) {
            return 0.0;
        }

        int matched = 0;

        for (int i = 0; i < required.size(); i++) {
            String r = required.get(i);

            if (r == null) {
                continue;
            }

            for (int j = 0; j < skills.size(); j++) {
                String s = skills.get(j);

                if (s != null && r.trim().equalsIgnoreCase(s.trim())) {
                    matched++;
                    break;
                }
            }
        }

        return matched * 100.0 / required.size();
    }

    private double scoreProjects(Candidate candidate, JobProfile job) {
        List<Project> projects = candidate.getProjects();
        List<String> required = job.getRequiredSkills();

        if (projects == null || projects.isEmpty()) {
            return 0.0;
        }

        if (required == null || required.isEmpty()) {
            return 100.0;
        }

        int matched = 0;
        int total = 0;

        for (int i = 0; i < required.size(); i++) {
            String r = required.get(i);

            if (r == null) {
                continue;
            }

            total++;
            boolean found = false;

            for (int j = 0; j < projects.size(); j++) {
                Project project = projects.get(j);

                if (project == null || project.getTechnologies() == null) {
                    continue;
                }

                List<String> technologies = project.getTechnologies();

                for (int k = 0; k < technologies.size(); k++) {
                    String technology = technologies.get(k);

                    if (technology != null
                            && r.trim().equalsIgnoreCase(technology.trim())) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    break;
                }
            }

            if (found) {
                matched++;
            }
        }

        if (total == 0) {
            return 0.0;
        }

        return matched * 100.0 / total;
    }

    private double scoreAI(Candidate candidate, JobProfile job) {
        if (!job.isRequireAiKnowledge()) {
            return 100.0;
        }

        List<String> skills = candidate.getTechnicalSkills();

        if (skills != null) {
            for (int i = 0; i < skills.size(); i++) {
                if (containsAI(skills.get(i))) {
                    return 100.0;
                }
            }
        }

        List<Project> projects = candidate.getProjects();

        if (projects != null) {
            for (int i = 0; i < projects.size(); i++) {
                Project project = projects.get(i);

                if (project == null) {
                    continue;
                }

                List<String> technologies = project.getTechnologies();

                if (technologies != null) {
                    for (int j = 0; j < technologies.size(); j++) {
                        if (containsAI(technologies.get(j))) {
                            return 100.0;
                        }
                    }
                }

                if (containsAI(project.getDescription())) {
                    return 100.0;
                }
            }
        }

        return 0.0;
    }

    private boolean containsAI(String text) {
        if (text == null) {
            return false;
        }

        String s = text.toLowerCase();

        return s.contains("machine learning")
                || s.contains("deep learning")
                || s.contains("artificial intelligence")
                || s.contains("trí tuệ nhân tạo")
                || s.contains("học máy")
                || s.equals("ai")
                || s.startsWith("ai ")
                || s.endsWith(" ai")
                || s.contains(" ai ");
    }
}
