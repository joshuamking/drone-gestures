package io.joshking.dronegestures.utils;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;

public class MsgBox {
    public static final int        DEFAULT_MSG_TIME = 2000;
    private final       JFrame     msgFrame;
    private final       JLabel     msgLabel;
    private final       JPanel     msgPanel;
    private             String     currentMsg;
    private             Dimension  frameSize;
    private             ScreenSize screenSize;

    public MsgBox() {
        screenSize = new ScreenSize();
        frameSize = new Dimension(400, 200);

        msgLabel = new JLabel("", SwingConstants.CENTER);
        msgPanel = new JPanel(new GridLayout(1, 1));
        msgFrame = new JFrame();

        msgLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));

        // msgPanel.add(msgLabel);

        msgFrame.setLayout(new BorderLayout(5, 5));
        msgFrame.add(msgLabel, BorderLayout.CENTER);
        msgFrame.setUndecorated(true);
        msgFrame.setSize(frameSize);
        msgFrame.setLocation(PointUtils.offsetByHalf(screenSize.getCenter(), frameSize));

        msgFrame.setOpacity(0);
        msgFrame.setVisible(true);
        msgFrame.setVisible(false);
        msgFrame.setOpacity(0.5f);
    }

    public void showMsg(String msg, int msgTime) {
        this.currentMsg = msg;
        FontMetrics metrics = msgLabel.getGraphics().getFontMetrics();
        int         width   = metrics.stringWidth(msg);

        screenSize = new ScreenSize();
        frameSize = new Dimension(width + 50, msgFrame.getHeight());

        msgLabel.setText(msg);

        msgFrame.setSize(frameSize);
        msgFrame.setLocation(PointUtils.offsetByHalf(screenSize.getCenter(), frameSize));
        msgFrame.setVisible(true);

        if (msgTime < 0) {
            msgTime = 0;
        }

        if (msgTime < Integer.MAX_VALUE) {
            ProjectUtils.delayRunnable(() -> msgFrame.setVisible(!Objects.equals(msg, currentMsg)),
                                       msgTime);
        }
    }

    public void showMsg(String msg) {
        showMsg(msg, DEFAULT_MSG_TIME);
    }
}
