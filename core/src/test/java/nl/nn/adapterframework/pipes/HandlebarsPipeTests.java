package nl.nn.adapterframework.pipes;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.Parameter.ParameterType;
import nl.nn.adapterframework.testutil.ParameterBuilder;
import nl.nn.adapterframework.util.XmlUtils;

public class HandlebarsPipeTests extends PipeTestBase<HandlebarsPipe> {

	@Override
	public HandlebarsPipe createPipe() throws ConfigurationException {
		return new HandlebarsPipe();
	}

	public Parameter setUp(String name){
		session.put(name,"value");
		return ParameterBuilder.create().withName(name).withValue("abs").withSessionKey("*");
	}

	@Test(expected = ConfigurationException.class)
	public void testNoTemplateSourceShouldThrowConfigurationException() throws Exception {
		pipe.configure();

		fail();
	}

	@Test(expected = ConfigurationException.class)
	public void testMultipleTemplateSourcesShouldThrowConfigurationException() throws Exception {
		pipe.setTemplateFile("file");
		pipe.setTemplateName("name");
		pipe.setTemplateNameSessionKey("sessionKey");

		pipe.configure();

		fail();
	}

//	@Test
//	public void testInputShouldInContextWhenUseInputAsContextEnabled() throws Exception {
//		pipe.setTemplateSessionKey("test");
//		pipe.setUseInputAsContext(true);
//	
//		exception.expect(ConfigurationException.class);
//		pipe.configure();
//		
//		fail();
//	}

	@Test(expected = ConfigurationException.class)
	public void testTemplateFileNotFoundShouldThrowConfigurationException() throws Exception {
		pipe.setTemplateFile("notFound.hbs");

		pipe.configure();

		fail();
	}

	@Test(expected = ConfigurationException.class)
	public void testTemplateNameNotFoundShouldThrowConfigurationException() throws Exception {
		pipe.setTemplateName("notFound");

		pipe.configure();

		fail();
	}

	@Test
	public void testParameterListContextShouldResolve() throws Exception {
		pipe.setTemplateFile("/HandlebarsPipe/simple-flat-variables.hbs");
		pipe.addParameter(setUp("name"));
		pipe.addParameter(setUp("description"));
		pipe.configure();
		String expected = 
				"<root>\r\n" +
				"	<name>testname</name>\r\n" +
				"	<description>testdescription</description>\r\n" +
				"</root>\r\n";

		session.put("name", "testname");
		session.put("description", "testdescription");

		PipeRunResult result = doPipe(pipe, "test", session);
		assertEquals("success", result.getPipeForward().getName());
		assertEquals(expected, result.getResult().asString());
	}

	@Test
	public void testParameterContextShouldResolveFromDomDocParameter() throws Exception {
		pipe.setTemplateFile("/HandlebarsPipe/simple-nested-variables.hbs");
		Document domdoc = XmlUtils.buildDomDocument(
				"<adapters>\n" +
				"	<adapter>\n" +
				"		<receiver>\n" +
				"			<name>receiver-name</name>\n" +
				"			<apilistener>\n" +
				"				<name>apilistener-name</name>\n" +
				"				<description>apilistener-description</description>\n" +
				"			</apilistener>\n" +
				"		</receiver>\n" +
				"	</adapter>\n" +
				"</adapters>\n"
				);
		String expectedDomdoc =
				"<Module>\r\n" +
				"	<Adapter>\r\n" +
				"		<Receiver name=\"receiver-name\">\r\n" +
				"			<ApiListener\r\n" +
				"				name=\"apilistener-name\"\r\n" +
				"				description=\"apilistener-description\"\r\n" +
				"				/>\r\n" +
				"		</Receiver>\r\n" +
				"	</Adapter>\r\n" +
				"</Module>\r\n";

		pipe.addParameter(
				ParameterBuilder.create()
				.withName("root")
				.withType(ParameterType.DOMDOC)
				.withSessionKey("*")
				);
		pipe.configure();
		session.put("root", domdoc);

		PipeRunResult result = doPipe(pipe, "test", session);
		assertEquals("success", result.getPipeForward().getName());
		assertEquals(expectedDomdoc, result.getResult().asString());
	}

//	@Test
//	public void testTemplateFileNotFoundShouldThrowConfigurationException() throws Exception {
//		
//		session.put(name,"value");
//		Parameter param = ParameterBuilder.create().withName("context").withValue("abs").withSessionKey("*");
//		pipe.addParameter(param);
//
//		pipe.configure();
//		PipeRunResult res = doPipe(pipe, "whatisthis", session);
//		assertThrows(ConfigurationException.class, pipe.configure());
//		exception.expect(PipeRunException.class);
//		assertEquals("inside the file", res.getResult().asString());
//	}

}
