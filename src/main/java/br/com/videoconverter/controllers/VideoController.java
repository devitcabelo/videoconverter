package br.com.videoconverter.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import br.com.videoconverter.infra.FileSaver;

@Controller
public class VideoController {

	@Autowired
	FileSaver fileSaver;

	@RequestMapping(value="/converter", method= RequestMethod.POST, name="converter")
	public ModelAndView converter(MultipartFile fileToConvert) {
		String amazonPathToFileToConvert;
		
		amazonPathToFileToConvert = fileSaver.write(fileToConvert);
		
		ModelAndView videoToView = new ModelAndView("redirect:resultado");		
		
		return videoToView;
	}
	
}
