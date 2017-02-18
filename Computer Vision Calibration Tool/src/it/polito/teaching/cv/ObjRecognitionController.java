package it.polito.teaching.cv;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The controller associated with the only view of our application. The
 * application logic is implemented here. It handles the button for
 * starting/stopping the camera, the acquired video stream, the relative
 * controls and the image segmentation process.
 * 
 * @author <a href="mailto:luigi.derussis@polito.it">Luigi De Russis</a>
 * @version 1.5 (2015-11-26)
 * @since 1.0 (2015-01-13)
 * 
 */
public class ObjRecognitionController
{
	// FXML camera button
	@FXML
	private Button cameraButton;
	
	// the FXML area for showing the current frame
	@FXML
	private ImageView originalFrame;
	
	// the FXML area for showing the output of the image processing operations
	@FXML
	private ImageView hsvImage;
	
	// the FXML area for showing the output of the image processing operations
	@FXML
	private ImageView hueImage;
	
	// the FXML area for showing the output of the image processing operations
	@FXML
	private ImageView saturationImage;
	
	// the FXML area for showing the output of the image processing operations
	@FXML
	private ImageView valueImage;
	
	// the FXML area for showing the mask
	@FXML
	private ImageView maskImage;

	// FXML slider for setting HSV ranges
	@FXML
	private Slider hueStart;
	@FXML
	private Slider hueStop;
	@FXML
	private Slider saturationStart;
	@FXML
	private Slider saturationStop;
	@FXML
	private Slider valueStart;
	@FXML
	private Slider valueStop;
	// FXML label to show the current values set with the sliders
	@FXML
	private Label hsvCurrentValues;
	
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	
	// the OpenCV object that performs the video capture
	private VideoCapture capture = new VideoCapture();
	
	// a flag to change the button behavior
	private boolean cameraActive;
	
	// property for object binding
	private ObjectProperty<String> hsvValuesProp;
	
	private Mat imageProcessingframe = new Mat();
	
	private ObjRecognitionProcessor processor =  new ObjRecognitionProcessor(); 
		
	/**
	 * The action triggered by pushing the button on the GUI
	 */
	@FXML
	private void startCamera()
	{
		// bind a text property with the string containing the current range of
		// HSV values for object detection
		hsvValuesProp = new SimpleObjectProperty<>();
		this.hsvCurrentValues.textProperty().bind(hsvValuesProp);
				
		// set a fixed width for all the image to show and preserve image ratio
		this.imageViewProperties(this.originalFrame, 400);
		this.imageViewProperties(this.hsvImage, 200);
		this.imageViewProperties(this.hueImage, 200);
		this.imageViewProperties(this.saturationImage, 200);
		this.imageViewProperties(this.valueImage, 200);
		this.imageViewProperties(this.maskImage, 200);
		
		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(0);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						try {
							onFXThread(hsvValuesProp, processor.toString());
							
							processor.setHueStart(hueStart.getValue());
							processor.setHueStop(hueStop.getValue());
							processor.setSaturationStart(saturationStart.getValue());
							processor.setSaturationStop(saturationStop.getValue());
							processor.setValueStart(valueStart.getValue());
							processor.setValueStop(valueStop.getValue());
							
							capture.read(imageProcessingframe);
							
							processor.findAndDrawObjectContours(imageProcessingframe);
							
							onFXThread(hsvImage.imageProperty(), mat2Image(processor.getMaskImage()));
							onFXThread(hueImage.imageProperty(), mat2Image(processor.getHueImage()));
							onFXThread(saturationImage.imageProperty(), mat2Image(processor.getSaturationImage()));
							onFXThread(valueImage.imageProperty(), mat2Image(processor.getValueImage()));
							onFXThread(maskImage.imageProperty(), mat2Image(processor.getHSVImage()));
							onFXThread(originalFrame.imageProperty(), mat2Image(imageProcessingframe));
						} catch (Exception e) {
							System.out.println(e.getMessage());
							e.printStackTrace();
						}
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 99, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.cameraButton.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraButton.setText("Start Camera");
			
			// stop the timer
			try
			{
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log the exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
			
			// release the camera
			this.capture.release();
		}
	}
	
	/**
	 * Set typical {@link ImageView} properties: a fixed width and the
	 * information to preserve the original image ration
	 * 
	 * @param image
	 *            the {@link ImageView} to use
	 * @param dimension
	 *            the width of the image to set
	 */
	private void imageViewProperties(ImageView image, int dimension)
	{
		// set a fixed width for the given ImageView
		image.setFitWidth(dimension);
		// preserve the image ratio
		image.setPreserveRatio(true);
	}
	
	/**
	 * Convert a {@link Mat} object (OpenCV) in the corresponding {@link Image}
	 * for JavaFX
	 * 
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	private Image mat2Image(Mat frame)
	{
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer, according to the PNG format
		Imgcodecs.imencode(".png", frame, buffer);
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	/**
	 * Generic method for putting element running on a non-JavaFX thread on the
	 * JavaFX thread, to properly update the UI
	 * 
	 * @param property
	 *            a {@link ObjectProperty}
	 * @param value
	 *            the value to set for the given {@link ObjectProperty}
	 */
	private <T> void onFXThread(final ObjectProperty<T> property, final T value)
	{
		Platform.runLater(new Runnable() {
			
			@Override
			public void run()
			{
				property.set(value);
			}
		});
	}
	
}
