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
import java.awt.color.ICC_ColorSpace;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.exceptions.LoggingObject;

/**
 * This class represents a color space in a pdf document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.11 $
 */
public final class PDColorSpaceFactory extends LoggingObject 
{
    /**
     * Private constructor for utility classes.
     */
    private PDColorSpaceFactory()
    {
        //utility class should not be implemented
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpace The color space object.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( COSBase colorSpace ) throws IOException
    {
        PDColorSpace retval = null;
        if( colorSpace instanceof COSName )
        {
            retval = createColorSpace( ((COSName)colorSpace).getName() );
        }
        else if( colorSpace instanceof COSArray )
        {
            COSArray array = (COSArray)colorSpace;
            COSName type = (COSName)array.getObject( 0 );
            if( type.getName().equals( PDCalGray.NAME ) )
            {
                retval = new PDCalGray( array );
            }
            else if( type.getName().equals( PDDeviceRGB.NAME ) )
            {
                retval = PDDeviceRGB.INSTANCE;
            }
            else if( type.getName().equals( PDCalRGB.NAME ) )
            {
                retval = new PDCalRGB( array );
            }
            else if( type.getName().equals( PDDeviceN.NAME ) )
            {
                retval = new PDDeviceN( array );
            }
            else if( type.getName().equals( PDIndexed.NAME ) ||
                   type.getName().equals( PDIndexed.ABBREVIATED_NAME ))
            {
                retval = new PDIndexed( array );
            }
            else if( type.getName().equals( PDLab.NAME ) )
            {
                retval = new PDLab( array );
            }
            else if( type.getName().equals( PDSeparation.NAME ) )
            {
                retval = new PDSeparation( array );
            }
            else if( type.getName().equals( PDICCBased.NAME ) )
            {
                retval = new PDICCBased( array );
            }
            else if( type.getName().equals( PDPattern.NAME ) )
            {
                retval = new PDPattern( array );
            }
            else
            {
                throw new IOException( "Unknown colorspace array type:" + type );
            }
        }
        else
        {
            throw new IOException( "Unknown colorspace type:" + colorSpace );
        }
        return retval;
    }

    /**
     * This will create the correct color space given the name.
     *
     * @param colorSpaceName The name of the colorspace.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( String colorSpaceName ) throws IOException
    {
        PDColorSpace cs = null;
        if( colorSpaceName.equals( PDDeviceCMYK.NAME ) ||
                 colorSpaceName.equals( PDDeviceCMYK.ABBREVIATED_NAME ) )
        {
            cs = PDDeviceCMYK.INSTANCE;
        }
        else if( colorSpaceName.equals( PDDeviceRGB.NAME ) ||
                 colorSpaceName.equals( PDDeviceRGB.ABBREVIATED_NAME ) )
        {
            cs = PDDeviceRGB.INSTANCE;
        }
        else if( colorSpaceName.equals( PDDeviceGray.NAME ) ||
                 colorSpaceName.equals( PDDeviceGray.ABBREVIATED_NAME ))
        {
            cs = new PDDeviceGray();
        }
        else if( colorSpaceName.equals( PDLab.NAME ) )
        {
            cs = new PDLab();
        }
        else if( colorSpaceName.equals( PDPattern.NAME ) )
        {
            cs = new PDPattern();
        }
        else
        {
            throw new IOException( "Error: Unknown colorspace '" + colorSpaceName + "'" );
        }
        return cs;
    }

    /**
     * This will create the correct color space from a java colorspace.
     *
     * @param doc The doc to potentiall write information to.
     * @param cs The awt colorspace.
     *
     * @return The color space.
     *
     * @throws IOException If the color space name is unknown.
     */
    public static PDColorSpace createColorSpace( PDDocument doc, ColorSpace cs ) throws IOException
    {
        PDColorSpace retval = null;
        if( cs.isCS_sRGB() )
        {
            retval = PDDeviceRGB.INSTANCE;
        }
        else if( cs instanceof ICC_ColorSpace )
        {
            ICC_ColorSpace ics = (ICC_ColorSpace)cs;
            PDICCBased pdCS = new PDICCBased( doc );
            retval = pdCS;
            COSArray ranges = new COSArray();
            for( int i=0; i<cs.getNumComponents(); i++ )
            {
                ranges.add( new COSFloat( ics.getMinValue( i ) ) );
                ranges.add( new COSFloat( ics.getMaxValue( i ) ) );
            }
            PDStream iccData = pdCS.getPDStream();
            OutputStream output = null;
            try
            {
                output = iccData.createOutputStream();
                output.write( ics.getProfile().getData() );
            }
            finally
            {
                if( output != null )
                {
                    output.close();
                }
            }
            pdCS.setNumberOfComponents( cs.getNumComponents() );
        }
        else
        {
            throw new IOException( "Not yet implemented:" + cs );
        }
        return retval;
    }
}
