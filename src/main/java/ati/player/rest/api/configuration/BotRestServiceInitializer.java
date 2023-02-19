package ati.player.rest.api.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

//@PropertySource("classpath:log.properties")
public class BotRestServiceInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] {
			BotRestServiceInitializer.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] {
			BotRestServiceConfiguration.class
		};
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {
			"/"
		};
	}

}
