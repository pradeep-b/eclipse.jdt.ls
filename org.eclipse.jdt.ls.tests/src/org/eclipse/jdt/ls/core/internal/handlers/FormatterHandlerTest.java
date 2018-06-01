/*******************************************************************************
 * Copyright (c) 2017-2018 Red Hat Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ls.core.internal.handlers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ls.core.internal.JDTUtils;
import org.eclipse.jdt.ls.core.internal.JavaLanguageServerTestPlugin;
import org.eclipse.jdt.ls.core.internal.ResourceUtils;
import org.eclipse.jdt.ls.core.internal.TextEditUtil;
import org.eclipse.jdt.ls.core.internal.managers.FormatterManager;
import org.eclipse.lsp4j.DocumentFormattingParams;
import org.eclipse.lsp4j.DocumentOnTypeFormattingParams;
import org.eclipse.lsp4j.DocumentRangeFormattingParams;
import org.eclipse.lsp4j.FormattingOptions;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.TextEdit;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.Bundle;

/**
 * @author Fred Bricon
 */
@RunWith(MockitoJUnitRunner.class)
public class FormatterHandlerTest extends AbstractCompilationUnitBasedTest {

	@Test
	public void testDocumentFormatting() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			"package org.sample   ;\n\n" +
			"      public class Baz {  String name;}\n"
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces
		DocumentFormattingParams params = new DocumentFormattingParams(textDocument, options);
		List<? extends TextEdit> edits = server.formatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			"package org.sample;\n"
			+ "\n"
			+ "public class Baz {\n"
			+ "    String name;\n"
			+ "}\n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test
	public void testJavaFormatEnable() throws Exception {
		String text =
		//@formatter:off
				"package org.sample   ;\n\n" +
				"      public class Baz {  String name;}\n";
			//@formatter:on"
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java", text);
		preferenceManager.getPreferences().setJavaFormatEnabled(false);
		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces
		DocumentFormattingParams params = new DocumentFormattingParams(textDocument, options);
		List<? extends TextEdit> edits = server.formatting(params).get();
		assertNotNull(edits);
		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(text, newText);
	}

	@Test
	public void testDocumentFormattingWithTabs() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			"package org.sample;\n\n" +
			"public class Baz {\n"+
			"    void foo(){\n"+
			"}\n"+
			"}\n"
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(2, false);// ident == tab
		DocumentFormattingParams params = new DocumentFormattingParams(textDocument, options);
		List<? extends TextEdit> edits = server.formatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			"package org.sample;\n"+
			"\n"+
			"public class Baz {\n"+
			"\tvoid foo() {\n"+
			"\t}\n"+
			"}\n";
		//@formatter:on
		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}


	@Test
	public void testFormatting_onOffTags() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			"package org.sample;\n\n" +
			"      public class Baz {\n"+
			"// @formatter:off\n"+
			"\tvoid foo(){\n"+
			"    }\n"+
			"// @formatter:on\n"+
			"}\n"
		//@formatter:off
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, false);// ident == tab
		DocumentFormattingParams params = new DocumentFormattingParams(textDocument, options);
		List<? extends TextEdit> edits = server.formatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			"package org.sample;\n\n" +
			"public class Baz {\n"+
			"// @formatter:off\n"+
			"\tvoid foo(){\n"+
			"    }\n"+
			"// @formatter:on\n"+
			"}\n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);

	}

	@Test
	public void testRangeFormatting() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			"package org.sample;\n" +
			"      public class Baz {\n"+
			"\tvoid foo(){\n" +
			"    }\n"+
			"	}\n"
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);

		Range range = new Range(new Position(2, 0), new Position(3, 5));// range around foo()
		DocumentRangeFormattingParams params = new DocumentRangeFormattingParams(range);
		params.setTextDocument(textDocument);
		params.setOptions(new FormattingOptions(3, true));// ident == 3 spaces

		List<? extends TextEdit> edits = server.rangeFormatting(params).get();
		//@formatter:off
		String expectedText =
			"package org.sample;\n" +
			"      public class Baz {\n"+
			"   void foo() {\n" +
			"   }\n"+
			"	}\n";
		//@formatter:on
		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test
	public void testDocumentFormattingWithCustomOption() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
			//@formatter:off
			"@Deprecated package org.sample;\n\n" +
				"public class Baz {\n"+
				"    /**Java doc @param a some parameter*/\n"+
				"\tvoid foo(int a){;;\n"+
				"}\n"+
				"}\n"
			//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(2, true);
		options.putNumber("org.eclipse.jdt.core.formatter.blank_lines_before_package", Integer.valueOf(2));
		options.putString("org.eclipse.jdt.core.formatter.insert_new_line_after_annotation_on_package", "do not insert");
		options.putBoolean("org.eclipse.jdt.core.formatter.put_empty_statement_on_new_line", Boolean.TRUE);
		DocumentFormattingParams params = new DocumentFormattingParams(textDocument, options);
		preferenceManager.getPreferences().setJavaFormatComments(false);
		List<? extends TextEdit> edits = server.formatting(params).get();
		assertNotNull(edits);

		String expectedText =
			"\n"+
				"\n"+
				"@Deprecated package org.sample;\n"+
				"\n"+
				"public class Baz {\n"+
				"  /**Java doc @param a some parameter*/\n"+
				"  void foo(int a) {\n"+
				"    ;\n"+
				"    ;\n"+
				"  }\n"+
				"}\n";
		String newText = TextEditUtil.apply(unit, edits);
		preferenceManager.getPreferences().setJavaFormatComments(true);
		assertEquals(expectedText, newText);
	}

	@Test
	public void testGoogleFormatter() throws Exception {
		try {
			String text =
			//@formatter:off
					"package org.sample;\n\n" +
					"public class Baz {\n" +
					"  String name;\n" +
					"}\n";
				//@formatter:on"
			ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java", text);
			String uri = JDTUtils.toURI(unit);
			TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
			FormattingOptions options = new FormattingOptions(2, true);// ident == 2 spaces
			DocumentFormattingParams params = new DocumentFormattingParams(textDocument, options);
			Bundle bundle = Platform.getBundle(JavaLanguageServerTestPlugin.PLUGIN_ID);
			URL googleFormatter = bundle.getEntry("/formatter resources/eclipse-java-google-style.xml");
			URL url = FileLocator.resolve(googleFormatter);
			preferenceManager.getPreferences().setFormatterUrl(url.toExternalForm());
			FormatterManager.configureFormatter(preferenceManager, projectsManager);
			List<? extends TextEdit> edits = server.formatting(params).get();
			assertNotNull(edits);
			String newText = TextEditUtil.apply(unit, edits);
			assertEquals(text, newText);
		} finally {
			preferenceManager.getPreferences().setFormatterUrl(null);
			FormatterManager.configureFormatter(preferenceManager, projectsManager);
		}
	}

	@Test
	public void testGoogleFormatterFilePath() throws Exception {
		try {
			String text =
			//@formatter:off
					"package org.sample;\n\n" +
					"public class Baz {\n" +
					"  String name;\n" +
					"}\n";
				//@formatter:on"
			ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java", text);
			String uri = JDTUtils.toURI(unit);
			TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
			FormattingOptions options = new FormattingOptions(2, true);// ident == 2 spaces
			DocumentFormattingParams params = new DocumentFormattingParams(textDocument, options);
			Bundle bundle = Platform.getBundle(JavaLanguageServerTestPlugin.PLUGIN_ID);
			URL googleFormatter = bundle.getEntry("/formatter resources/eclipse-java-google-style.xml");
			URL url = FileLocator.resolve(googleFormatter);
			File file = ResourceUtils.toFile(URIUtil.toURI(url));
			preferenceManager.getPreferences().setFormatterUrl(file.getAbsolutePath());
			FormatterManager.configureFormatter(preferenceManager, projectsManager);
			List<? extends TextEdit> edits = server.formatting(params).get();
			assertNotNull(edits);
			String newText = TextEditUtil.apply(unit, edits);
			assertEquals(text, newText);
		} finally {
			preferenceManager.getPreferences().setFormatterUrl(null);
			FormatterManager.configureFormatter(preferenceManager, projectsManager);
		}
	}

	@Test // typing ; should format the current line
	public void testFormattingOnTypeSemiColumn() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			  "package org.sample;\n\n"
			+ "public class Baz {  \n"
			+ "String          name       ;\n"//typed ; here
			+ "}\n"
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(3, 27), ";");
		params.setTextDocument(textDocument);
		params.setOptions(options);

		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(true);
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);


		//@formatter:off
		String expectedText =
			  "package org.sample;\n"
			+ "\n"
			+ "public class Baz {  \n"
			+ "    String name;\n"
			+ "}\n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test // typing new_line should format the current line if previous character doesn't close a block
	public void testFormattingOnTypeNewLine() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			  "package org.sample;\n"
			+ "\n"
			+ "    public      class     Baz {  \n"
			+ "String          name       ;\n"//typed \n here
			+ "}\n"
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(3, 28), "\n");
		params.setTextDocument(textDocument);
		params.setOptions(options);

		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(true);
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			  "package org.sample;\n"
			+ "\n"
			+ "    public      class     Baz {  \n"//this part won't be formatted
			+ "    String name;\n"
			+ "}\n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test // typing } should format the previous block
	public void testFormattingOnTypeCloseBlock() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			  "package org.sample;\n"
			+ "\n"
			+ "    public      class     Baz {  \n"
			+ "String          name       ;\n"
			+ "}  "//typed } here
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(4, 0), "}");
		params.setTextDocument(textDocument);
		params.setOptions(options);

		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(true);
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			  "package org.sample;\n"
			+ "\n"
			+ "public class Baz {\n"
			+ "    String name;\n"
			+ "}";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test // typing new_line after opening a block should only format the current line
	public void testFormattingOnTypeReturnAfterOpeningBlock() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			  "package org.sample;\n"
			+ "\n"
			+ "    public      class     Baz {  \n"//typed \n here
			+ "String          name       ;\n"
			+ "}  \n"
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(2, 33), "\n");
		params.setTextDocument(textDocument);
		params.setOptions(options);

		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(true);
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			  "package org.sample;\n"
			+ "\n"
			+ "public class Baz {\n"
			+ "String          name       ;\n"
			+ "}  \n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test // typing new_line after closing a block should format the that block
	public void testFormattingOnTypeReturnAfterClosedBlock() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			  "package org.sample;\n"
			+ "\n"
			+ "    public      class     Baz {  \n"
			+ "String          name       ;\n"
			+ "}  \n"//typed \n here
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(4, 3), "\n");
		params.setTextDocument(textDocument);
		params.setOptions(options);

		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(true);
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			  "package org.sample;\n"
			+ "\n"
			+ "public class Baz {\n"
			+ "    String name;\n"
			+ "}\n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test // typing new_line after inserting a new line should format the previous block if previous non-whitespace char is }
	public void testFormattingOnTypeReturnAfterEmptyLine() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			  "package org.sample;\n"
			+ "\n"
			+ "    public      class     Baz {  \n"
			+ "String          name       ;\n"
			+ "}  \n"
			+ "   \n"//typed \n here
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(5, 3), "\n");
		params.setTextDocument(textDocument);
		params.setOptions(options);

		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(true);
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			  "package org.sample;\n"
			+ "\n"
			+ "public class Baz {\n"
			+ "    String name;\n"
			+ "}\n"
			+ "   \n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test // typing new_line after an empty block on a single line should format that block
	public void testFormattingOnTypeReturnAfterEmptyBlock() throws Exception {
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java",
		//@formatter:off
			  "package org.sample;\n"
			+ "\n"
			+ "    public      class     Baz {}  \n"//typed \n here
		//@formatter:on
		);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(2, 34), "\n");
		params.setTextDocument(textDocument);
		params.setOptions(options);

		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(true);
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);

		//@formatter:off
		String expectedText =
			  "package org.sample;\n"
			+ "\n"
			+ "public class Baz {\n"
			+ "}\n";
		//@formatter:on

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(expectedText, newText);
	}

	@Test
	public void testDisableFormattingOnType() throws Exception {
		//@formatter:off
		String text =  "package org.sample;\n"
					+ "\n"
					+ "    public      class     Baz {  \n"
					+ "String          name       ;\n"
					+ "}\n";
				//@formatter:on
		ICompilationUnit unit = getWorkingCopy("src/org/sample/Baz.java", text);

		String uri = JDTUtils.toURI(unit);
		TextDocumentIdentifier textDocument = new TextDocumentIdentifier(uri);
		FormattingOptions options = new FormattingOptions(4, true);// ident == 4 spaces

		DocumentOnTypeFormattingParams params = new DocumentOnTypeFormattingParams(new Position(3, 28), "\n");
		params.setTextDocument(textDocument);
		params.setOptions(options);
		//Check it's disabled by default
		List<? extends TextEdit> edits = server.onTypeFormatting(params).get();
		assertNotNull(edits);

		String newText = TextEditUtil.apply(unit, edits);
		assertEquals(text, newText);
	}

	@After
	public void tearDown() {
		preferenceManager.getPreferences().setJavaFormatOnTypeEnabled(false);
	}


}
