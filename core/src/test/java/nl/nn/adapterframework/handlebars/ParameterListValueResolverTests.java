package nl.nn.adapterframework.handlebars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Context;

import nl.nn.adapterframework.parameters.Parameter;
import nl.nn.adapterframework.parameters.Parameter.ParameterType;
import nl.nn.adapterframework.parameters.ParameterList;
import nl.nn.adapterframework.parameters.ParameterValueList;
import nl.nn.adapterframework.testutil.ParameterBuilder;
import nl.nn.adapterframework.util.XmlUtils;

public class ParameterListValueResolverTests {

	@Test()
	public void nonParameterValueListTypeShouldReturnUnresolved() throws Exception {
		Parameter notAParameterValueList = new Parameter();
		ParameterListValueResolver resolver = ParameterListValueResolver.INSTANCE;

		Object result = resolver.resolve(notAParameterValueList, "test");

		assertSame(result, resolver.UNRESOLVED);
	}

	@Test()
	public void emptyParameterValueListTypeShouldReturnUnresolved() throws Exception {
		ParameterList pl = new ParameterList();
		ParameterValueList pvl = ParameterBuilder.getPVL(pl);
		ParameterListValueResolver resolver = ParameterListValueResolver.INSTANCE;

		Object result = resolver.resolve(pvl, "test");

		assertSame(result, resolver.UNRESOLVED);
	}

	@Test()
	public void StringParameterShouldReturnStringValue() throws Exception {
		ParameterList pl = new ParameterList();
		pl.add(ParameterBuilder.create().withName("test").withValue("value").withType(ParameterType.STRING));
		ParameterValueList pvl = ParameterBuilder.getPVL(pl);

		Context context = Context
				.newBuilder(pvl)
				.resolver(ParameterListValueResolver.INSTANCE)
				.build();

		Object result = context.get("test");

		assertNotNull(context);
		assertTrue(result instanceof String);
		assertEquals("value", result);
	}

	@Test()
	public void BooleanParameterShouldReturnBooleanValue() throws Exception {
		ParameterList pl = new ParameterList();
		pl.add(ParameterBuilder.create().withName("test").withValue("true").withType(ParameterType.BOOLEAN));
		ParameterValueList pvl = ParameterBuilder.getPVL(pl);

		Context context = Context
				.newBuilder(pvl)
				.resolver(ParameterListValueResolver.INSTANCE)
				.build();

		Object result = context.get("test");

		assertNotNull(context);
		assertTrue(result instanceof Boolean);
		assertEquals(true, result);
	}

	@Test()
	public void NodeParameterShouldReturnNodeObject() throws Exception {
		ParameterList pl = new ParameterList();
		pl.add(ParameterBuilder.create().withName("test").withValue("<root>value</root>").withType(ParameterType.NODE));
		ParameterValueList pvl = ParameterBuilder.getPVL(pl);
		ParameterListValueResolver resolver = ParameterListValueResolver.INSTANCE;

		Object result = resolver.resolve(pvl, "test");

		assertTrue(result instanceof Node);
		assertEquals("root", ((Node)result).getNodeName());
		assertEquals("value", ((Node)result).getFirstChild().getNodeValue());
	}

	@Test()
	public void DomdocParameterShouldReturnDomdocObject() throws Exception {
		ParameterList pl = new ParameterList();
		pl.add(ParameterBuilder.create().withName("test").withValue("<root>value</root>").withType(ParameterType.DOMDOC));
		ParameterValueList pvl = ParameterBuilder.getPVL(pl);
		ParameterListValueResolver resolver = ParameterListValueResolver.INSTANCE;

		Object result = resolver.resolve(pvl, "test");

		assertTrue(result instanceof Document);
		assertEquals("root", ((Node)result).getFirstChild().getNodeName());
		assertEquals("value", ((Node)result).getFirstChild().getFirstChild().getNodeValue());
	}

	@Test()
	public void JsonParameterShouldReturnJsonObject() throws Exception {
		ParameterList pl = new ParameterList();

		pl.add(ParameterBuilder.create().withName("test").withValue("{\"firstName\":\"John\", \"lastName\":\"Smith\"}").withType(ParameterType.STRING));
		ParameterValueList pvl = ParameterBuilder.getPVL(pl);
		ParameterListValueResolver resolver = ParameterListValueResolver.INSTANCE;

		Object result = resolver.resolve(pvl, "test");

		assertTrue(result instanceof String);
		assertEquals("{\"firstName\":\"John\", \"lastName\":\"Smith\"}", (String)result);
	}

}