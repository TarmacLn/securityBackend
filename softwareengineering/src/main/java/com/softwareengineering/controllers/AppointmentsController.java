package com.softwareengineering.controllers;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.softwareengineering.dto.AppointmentBody;
import com.softwareengineering.models.enums.UserTypeEnum;
import com.softwareengineering.services.AppointmentsService;
import com.softwareengineering.utils.AuthUtils;
import com.softwareengineering.utils.AuthUtils.UnauthorizedException;
import com.softwareengineering.utils.InputValidator;
import com.softwareengineering.utils.ValidationException;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class AppointmentsController {
    public static void init(Javalin app) {
        app.get("/doctor-appointments", AppointmentsController::getDoctorAppointments);
        app.get("/patient-appointments", AppointmentsController::getPatientAppointments);
        app.get("/view-appointment-details", AppointmentsController::viewAppointmentDetails);
        app.post("/set-appointment", AppointmentsController::setAppointment);
        app.patch("/cancel-appointment", AppointmentsController::cancelAppointment);
        app.patch("/complete-appointment", AppointmentsController::completeAppointment);
    }

    private static void getDoctorAppointments(Context context) {
        try {
            int doctorID = AuthUtils.validateDoctorAndGetId(context);
            String dateParam = context.queryParam("date");

            List<Map<String, Object>> appointments;

            if (dateParam != null && !dateParam.isEmpty()) {
                try {
                    // Try to parse ISO 8601 format (2023-05-15T12:00:00)
                    LocalDateTime localDateTime = LocalDateTime.parse(
                            dateParam, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    Timestamp date = Timestamp.valueOf(localDateTime);

                    appointments = AppointmentsService.getDoctorAppointmentsAfterDate(doctorID, date);
                } catch (Exception e) {
                    context.status(400).json(Map.of("error",
                            "Invalid date format. Use ISO 8601 format (YYYY-MM-DDThh:mm:ss)"));
                    return;
                }
            } else {
                // No date filter, get all appointments
                appointments = AppointmentsService.getDoctorAppointments(doctorID);
            }

            context.json(appointments);
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }

    private static void getPatientAppointments(Context context) {
        try {
            int patientID = AuthUtils.validatePatientAndGetId(context);
            String dateParam = context.queryParam("date");

            List<Map<String, Object>> appointments;

            if (dateParam != null && !dateParam.isEmpty()) {
                try {
                    // Try to parse ISO 8601 format (2023-05-15T12:00:00)
                    LocalDateTime localDateTime = LocalDateTime.parse(
                            dateParam, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    Timestamp date = Timestamp.valueOf(localDateTime);

                    appointments = AppointmentsService.getPatientAppointmentsAfterDate(patientID, date);
                } catch (Exception e) {
                    context.status(400).json(Map.of("error",
                            "Invalid date format. Use ISO 8601 format (YYYY-MM-DDThh:mm:ss)"));
                    return;
                }
            } else {
                // No date filter, get all appointments
                appointments = AppointmentsService.getPatientAppointments(patientID);
            }

            context.json(appointments);
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }

    private static void setAppointment(Context context) {
        try {
            int patientID = AuthUtils.validatePatientAndGetId(context);

            AppointmentBody body = context.bodyAsClass(AppointmentBody.class);
            body.validate(); // Validate input
            
            AppointmentsService.setAppointment(patientID, body.doctorID, body.slotID, body.status, body.reason);
            context.status(201);
            return;
        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }

    private static void viewAppointmentDetails(Context context) {
        try {
            // Either doctor or patient can view appointment details - just validate
            // authentication
            Integer userId = context.sessionAttribute("id");
            String userType = context.sessionAttribute("userType");
            if (userId == null || userId == 0 || userType == null) {
                throw new UnauthorizedException("No valid session found");
            }

            String appointmentIDParam = context.queryParam("appointmentID");
            if (appointmentIDParam == null || appointmentIDParam.isEmpty()) {
                context.status(400).json(Map.of("error", "Appointment ID is required"));
                return;
            }
            
            try {
                int appointmentID = Integer.parseInt(appointmentIDParam);
                if (!InputValidator.isValidID(appointmentID)) {
                    context.status(400).json(Map.of("error", "Invalid appointment ID"));
                    return;
                }
                
                Map<String, Object> appointmentDetails = AppointmentsService.getAppointmentDetails(appointmentID);
                if (appointmentDetails == null) {
                    context.status(404).json(Map.of("error", "Appointment not found"));
                    return;
                }
                context.json(appointmentDetails);
            } catch (NumberFormatException e) {
                context.status(400).json(Map.of("error", "Invalid appointment ID format"));
            }
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }

    private static void cancelAppointment(Context context) {
        try {
            UserTypeEnum userType = AuthUtils.getUserTypeFromSession(context);

            AuthUtils.validateUserAndGetId(context, userType);

            AppointmentBody body = context.bodyAsClass(AppointmentBody.class);
            body.validateForCancel();
            
            AppointmentsService.cancelAppointment(body.appointmentID);
            context.status(200);
        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }

    private static void completeAppointment(Context context) {
        try {
            // Only doctors can mark appointments as completed
            AuthUtils.validateDoctorAndGetId(context);

            AppointmentBody body = context.bodyAsClass(AppointmentBody.class);
            body.validateForComplete();
            
            AppointmentsService.completeAppointment(body.appointmentID);
            context.status(200).json(Map.of("message", "Appointment marked as completed"));
        } catch (ValidationException e) {
            context.status(400).json(Map.of("error", "Validation error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            AuthUtils.handleUnauthorized(context, e);
        }
    }
}