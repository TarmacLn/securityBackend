package com.softwareengineering.controllers;

import java.util.List;
import java.util.Map;

import com.softwareengineering.dto.RatingBody;
import com.softwareengineering.models.Rating;
import com.softwareengineering.models.enums.UserTypeEnum;
import com.softwareengineering.services.RatingsService;
import com.softwareengineering.utils.AuthUtils;
import com.softwareengineering.utils.AuthUtils.UnauthorizedException;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class RatingsController {
    public static void init(Javalin app) {
        app.get("/ratings", RatingsController::getRatings);
        app.get("/ratings/{appointmentID}", RatingsController::getAppointmentRating);
        app.put("/ratings/{appointmentID}", RatingsController::updateRating);
        app.post("/set-rating", RatingsController::setRating);
    }

    private static void getRatings(Context context) {
        try {
            UserTypeEnum userType = AuthUtils.getUserTypeFromSession(context);
            String doctorID = context.queryParam("doctorID");
            int doctorId = userType == UserTypeEnum.PATIENT
                    ? getDoctorIdFromPatientRequest(context, doctorID)
                    : AuthUtils.validateDoctorAndGetId(context);

            List<Map<String, Object>> ratings = RatingsService.getRatings(doctorId);
            context.json(ratings);
        } catch (UnauthorizedException exception) {
            AuthUtils.handleUnauthorized(context, exception);
        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        }
    }

    private static int getDoctorIdFromPatientRequest(Context context, String doctorID) 
            throws UnauthorizedException, ValidationException {
        AuthUtils.validateUserAndGetId(context, UserTypeEnum.PATIENT);
        if (doctorID == null || doctorID.isEmpty()) {
            throw new ValidationException("Doctor ID is required");
        }
        try {
            int id = Integer.parseInt(doctorID);
            if (!InputValidator.isValidID(id)) {
                throw new ValidationException("Invalid doctor ID");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException("Doctor ID must be a valid integer");
        }
    }

    public static void setRating(Context context) {
        try {
            RatingBody body = context.bodyAsClass(RatingBody.class);
            body.validate();
            
            int patientID = AuthUtils.validateUserAndGetId(context, UserTypeEnum.PATIENT);
            Rating newRating = RatingsService.setRating(body.appointmentID, body.stars, body.comments, patientID);
            RatingsService.calcRating(newRating.getDoctorId());
            context.status(201);
        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        } catch (UnauthorizedException exception) {
            AuthUtils.handleUnauthorized(context, exception);
        } catch (NumberFormatException e) {
            context.status(400).json(Map.of("error", "Invalid format"));
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                context.status(400).json(Map.of("error", "Patient has already rated this appointment"));
            } else {
                context.status(500).json(Map.of("error", "An error occurred while setting the rating"));
            }
        }
    }

    public static void updateRating(Context context) {
        try {
            String appointmentIDParam = context.pathParam("appointmentID");
            if (appointmentIDParam == null || appointmentIDParam.isEmpty()) {
                context.status(400).json(Map.of("error", "Appointment ID is required"));
                return;
            }
            
            int appointmentID = Integer.parseInt(appointmentIDParam);
            if (!InputValidator.isValidID(appointmentID)) {
                context.status(400).json(Map.of("error", "Invalid appointment ID"));
                return;
            }
            
            RatingBody body = context.bodyAsClass(RatingBody.class);
            body.validate();
            
            int userId = AuthUtils.validatePatientAndGetId(context);

            Rating updatedRating = RatingsService.updateRating(appointmentID, body.stars, body.comments, userId);
            RatingsService.calcRating(updatedRating.getDoctorId());
            context.json(updatedRating);
        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        } catch (UnauthorizedException exception) {
            AuthUtils.handleUnauthorized(context, exception);
        } catch (NumberFormatException e) {
            context.status(400).json(Map.of("error", "Invalid appointment ID format"));
        } catch (Exception e) {
            context.status(500).json(Map.of("error", "An error occurred while updating the rating"));
        }
    }

    public static void getAppointmentRating(Context context) {
        try {
            String appointmentIDParam = context.pathParam("appointmentID");
            if (appointmentIDParam == null || appointmentIDParam.isEmpty()) {
                context.status(400).json(Map.of("error", "Appointment ID is required"));
                return;
            }
            
            int appointmentID = Integer.parseInt(appointmentIDParam);
            if (!InputValidator.isValidID(appointmentID)) {
                context.status(400).json(Map.of("error", "Invalid appointment ID"));
                return;
            }
            
            UserTypeEnum userType = AuthUtils.getUserTypeFromSession(context);
            int userId = AuthUtils.validateUserAndGetId(context, userType);

            Rating appointmentRating = RatingsService.getRatingByAppointmentID(appointmentID, userId, userType);

            context.json(appointmentRating);
        } catch (UnauthorizedException exception) {
            AuthUtils.handleUnauthorized(context, exception);
        } catch (NumberFormatException e) {
            context.status(400).json(Map.of("error", "Invalid appointment ID format"));
        }
    }
}
