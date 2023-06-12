package com.example.hashmaps;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HashmapsApplicationTests {
    private final Map<CompositeKey, String> values = new ConcurrentHashMap<>();

    @Test
    void saveWithSubAndSidAndLoadWithSidOnly() {
        CompositeKey key = new CompositeKey("sub1", "sid1"); // sign in passed both sub and sid
        String value = "hello";
        this.values.put(key, value); // saved to registry
        String retrieved = this.values.get(new CompositeKey(null, "sid1")); // logout token only has sid
        assertThat("hello").isSameAs(retrieved); // fails
    }

    @Test
    void saveWithSubAndLoadWithSub() {
        CompositeKey key = new CompositeKey("sub1", null); // sign in passed only sub
        String value = "hello";
        this.values.put(key, value); // saved to registry
        String retrieved = this.values.get(new CompositeKey("sub1", null)); // logout token only has sub
        assertThat("hello").isSameAs(retrieved); // succeeds
    }

    @Test
    void saveWithSubAndSidAndLoadWithSubAndSid() {
        CompositeKey key = new CompositeKey("sub1", "sid1"); // sign in passed both sub and sid
        String value = "hello";
        this.values.put(key, value); // saved to registry
        String retrieved = this.values.get(new CompositeKey("sub1", "sid1")); // logout token only has sub and sid
        assertThat("hello").isSameAs(retrieved); // succeeds
    }

    @Test
    void saveWithSubAndSidAndLoadWithSubOnly() {
        CompositeKey key = new CompositeKey("sub1", "sid1"); // sign in passed both sub and sid
        String value = "hello";
        this.values.put(key, value); // saved to registry
        String retrieved = this.values.get(new CompositeKey("sub1", null)); // logout token only has sub
        assertThat("hello").isSameAs(retrieved); // fails
    }

    private static final class CompositeKey {
        private final String subject;
        private final String sessionId;

        public CompositeKey(String subject, String sessionId) {
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
            if (!(obj instanceof CompositeKey that)) {
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

        private boolean equalsIdeaOne(CompositeKey that) {
            if (that.sessionId == null) {
                return this.subject.equals(that.subject);
            }
            return this.sessionId.equals(that.sessionId) && this.subject.equals(that.subject);
        }

        private boolean equalsIdeaTwo(CompositeKey that) {
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
