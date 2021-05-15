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
public class StatisticsTree {

	private Document document;
	private Map<Integer,Node> current = new HashMap<Integer, Node>();
	
	public StatisticsTree() {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.newDocument();
			// root element
			Element rootElement = document.createElement("ClingoStatistics");
			current.put(0, document.appendChild(rootElement));
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addNode(String name, int depth) {
		Element e = document.createElement(name);
		current.get(depth).appendChild(e);
        current.put(depth + 1, e);
	}

	public void addIndex(int j, int depth) {
        Element e = document.createElement("index");
        Attr attr = document.createAttribute("id");
        attr.setValue("" + j);
        e.setAttributeNode(attr);
		current.get(depth).appendChild(e);
        current.put(depth + 1, e);
	}

	public void addValue(double value, int depth) {
		current.get(depth).appendChild(document.createTextNode("" + value));
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
	public double queryXpath(String xPathExpression ) {
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
	
}