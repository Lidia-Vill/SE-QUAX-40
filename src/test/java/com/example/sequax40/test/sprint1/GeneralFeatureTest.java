package com.example.sequax40.test.sprint1;

/*import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class GeneralFeatureTest {

	public class TimeoutTest {
	
		@Test
	    void testTimeout() {
	        // This will fail because 1 second > 100 milliseconds
	        assertTimeout(Duration.ofMillis(100), () -> {
	            Thread.sleep(1000); 
	        });
	    }
	}
}
*/
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout; // Import for JUnit 5
import java.util.concurrent.TimeUnit;

class GeneralFeatureTest {

    @Test
    @Timeout(value = 100, unit = TimeUnit.MILLISECONDS) // JUnit 5 style
    void testTimeout() throws Exception {
        TimeUnit.SECONDS.sleep(1); 
    }
}