package response;

import util.JsonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Response {

    public static void sendOk(HttpServletResponse resp, Object data) throws IOException {
        JsonUtil.sendJsonResponse(resp, data, HttpStatusCode.OK.getValue());
    }

    public static void sendCreated(HttpServletResponse resp, Object data) throws IOException {
        JsonUtil.sendJsonResponse(resp, data, HttpStatusCode.CREATED.getValue());
    }

}
