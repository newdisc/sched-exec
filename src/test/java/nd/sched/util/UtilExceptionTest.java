package nd.sched.util;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class UtilExceptionTest {
	@Test
	public void testCtr() {
		final UtilException ue = new UtilException("Test", null);
		assertNull(ue.getCause());
	}
}
