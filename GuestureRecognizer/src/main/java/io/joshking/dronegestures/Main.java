package io.joshking.dronegestures;

import com.apple.eawt.FullScreenUtilities;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import io.joshking.dronegestures.drone.DroneController;
import io.joshking.dronegestures.drone.commands.command.ChangeAltitudeCommand;
import io.joshking.dronegestures.gesture.GestureDetector;
import io.joshking.dronegestures.gesture.Hand;
import io.joshking.dronegestures.utils.ScreenSize;
import io.joshking.dronegestures.view.FlightIndicatorPanel;


public class Main {
    private static final int                     VIEW_CAMERA            = 1;
    private static final int                     VIEW_FLIGHT_CONTROLLER = 0;
    private static final int                     VIEW_NOTHING           = -1;
    static               Mat                     frame                  = null;
    private static       int                     cameraId;
    private static       VideoCapture            capture;
    private static       DroneController         drone;
    private static       HSVColor                filterToAdjust;
    private static       Map<Integer, Container> guiContainers;
    private static       int                     imageRotation;
    private static       boolean                 showCameraPreviewWindow;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        guiContainers = new HashMap<>();
        imageRotation = 0;
        cameraId = 1;
    }

    private static List<MatOfPoint> detectBlobs(Mat img) {
        List<MatOfPoint> contours  = new ArrayList<>();
        Mat              hierarchy = new Mat();
        Imgproc.GaussianBlur(img, img, new Size(29, 29), 0, 0);
        Imgproc.findContours(img,
                             contours,
                             hierarchy,
                             Imgproc.RETR_TREE,
                             Imgproc.CHAIN_APPROX_SIMPLE);
        hierarchy.release();
        return contours;
    }

    private static void drawIndicatorsOnImg(Mat img, List<MatOfPoint> contours) {
        Imgproc.drawContours(img, contours, -1, new Scalar(255, 255, 255), 3);
    }

    public static void main(String arg[]) {
        Mat      frame           = new Mat();
        HSVColor pinkColorFilter = HSVColor.Pink;
        // HSVColor purpleColorFilter = HSVColor.Purple;
        HSVColor blueColorFilter = HSVColor.Blue;
        HSVColor limeColorFilter = HSVColor.Lime;
        // HSVColor greenColorFilter  = HSVColor.Green;

        ScreenSize screenSize = new ScreenSize();
        int        width      = screenSize.getWidthInt();
        int        height     = screenSize.getHeightInt();


        JFrame jframe = new JFrame("Gesture Recognizer");
        jframe.setSize(screenSize.getSize());
        jframe.setLayout(new BorderLayout());
        // jframe.setMinimumSize(size);
        // jframe.setMaximumSize(size);


        jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        // JLabel greenVid  = new JLabel();
        // JLabel purpleVid = new JLabel();
        JLabel limeVid = new JLabel();
        JLabel blueVid = new JLabel();
        JLabel pinkVid = new JLabel();
        JLabel allVid  = new JLabel();

        // greenVid.setHorizontalTextPosition(SwingConstants.CENTER);
        // purpleVid.setHorizontalTextPosition(SwingConstants.CENTER);
        limeVid.setHorizontalTextPosition(SwingConstants.CENTER);
        blueVid.setHorizontalTextPosition(SwingConstants.CENTER);
        pinkVid.setHorizontalTextPosition(SwingConstants.CENTER);
        allVid.setHorizontalTextPosition(SwingConstants.CENTER);

        // greenVid.setVerticalTextPosition(SwingConstants.TOP);
        // purpleVid.setVerticalTextPosition(SwingConstants.TOP);
        limeVid.setVerticalTextPosition(SwingConstants.TOP);
        blueVid.setVerticalTextPosition(SwingConstants.TOP);
        pinkVid.setVerticalTextPosition(SwingConstants.TOP);
        allVid.setVerticalTextPosition(SwingConstants.TOP);

        // greenVid.setText("GREEN");
        // purpleVid.setText("PURPLE");
        limeVid.setText("LIME");
        blueVid.setText("BLUE");
        pinkVid.setText("PINK");
        allVid.setText("ALL");

        // Begin slider window

        JFrame  adjustHSVRangeFrame       = new JFrame();
        JPanel  adjustHSVRangeParentPanel = new JPanel(new GridLayout(3, 1, 2, 2));
        JPanel  adjustHSVRangeSliderPanel = new JPanel(new GridLayout(2, 3, 2, 2));
        JSlider hsvMinSlider              = new JSlider(0, 179);
        JSlider hsvMaxSlider              = new JSlider(0, 179);
        JLabel  hsvMinSliderLabel         = new JLabel("Min Hue");
        JLabel  hsvMaxSliderLabel         = new JLabel("Max Hue");
        JLabel  hsvMinSliderMin           = new JLabel();
        JLabel  hsvMinSliderMax           = new JLabel();
        JLabel  hsvMaxSliderMin           = new JLabel();
        JLabel  hsvMaxSliderMax           = new JLabel();

        int adjustHSVRangeFrameW = 500;
        int adjustHSVRangeFrameH = 200;
        adjustHSVRangeFrame.setSize(adjustHSVRangeFrameW, adjustHSVRangeFrameH);
        adjustHSVRangeFrame.setLocation((width / 2) - (adjustHSVRangeFrameW / 2),
                                        (height / 2) - (adjustHSVRangeFrameH / 2));


        hsvMinSliderLabel.setSize(adjustHSVRangeFrameW, 20);
        hsvMaxSliderLabel.setSize(adjustHSVRangeFrameW, 20);

        hsvMinSliderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hsvMaxSliderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hsvMinSliderMin.setHorizontalAlignment(SwingConstants.RIGHT);
        hsvMaxSliderMin.setHorizontalAlignment(SwingConstants.RIGHT);

        hsvMinSliderMin.setText("0");
        hsvMaxSliderMax.setText("179");

        adjustHSVRangeSliderPanel.add(hsvMinSliderMin);
        adjustHSVRangeSliderPanel.add(hsvMinSlider);
        adjustHSVRangeSliderPanel.add(hsvMinSliderMax);

        adjustHSVRangeSliderPanel.add(hsvMaxSliderMin);
        adjustHSVRangeSliderPanel.add(hsvMaxSlider);
        adjustHSVRangeSliderPanel.add(hsvMaxSliderMax);

        adjustHSVRangeParentPanel.add(hsvMinSliderLabel);
        adjustHSVRangeParentPanel.add(adjustHSVRangeSliderPanel);
        adjustHSVRangeParentPanel.add(hsvMaxSliderLabel);
        adjustHSVRangeFrame.add(adjustHSVRangeParentPanel);

        FullScreenUtilities.setWindowCanFullScreen(adjustHSVRangeFrame, false);

        BiConsumer<Integer, Integer> resetLabels = (minHue, maxHue) -> {
            hsvMinSlider.setMaximum(maxHue);
            hsvMaxSlider.setMinimum(minHue);
            hsvMinSliderMax.setText(String.valueOf(maxHue));
            hsvMaxSliderMin.setText(String.valueOf(minHue));
            hsvMinSliderLabel.setText("Min Hue: " + minHue);
            hsvMaxSliderLabel.setText("Max Hue: " + maxHue);
        };
        hsvMaxSlider.addChangeListener(e -> {
            if (filterToAdjust != null && hsvMaxSlider.getValueIsAdjusting()) {
                int minHue = hsvMinSlider.getValue();
                int maxHue = hsvMaxSlider.getValue();
                resetLabels.accept(minHue, maxHue);
                filterToAdjust.setHueMax(new Scalar(maxHue, 255, 255, 0));
            }
        });
        hsvMinSlider.addChangeListener(e -> {
            if (filterToAdjust != null && hsvMinSlider.getValueIsAdjusting()) {
                int minHue = hsvMinSlider.getValue();
                int maxHue = hsvMaxSlider.getValue();
                resetLabels.accept(minHue, maxHue);
                filterToAdjust.setHueMin(new Scalar(minHue, 0, 0, 0));
            }
        });

        BiConsumer<String, HSVColor> openHueAdjusterWindow = (colorName, color) -> {
            filterToAdjust = color;
            adjustHSVRangeFrame.setTitle("Adjust HSV Range: " + colorName);
            int minHue = (int) filterToAdjust.getHueMin().val[0];
            int maxHue = (int) filterToAdjust.getHueMax().val[0];
            hsvMinSlider.setValue(minHue);
            hsvMaxSlider.setValue(maxHue);
            resetLabels.accept(minHue, maxHue);
            adjustHSVRangeFrame.setVisible(true);
        };

        // setColorPanelListener(greenVid, point -> openHueAdjusterWindow.accept("Green", greenColorFilter));
        // setColorPanelListener(purpleVid, point -> openHueAdjusterWindow.accept("Purple", purpleColorFilter));
        setColorPanelListener(limeVid,
                              point -> openHueAdjusterWindow.accept("Lime", limeColorFilter));
        setColorPanelListener(blueVid,
                              point -> openHueAdjusterWindow.accept("Blue", blueColorFilter));
        setColorPanelListener(pinkVid,
                              point -> openHueAdjusterWindow.accept("Pink", pinkColorFilter));

        // End slider window

        // jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
        FullScreenUtilities.setWindowCanFullScreen(jframe, true);

        Container cameraContentPanels = new JPanel(new GridLayout(2, 3, 2, 2));
        // cameraContentPanels.add(greenVid);
        // cameraContentPanels.add(purpleVid);
        cameraContentPanels.add(limeVid);
        cameraContentPanels.add(blueVid);
        cameraContentPanels.add(pinkVid);
        cameraContentPanels.add(allVid);


        cameraId = 0;
        capture = new VideoCapture(cameraId);
        capture.read(frame);

        // Container            flightControlContent = new JPanel(new GridLayout(3, 3, 0, 0));
        FlightIndicatorPanel flightIndicatorPanel = new FlightIndicatorPanel();

        guiContainers.put(VIEW_FLIGHT_CONTROLLER, flightIndicatorPanel);
        guiContainers.put(VIEW_CAMERA, cameraContentPanels);
        guiContainers.put(VIEW_NOTHING, null);

        Consumer<Integer> showPanel = panelId -> {
            showCameraPreviewWindow = panelId == VIEW_CAMERA;

            jframe.setVisible(false);
            Optional.ofNullable(guiContainers.get(panelId)).ifPresent(container -> {
                jframe.getContentPane().removeAll();
                jframe.getContentPane().setLayout(new BorderLayout());
                jframe.getContentPane().add(container, BorderLayout.CENTER);
                jframe.revalidate();
                jframe.pack();
                jframe.setSize(screenSize.getSize());
                jframe.setVisible(true);
            });
        };

        showPanel.accept(VIEW_NOTHING);
        // Application.getApplication().requestToggleFullScreen(jframe);

        Runnable connectedCallback = () -> showPanel.accept(VIEW_CAMERA);
        Runnable disconnectedCallback = new Runnable() {
            @Override
            public void run() {
                showPanel.accept(VIEW_NOTHING);
                drone.connect(connectedCallback, this);
            }
        };
        drone = new DroneController(connectedCallback, disconnectedCallback);
        GestureDetector gestureDetector = new GestureDetector(drone);
        gestureDetector.setHandListener((hand1, gesture) -> flightIndicatorPanel.updateData(hand1,
                                                                                            gesture,
                                                                                            frame.width()));

        jframe.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 0x0A:
                        // Enter
                        drone.takeOff();
                        break;
                    case 27:
                        // Escape
                        showCameraPreviewWindow = !showCameraPreviewWindow;

                        showPanel.accept(showCameraPreviewWindow
                                         ? VIEW_FLIGHT_CONTROLLER
                                         : VIEW_CAMERA);
                        break;
                    case 157:
                        // Tick mark (`)
                        capture = new VideoCapture(++cameraId);
                        break;
                    case 70:
                        // F
                        imageRotation += 90;
                        imageRotation = imageRotation >= 360 ? 0 : imageRotation;
                        break;
                    case 0x10:
                        // Shift
                        drone.land();
                        break;
                    case 0x25:
                        // Left
                        break;
                    case 0x27:
                        // Right
                        break;
                    case 0x26:
                        // Up
                        drone.sendCommand(new ChangeAltitudeCommand(1));
                        break;
                    case 0x28:
                        // Down
                        drone.sendCommand(new ChangeAltitudeCommand(-1));
                        break;
                    case 0x51:
                        // Q
                        break;
                    case 0x41:
                        // A
                        break;
                    default:
                        System.out.println("Unkown key pressed: " + e.getKeyCode());
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        // jframe.setVisible(true);
        // showCameraPreviewWindow = true;

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                // System.out.println("Capture: " + cameraId);
                capture.read(frame);
                if (!capture.isOpened()) {
                    cameraId = 0;
                    try {
                        capture = new VideoCapture(cameraId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (!frame.empty()) {
                    // if (jframe.isVisible() != showCameraPreviewWindow)
                    //     jframe.setVisible(showCameraPreviewWindow);

                    int imgHeight = 600;
                    int imgWidth  = 800;

                    Size sz = new Size(imgWidth, imgHeight);
                    Imgproc.resize(frame, frame, sz);
                    Core.flip(frame, frame, 1);

                    switch (imageRotation) {
                        case 90:
                            Core.rotate(frame, frame, Core.ROTATE_90_COUNTERCLOCKWISE);
                            break;
                        case 180:
                            Core.rotate(frame, frame, Core.ROTATE_180);
                            break;
                        case 270:
                            Core.rotate(frame, frame, Core.ROTATE_90_CLOCKWISE);
                            break;
                        default:
                    }

                    Mat blurredFrame = new Mat();
                    Imgproc.GaussianBlur(frame, blurredFrame, new Size(9, 15), 0, 0);

                    Mat hsv_image = new Mat();
                    Imgproc.cvtColor(blurredFrame, hsv_image, Imgproc.COLOR_BGR2HSV);

                    Mat allColorsImg = Mat.zeros(hsv_image.rows(), hsv_image.cols(), 0);

                    // MatOfPoint purple = processImageForColor(hsv_image, allColorsImg, purpleColorFilter,
                    //         showCameraPreviewWindow
                    //         ? purpleVid
                    //         : null);
                    // MatOfPoint green = processImageForColor(hsv_image, allColorsImg, greenColorFilter,
                    //         showCameraPreviewWindow
                    //         ? greenVid
                    //         : null);
                    MatOfPoint pink = processImageForColor(hsv_image,
                                                           allColorsImg,
                                                           pinkColorFilter,
                                                           showCameraPreviewWindow
                                                           ? pinkVid
                                                           : null);
                    MatOfPoint blue = processImageForColor(hsv_image,
                                                           allColorsImg,
                                                           blueColorFilter,
                                                           showCameraPreviewWindow
                                                           ? blueVid
                                                           : null);
                    MatOfPoint lime = processImageForColor(hsv_image,
                                                           allColorsImg,
                                                           limeColorFilter,
                                                           showCameraPreviewWindow
                                                           ? limeVid
                                                           : null);

                    Hand hand = Hand.of(pink, /*purple,*/ blue, lime/*, green*/);
                    // Slows down processing by a notable factor, should only be used when NOT detecting gestures
                    if (showCameraPreviewWindow) {
                        List<MatOfPoint> allDetectedBlobs = Stream.of(pink, /*purple,*/
                                                                      blue, lime/*, green*/)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());

                        drawIndicatorsOnImg(frame, allDetectedBlobs);
                        showImage(frame, allVid);
                    }

                    boolean isTrackingGestures = gestureDetector.detectGesture(hand,
                                                                               () -> matToBufferedImage(
                                                                                       frame));
                    // if (!isTrackingGestures) {
                    //     jframe.setVisible(true);
                    //     Slows down processing by a notable factor, should only be used when NOT detecting gestures
                    // showImage(frame, allVid);
                    // try {
                    //     Thread.sleep(1000);
                    // } catch (InterruptedException e) {
                    //     e.printStackTrace();
                    // }
                    // } else {
                    //     jframe.setVisible(false);
                    // }

                    System.gc();
                    // System.runFinalization();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static BufferedImage matToBufferedImage(Mat image) {
        MatOfByte bytemat = new MatOfByte();
        Imgcodecs.imencode(".jpg", image, bytemat);
        byte[]        bytes = bytemat.toArray();
        InputStream   in    = new ByteArrayInputStream(bytes);
        BufferedImage img   = null;
        try {
            img = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bytemat.release();
        return img;
    }

    private static MatOfPoint processImageForColor(Mat srcImg, Mat dstImg, HSVColor color, JLabel vidOutput) {
        Mat  thisColorDstImg = new Mat();
        Size imgSize         = new Size(srcImg.width(), srcImg.height());

        Core.inRange(srcImg, color.getHueMin(), color.getHueMax(), thisColorDstImg);
        // Core.inRange(srcImg, hsv_min2, hsv_max2, thresholded2);
        // Core.bitwise_or(thresholded, thresholded2, thresholded);


        // Begin Blur
        Mat array255 = new Mat(imgSize, CvType.CV_8UC1);
        array255.setTo(new Scalar(255));
        List<Mat> lhsv     = new ArrayList<>(3);
        Mat       distance = new Mat(imgSize, CvType.CV_8UC1);
        Core.split(srcImg, lhsv);

        Mat S = lhsv.get(1);
        Mat V = lhsv.get(2);
        Core.subtract(array255, S, S);
        Core.subtract(array255, V, V);
        S.convertTo(S, CvType.CV_32F);
        V.convertTo(V, CvType.CV_32F);

        Mat thresholded = new Mat();

        Core.magnitude(S, V, distance);
        Core.inRange(distance,
                     new Scalar(color.getLowerThreshold()),
                     new Scalar(color.getUpperThreshold()),
                     thresholded);
        Core.bitwise_and(thisColorDstImg, thresholded, thisColorDstImg);
        Core.bitwise_or(dstImg, thisColorDstImg, dstImg);

        Imgproc.GaussianBlur(thisColorDstImg, thisColorDstImg, new Size(23, 29), 0, 0);
        if (vidOutput != null) {
            showImage(thisColorDstImg, vidOutput);
        }
        MatOfPoint matOfPoint1 = detectBlobs(thisColorDstImg).stream()
                // .peek(matOfPoint -> System.out.println(matOfPoint.size().area()))
                .filter(matOfPoint -> matOfPoint.size().area() > 60)
                .reduce(BinaryOperator.maxBy((pointA, pointB) -> (int) (pointA.size()
                        .area() - pointB.size().area())))
                .orElse(null);

        S.release();
        V.release();
        array255.release();
        distance.release();
        thresholded.release();
        thisColorDstImg.release();

        lhsv.forEach(Mat::release);

        return matOfPoint1;
    }

    private static void setColorPanelListener(JLabel greenVid, Consumer<Point> mouseEventConsumer) {
        greenVid.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mouseEventConsumer.accept(e.getLocationOnScreen());
                }
            }
        });
    }

    private static void showImage(Mat img, JLabel video) {
        ImageIcon image = new ImageIcon(matToBufferedImage(img));
        video.setIcon(image);
        video.repaint();
    }

    static class HSVColor {
        static HSVColor Blue = new HSVColor(99, 106, 0, 215);
        // static HSVColor Green  = new HSVColor(89, 96, 100,235);
        // static HSVColor Green  = new HSVColor(71, 75);
        // static HSVColor Purple = new HSVColor(145, 167, 20, 215);
        static HSVColor Lime = new HSVColor(66, 73);
        static HSVColor Pink = new HSVColor(174, 179);

        Scalar hueMax;
        Scalar hueMin;
        int    lowerThreshold;
        int    upperThreshold;

        HSVColor(int hueMin, int hueMax) {
            this.hueMin = new Scalar(hueMin, 0, 0, 0);
            this.hueMax = new Scalar(hueMax, 255, 255, 0);
            this.lowerThreshold = 0;
            this.upperThreshold = 200;
        }

        @SuppressWarnings("SameParameterValue")
        HSVColor(int hueMin, int hueMax, int lowerThreshold, int upperThreshold) {
            this.hueMin = new Scalar(hueMin, 0, 0, 0);
            this.hueMax = new Scalar(hueMax, 255, 255, 0);
            this.lowerThreshold = lowerThreshold;
            this.upperThreshold = upperThreshold;
        }

        @Override
        public String toString() {
            return "HSVColor{" + "hueMax=" + hueMax + ", hueMin=" + hueMin + '}';
        }

        Scalar getHueMax() {
            return hueMax;
        }

        void setHueMax(Scalar hueMax) {
            this.hueMax = hueMax;
        }

        Scalar getHueMin() {
            return hueMin;
        }

        void setHueMin(Scalar hueMin) {
            this.hueMin = hueMin;
        }

        public int getLowerThreshold() {
            return lowerThreshold;
        }

        public int getUpperThreshold() {
            return upperThreshold;
        }
    }

}
