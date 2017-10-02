package com.reader;

import java.io.IOException;
import java.util.List;

public interface FileReader {
	
	public String getNextLines() throws IOException ;
	
	public void close();

}
