package pt.ist.test.api;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.FenixEduUserDetails;
import org.fenixedu.sdk.exception.FenixEduClientException;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class TestAPI {

    private static FenixEduClientImpl client;

    public static void main(String[] args) throws FenixEduClientException, IOException, URISyntaxException, InterruptedException {
        client = new FenixEduClientImpl(ApplicationConfiguration.fromPropertyFilename("/fenixedu.properties"));
        System.out.println(client.getAbout());

//        FenixEduUserDetails userDetailsFromCode = client.getUserDetailsFromCode("alsdksal");

//        client.getPerson(userDetailsFromCode.getAuthorization());

        HttpServer server = HttpServer.create(new InetSocketAddress(9999), 0);

        server.createContext("/", new MyHandler());

        server.setExecutor(null); // creates a default executor
        server.start();

        System.out.println("Opening browser ...");
        Desktop.getDesktop().browse(new URI(client.getAuthenticationUrl()));
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            if (t.getRequestMethod().equals("GET")) {
                String response = doit(getCode(t));
                System.out.println("got response: " + response);
                t.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private String getCode(HttpExchange t) {
            String uri = t.getRequestURI().toString();
            return uri.split("code=")[1];
        }
    }

    static String doit(String code) {
        System.out.println("code: " + code);
        FenixEduUserDetails userDetailsFromCode = client.getUserDetailsFromCode(code);
        System.out.println("accessToken: " + userDetailsFromCode.getAuthorization().asOAuthAuthorization().getOAuthAccessToken());
        System.out.println("refreshToken: "
                + userDetailsFromCode.getAuthorization().asOAuthAuthorization().getOAuthRefreshToken());
        JsonObject person = client.getPerson(userDetailsFromCode.getAuthorization());
        return person.get("name").getAsString();
    }
}
