package com.softwareengineering.controllers;

import java.util.List;
import java.util.Map;

import com.softwareengineering.dto.DiagnosisBody;
import com.softwareengineering.services.DiagnosesService;
import com.softwareengineering.utils.AuthUtils;
import com.softwareengineering.utils.AuthUtils.UnauthorizedException;
import com.softwareengineering.utils.ValidationException;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class DiagnosesController {
    public static void init(Javalin app) {
        app.get("/diagnoses", DiagnosesController::getDiagnoses);
        app.post("/diagnosis", DiagnosesController::setDiagnosis);
    }

    public static void getDiagnoses(Context context) {
        try {
            int patientID = AuthUtils.validatePatientAndGetId(context);
            List<Map<String, Object>> diagnoses = DiagnosesService.getDiagnoses(patientID);
            context.json(diagnoses);
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }

    public static void setDiagnosis(Context context) {
        try {
            // Only doctors can set diagnoses
            AuthUtils.validateDoctorAndGetId(context);

            DiagnosisBody body = context.bodyAsClass(DiagnosisBody.class);
            body.validate();
            
            DiagnosesService.setDiagnosis(body.appointmentID, body.decease, body.details);
            context.status(201);
        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }
}
