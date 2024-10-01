package org.security.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.security.response.APIResponse;
import org.security.utils.FileProcessor;
import org.security.utils.properties.KerberosProperties;
import org.security.utils.properties.LdapProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @PostMapping("/ldap")
    @PreAuthorize("hasAuthority('Users')")
    public ResponseEntity<APIResponse> configLDAP(@RequestBody LdapProperties prop) {
        APIResponse response = new APIResponse();
        try {
            String config = prop.replaceString();
            FileProcessor.createNewFile("ldap_new.properties", config);
            response.setMessage("LDAP Config file created successfully");
        } catch (Exception e) {
            response.setError(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/kerberos")    
    @PreAuthorize("hasAuthority('Users')")
    public ResponseEntity<APIResponse> configKerberos(@RequestBody KerberosProperties prop) {
        APIResponse response = new APIResponse();
        try {
            String config = prop.replaceString();
            FileProcessor.createNewFile("krb5_new.conf", config);
            response.setMessage("KERBEROS Config file created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            response.setError(e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
