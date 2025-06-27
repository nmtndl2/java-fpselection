package com.task.validation;

import com.task.annotation.ValidatePressRequestFields;
import com.task.dto.product.request.PressRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PressRequestValidator implements ConstraintValidator<ValidatePressRequestFields, PressRequest> {

    @Override
    public boolean isValid(PressRequest pressRequest, ConstraintValidatorContext context) {
        if (pressRequest == null) {
            return true;
        }

        boolean valid = true;

        context.disableDefaultConstraintViolation();

        // Drip Tray validation
        if (pressRequest.getDtAvailable() != null && pressRequest.getDtAvailable()) {
            if (pressRequest.getDtOpenT() == null) {
                context.buildConstraintViolationWithTemplate("dtOpenT must not be null when dtAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getDtClosedT() == null) {
                context.buildConstraintViolationWithTemplate("dtClosedT must not be null when dtAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
        }

        // Plate Shifter validation
        if (pressRequest.getPsAvailable() != null && pressRequest.getPsAvailable()) {
            if (pressRequest.getPsFwdFPlateT() == null) {
                context.buildConstraintViolationWithTemplate("psFwdFPlateT must not be null when psAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getPsFwdT() == null) {
                context.buildConstraintViolationWithTemplate("psFwdT must not be null when psAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getPsFwdDT() == null) {
                context.buildConstraintViolationWithTemplate("psFwdDT must not be null when psAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getPsRevT() == null) {
                context.buildConstraintViolationWithTemplate("psRevT must not be null when psAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getPsRevDT() == null) {
                context.buildConstraintViolationWithTemplate("psRevDT must not be null when psAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
        }

        // Cloth Washing validation
        if (pressRequest.getCwAvailable() != null && pressRequest.getCwAvailable()) {
            if (pressRequest.getCwFwdT() == null) {
                context.buildConstraintViolationWithTemplate("cwFwdT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getCwFwdDT() == null) {
                context.buildConstraintViolationWithTemplate("cwFwdDT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getCwRevT() == null) {
                context.buildConstraintViolationWithTemplate("cwRevT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getCwRevDT() == null) {
                context.buildConstraintViolationWithTemplate("cwRevDT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getCwDownT() == null) {
                context.buildConstraintViolationWithTemplate("cwDownT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getCwDownDT() == null) {
                context.buildConstraintViolationWithTemplate("cwDownDT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getCwUpT() == null) {
                context.buildConstraintViolationWithTemplate("cwUpT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
            if (pressRequest.getCwUpDT() == null) {
                context.buildConstraintViolationWithTemplate("cwUpDT must not be null when cwAvailable is true")
                        .addConstraintViolation();
                valid = false;
            }
        }

        return valid;
    }
}
