package com.example.hashmaps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.util.Assert;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HashmapsApplicationTests {
    private final SessionRegistry sessions = new SessionRegistryImpl();

    //private final Map<CompositeKey, String> values = new ConcurrentHashMap<>();

    @Test
    void saveWithSubAndSidAndLoadWithSidOnly() {
        Principal principal = new Principal("sub1", "sid1"); // sign in passed both sub and sid
        this.sessions.registerNewSession("clientSid", principal);
        Principal fromToken = new Principal(null, "sid1"); // logout token only has sid
        assertThat(this.sessions.getAllSessions(fromToken, true)).isNotEmpty(); // fails
    }

    @Test
    void saveWithSubAndLoadWithSub() {
        Principal principal = new Principal("sub1", null); // sign in passed both sub
        this.sessions.registerNewSession("clientSid", principal);
        Principal fromToken = new Principal("sub1", null); // logout token only has sub
        assertThat(this.sessions.getAllSessions(fromToken, true)).isNotEmpty();
    }

    @Test
    void saveWithSubAndSidAndLoadWithSubAndSid() {
        Principal principal = new Principal("sub1", "sid1"); // sign in passed both
        this.sessions.registerNewSession("clientSid", principal);
        Principal fromToken = new Principal("sub1", "sid1"); // logout token has both
        assertThat(this.sessions.getAllSessions(fromToken, true)).isNotEmpty();
    }

    @Test
    void saveWithSubAndSidAndLoadWithSubOnly() {
        Principal principal = new Principal("sub1", "sid1"); // sign in passed both
        this.sessions.registerNewSession("clientSid", principal);
        Principal fromToken = new Principal("sub1", null); // logout token only has sub
        assertThat(this.sessions.getAllSessions(fromToken, true)).isNotEmpty(); // fails
    }

    private static final class Principal {
        private final String subject;
        private final String sessionId;

        public Principal(String subject, String sessionId) {
            Assert.isTrue(subject != null || sessionId != null, "either subject or sessionid must not be null");
            this.subject = subject;
            this.sessionId = sessionId;
        }

        public String getSubject() {
            return subject;
        }

        public String getSessionId() {
            return sessionId;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Principal that)) {
                return false;
            }
            return equalsIdeaOne(that);
            /*
             * Below is what I would imagine a normal equals method to be like.
             * This also does not make the tests pass, but I'm including it to demonstrate a secondary concern
             * that using an unconventional equals should be a code smell.
             */
            // return equalsIdeaTwo(that);
        }

        private boolean equalsIdeaOne(Principal that) {
            if (that.sessionId == null) {
                return this.subject.equals(that.subject);
            }
            return this.sessionId.equals(that.sessionId) && this.subject.equals(that.subject);
        }

        private boolean equalsIdeaTwo(Principal that) {
            return Objects.equals(that.sessionId, this.sessionId) && Objects.equals(that.subject, this.subject);
        }

        @Override
        public int hashCode() {
             return hashCodeIdeaOne();
            /**
             * Below is what I would imagine a normal hashCode method to be like.
             * This also does not make the tests pass, but I'm including it to demonstrate a secondary concern
             * that using an unconventional equals should be a code smell.
             */
            //return hashCodeIdeaTwo();
        }

        private int hashCodeIdeaOne() {
            if (this.sessionId == null) {
                return Objects.hash(this.subject);
            }
            return Objects.hash(this.sessionId, this.subject);
        }

        private int hashCodeIdeaTwo() {
            return Objects.hash(this.sessionId, this.subject);
        }
    }
}
