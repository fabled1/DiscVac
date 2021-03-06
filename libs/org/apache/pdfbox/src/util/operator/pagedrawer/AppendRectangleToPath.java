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
package org.apache.pdfbox.util.operator.pagedrawer;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.io.IOException;

import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.operator.OperatorProcessor;

/**
 * Implementation of content stream operator for page drawer.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.3 $
 */
public class AppendRectangleToPath extends OperatorProcessor
{


    /**
     * process : re : append rectangle to path.
     * @param operator The operator that is being executed.
     * @param arguments List
     */
    public void process(PDFOperator operator, List arguments) throws IOException
    {
        PageDrawer drawer = (PageDrawer)context;

        COSNumber x = (COSNumber)arguments.get( 0 );
        COSNumber y = (COSNumber)arguments.get( 1 );
        COSNumber w = (COSNumber)arguments.get( 2 );
        COSNumber h = (COSNumber)arguments.get( 3 );

        double x1 = x.floatValue();
        double y1 = y.floatValue();
        // create a pair of coordinates for the transformation 
        double x2 = w.floatValue()+x1;
        double y2 = h.floatValue()+y1;

        Point2D startCoords = drawer.TransformedPoint(x1,y1);
        Point2D endCoords = drawer.TransformedPoint(x2,y2);

        double width = endCoords.getX()-startCoords.getX();
        double height =  endCoords.getY()-startCoords.getY();
        double xStart = startCoords.getX();
        double yStart = startCoords.getY();
        // we have to calculate the width and height from the two pairs of coordinates
        // if the endCoords are above the startCoords we have to switch them
        if (width < 0) 
        {
        	xStart += width;
        	width = -width;
        }
        if (height < 0) {
        	yStart += height;
        	height = -height;
        }
        Rectangle2D rect = new Rectangle2D.Double(xStart, yStart, width, height);
        drawer.getLinePath().append( rect, false );
    }
}
