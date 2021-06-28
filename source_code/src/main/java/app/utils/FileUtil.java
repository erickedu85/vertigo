package app.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Erick
 *
 */
public class FileUtil {

	/**
	 * Method to Recursive delete in a directory,
	 * 
	 * @param path
	 *            Path
	 */
	public static void deleteFilesFromPath(String path) {
		File files = new File(path);

		// make sure directory exists
		if (!files.exists()) {
			// System.out.println("Directory does not exist: " + path);
			// System.exit(0);
		} else {
			try {
				delete(files);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

	}

	public static void delete(File file) throws IOException {
		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
				// System.out.println("Directory is deleted : " +
				// file.getAbsolutePath());
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					// System.out.println("Directory is deleted : " +
					// file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			// file.delete();
			try {
				file.setWritable(true);
				file.delete();
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("File Not deleted : " + file.getAbsolutePath());
			}
		}
	}

	public static void createFolder(String pathString) {
		Path path = Paths.get(pathString);
		// if directory exists?
		if (!Files.exists(path)) {
			// crear el path
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				// fail to create directory
				e.printStackTrace();
			}
		}
	}

}
