package org.security.controller;

import org.trace.service.APICallDetails;
import org.trace.service.APILogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.security.response.APIResponse;
import org.security.service.ADAuth;
import org.security.utils.ADGroupRetrieval;
import org.security.utils.Credentials;
import org.security.utils.KerberosObject;
import org.security.utils.jwt.JWTUtilRSA;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private ADAuth adAuthService;
    
    @SuppressWarnings("finally")
    @PostMapping("/authenticate")
    public ResponseEntity<APIResponse> authenticate(
            @RequestBody Credentials credentials,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            HttpServletRequest request, 
            HttpServletResponse response
        ) { 
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        APIResponse result = new APIResponse();
        HttpStatus status =  HttpStatus.OK;
        try {
            ADGroupRetrieval groupRetrieval = new ADGroupRetrieval();
            KerberosObject ticket = adAuthService.authenticateDetails(username, password);
            Set<String> roles = groupRetrieval.getGroupsFromAD(username, password);
            if (ticket != null) {
                HashMap<String, Object> datas = new HashMap<>();
                String token = JWTUtilRSA.generateTokenWithKerberosTicket(
                    username,
                    ticket.getSessionKey(),
                    roles
                );
                datas.put("token", token);
                result.setData(datas);
            } else {
                result.setError("Authentication failed: No Kerberos ticket obtained.");
            }
        } catch (Exception e) {
            result.setError("Authentication failed: " + e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        } finally{
            APILogger.logRequest(APICallDetails.getClientDetails(forwardedFor, request, response, credentials));
            return ResponseEntity.status(status).body(result);
        }
    }

    @GetMapping("/public_key")
    public String getPublicKey(HttpServletRequest request, HttpServletResponse response){
        return JWTUtilRSA.getPublicKeyAsBase64();
    }
}

