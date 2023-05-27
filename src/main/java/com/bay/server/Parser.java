package com.bay.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;

public class Parser {
    public static JsonNode parseJson(InputStream inputStream) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Scanner scanner = new Scanner(inputStream);
        StringBuffer stringBuffer = new StringBuffer();
        while(scanner.hasNext()){
            stringBuffer.append(scanner.nextLine());
        }
        System.out.println(stringBuffer);
        return objectMapper.readTree(stringBuffer.toString());
    }

    public static String[] splitString(String text, String delimiter) {
        return Arrays.stream(text.split(delimiter)).filter(value -> value != null && value.length() > 0).toArray(String[]::new);
    }

    public static String parseRequestQuery(String requestQuery) {
        String[] queries = splitString(requestQuery, "&");
        String field = "";
        String condition = "";
        String value = "";
        try {
            for (String query : queries) {
                String queryKey = splitString(query, "=")[0];
                String queryValue = splitString(query, "=")[1];

                switch (queryKey) {
                    case "f":
                        field = queryValue;
                        break;
                    case "c":
                        switch (queryValue) {
                            case "greaterEqual":
                                condition = ">=";
                                break;
                            case "greater":
                                condition = ">";
                                break;
                            case "lessEqual":
                                condition = "<=";
                                break;
                            case "less":
                                condition = "<";
                                break;
                            case "equal":
                                condition = "=";
                                break;
                            case "notEqual":
                                condition = "<>";
                                break;
                            case "like":
                                condition = "LIKE";
                                break;
                            default:
                                return null;
                        }
                        break;
                    case "v":
                        value = queryValue;
                        break;
                    default:
                        return null;
                }
            }
            return field + " " + condition + " " + value;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
