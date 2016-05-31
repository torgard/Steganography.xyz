package com.akseltorgard.steganography.http;

import com.akseltorgard.steganography.AsyncResponse;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;

public class DecodeHttpRequestTask extends HttpRequestTask {
    public DecodeHttpRequestTask(AsyncResponse<RestParams> delegate) {
        super(delegate);
    }

    @Override
    protected RestParams execute(RestParams restParams) throws RestClientException {
        RestTemplate restTemplate = new RestTemplate();

        //Handles Strings
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        //Handles MultiValueMaps
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

        MultiValueMap<String, Object> map = new LinkedMultiValueMap();
        map.add("image", new FileSystemResource(new File(restParams.getFilePath())));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> imageEntity = new HttpEntity(map, headers);

        String url = WEBSERVICE + DECODE;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, imageEntity, String.class);

        restParams.setMessage(response.getBody());
        restParams.setType(AsyncResponse.Type.DECODE_SUCCESS);
        return restParams;
    }
}