/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.aurora.engine;

import org.thymeleaf.aurora.templatemode.TemplateMode;
import org.thymeleaf.aurora.util.TextUtil;
import org.thymeleaf.util.Validate;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public final class MatchingAttributeName {

    final TemplateMode templateMode;
    private final AttributeName matchingAttributeName;
    private final String matchingAllAttributesWithPrefix;
    private final boolean matchingAllAttributes;



    public static MatchingAttributeName forAttributeName(final TemplateMode templateMode, final AttributeName matchingAttributeName) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        Validate.notNull(matchingAttributeName, "Matching attribute name cannot be null");
        if (templateMode.isHTML() && !(matchingAttributeName instanceof HTMLAttributeName)) {
            throw new IllegalArgumentException("Attribute names for HTML template mode must be of class " + HTMLAttributeName.class.getName());
        } else if (templateMode.isXML() && !(matchingAttributeName instanceof XMLAttributeName)) {
            throw new IllegalArgumentException("Attribute names for XML template mode must be of class " + XMLAttributeName.class.getName());
        }
        return new MatchingAttributeName(templateMode, matchingAttributeName, null, false);
    }


    public static MatchingAttributeName forAllAttributesWithPrefix(final TemplateMode templateMode, final String matchingAllAttributesWithPrefix) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        // Prefix can actually be null -> match all attributes with no prefix
        return new MatchingAttributeName(templateMode, null, matchingAllAttributesWithPrefix, false);
    }


    public static MatchingAttributeName forAllAttributes(final TemplateMode templateMode) {
        Validate.notNull(templateMode, "Template mode cannot be null");
        return new MatchingAttributeName(templateMode, null, null, true);
    }



    private MatchingAttributeName(
            final TemplateMode templateMode, final AttributeName matchingAttributeName,
            final String matchingAllAttributesWithPrefix, final boolean matchingAllAttributes) {
        super();
        this.templateMode = templateMode;
        this.matchingAttributeName = matchingAttributeName;
        this.matchingAllAttributesWithPrefix = matchingAllAttributesWithPrefix;
        this.matchingAllAttributes = matchingAllAttributes;
    }




    public TemplateMode getTemplateMode() {
        return this.templateMode;
    }


    public AttributeName getMatchingAttributeName() {
        return this.matchingAttributeName;
    }


    public String getMatchingAllAttributesWithPrefix() {
        return this.matchingAllAttributesWithPrefix;
    }


    public boolean isMatchingAllAttributes() {
        return this.matchingAllAttributes;
    }




    public boolean matches(final AttributeName attributeName) {

        Validate.notNull(attributeName, "Attributes name cannot be null");

        if (this.matchingAttributeName == null) {

            if (templateMode.isHTML() && !(attributeName instanceof HTMLAttributeName)) {
                return false;
            } else if (templateMode.isXML() && !(attributeName instanceof XMLAttributeName)) {
                return false;
            } else if (templateMode.isText()) {
                // Nothing to do with text and matching attributes!
                return false;
            }

            if (this.matchingAllAttributes) {
                return true;
            }

            if (this.matchingAllAttributesWithPrefix == null) {
                return attributeName.getPrefix() == null;
            }

            final String attributeNamePrefix = attributeName.getPrefix();
            if (attributeNamePrefix == null) {
                return false; // we already checked we are not matching nulls
            }

            return TextUtil.equals(!this.templateMode.isHTML(), this.matchingAllAttributesWithPrefix, attributeNamePrefix);

        }

        return this.matchingAttributeName.equals(attributeName);

    }



    @Override
    public String toString() {
        if (this.matchingAttributeName == null) {
            if (this.matchingAllAttributes) {
                return "*";
            }
            if (this.matchingAllAttributesWithPrefix == null) {
                return "[^:]*";
            }
            return this.matchingAllAttributesWithPrefix + ":*";
        }
        return matchingAttributeName.toString();
    }


}
