package com.sarangjoshi.rhsmustangs;

import org.htmlcleaner.*;

import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class WebParser {
	HtmlCleaner pageParser = new HtmlCleaner();
	CleanerProperties props = pageParser.getProperties();
	URL url;

	public enum Parse {
		TWITTER, FACEBOOK
	};

	public String parsePage(String myURL, Parse parseMethod)
			throws MalformedURLException {
		url = new URL(myURL);

		// Setting up properties
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);

		try {
			URLConnection connection = url.openConnection();
			TagNode node = pageParser.clean(new InputStreamReader(connection
					.getInputStream()));
			// XPath string for locating download links...
			String xPathExpression = "";
			// xPathExpression = "//div[@data-sigil='unit-container']";
			// xPathExpression = "//*[@class=\"tlAboveUnit\"]";
			// xPathExpression = "//*[@id=\"recent\"]/div/div";
			if (parseMethod == Parse.TWITTER) {
				xPathExpression = "//body";// "//div[@class=\"timeline\"]";
			}
			try {
				Object[] downloadNodes = node.evaluateXPath(xPathExpression);

				String text = "";

				if (downloadNodes.length >= 1) {
					TagNode node2 = (TagNode) downloadNodes[0];
					text = pageParser.getInnerHtml(node2);
					text = text.substring(500);
					return text; // getTextAfterKeyWord(text, "tlActorText");
				} else {
					return "No xPath matches";
				}
			} catch (XPatherException e) {
				Log.e("ERROR", e.getMessage());
			}
		} catch (IOException e) {
			Log.e("ERROR", e.getMessage());
		}
		return "";
	}

	private String getTextAfterKeyWord(String bodyContent, String keywordS) {
		String text = "";

		// keyWord is the key word that we are looking for.
		char[] keyWord = keywordS.toCharArray();
		// keyPlace is the buffer which is constantly updated
		char[] keyPlace = new char[keyWord.length];

		// this is set to be true when the parser reaches the keyWord
		boolean hasReachedKeyWord = false;

		int i = 0;

		// fills up keyPlace with the first .length characters
		for (i = 0; i < keyPlace.length; i++) {
			keyPlace[i] = bodyContent.charAt(i);
		}

		char currentChar = '\u0000';
		for (i = keyWord.length; i < bodyContent.length(); i++) {
			currentChar = bodyContent.charAt(i);
			if (!hasReachedKeyWord) {
				shiftLeft(keyPlace, currentChar);
			} else {
				text += currentChar;
			}

			if (new String(keyPlace).equals(new String(keyWord))) {
				hasReachedKeyWord = true;
			}
		}

		return text;
	}

	private void shiftLeft(char[] charArray, char newChar) {
		for (int i = 0; i < charArray.length - 1; i++)
			charArray[i] = charArray[i + 1];
		charArray[charArray.length - 1] = newChar;
	}

	private String cleanText(String text) {
		String x = "";
		char currentChar = '\u0000';
		char[] currentString = new char[6];
		for (int i = 0; i < text.length(); i++) {
			currentChar = text.charAt(i);

			if (currentChar == '\u003c') {
				x += '<';
			}

		}
		return x;
	}

	private TagNode[] convertToTagNodes(Object[] downloadNodes) {
		TagNode[] tagNodes = new TagNode[downloadNodes.length];
		for (int i = 0; i < downloadNodes.length; i++) {
			tagNodes[i] = (TagNode) downloadNodes[i];
		}
		return tagNodes;
	}
}
