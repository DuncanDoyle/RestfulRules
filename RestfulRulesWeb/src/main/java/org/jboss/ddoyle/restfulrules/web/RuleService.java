package org.jboss.ddoyle.restfulrules.web;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.ddoyle.restfulrules.model.Person;
import org.kie.api.KieBase;
import org.kie.api.cdi.KReleaseId;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a RESTful interface into the Drools {@link KieSession}.
 * <p/>
 * The actual {@link KieSession} is injected via CDI.
 * 
 * 
 */
@Path("/rules")
@ManagedBean
public class RuleService {

	/**
	 * SLF4J Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RuleService.class);

	//Inject a KieBase here because our KieSessions need to be threadsafe. So creating a new KieSession on every request.
	@Inject
	@KReleaseId(groupId = "org.jboss.ddoyle.restfulrules", artifactId = "RestfulRulesKnowledge", version = "1.0.0")
	private KieBase kieBase;

	@POST
	@Path("/fire")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getKBaseContent(Person person) {
		// TODO: Is this method thread-safe?
		LOGGER.info("Person: " + person.getName() + ":" + person.getSurname());
		KieSession kieSession = kieBase.newKieSession();
		try {
			kieSession.insert(person);
			LOGGER.info("Firing rules!!!");
			kieSession.fireAllRules();

			long factCount = kieSession.getFactCount();
			LOGGER.info("Number of facts in session: " + factCount);

			return Response.ok().build();
		} finally {
			kieSession.dispose();
		}
	}

}
