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

import java.util.List;
import java.awt.geom.Point2D;

import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdfviewer.PageDrawer;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.util.operator.OperatorProcessor;

/**
 * Implementation of content stream operator for page drawer.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.2 $
 */
public class CurveToReplicateFinalPoint extends OperatorProcessor
{


    /**
     * process : y : Append curved segment to path (final point replicated).
     * @param operator The operator that is being executed.
     * @param arguments List
     */
    public void process(PDFOperator operator, List arguments)
    {
        PageDrawer drawer = (PageDrawer)context;

        COSNumber x1 = (COSNumber)arguments.get( 0 );
        COSNumber y1 = (COSNumber)arguments.get( 1 );
        COSNumber x3 = (COSNumber)arguments.get( 2 );
        COSNumber y3 = (COSNumber)arguments.get( 3 );
        /*float x1f = x1.floatValue();
        float y1f = (float)drawer.fixY( x1f, y1.floatValue() );
        float x3f = x3.floatValue();
        float y3f = (float)drawer.fixY( x3f, y3.floatValue() );
        drawer.getLinePath().curveTo(x1f,y1f,x3f,y3f,x3f,y3f);
        */

        Point2D P1 = drawer.TransformedPoint(x1.doubleValue(), y1.doubleValue());
        Point2D P3 = drawer.TransformedPoint(x3.doubleValue(), y3.doubleValue());

        drawer.getLinePath().curveTo((float)P1.getX(), (float)P1.getY(), (float)P3.getX(), (float)P3.getY(), (float)P3.getX(), (float)P3.getY());
    }
}
