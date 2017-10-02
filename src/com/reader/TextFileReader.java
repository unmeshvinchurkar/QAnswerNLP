package com.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class TextFileReader implements FileReader{

	private String filePath;
	private int noOfLines = 10;
	private BufferedReader bf = null;

	public TextFileReader(String filePath) {
		this.filePath = filePath;
		_init();
	}

	public TextFileReader(String filePath, int noOfLines) {
		this.filePath = filePath;
		this.noOfLines = noOfLines;
		_init();
	}

	private void _init() {

		try {
			File f = new File(filePath);

			bf = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getNextLines() throws IOException {

		String line = null;
		StringBuffer sb  = new StringBuffer();

		int i = 0;
		while ((line = bf.readLine()) != null) {
			sb.append(line);
			i++;
			if (i == noOfLines) {
				break;
			}
		}
		return line.toString();
	}

	public void close() {
		try {
			bf.close();
		} catch (IOException e) {
		}
	}

}
