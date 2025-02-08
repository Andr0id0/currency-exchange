package response;

import util.JsonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Response {

    public static void sendOk(HttpServletResponse resp, Object data) throws IOException {
        JsonUtil.sendJsonResponse(resp, data, HttpServletResponse.SC_OK);
    }

    public static void sendCreated(HttpServletResponse resp, Object data) throws IOException {
        JsonUtil.sendJsonResponse(resp, data, HttpServletResponse.SC_CREATED);
    }

}
