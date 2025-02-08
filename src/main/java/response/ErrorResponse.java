package response;

import util.JsonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorResponse {

    public static void sendBadRequest(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpServletResponse.SC_BAD_REQUEST);
    }

    public static void sendNotFound(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpServletResponse.SC_NOT_FOUND);
    }

    public static void sendConflict(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpServletResponse.SC_CONFLICT);
    }

    public static void sendInternalServerError(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }


}
