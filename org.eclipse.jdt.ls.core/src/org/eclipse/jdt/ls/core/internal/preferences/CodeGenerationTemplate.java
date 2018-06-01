/*******************************************************************************
 * Copyright (c) 2017 Microsoft Corporation. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Microsoft Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.ls.core.internal.preferences;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ls.core.internal.corext.template.java.CodeTemplateContextType;
import org.eclipse.jface.text.templates.Template;

public enum CodeGenerationTemplate {
	/**
	 * Field comment template
	 */
	FIELDCOMMENT(CodeTemplatePreferences.CODETEMPLATE_FIELDCOMMENT, CodeTemplateContextType.FIELDCOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_FIELDCOMMENT_DEFAULT),
	/**
	 * Method comment template
	 */
	METHODCOMMENT(
			CodeTemplatePreferences.CODETEMPLATE_METHODCOMMENT,
			CodeTemplateContextType.METHODCOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_METHODCOMMENT_DEFAULT),
	/**
	 * Constructor comment template
	 */
	CONSTRUCTORCOMMENT(
			CodeTemplatePreferences.CODETEMPLATE_CONSTRUCTORCOMMENT,
			CodeTemplateContextType.CONSTRUCTORCOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_CONSTRUCTORCOMMENT_DEFAULT),
	/**
	 * Constructor method body template
	 */
	CONSTRUCTORBODY(
			CodeTemplatePreferences.CODETEMPLATE_CONSTRUCTORBODY,
			CodeTemplateContextType.CONSTRUCTORBODY_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_CONSTRUCTORBODY_DEFAULT),
	/**
	 * Delegate comment template
	 */
	DELEGATECOMMENT(
			CodeTemplatePreferences.CODETEMPLATE_DELEGATECOMMENT,
			CodeTemplateContextType.DELEGATECOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_DELEGATECOMMENT_DEFAULT),
	/**
	 * Override comment template
	 */
	OVERRIDECOMMENT(
			CodeTemplatePreferences.CODETEMPLATE_OVERRIDECOMMENT,
			CodeTemplateContextType.OVERRIDECOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_OVERRIDECOMMENT_DEFAULT),
	/**
	 * Type comment template
	 */
	TYPECOMMENT(
			CodeTemplatePreferences.CODETEMPLATE_TYPECOMMENT,
			CodeTemplateContextType.TYPECOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_TYPECOMMENT_DEFAULT),
	/**
	 * Getter comment template
	 */
	GETTERCOMMENT(
			CodeTemplatePreferences.CODETEMPLATE_GETTERCOMMENT,
			CodeTemplateContextType.GETTERCOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_GETTERCOMMENT_DEFAULT),

	/**
	 * Getter comment template
	 */
	SETTERCOMMENT(
			CodeTemplatePreferences.CODETEMPLATE_SETTERCOMMENT,
			CodeTemplateContextType.SETTERCOMMENT_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_SETTERCOMMENT_DEFAULT),

	/**
	 * Getter method body content
	 */
	GETTERBODY(
			CodeTemplatePreferences.CODETEMPLATE_GETTERBODY,
			CodeTemplateContextType.GETTERBODY_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_GETTERBODY_DEFAULT),

	/**
	 * Setter method body content
	 */
	SETTERBOY(
			CodeTemplatePreferences.CODETEMPLATE_SETTERBODY,
			CodeTemplateContextType.SETTERBODY_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_SETTERBODY_DEFAULT),

	/**
	 * Catch body content
	 */
	CATCHBODY(
			CodeTemplatePreferences.CODETEMPLATE_CATCHBODY,
			CodeTemplateContextType.CATCHBODY_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_CATCHBODY_DEFAULT),
	/**
	 * Method body content template
	 */
	METHODBODY(
			CodeTemplatePreferences.CODETEMPLATE_METHODBODY,
			CodeTemplateContextType.METHODBODY_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_METHODBODY_DEFAULT),

	/**
	 * Snippet `class` content template
	 */
	CLASSSNIPPET(
			CodeTemplatePreferences.CODETEMPLATE_CODESNIPPET,
			CodeTemplateContextType.CLASSSNIPPET_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_CLASSSNIPPET_DEFAULT),

	/**
	 * Snippet `interface` content template
	 */
	INTERFACESNIPPET(
			CodeTemplatePreferences.CODETEMPLATE_CODESNIPPET,
			CodeTemplateContextType.INTERFACESNIPPET_CONTEXTTYPE,
			CodeTemplatePreferences.CODETEMPLATE_INTERFACESNIPPET_DEFAULT);


	private final String preferenceId;
	private final String contextType;
	private final String defaultContent;

	private CodeGenerationTemplate(String preferenceId, String contextType, String defaultContent) {
		this.preferenceId = preferenceId;
		this.contextType = contextType;
		this.defaultContent = defaultContent;

	}

	public Template createTemplate(IJavaProject project) {
		return new Template(this.name(), this.preferenceId, this.contextType, this.defaultContent, false);
	}
}

/**
 * Preference key names, internal for now.
 */
class CodeTemplatePreferences {
	private static final String CODETEMPLATES_PREFIX = "java.codetemplates."; //$NON-NLS-1$
	public static final String COMMENT_SUFFIX = ".comment"; //$NON-NLS-1$
	public static final String BODY_SUFFIX = ".body"; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for field comments
	 */
	public static final String CODETEMPLATE_FIELDCOMMENT = CODETEMPLATES_PREFIX + "field" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for constructor comments
	 */
	public static final String CODETEMPLATE_CONSTRUCTORCOMMENT = CODETEMPLATES_PREFIX + "constructor" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for constructor body content
	 */
	public static final String CODETEMPLATE_CONSTRUCTORBODY = CODETEMPLATES_PREFIX + "constructor" + BODY_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for method body content
	 */
	public static final String CODETEMPLATE_METHODBODY = CODETEMPLATES_PREFIX + "method" + BODY_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for delegate method comments
	 */
	public static final String CODETEMPLATE_DELEGATECOMMENT = CODETEMPLATES_PREFIX + "delegate" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for overridden method comments
	 */
	public static final String CODETEMPLATE_OVERRIDECOMMENT = CODETEMPLATES_PREFIX + "overriddenMethod" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for method comments
	 */
	public static final String CODETEMPLATE_METHODCOMMENT = CODETEMPLATES_PREFIX + "method" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for snippet body content
	 */
	public static final String CODETEMPLATE_CODESNIPPET = CODETEMPLATES_PREFIX + "snippet" + BODY_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for type comments
	 */
	public static final String CODETEMPLATE_TYPECOMMENT = CODETEMPLATES_PREFIX + "type" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for getter comments
	 */
	public static final String CODETEMPLATE_GETTERCOMMENT = CODETEMPLATES_PREFIX + "getter" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for setter comments
	 */
	public static final String CODETEMPLATE_SETTERCOMMENT = CODETEMPLATES_PREFIX + "setter" + COMMENT_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for getter method body content
	 */
	public static final String CODETEMPLATE_GETTERBODY = CODETEMPLATES_PREFIX + "getter" + BODY_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for setter method body content
	 */
	public static final String CODETEMPLATE_SETTERBODY = CODETEMPLATES_PREFIX + "setter" + BODY_SUFFIX; //$NON-NLS-1$

	/**
	 * A named preference that defines the template for setter method body content
	 */
	public static final String CODETEMPLATE_CATCHBODY = CODETEMPLATES_PREFIX + "catch" + BODY_SUFFIX; //$NON-NLS-1$

	/**
	 * Default value for field comments
	 */
	public static final String CODETEMPLATE_FIELDCOMMENT_DEFAULT = "/**\n" + " *\n" + " */\n";

	/**
	 * Default value for constructor comments
	 */
	public static final String CODETEMPLATE_CONSTRUCTORCOMMENT_DEFAULT = "/**\n" + " * ${tags}\n" + " */\n";

	/**
	 * Default value for delegate comments
	 */
	public static final String CODETEMPLATE_DELEGATECOMMENT_DEFAULT = "/**\n" + " * ${tags}\n" + " * ${see_to_target}\n" + " */\n";

	/**
	 * Default value for override comments
	 */
	public static final String CODETEMPLATE_OVERRIDECOMMENT_DEFAULT = "/* (non-Javadoc)\n" + " * ${see_to_overridden}\n"
			+ " */\n";

	/**
	 * Default value for method comments
	 */
	public static final String CODETEMPLATE_METHODCOMMENT_DEFAULT = "/**\n" + " * ${tags}\n" + " */\n";

	/**
	 * Default value for type comments
	 */
	public static final String CODETEMPLATE_TYPECOMMENT_DEFAULT = "/**\n" + " * ${tags}\n" + " */\n";

	/**
	 * Default value for getter comments
	 */
	public static final String CODETEMPLATE_GETTERCOMMENT_DEFAULT = "/**\n" + " * @return the ${bare_field_name}\n" + " */\n";

	/**
	 * Default value for setter comments
	 */
	public static final String CODETEMPLATE_SETTERCOMMENT_DEFAULT = "/**\n" + " * @param ${param} the ${bare_field_name} to set\n" + " */\n";

	/**
	 * Default value for getter method body content
	 */
	public static final String CODETEMPLATE_GETTERBODY_DEFAULT = "return ${field};\n";

	/**
	 * Default value for setter method body content
	 */
	public static final String CODETEMPLATE_SETTERBODY_DEFAULT = "${field} = ${param};\n";

	/**
	 * Default value for constructor method body content
	 */
	public static final String CODETEMPLATE_CONSTRUCTORBODY_DEFAULT = "${body_statement}\n//${todo} Auto-generated constructor stub";

	/**
	 * Default value from method body content
	 */
	public static final String CODETEMPLATE_METHODBODY_DEFAULT = "// ${todo} Auto-generated method stub\n${body_statement}";
	/**
	 * Default value for catch body content
	 */
	public static final String CODETEMPLATE_CATCHBODY_DEFAULT = "// ${todo} Auto-generated catch block\n${exception_var}.printStackTrace();";

	/**
	 * Default value for class snippet body content
	 */
	public static final String CODETEMPLATE_CLASSSNIPPET_DEFAULT = "${package_header}/**\n * ${type_name}\n */\npublic class ${type_name} {\n\n\t${cursor}\n}";

	/**
	 * Default value for interface snippet body content
	 */
	public static final String CODETEMPLATE_INTERFACESNIPPET_DEFAULT = "${package_header}/**\n * ${type_name}\n */\npublic interface ${type_name} {\n\n\t${cursor}\n}";
}
