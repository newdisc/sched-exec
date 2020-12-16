package nd.sched.util;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class UtilExceptionTest {
	@Test
	void testCtr() {
		final UtilException ue = new UtilException("Test", null);
		assertNull(ue.getCause());
	}
}
