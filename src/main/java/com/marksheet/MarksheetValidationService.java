
package com.marksheet;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MarksheetValidationService {

    public Map<String, Object> validateMarks(
            List<Integer> marks, int maximumMarks, int passingMarks) {

        Map<String, Object> result = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();

        if (marks == null || marks.isEmpty()) {
            errors.add("At least one subject mark is required.");
        }

        if (maximumMarks <= 0) {
            errors.add("Maximum marks must be greater than zero.");
        }

        if (passingMarks < 0 || passingMarks > maximumMarks) {
            errors.add("Passing marks must be between zero and maximum marks.");
        }

        if (marks != null) {
            for (int i = 0; i < marks.size(); i++) {
                Integer mark = marks.get(i);

                if (mark == null || mark < 0 || mark > maximumMarks) {
                    errors.add("Invalid marks for subject " + (i + 1) + ".");
                }
            }
        }

        result.put("validInput", errors.isEmpty());
        result.put("errors", errors);

        if (!errors.isEmpty()) {
            return result;
        }

        int total = marks.stream().mapToInt(Integer::intValue).sum();
        int totalMaximum = marks.size() * maximumMarks;
        double percentage = total * 100.0 / totalMaximum;

        boolean passed = marks.stream()
                .allMatch(mark -> mark >= passingMarks);

        result.put("subjectCount", marks.size());
        result.put("totalMarks", total);
        result.put("maximumTotalMarks", totalMaximum);
        result.put("percentage", Math.round(percentage * 100.0) / 100.0);
        result.put("result", passed ? "PASS" : "FAIL");

        return result;
    }
}
