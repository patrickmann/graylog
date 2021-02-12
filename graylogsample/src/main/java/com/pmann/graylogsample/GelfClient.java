package com.pmann.graylogsample;

import java.io.IOException;

/**
 * Interface for sending GELF messages to a GELF server
 * @author pmann
 *
 */
public interface GelfClient {
	/**
	 * Send the given GELF message to the GELF server
	 * @param gelfMsg a valid GELF message
	 * @throws IOException thrown if we encounter errors during sending
	 */
	public abstract void sendGelf(String gelfMsg) throws IOException;
}
