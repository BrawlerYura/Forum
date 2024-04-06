//package org.example.common.exceptions;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestControllerAdvice
//public class ApplicationExceptionHandler {
//
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(UserNotFoundException.class)
//    public Map<String, String> handleUserNotFoundException(UserNotFoundException e) {
//        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("errorMessage", e.getMessage());
//        return errorMap;
//    }
//
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(NotFoundException.class)
//    public Map<String, String> handleUserNotFoundException(NotFoundException e) {
//        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("errorMessage", e.getMessage());
//        return errorMap;
//    }
//
//    @ResponseStatus(HttpStatus.CONFLICT)
//    @ExceptionHandler(UserAlreadyExistException.class)
//    public Map<String, String> handleUserAlreadyExistException(UserAlreadyExistException e) {
//        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("errorMessage", e.getMessage());
//        return errorMap;
//    }
//
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(UnauthorizedException.class)
//    public Map<String, String> handleUnauthorizedException(UnauthorizedException e) {
//        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("errorMessage", e.getMessage());
//        return errorMap;
//    }
//
//    @ResponseStatus(HttpStatus.FORBIDDEN)
//    @ExceptionHandler(ForbiddenException.class)
//    public Map<String, String> handleForbiddenException(ForbiddenException e) {
//        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("errorMessage", e.getMessage());
//        return errorMap;
//    }
//
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(BadRequestException.class)
//    public Map<String, String> handleBadRequestException(BadRequestException e) {
//        Map<String, String> errorMap = new HashMap<>();
//        errorMap.put("errorMessage", e.getMessage());
//        return errorMap;
//    }
//}
