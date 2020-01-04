package com.tung.bcbe.controller;

import com.auth0.client.auth.AuthAPI;
import com.auth0.json.auth.UserInfo;
import com.auth0.net.Request;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import javax.ws.rs.ForbiddenException;
import java.util.Map;

@Component
@Slf4j
public class Auth0Util {

    @Autowired
    private AuthAPI auth;

    public String getContractorId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String token = jwt.getTokenValue();

        Request<UserInfo> request = auth.userInfo(token);
        try {
            UserInfo userInfo = request.execute();
            Map<String, String> metadata = (Map<String, String>) userInfo.getValues().get("https://tungcb:auth0:com/user_metadata");
            String contractorId = metadata.get("contractor_id");
            return contractorId;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Validate that the contractor id from token is the same as that from path variable
     */
    public void validateContractorId(String genIdFromPathVariable) {
        String conId = getContractorId();
        if (conId.equalsIgnoreCase(genIdFromPathVariable)) {
            log.error("contractor_id {} from token doesn't match path variable {}", conId, genIdFromPathVariable);
            throw new ForbiddenException();
        }
    }

}
