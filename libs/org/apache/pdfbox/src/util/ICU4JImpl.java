/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.util;

import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.Normalizer;

/**
 * This class is an implementation the the ICU4J class. TextNormalize 
 * will call this only if the ICU4J library exists in the classpath.
 */
public class ICU4JImpl {
    Bidi bidi;

    public ICU4JImpl() {
        bidi = new Bidi();

        /* We do not use bidi.setInverse() because that uses
         * Bidi.REORDER_INVERSE_NUMBERS_AS_L, which caused problems
         * in some test files. For example, a file had a line of:
         * 0 1 / ARABIC
         * and the 0 and 1 were reversed in the end result.  
         * REORDER_INVERSE_LIKE_DIRECT is the inverse Bidi mode 
         * that more closely reflects the Unicode spec.
         */
        bidi.setReorderingMode(Bidi.REORDER_INVERSE_LIKE_DIRECT);
    }

    /**
     * Takes a line of text in presentation order and converts it to logical order.
     * @see TextNormalize.makeLineLogicalOrder(String, boolean)     
     *  
     */
    public String makeLineLogicalOrder(String a_str, boolean a_isRtlDominant) {    	
        bidi.setPara(a_str, a_isRtlDominant?Bidi.RTL:Bidi.LTR, null);

        /* Set the mirror flag so that parentheses and other mirror symbols
         * are properly reversed, when needed.  With this removed, lines
         * such as (CBA) in the PDF file will come out like )ABC( in logical
         * order.
         */
        return bidi.writeReordered(Bidi.DO_MIRRORING);
    }

    /**
     * Normalize presentation forms of characters to the separate parts. 
     * @see TextNormalize.normalizePres(String)
     * 
     * @param a_str String to normalize
     * @return Normalized form
     */
    public String normalizePres(String a_str) {
        String retStr = "";
        for (int i = 0; i < a_str.length(); i++) {
            /* We only normalize if the codepoint is in a given range. Otherwise, 
             * NFKC converts too many things that would cause confusion. For example,
             * it converts the micro symbol in extended latin to the value in the greek
             * script. We normalize the Unicode Alphabetic and Arabic A&B Presentation forms.
             */
            char c = a_str.charAt(i);
            if (((c >= 0xFB00) && (c <= 0xFDFF)) ||
                    ((c >= 0xFE70) && (c <= 0xFEFF)))	{
                /* Some fonts map U+FDF2 differently than the Unicode spec.
                 * They add an extra U+0627 character to compensate.  
                 * This removes the extra character for those fonts. */ 
                if((c == 0xFDF2) && (i > 0) && ((a_str.charAt(i-1) == 0x0627) || (a_str.charAt(i-1) == 0xFE8D))) {
                    retStr += "\u0644\u0644\u0647";
                }
                else{
                    /*
                     * Trim because some decompositions have an extra space, such as
                     * U+FC5E
                     */
                    retStr += Normalizer.normalize(c, Normalizer.NFKC).trim(); 
                }
            }
            else {
                retStr += a_str.charAt(i);
            }
        }
        return retStr;
    }
    /**
     * Decomposes Diacritic characters to their combining forms
     * 
     * @param a_str String to be Normalized
     * @return A Normalized String
     */      
    public String normalizeDiac(String a_str){
        String retStr = "";
        for (int i = 0; i < a_str.length(); i++) {
            char c = a_str.charAt(i);
            if(Character.getType(c) == Character.NON_SPACING_MARK 
                    || Character.getType(c) == Character.MODIFIER_SYMBOL
                    || Character.getType(c) == Character.MODIFIER_LETTER){
                /*
                 * Trim because some decompositions have an extra space, such as
                 * U+00B4
                 */
                retStr += Normalizer.normalize(c, Normalizer.NFKC).trim();
            }
            else{
                retStr += a_str.charAt(i);
            }
        }
        return retStr;
    }
}
