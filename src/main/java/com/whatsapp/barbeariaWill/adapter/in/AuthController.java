package com.whatsapp.barbeariaWill.adapter.in;

import com.whatsapp.barbeariaWill.application.dto.AuthenticationRequest;
import com.whatsapp.barbeariaWill.application.dto.AuthenticationResponse;
import com.whatsapp.barbeariaWill.security.JwtUtil;
import com.whatsapp.barbeariaWill.security.MyUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager  authenticationManager, 
                            MyUserDetailsService userDetailsService, 
                            JwtUtil              jwtUtil){
        this.authenticationManager  = authenticationManager;
        this.userDetailsService     = userDetailsService;
        this.jwtUtil                = jwtUtil;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) 
        throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), 
                    authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Usuário ou senha incorretas", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
} 