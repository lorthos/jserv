package com.lorthos.jserv;

import com.lorthos.jserv.util.IOUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BufferedHeader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * BasicHttpClientConnectionManager maintains 1 connection
 * keep-alive must work for this to succeed
 */
public class AppTest {

    static {
        System.setProperty("java.util.logging.config.file", ClassLoader.getSystemResource("logging-test.properties").getPath());
    }

    private final String rootUrl = "http://localhost:8080";
    private final String url1 = rootUrl + "/SERVER_ROOT.txt";

    //dont start stop server between the tests
    private static App app;
    private static CloseableHttpClient httpClient;

    @BeforeClass
    public static void setUp() throws Exception {
        app = App.build(new String[]{"src/test/resources/server.properties"});
        app.start();
        httpClient = HttpClients.createMinimal(new BasicHttpClientConnectionManager());
    }

    @Test
    public void shouldRespondToHttpRequests() throws IOException {
        HttpGet httpGet = new HttpGet(rootUrl);
        CloseableHttpResponse res = httpClient.execute(httpGet);
        assertEquals(200, res.getStatusLine().getStatusCode());
        res.close();
    }


    @Test
    public void headTXTFiles() throws IOException {
        HttpHead head = new HttpHead(url1);
        CloseableHttpResponse res = httpClient.execute(head);
        assertEquals(200, res.getStatusLine().getStatusCode());
        List<BufferedHeader> list = Arrays.stream(res.getAllHeaders()).map(header -> (BufferedHeader) header).collect(Collectors.toList());
        boolean includesContentLengthHeader = false;
        for (BufferedHeader header : list) {
            if (header.getName().toLowerCase().equals("content-length")) {
                includesContentLengthHeader = true;
                assertEquals("27", header.getValue());
            }
        }
        assertTrue(includesContentLengthHeader);
        res.close();
    }

    @Test
    public void getTXTFiles() throws IOException, InterruptedException {

        HttpGet httpGet = new HttpGet(url1);
        CloseableHttpResponse res = httpClient.execute(httpGet);
        assertEquals(200, res.getStatusLine().getStatusCode());
        assertEquals("CONTENTS_OF_SERVER_ROOT.txt", IOUtils.streamToString(res.getEntity().getContent()));
        res.close();

        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            String url2 = rootUrl + "/dir1/TEXT_FILE.txt";
            httpGet = new HttpGet(url2);
            res = httpClient.execute(httpGet);
            assertEquals(200, res.getStatusLine().getStatusCode());
            assertEquals("CONTENTS_OF_TEXT_FILE.txt", IOUtils.streamToString(res.getEntity().getContent()));
            res.close();
        }

    }

    @Test(expected = SocketException.class)
    public void exceedCount() throws IOException, InterruptedException {
        TelnetClient telnetClient = new TelnetClient();
        telnetClient.connect("localhost", 8080);
        OutputStream clientOutputStream = telnetClient.getOutputStream();
        for (int i = 0; i < 30; i++) {
            clientOutputStream.write("GET /SERVER_ROOT.txt HTTP/1.1".getBytes());
            clientOutputStream.flush();
            Thread.sleep(100);
        }
    }

    @Test(expected = SocketException.class)
    public void exceedTime() throws IOException, InterruptedException {
        TelnetClient telnetClient = new TelnetClient();
        telnetClient.connect("localhost", 8080);
        OutputStream clientOutputStream = telnetClient.getOutputStream();
        for (int i = 0; i < 3; i++) {
            clientOutputStream.write("GET /SERVER_ROOT.txt HTTP/1.1".getBytes());
            clientOutputStream.flush();
            Thread.sleep(13000);
        }
    }

    @Test(expected = SocketException.class)
    public void disableWithHeader() throws IOException, InterruptedException {
        TelnetClient telnetClient = new TelnetClient();
        telnetClient.connect("localhost", 8080);
        OutputStream clientOutputStream = telnetClient.getOutputStream();
        for (int i = 0; i < 3; i++) {
            clientOutputStream.write("GET /SERVER_ROOT.txt HTTP/1.1\n".getBytes());
            clientOutputStream.write("Connection: Close".getBytes());
            clientOutputStream.flush();
            Thread.sleep(100);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        app.stop();
        httpClient.close();
    }

}
