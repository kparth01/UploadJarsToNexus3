package upload.jars.nexus3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadJarsHelper {

	
	public final static String GROUP_ID = "groupId";
	public final static String VERSION = "version";
	public final static String ARTIFACT_ID = "artifactId";
	public final static String FILE_NAME = "fileName";
	public final static String CLASSIFIER = "classifier";

	private UploadJarsHelper(){}
	
	public static Map<String, String> extractMavenParamsFromFilePath(String file) {
		Map<String, String> returnMap = new HashMap<String, String>();
		
		String groupId = file.substring(file.indexOf("repository") + ("repository".length() + 1), nthLastIndex(file, File.separatorChar, 3));
		groupId = groupId.replaceAll ("\\\\", ".");
		
		String version = file.substring(nthLastIndex(file, File.separatorChar, 2) + 1, file.lastIndexOf(File.separator));
		String fileName = file.substring(file.lastIndexOf(File.separator) + 1);
		String classifier = getClassifier(fileName, version);
		
		returnMap.put(GROUP_ID, groupId);
		returnMap.put(VERSION, version);
		returnMap.put(FILE_NAME, fileName);
		returnMap.put(ARTIFACT_ID, file.substring(nthLastIndex(file, File.separatorChar, 3) + 1, nthLastIndex(file, File.separatorChar, 2)));
		returnMap.put(CLASSIFIER, classifier);
		

		return returnMap;
	}
	
	private static String getClassifier(String fileName, String version) {
		if (fileName != null && fileName.length() > 0 && version != null && version.length() > 0) {
			if (fileName.contains(version) && fileName.endsWith(".jar") && !fileName.endsWith(version + ".jar")) {
				return fileName.substring(fileName.indexOf(version) + version.length() + 1, fileName.indexOf(".jar"));
			}
			if (fileName.contains(version) && fileName.endsWith(".pom") && !fileName.endsWith(version + ".pom")) {
				return fileName.substring(fileName.indexOf(version) + version.length() + 1, fileName.indexOf(".pom"));
			}
		}
		return "";
	}
	
    public static List <String> getFilesRecursively(File dir) {
        List <String> ls = new ArrayList<String>();
        if (dir.isDirectory())
            for (File fObj : dir.listFiles()) {
                if(fObj.isDirectory()) {
                    ls.add(String.valueOf(fObj));
                    ls.addAll(getFilesRecursively(fObj));               
                } else {
                    ls.add(String.valueOf(fObj));       
                }
            }
        else
            ls.add(String.valueOf(dir));

        return ls;
    }
    
    public static void writeToFile(StringBuilder sb) {
    	try {
    	      FileWriter myWriter = new FileWriter("filename.txt");
    	      myWriter.write(sb.toString());
    	      myWriter.close();
    	      System.out.println("Successfully wrote to the file.");
    	    } catch (IOException e) {
    	      System.out.println("An error occurred.");
    	      e.printStackTrace();
    	    }	
    }
    
    public static int nthLastIndex(String str, char subStr, int lastIndexCnt) {
    	int len = str.length() - 1;
    	int occurenceCnt = 0;
    	for (int i = len; i >= 0 ; i--) {
    		char ch = str.charAt(i);
    		if (ch == subStr) {
    			occurenceCnt++;
    		}
    		if (occurenceCnt == lastIndexCnt) {
    			return i;
    		}
    	}    	
    	return 0;
    }
	
	public static void copyFile(String from, String to) throws IOException {
		Path src = Paths.get(from);
		Path dest = Paths.get(to);
		Files.createDirectories(dest.getParent());
		Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
	}
	
}
