package rdapit.rest;

import org.apache.log4j.Logger;

import rdapit.pitservice.TypingService;

/**
 * Singleton. Holds information persistent throughout the life time of the server. 
 */
public class ApplicationContext {

	public static ApplicationContext instance;
	
	private TypingService typingService;

	private static final Logger logger = Logger.getLogger(ApplicationContext.class);

	public ApplicationContext(TypingService typingService) {
		instance = this;
		this.typingService = typingService;
	}

	public static ApplicationContext getInstance() {
		if (ApplicationContext.instance == null)
			throw new IllegalStateException("Singleton not initialized!");
		return instance;
	}
	
	public TypingService getTypingService() {
		return typingService;
	}
	
}
