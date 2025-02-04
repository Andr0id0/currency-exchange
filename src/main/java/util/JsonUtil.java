package util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void sendJsonResponse(HttpServletResponse resp, Object data, int status) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(status);
        mapper.writeValue(resp.getWriter(), data);
    }

    public static void sendErrorResponse(HttpServletResponse resp, String message, int errorStatus) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(errorStatus);
        mapper.writeValue(resp.getWriter(), Map.of( "massage", message));
    }
}
