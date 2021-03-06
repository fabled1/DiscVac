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
package org.apache.pdfbox.pdmodel.graphics.color;

import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;

import java.io.IOException;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

/**
 * This class represents a Separation color space.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.5 $
 */
public class PDSeparation extends PDColorSpace
{
    /**
     * The name of this color space.
     */
    public static final String NAME = "Separation";


    /**
     * Constructor.
     */
    public PDSeparation()
    {
        array = new COSArray();
        array.add( COSName.getPDFName( NAME ) );
        array.add( COSName.getPDFName( "" ) );
    }

    /**
     * Constructor.
     *
     * @param separation The array containing all separation information.
     */
    public PDSeparation( COSArray separation )
    {
        array = separation;
    }

    /**
     * This will return the name of the color space.  For a PDSeparation object
     * this will always return "Separation"
     *
     * @return The name of the color space.
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * This will get the number of components that this color space is made up of.
     *
     * @return The number of components in this color space.
     *
     * @throws IOException If there is an error getting the number of color components.
     */
    public int getNumberOfComponents() throws IOException
    {
        //return 1;
    return array.size();
    }

    /**
     * Create a Java colorspace for this colorspace.
     *
     * @return A color space that can be used for Java AWT operations.
     *
     * @throws IOException If there is an error creating the color space.
     */
    public ColorSpace createColorSpace() throws IOException
    {
        //throw new IOException( "Not implemented" );
        try{

            ///dump some information to help figure these things out
            //logger().info( array.toString());

            PDColorSpace alt = getAlternateColorSpace();

            //logger().info(alt.toString());

            ColorSpace CS = alt.createColorSpace();///dwilson 12/15/07
            //logger().info(CS.toString() + " reporting type " + CS.getType() + " and having component count of " + CS.getNumComponents());

            return CS;
        }catch (IOException IOe){
            logger().severe(IOe.toString() + "\n at\n" + FullStackTrace(IOe));

            throw IOe;
        }catch (Exception e){
            logger().severe(e.toString() + "\n at\n" +FullStackTrace(e));
            throw new IOException("Failed to Create ColorSpace");
        }
    }

    /**
     * Create a Java color model for this colorspace.
     *
     * @param bpc The number of bits per component.
     *
     * @return A color model that can be used for Java AWT operations.
     *
     * @throws IOException If there is an error creating the color model.
     */
    public ColorModel createColorModel( int bpc ) throws IOException
    {
        logger().info("About to create ColorModel for " + getAlternateColorSpace().toString());
        return getAlternateColorSpace().createColorModel(bpc);
    }

    /**
     * This will get the separation name.
     *
     * @return The name in the separation.
     */
    public String getColorantName()
    {
        COSName name = (COSName)array.getObject( 1 );
        return name.getName();
    }

    /**
     * This will set the separation name.
     *
     * @param name The separation name.
     */
    public void setColorantName( String name )
    {
        array.set( 1, COSName.getPDFName( name ) );
    }

    /**
     * This will get the alternate color space for this separation.
     *
     * @return The alternate color space.
     *
     * @throws IOException If there is an error getting the alternate color space.
     */
    public PDColorSpace getAlternateColorSpace() throws IOException
    {
        COSBase alternate = array.getObject( 2 );
        PDColorSpace cs = PDColorSpaceFactory.createColorSpace( alternate );

        //logger().info("Returning " + cs.toString() + " for input " + alternate.toString());

        return cs;
    }

    /**
     * This will set the alternate color space.
     *
     * @param cs The alternate color space.
     */
    public void setAlternateColorSpace( PDColorSpace cs )
    {
        COSBase space = null;
        if( cs != null )
        {
            space = cs.getCOSObject();
        }
        array.set( 2, space );
    }

    /**
     * This will get the tint transform function.
     *
     * @return The tint transform function.
     *
     * @throws IOException If there is an error creating the PDFunction
     */
    public PDFunction getTintTransform() throws IOException
    {
        return PDFunction.create( array.getObject( 3 ) );
    }

    /**
     * This will set the tint transform function.
     *
     * @param tint The tint transform function.
     */
    public void setTintTransform( PDFunction tint )
    {
        array.set( 3, tint );
    }
    
    
    
    /*
    Some of the key values are stored within the COSDictionary, item 3 in the array.
    I don't necessarily want to expose the entire dictionary publicly (except in toString()),
    but need access privately in order to expose the color values publicly.
    */
    private COSDictionary getDictionary() throws IOException
    {
        return (COSDictionary) array.getObject( 3);
    }
    
    public COSArray getColorValues() throws IOException
    {
        return (COSArray) getDictionary().getDictionaryObject("C1");
    }
}
