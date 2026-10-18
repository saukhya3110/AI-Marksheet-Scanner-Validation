
package com.marksheet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    private final MarksheetValidationService validationService;

    public ValidationController(
            MarksheetValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping("/marks")
    public ResponseEntity<Map<String, Object>> validate(
            @RequestBody ValidationRequest request) {

        if (request == null
                || request.marks() == null
                || request.maximumMarks() == null
                || request.passingMarks() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Marks, maximum marks and passing marks are required."));
        }

        Map<String, Object> result = validationService.validateMarks(
                request.marks(),
                request.maximumMarks(),
                request.passingMarks());

        return ResponseEntity.ok(result);
    }

    public record ValidationRequest(
            List<Integer> marks,
            Integer maximumMarks,
            Integer passingMarks) {}
}
