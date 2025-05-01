package nl.nn.adapterframework.handlebars;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.github.jknack.handlebars.TagType;
import com.github.jknack.handlebars.Template;

import nl.nn.adapterframework.configuration.ConfigurationException;
import nl.nn.adapterframework.core.ConfiguredTestBase;
import nl.nn.adapterframework.core.PipeRunResult;
import nl.nn.adapterframework.handlebars.HandlebarsEngine;
import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.Parameter.ParameterType;
import nl.nn.adapterframework.testutil.ParameterBuilder;
import nl.nn.adapterframework.util.XmlUtils;

public class HandlebarsEngineTests extends ConfiguredTestBase{

	public HandlebarsEngine createHandlebarsEngine() throws BeansException {
		HandlebarsEngine he = new HandlebarsEngine();
		this.autowireByType(he);

		return he;
	}

	@Test()
	public void testCompileInlineWithNonEmptySourceShouldReturnValidTemplate() throws Exception {
		HandlebarsEngine he = createHandlebarsEngine();
		Template template = null;

		template = he.compileInline("{{test}}");

		assertNotNull(template);
		assertEquals("test", template.collect(TagType.VAR).get(0));
	}

	@Test()
	public void testCompileInlineWithEmptySourceReturnsValidTemplate() throws Exception {
		HandlebarsEngine he = createHandlebarsEngine();
		Template template = null;

		template = he.compileInline("");

		assertNotNull(template);
		assertEquals(Collections.EMPTY_LIST, template.collect(TagType.VAR));
	}

	@Test()
	public void testCompileInlineWithNullSourceThrowsNullPointerException() throws Exception {
		HandlebarsEngine he = createHandlebarsEngine();

		assertThrows(NullPointerException.class, () -> he.compileInline(null));
	}
}