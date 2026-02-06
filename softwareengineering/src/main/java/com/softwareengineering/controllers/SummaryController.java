package com.softwareengineering.controllers;

import java.util.Map;

import com.softwareengineering.dto.SummaryBody;
import com.softwareengineering.services.SummaryService;
import com.softwareengineering.utils.ValidationException;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class SummaryController {
    public static void init(Javalin app) {
        app.post("/generate-summary", SummaryController::generateSummary);
    }

    private static void generateSummary(Context context) {
        try {
            SummaryBody body = context.bodyAsClass(SummaryBody.class);
            body.validate();

            Map<String, Object> result = SummaryService.generateSummary(body.prompt);
            context.status(200).json(result);

        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        } catch (IllegalStateException e) {
            context.status(500).json(Map.of("error", "Configuration error: " + e.getMessage()));
        } catch (Exception e) {
            context.status(500).json(Map.of("error", "Failed to generate summary: " + e.getMessage()));
        }
    }
}
