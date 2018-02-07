package it.polito.teaching.cv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class ObjRecognitionProcessor extends TargetScope
{
	private Mat blurredImage = new Mat();
	private Mat hsvImage = new Mat();
	private Mat hueImage = new Mat();
	private Mat saturationImage = new Mat();

	private Mat valueImage = new Mat();
	private Mat maskImage = new Mat();
	private Mat contourFilter = new Mat();
	
	private Mat contouredImage = new Mat();

	private double hueStart;
	private double hueStop;

	private double saturationStart;
	private double saturationStop;

	private double valueStart;
	private double valueStop;
	
	private String valuesToPrint;
	
	private Scalar color = new Scalar(0,0,255);

	/**
	 * Given a binary image containing one or more closed surfaces, use it as a
	 * mask to find and highlight the objects contours
	 * 
	 * @param frame
	 *            the original {@link Mat} image to be used for drawing the
	 *            objects contours
	 * @return the {@link Mat} image with the objects contours framed
	 */
	public void findAndDrawObjectContours(Mat frame)
	{
		// init
		buildContourMask(frame);
		
		List<MatOfPoint> contours = new ArrayList<>();
		List<Rect> rectangles = new ArrayList<>();

		// find contours
		Imgproc.findContours(contourFilter, contours, contouredImage, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

		// filter contours by area and then by width and height
		for (int i = 0; i < contours.size(); i++) {
			if (Imgproc.contourArea(contours.get(i)) > 200 ) {
				
				Rect rect = Imgproc.boundingRect(contours.get(i));
				
//				System.out.println("rect.width = " + rect.width);
//				System.out.println("rect.height = " + rect.height);
				
				if (isTargetStripe(rect)) {
					rectangles.add(rect);
				}
			}
		}
		
		Rect leftRect = null;
		Rect rightRect = null;
		
	    if (rectangles.size() == 2) {
	    	leftRect = rectangles.get(0);
	    	rightRect = rectangles.get(1);
	        
	    	if (leftRect.x > rightRect.x) {
	        	Rect temp = rightRect;
	        	
	        	rightRect = leftRect;
	        	leftRect = temp;
	        }
	    	
	        Imgproc.rectangle(frame, new Point(leftRect.x, leftRect.y), 
	        		new Point(leftRect.x+leftRect.width,leftRect.y+leftRect.height), color, LINE_THICKNESS);
	    	
	        Imgproc.rectangle(frame, new Point(rightRect.x, rightRect.y), 
	        		new Point(rightRect.x+rightRect.width,rightRect.y+rightRect.height), color, LINE_THICKNESS);
	    }
	    
	    if (leftRect != null && rightRect != null) {
	    	Point centerPoint = getCenterPoint(leftRect, rightRect);
	    	
	    	Imgproc.circle(frame, centerPoint, TARGET_RADIUS, color, LINE_THICKNESS);
	    }
	    
	    TargetScope targetScope = new TargetScope();
	    
	    targetScope.draw(frame);
	}

	/**
	 * Given an original image, build a contour filter mask
	 * 
	 * @return the {@link Mat} for drawing contours
	 */
	private Mat buildContourMask(Mat frame)
	{
		// if the frame is not empty, process it
		if (frame.empty()) {
			return frame;
		}

		try {
			// remove some noise
			Imgproc.blur(frame, blurredImage, new Size(9, 9));

			// convert the frame to HSV
			Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

			// get thresholding values from the UI
			// remember: H ranges 0-180, S and V range 0-255
			Scalar minValues = new Scalar(hueStart, saturationStart, valueStart);
			Scalar maxValues = new Scalar(hueStop, saturationStop, valueStop);

			// show the current selected HSV range
			valuesToPrint = "Hue range: " + minValues.val[0] + "-" + maxValues.val[0]
					+ "\tSaturation range: " + minValues.val[1] + "-" + maxValues.val[1] + "\tValue range: "
					+ minValues.val[2] + "-" + maxValues.val[2];
			
			// threshold HSV image to select tennis balls
			Core.inRange(hsvImage, minValues, maxValues, maskImage);
			
			ArrayList matList = new ArrayList<Mat>();
			
			Core.split(hsvImage, matList);
			
			hueImage = (Mat) matList.get(0);
			saturationImage = (Mat) matList.get(1);
			valueImage = (Mat) matList.get(2);
			
			// threshold HSV image to select tennis balls
//			Core.inRange((Mat) matList.get(0), new Scalar(hueStart, 0, 0), new Scalar(hueStart, 0, 0), hueImage);
//			Core.inRange((Mat) matList.get(1), new Scalar(0, saturationStart, 0), new Scalar(0, saturationStart, 0), saturationImage);
//			Core.inRange((Mat) matList.get(2), new Scalar(0, 0, valueStart), new Scalar(0, 0, valueStart), valueImage);

			// blur the filtered image
			Imgproc.GaussianBlur(maskImage, contourFilter, new Size(9, 9), 0);
			
			// morphological operators
			// dilate with large element, erode with small ones
//			Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
//			Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

//			Imgproc.erode(mask, contourFilter, erodeElement);
//			Imgproc.erode(mask, contourFilter, erodeElement);
//
//			Imgproc.dilate(mask, contourFilter, dilateElement);
//			Imgproc.dilate(mask, contourFilter, dilateElement);
		}
		catch (Exception e) {
			// log the (full) error
			System.err.print("ERROR building contour mask");
			e.printStackTrace();
		}

		return contourFilter;
	}

	public void setHueStart(double hueStart) {
		this.hueStart = hueStart;
	}

	public void setHueStop(double hueStop) {
		this.hueStop = hueStop;
	}

	public void setSaturationStart(double saturationStart) {
		this.saturationStart = saturationStart;
	}

	public void setSaturationStop(double saturationStop) {
		this.saturationStop = saturationStop;
	}

	public void setValueStart(double valueStart) {
		this.valueStart = valueStart;
	}

	public void setValueStop(double valueStop) {
		this.valueStop = valueStop;
	}

	public Mat getHSVImage() {
		return hsvImage;
	}
	
	public Mat getHueImage() {
		return hueImage;
	}

	public Mat getSaturationImage() {
		return saturationImage;
	}

	public Mat getValueImage() {
		return valueImage;
	}

	public Mat getMaskImage() {
		return maskImage;
	}

	public Mat getContourFilter() {
		return contourFilter;
	}

	public String toString() {
		return valuesToPrint;
	}
}