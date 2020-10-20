package com.varna.code.challenge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

@Configuration
public class MockRequest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public ResultActions performGet(String url, Map<String, Object> queryParams) throws Exception
    {
        return mockMvc.perform(getRequest(url, queryParams));
    }

    public ResultActions performPost(String url, Object body) throws Exception
    {
        return mockMvc.perform(postRequest(url, body));
    }

    public MockHttpServletRequestBuilder getRequest(String url, Map<String, Object> queryParams) throws JsonProcessingException
    {
        // Maps query param map to string
        String queryParamsString = "?" + queryParams.keySet().stream().reduce("", (acc, key) -> {
            acc += key + "=" + queryParams.get(key) + "&";
            return acc;
        });
        return MockMvcRequestBuilders.get(url + queryParamsString);
    }

    public MockHttpServletRequestBuilder postRequest(String url, Object body) throws JsonProcessingException
    {
        return MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(body));
    }
}
