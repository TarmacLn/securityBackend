package com.softwareengineering.controllers;

import java.util.List;
import java.util.Map;

import com.softwareengineering.models.User;
import com.softwareengineering.services.PatientsService;
import com.softwareengineering.utils.AuthUtils;
import com.softwareengineering.utils.AuthUtils.UnauthorizedException;
import com.softwareengineering.utils.InputValidator;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class PatientsController {
    public static void init(Javalin app) {
        app.get("/patients", PatientsController::getPatients);
        app.get("/patients/{id}", PatientsController::getPatientByID);
    }

    private static void getPatients(Context context) {
        try {
            // Only doctors can access patient list
            AuthUtils.validateDoctorAndGetId(context);
            List<Map<String, Object>> patients = PatientsService.getPatients();
            context.json(patients);
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }

    private static void getPatientByID(Context context) {
        try {
            // Only doctors can access patient details
            AuthUtils.validateDoctorAndGetId(context);

            String patientIdParam = context.pathParam("id");
            if (patientIdParam == null || patientIdParam.isEmpty()) {
                context.status(400).json(Map.of("error", "Patient ID is required"));
                return;
            }
            
            try {
                int patientId = Integer.parseInt(patientIdParam);
                if (!InputValidator.isValidID(patientId)) {
                    context.status(400).json(Map.of("error", "Invalid patient ID"));
                    return;
                }
                
                User patientModel = PatientsService.getPatientById(patientId);
                if (patientModel != null) {
                    context.json(patientModel.toMap());
                } else {
                    context.status(404).json(Map.of("error", "Patient not found"));
                }
            } catch (NumberFormatException e) {
                context.status(400).json(Map.of("error", "Invalid patient ID format"));
            }
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }
}
