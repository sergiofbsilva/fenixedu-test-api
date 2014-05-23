package pt.ist.test.api;

import org.fenixedu.sdk.ApplicationConfiguration;
import org.fenixedu.sdk.FenixEduClientImpl;
import org.fenixedu.sdk.exception.FenixEduClientException;

public class TestAPI {

    public static void main(String[] args) throws FenixEduClientException {
        FenixEduClientImpl client = new FenixEduClientImpl(ApplicationConfiguration.fromPropertyFilename("/fenixedu.properties"));
        System.out.println(client.getAbout());
    }
}
