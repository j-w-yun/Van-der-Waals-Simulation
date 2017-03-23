package org.yoon_technology.gpu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Refer to LICENSE
 *
 * @author Jaewan Yun (jay50@pitt.edu)
 */

public class ProgramReader {

	public static String readFile(String fileName) {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(ProgramReader.class.getResourceAsStream("/kernel/" + fileName + ".cl")))) {
			StringBuilder sb = new StringBuilder();
			String line = null;
			while (true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				sb.append(line + "\n");
			}
			return sb.toString();

		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}

}
