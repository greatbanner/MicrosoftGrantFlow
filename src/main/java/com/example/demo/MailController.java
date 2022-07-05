package com.example.demo;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.Properties;

@RestController()
@RequestMapping("/api/dealer")
public class MailController {

    private final String GMAIL_SERVER_OAUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";

    @PostMapping( consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createDealer(@RequestBody String dealerRequest) {
            return ResponseEntity.ok(dealerRequest);
    }

    @PutMapping( consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> updateDealer(@RequestBody String dealerRequest) {
        return ResponseEntity.ok(dealerRequest);
    }

    @GetMapping()
    public String getMailOauthLink( @RequestParam String emailAddress) {
        Properties props = new Properties();
        props.put("mail.imap.ssl.enable", "true"); // required for Gmail
        props.put("mail.imap.auth.mechanisms", "XOAUTH2");
        Session session = Session.getInstance(props);
        Store store = null;
        try {
            store = session.getStore("imap");
            String oauth2_access_token = "";
            store.connect("imap.gmail.com", emailAddress , oauth2_access_token);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return "hello";
    }

    @GetMapping("/oauth")
    public MsTokenResponse getMailOauthLink(@RequestParam String code, @RequestParam String state ) {
        System.out.println("microsoft response");
        System.out.println(code);
        System.out.println(state);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("client_id", "YOUR_APPLICATION_CLIENT_ID");
        map.add("scope", "IMAP.AccessAsUser.All");
        map.add("code", code);
        map.add("redirect_uri", "YOUR_REDIRECT_URL");
        map.add("grant_type", "authorization_code");
        map.add("client_secret", "YOUR_TOTALLY_SECRET_CLIENT");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        String microsoftTokenURL = "https://login.microsoftonline.com/common/oauth2/v2.0/token";
        MsTokenResponse response
                = restTemplate.postForObject(microsoftTokenURL, request, MsTokenResponse.class);
        return response;
    }
}
