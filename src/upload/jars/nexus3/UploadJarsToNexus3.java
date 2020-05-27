package upload.jars.nexus3;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UploadJarsToNexus3 {

	    public static void main(String[] args) throws IOException {
	    	Scanner src = new Scanner(System.in);
	    	
	    	System.out.println("Enter the local repository path: (Ex: C:/Users/MSIWKSUSR/.m2/repository) ");
	    	System.out.println(" -- Note: The root directory must be named 'repository'. -- ");
	    	String parentFolder = src.nextLine();
	    	
	    	System.out.println("Enter the nexus3 URL: (Ex: http://<username>:<password>@<hostname>:<port>/repository/rpa-releases/");
	    	String url = src.nextLine();
	    	
	    	StringBuilder sb = new StringBuilder();
	    	
	    	List <String> ls = UploadJarsHelper.getFilesRecursively(new File(parentFolder));
	    	int cnt = 0;
	    	
	    	String randmStr = "random58";
	    	
	        for (String file : ls) {
	        	if (file.endsWith(".jar")) {
	        		cnt++;
	        		String destPath = parentFolder;
	        		Map<String, String> returnMap = UploadJarsHelper.extractMavenParamsFromFilePath(file);
	        		String groupId = returnMap.get(UploadJarsHelper.GROUP_ID);
	        		String version = returnMap.get(UploadJarsHelper.VERSION);
	        		String jarfileName = returnMap.get(UploadJarsHelper.FILE_NAME);
	        		String artifactId = returnMap.get(UploadJarsHelper.ARTIFACT_ID);
	        		String classifier = returnMap.get(UploadJarsHelper.CLASSIFIER);
	        		
	        		destPath = destPath + File.separator + "TEMP" + File.separator + groupId + randmStr + File.separator + artifactId + randmStr +  File.separator + jarfileName;
	        		System.out.println(destPath);
	        		
	        		sb.append(
	        				"call mvn deploy:deploy-file -Dfile=" + destPath + 
		        				" -DgroupId=" + groupId + 
		        				" -DartifactId=" + artifactId + 
		        				" -Dversion=" + version)
	        			.append( (classifier != null && classifier.length() > 0) ? " -Dclassifier=" +classifier+ "" : "" )
	        			.append(" -Dpackaging=jar -DgeneratePom=false -DupdateReleaseInfo=true -Durl=" + url)
	        			.append("\r\n");
	        		
	        		UploadJarsHelper.copyFile(file, destPath);
	        	}
	        }
	        
	        sb.append("\r\n -- POMs \r\n");
	      
	        for (String file : ls) {
	        	if (file.endsWith(".pom")) {
	        		cnt++;
	        		String destPath = parentFolder;
	        		Map<String, String> returnMap = UploadJarsHelper.extractMavenParamsFromFilePath(file);
	        		String groupId = returnMap.get(UploadJarsHelper.GROUP_ID);
	        		String version = returnMap.get(UploadJarsHelper.VERSION);
	        		String jarfileName = returnMap.get(UploadJarsHelper.FILE_NAME);
	        		String artifactId = returnMap.get(UploadJarsHelper.ARTIFACT_ID);
					
					destPath = destPath + File.separator + "TEMP" + File.separator + groupId + randmStr + File.separator + artifactId + randmStr  + File.separator + jarfileName;
	        		System.out.println(destPath);
	        		
					sb.append(
						"call mvn deploy:deploy-file -Dfile=" + destPath 
								+ " -DgroupId=" + groupId 
								+ " -Dversion=" + version
								+ " -DartifactId=" + artifactId
								+ " -Dpackaging=pom -Durl=" + url)
	        		.append("\r\n");
					
					UploadJarsHelper.copyFile(file, destPath);
	        	}
	        }
	             
	        UploadJarsHelper.writeToFile(sb);
	        
	        System.out.println("Total Count: " + cnt);
	    }
}
