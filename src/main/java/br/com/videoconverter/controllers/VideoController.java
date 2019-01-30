package br.com.videoconverter.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.videoconverter.infra.FileSaver;

@Controller
public class VideoController {

	@Autowired
	FileSaver fileSaver;

	@RequestMapping(value="/converter", method= RequestMethod.POST, name="converter")
	public ModelAndView converter(MultipartFile fileToConvert, 
			RedirectAttributes redirectAttributes) {
		String amazonPathToFileToConvert, state;
		String[] idAndPath = new String[2];
		
		amazonPathToFileToConvert = fileSaver.write(fileToConvert);
		idAndPath = requestEncodeJob(amazonPathToFileToConvert);
		state = waitEncodeJobDone(idAndPath[0]);
		
		redirectAttributes.addFlashAttribute("resultUrl", idAndPath[1]);
		return new ModelAndView("redirect:result");		
	}
	
	@RequestMapping("result")
	public ModelAndView result() {
		return new ModelAndView("home");
	}
	
	public String[] requestEncodeJob(String pathToFileToEncode) {
		JsonObject requestParams = new JsonObject();
		JsonObject responseJson;
		String pathToEncodedFile = "", jobId = "";
		String[] returnIdAndPath = new String[2];
		
		requestParams.addProperty("test", true);
		requestParams.addProperty("input", pathToFileToEncode);
		
		try {
			URL obj = new URL("https://app.zencoder.com/api/v2/jobs");
		    
		    HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
			postConnection.setRequestMethod("POST");

		    postConnection.setRequestProperty("Zencoder-Api-Key", "24c328a62abd7db549b531273d07d7dc");
		    postConnection.setRequestProperty("Content-Type", "application/json");

		    postConnection.setDoOutput(true);
		    OutputStream os = postConnection.getOutputStream();
		    os.write(requestParams.toString().getBytes());
		    os.flush();
		    os.close();

		    responseJson = getResponseFromConection(postConnection);
		    pathToEncodedFile = responseJson.get("outputs").getAsJsonArray().get(0)
		    		.getAsJsonObject().get("url").getAsString();
	        jobId = responseJson.get("id").getAsString();

		    
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		returnIdAndPath[0] = jobId;
		returnIdAndPath[1] = pathToEncodedFile;
		return returnIdAndPath;

	}
	
	public String waitEncodeJobDone(String jobId) {
		JsonObject responseJson;
		String state = "";
		
		do {
        	try {
				TimeUnit.SECONDS.sleep(3);
				URL urlJobId = new URL("https://app.zencoder.com/api/v2/jobs/" + jobId + "/progress");
	    	    
	        	HttpURLConnection getConnection = (HttpURLConnection) urlJobId.openConnection();
	        	getConnection.setRequestMethod("GET");
	        	getConnection.setRequestProperty("Zencoder-Api-Key", "24c328a62abd7db549b531273d07d7dc");

	        	responseJson = getResponseFromConection(getConnection );
	        	state = responseJson.get("state").getAsString();
	        	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }while(!state.equals("finished") && !state.equals("failed"));
		return state;
	}
	
	public JsonObject getResponseFromConection(HttpURLConnection postConnection) {
		InputStreamReader inputStreamReader;
		
		String inputLine;
        StringBuffer response = new StringBuffer();
        JsonParser jsonParser = new JsonParser();
        
        BufferedReader in;
        
		try {
			inputStreamReader = new InputStreamReader(
			        postConnection.getInputStream());
			in = new BufferedReader(inputStreamReader);

			while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        } in.close();
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        JsonObject objectFromString = jsonParser.parse(response.toString()).getAsJsonObject();
        
		return objectFromString;
	}
	
}
