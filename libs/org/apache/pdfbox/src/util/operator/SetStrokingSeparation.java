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
package org.apache.pdfbox.util.operator;

import java.util.List;

import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.graphics.color.*;
import org.apache.pdfbox.util.PDFOperator;
import org.apache.pdfbox.pdfviewer.PageDrawer;

import java.io.IOException;

/**
 * 
 * @author <a href="mailto:WilliamstonConsulting@GMail.com">Daniel Wilson</a>
 * @version $Revision: 1.0 $
 */
public class SetStrokingSeparation extends OperatorProcessor 
{
    /**
     * scn Set color space for non stroking operations.
     * @param operator The operator that is being executed.
     * @param arguments List
     * @throws IOException If an error occurs while processing the font.
     */
    public void process(PDFOperator operator, List arguments) throws IOException
    {
        PDColorSpaceInstance colorInstance = context.getGraphicsState().getStrokingColorSpace();
        PDColorSpace colorSpace = colorInstance.getColorSpace() ;//.getAlternateColorSpace();
	    logger().info("handling color space " + colorSpace.toString());
	    //logger().info("Arguments: " + arguments.toString());
	if (colorSpace instanceof PDSeparation){
		PDSeparation sep = (PDSeparation) colorSpace;
		colorSpace = sep.getAlternateColorSpace();
		logger().info("now handling alternate color space " + colorSpace.toString());
		
		if (colorSpace != null) 
		{
			OperatorProcessor newOperator = null;
			if (colorSpace instanceof PDDeviceGray) 
			    newOperator = new SetStrokingGrayColor();
			else if (colorSpace instanceof PDDeviceRGB)
			    newOperator = new SetStrokingRGBColor();
			else if (colorSpace instanceof PDDeviceCMYK)
				newOperator = new SetStrokingCMYKColor();
			else if (colorSpace instanceof PDICCBased)
				newOperator = new SetStrokingICCBasedColor();
			else if (colorSpace instanceof PDCalRGB)
				newOperator = new SetStrokingCalRGBColor();
			else if (colorSpace instanceof PDSeparation)
				newOperator = new SetStrokingSeparation();

			if (newOperator != null) 
			{
				newOperator.setContext(getContext());
				newOperator.process(operator, sep.getColorValues().toList());
			}
			else
				logger().warning("Not supported colorspace "+colorSpace.getName() + " within operator "+operator.getOperation());
		}
		
	} else throw new IOException ("Invalid attempt to process colorspace " + colorSpace.toString() + " in SetStrokingSeparation");
        
    }
}
