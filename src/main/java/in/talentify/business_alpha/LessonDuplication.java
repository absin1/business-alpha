package in.talentify.business_alpha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

public class LessonDuplication {
	private static String serverType = "windows";
	private static String OldMediaLessonPath = "/var/www/html/lessonXMLs/";
	private static String NewMediaLessonPath = "/var/www/html/newLessonXMLs/";

	public static void main(String args[]) throws IOException {
		new LessonDuplication().action();
	}

	public void action() throws IOException {
		int oldLessonId = 1234;
		int newLessonId = 9876;
		System.out.println(oldLessonId + " >> " + newLessonId);
		if (checkXMLExists(oldLessonId)) {
			copyLessonXMLFolder(oldLessonId, newLessonId);
		}
	}

	/**
	 * Copies lessonXML folder with all its inner content, this is the main
	 * function for all the copying action
	 * 
	 * @param oldLessonId
	 *            The Id of the lesson in business.talentify.in
	 * @param newLessonId
	 *            The ID of the lesson in the alpha.talentify.in
	 */
	public void copyLessonXMLFolder(int oldLessonId, int newLessonId) throws IOException {
		MediaUploadServices services = new MediaUploadServices();
		Set<PosixFilePermission> perms = services.getPermissions();
		String sourcePath = OldMediaLessonPath + "" + oldLessonId + "/" + oldLessonId + "/";
		String destPath = NewMediaLessonPath + "" + newLessonId + "/" + newLessonId + "/";
		Boolean newLessonFolderExists = createLessonFolder(newLessonId);
		File source = new File(sourcePath);
		if (newLessonFolderExists) {
			if (source.exists() && source.isDirectory()) {
				File[] sourceFiles = source.listFiles();
				for (File sourceFile : sourceFiles) {
					if (sourceFile.isFile()) {
						if (FilenameUtils.getExtension(sourceFile.getAbsolutePath()).equalsIgnoreCase("xml")) {
							File destFile = new File(destPath + newLessonId + ".xml");
							copyFileUsingStream(sourceFile, destFile);

							String readLessonXML = readLessonXML(oldLessonId);
							String regex = "/" + oldLessonId + "/" + oldLessonId;
							String replacement = "/" + newLessonId + "/" + newLessonId;
							readLessonXML = readLessonXML.replaceAll(regex, replacement);
							saveLessonXML(readLessonXML, newLessonId);

							if (serverType.equalsIgnoreCase("linux")) {
								Files.setPosixFilePermissions(destFile.toPath(), perms);
							}
						} else {
							File destFile = new File(destPath + sourceFile.getName());
							copyFileUsingStream(sourceFile, destFile);
							if (serverType.equalsIgnoreCase("linux")) {
								Files.setPosixFilePermissions(destFile.toPath(), perms);
							}
						}
					} else {
						System.err.println(sourceFile.getAbsolutePath() + " is not a file");
					}
				}

			} else {
				System.err.println("Lesson xml folder for old lesson " + oldLessonId + " doesn't exist");
			}
		} else

		{
			System.err.println("Lesson creation folder for new lesson " + newLessonId + " failed!");
		}

	}

	/**
	 * This method checks if the xml file for the oldLessonId exists or not
	 * 
	 * @return boolean A true if the xml file is found in the file system or
	 *         false otherwise
	 * @param lessonID
	 *            The Id of the lesson in business.talentify.in
	 */
	private boolean checkXMLExists(int lessonID) {

		String sourcePath = OldMediaLessonPath + "" + lessonID + "/" + lessonID + "/" + lessonID + ".xml";
		File source = new File(sourcePath);
		if (source.exists() && source.isFile()) {
			System.out.println("Lesson " + lessonID + "folder found");
			return true;
		} else {
			System.err.println("Lesson xml folder for " + lessonID + " doesn't exist");
			return false;
		}

	}

	/**
	 * Creates lessonXML folder for the new lesson
	 * 
	 * @param newLessonId
	 *            The ID of the lesson in the alpha.talentify.in
	 */
	public Boolean createLessonFolder(int newLessonId) throws IOException {
		Boolean success = false;
		MediaUploadServices services = new MediaUploadServices();
		Set<PosixFilePermission> perms = services.getPermissions();
		Path path = Paths.get(NewMediaLessonPath + newLessonId + "/" + newLessonId);
		if (Files.exists(path)) {
			success = true;
		} else {
			Files.createDirectories(path);
			if (serverType.equalsIgnoreCase("linux")) {
				Files.setPosixFilePermissions(path, perms);
				Files.setPosixFilePermissions(Paths.get(NewMediaLessonPath + newLessonId), perms);
			}
			success = true;
		}
		return success;
	}

	/**
	 * Creates lessonXML folder for the new lesson
	 * 
	 * @param source
	 *            A java.io.File object from which stuff will be copied
	 * @param dest
	 *            A java.io.File object to which stuff will be copied
	 */
	public void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			if (!(is == null)) {
				is.close();
			}
			if (!(os == null)) {
				os.close();
			}
		}
	}

	/**
	 * Reads lessonXML folder for the old lesson
	 * 
	 * @param lessonId
	 *            AThe ID of the lesson in the old system
	 */
	public static String readLessonXML(int lessonId) throws IOException {
		String lessonXML = "";
		String lessonXMLPath = "";
		lessonXMLPath = OldMediaLessonPath + lessonId + "/" + lessonId + "/" + lessonId + ".xml";
		File f = new File(lessonXMLPath);
		if (f.exists() && !f.isDirectory()) {
			BufferedReader br = null;
			FileReader fr = null;
			fr = new FileReader(lessonXMLPath);
			br = new BufferedReader(fr);
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line.trim());
			}
			if (br != null)
				br.close();
			if (fr != null)
				fr.close();
			lessonXML = sb.toString();
		}
		return lessonXML;
	}

	/**
	 * Saves the lesson XML for the newly created lesson
	 * 
	 * @param lessonXML
	 *            The string lessonXML of the lesson
	 * @param lessonId
	 *            The ID of the lesson in the new system
	 */
	public Boolean saveLessonXML(String lessonXML, int newLessonId) throws IOException {
		Boolean success = false;
		String lessonXMLPath = "";
		if (!lessonXML.trim().equalsIgnoreCase("")) {
			lessonXMLPath = NewMediaLessonPath + newLessonId + "/" + newLessonId + "/" + newLessonId + ".xml";
			Writer out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(lessonXMLPath), "UTF-8"));
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				out.write(lessonXML);
				success = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				out.close();
			}
		} else {
			System.err.println("Lesson folder could not be created or lesson XML is empty!");
		}
		return success;
	}
}
