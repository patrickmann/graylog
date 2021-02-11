package com.pmann.graylogsample;

import java.io.IOException;

public interface HttpGelfClient {
	public abstract void sendGelf(String gelfMsg) throws IOException;
}
