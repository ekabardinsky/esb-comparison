package ru.ekabardinsky.magister.commons.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ekabardinsky on 4/4/17.
 */
public class HttpClient {
    public String get(String url) throws IOException {
        org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IllegalStateException("Wrong response from client with status" + response.getStatusLine().getStatusCode());
        }
        return getResponse(response);
    }

    public String post(String url, String json) throws IOException {
        org.apache.http.client.HttpClient client = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(url);

        //set body
        HttpEntity entity = new ByteArrayEntity(json.getBytes("UTF-8"));
        request.setEntity(entity);

        //set headers
        request.setHeader("Content-Type", "application/json");
        HttpResponse response = client.execute(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IllegalStateException("Wrong response from client with status " + response.getStatusLine().getStatusCode());
        }
        return getResponse(response);
    }

    private String getResponse(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
