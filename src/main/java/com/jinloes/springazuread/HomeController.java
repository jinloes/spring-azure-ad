package com.jinloes.springazuread;

import com.microsoft.aad.adal4j.AuthenticationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.invoke.MethodHandles;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by jinloes on 6/21/17.
 */
@Controller
public class HomeController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@RequestMapping("/greeting")
	public String greeting(HttpServletRequest httpRequest, @RequestParam(value = "name", required = false,
			defaultValue = "World") String name, Model model) {
		HttpSession session = httpRequest.getSession();
		AuthenticationResult result = (AuthenticationResult) session.getAttribute(AuthHelper.PRINCIPAL_SESSION_NAME);
		LOGGER.info("Token: {}", result);
		model.addAttribute("name", name);
		return "greeting";
	}

}
