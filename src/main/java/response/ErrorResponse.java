package response;

import util.JsonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ErrorResponse {

    public static void sendBadRequest(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpStatusCode.BAD_REQUEST.getValue());
    }

    public static void sendNotFound(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpStatusCode.NOT_FOUND.getValue());
    }

    public static void sendConflict(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpStatusCode.CONFLICT.getValue());
    }

    public static void sendInternalServerError(HttpServletResponse resp, String message) throws IOException {
        JsonUtil.sendErrorResponse(resp, message, HttpStatusCode.INTERNAL_SERVER_ERROR.getValue());
    }


}
