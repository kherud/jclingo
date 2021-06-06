package org.potassco.util;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Josef Schneeberger
 *
 */
public class PropertyTree {
	private Document document;
	private Map<Integer,Node> current = new HashMap<Integer, Node>();
	private Element currentElement;
	
	public PropertyTree(String rootElementName) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.newDocument();
			// root element
			currentElement = document.createElement(rootElementName);
			current.put(0, document.appendChild(currentElement));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void addNode(String name, String description, int depth) {
		currentElement = document.createElement(name);
        if (description != null) {
			Attr descAttr = document.createAttribute("description");
			descAttr.setValue(description);
			currentElement.setAttributeNode(descAttr);
		}
		current.get(depth).appendChild(currentElement);
        current.put(depth + 1, currentElement);
	}

	protected void addAttribute(String attributeName, String value) {
		Attr attr = document.createAttribute(attributeName);
		attr.setValue(value);
		currentElement.setAttributeNode(attr);
	}

	protected void addIndex(int j, String description, int depth) {
		currentElement = document.createElement("index");
        Attr idAttr = document.createAttribute("id");
        idAttr.setValue("" + j);
        currentElement.setAttributeNode(idAttr);
        if (description != null) {
			Attr descAttr = document.createAttribute("description");
			descAttr.setValue(description);
			currentElement.setAttributeNode(descAttr);
		}
		current.get(depth).appendChild(currentElement);
        current.put(depth + 1, currentElement);
	}

	protected void addValue(double value, int depth) {
		current.get(depth).appendChild(document.createTextNode("" + value));
	}

	protected void addValue(String value, int depth) {
		current.get(depth).appendChild(document.createTextNode(value));
	}
	
	public void showXml() {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			
			DOMSource source = new DOMSource(document);
			StringWriter sw = new StringWriter();
			transformer.transform(source, new StreamResult(sw));
			System.out.println(sw.toString());
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// TODO make generic!
	public double queryXpathAsDouble(String xPathExpression) {
        XPath xpath = XPathFactory.newInstance().newXPath();
        double result = 0.0;
		try {
			result = (double) xpath.evaluate(xPathExpression, document, XPathConstants.NUMBER);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String queryXpathAsString(String xPathExpression) {
        XPath xpath = XPathFactory.newInstance().newXPath();
		String result = null;
		try {
			result = (String) xpath.evaluate(xPathExpression, document, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
