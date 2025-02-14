package com.wjfzk;

/**
 * projectName: CS6650Assignment1
 *
 * @author Zekun Fang
 * @version 1.0
 * description:
 * @date 2025/2/13
 */
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@WebServlet(name = "SkierServlet", urlPatterns = "/skiers/*")
public class SkierServlet extends HttpServlet {

    private static final int MIN_RESORT_ID = 1, MAX_RESORT_ID = 10;
    private static final int MIN_SKIER_ID = 1, MAX_SKIER_ID = 100000;
    private static final String JSON_TYPE = "application/json";

    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(JSON_TYPE);

        String[] urlSegments = parsePathParameters(request);
        if (urlSegments == null) {
            sendErrorResponse(response, "Invalid URL format! Expected format: /skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}");
            return;
        }

        int resortID = Integer.parseInt(urlSegments[1]);
        String dayID = urlSegments[5];
        int skierID = Integer.parseInt(urlSegments[7]);

        if (!isValidDayID(dayID)) {
            sendErrorResponse(response, "Invalid dayID: Must be between 1 and 366");
            return;
        }
        if (!isInRange(skierID, MIN_SKIER_ID, MAX_SKIER_ID)) {
            sendErrorResponse(response, "Invalid skierID: Must be between " + MIN_SKIER_ID + " and " + MAX_SKIER_ID);
            return;
        }
        if (!isInRange(resortID, MIN_RESORT_ID, MAX_RESORT_ID)) {
            sendErrorResponse(response, "Invalid resortID: Must be between " + MIN_RESORT_ID + " and " + MAX_RESORT_ID);
            return;
        }

        JsonObject jsonBody = extractJsonBody(request);
        if (jsonBody == null) {
            sendErrorResponse(response, "Invalid JSON format");
            return;
        }
        if (!jsonBody.has("liftID") || !jsonBody.has("time")) {
            sendErrorResponse(response, "Missing required fields: liftID and time");
            return;
        }

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(gson.toJson(new SuccessResponse("Skier data recorded successfully")));
    }

    private String[] parsePathParameters(HttpServletRequest request) {
        if (request.getPathInfo() == null) {
            return null;
        }
        String[] parts = request.getPathInfo().split("/");
        return (parts.length == 8) ? parts : null;
    }

    private JsonObject extractJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return gson.fromJson(requestBody.toString(), JsonObject.class);
    }

    private boolean isValidDayID(String dayID) {
        return dayID.matches("^([1-9]|[1-9][0-9]|[12][0-9][0-9]|3[0-5][0-9]|36[0-6])$");
    }

    private boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write(gson.toJson(new ErrorResponse(message)));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            out.println("<h1>Server is up and running!</h1>");
        }
    }

    static class ErrorResponse {
        String message;
        ErrorResponse(String message) { this.message = message; }
    }

    static class SuccessResponse {
        String message;
        SuccessResponse(String message) { this.message = message; }
    }
}
