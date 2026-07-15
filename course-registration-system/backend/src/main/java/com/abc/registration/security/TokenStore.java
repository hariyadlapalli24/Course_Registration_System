package com.abc.registration.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal in-memory session-token store.
 *
 * This keeps auth simple (no JWT signing, no external session store) while
 * still giving the app a real, server-enforced notion of "who is logged in
 * and what is their role" that Spring Security checks on every request.
 *
 * Swapping this for JWTs, Redis-backed sessions, or full Spring Security
 * authentication later only means changing this class and
 * TokenAuthenticationFilter -- controllers and SecurityConfig don't need
 * to change.
 *
 * Note: tokens live in memory only, so they are cleared on server restart,
 * and this will not work across multiple backend instances without a
 * shared store. Fine for a single-instance coursework/demo deployment.
 */
@Component
public class TokenStore {

    private final Map<String, AuthToken> tokens = new ConcurrentHashMap<>();

    public String issueToken(AuthToken authToken) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, authToken);
        return token;
    }

    public AuthToken resolve(String token) {
        return tokens.get(token);
    }

    public void revoke(String token) {
        tokens.remove(token);
    }
}
