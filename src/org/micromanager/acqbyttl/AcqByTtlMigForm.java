package org.micromanager.acqbyttl;

import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.micromanager.api.DataProcessor;
import org.micromanager.api.ScriptInterface;
import org.micromanager.overlayarduino.*;
import org.micromanager.subtractbackground.*;
import org.micromanager.utils.MMDialog;

import mmcorej.CMMCore;
import net.miginfocom.swing.MigLayout;
import org.micromanager.utils.ReportingUtils;

@SuppressWarnings("serial")
public class AcqByTtlMigForm extends MMDialog implements ArduinoInputListener{

	private ScriptInterface gui_;
	private SubtractBackgroundProcessor subtractProcessor_ = null;
	private OverlayArduinoProcessor arduinoProcessor_ = null;
	private CMMCore mmc_ = null;
	private final JLabel statusLabel_;
	private final Font arialSmallFont_;
	private final Font arialLargeFont_;
//	private final JButton setBottomButton_;

	private AcqByTtlStatus acqStatus_;

	public AcqByTtlMigForm(ScriptInterface gui) {
		gui_ = gui;
		gui_.addMMBackgroundListener(this);
		mmc_ = gui.getMMCore();

		// Show current status
		// * idle
		// * wating for trigger
		// * Acquisition after trigger
		// * capturing background
		acqStatus_ = new AcqByTtlStatus(gui_);

		// Do action from this form.
		// Ask AcqByTtlStatus to check it's allowed to do.
		// * Start : Start waiting for trigger
		// * Stop : Stop waiting for trigger
		// * Acquire : Acquire now
		// * Capture-BG : Capture BG image and register to SubtractBG processor

		arialSmallFont_ = new Font("Arial", Font.PLAIN, 12);
		arialLargeFont_ = new Font("Arial", Font.PLAIN, 16);
		this.setLayout(new MigLayout("flowx, fill, insets 8"));
		this.setTitle(AcqByTTL.menuName);

		// Label for TTL signal triggering
		final JLabel ttlLabel = new JLabel("Triggering by TTL");

		// Buttons
		final JButton waitTriggerStartButton = new JButton();
		waitTriggerStartButton.setFont(arialLargeFont_);
		waitTriggerStartButton.setText("Wait for Triggers");
		waitTriggerStartButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(acqStatus_.canStartWaitingTriggers()) {
					acqStatus_.setStatusWaitForTTL(true);
					ReportingUtils.logMessage("Started waiting for TTL signals. (not impl)");
				}
			}
		});
		final JButton waitTriggerStopButton = new JButton();
		waitTriggerStopButton.setFont(arialLargeFont_);
		waitTriggerStopButton.setText("STOP");
		waitTriggerStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(acqStatus_.canStopWaitingTriggers()) {
					acqStatus_.setStatusWaitForTTL(false);
					ReportingUtils.logMessage("Stopped for waiting. (not impl)");
				}
			}
		});

		add(ttlLabel,"wrap");
		add(waitTriggerStartButton);
		add(waitTriggerStopButton,"wrap");

		
		// Label for capturing background image
		final JLabel captureLabel = new JLabel("Capture background image now");
		final JButton captureButton = new JButton();
		captureButton.setFont(arialLargeFont_);
		captureButton.setText("Capture BG image");
		captureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!acqStatus_.isAcquiring()) {
					acqStatus_.setStatusWaitForTTL(false);
					ReportingUtils.logMessage("Started capturing background image(not impl)");
				}else {
					ReportingUtils.logMessage("Acquiring is already running. Canceled.");
				}
			}
		});
		add(captureLabel,"wrap");
		add(captureButton,"wrap");


		// Label for acquring without TTL signal triggering
		final JLabel actionLabel = new JLabel("Actions without TTL triggering");
		
		final JButton acquireButton = new JButton();
		acquireButton.setFont(arialLargeFont_);
		acquireButton.setText("Acquire now");
		acquireButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!acqStatus_.isAcquiring()) {
					acqStatus_.setStatusWaitForTTL(false);
					ReportingUtils.logMessage("Started acquring without trigger.(not impl)");
				}else {
					ReportingUtils.logMessage("Acquiring is already running. Canceled.");
				}
			}
		});

		add(actionLabel,"wrap");
		add(acquireButton,"wrap");

		// Status bar
		statusLabel_ = new JLabel(" ");
		add(statusLabel_, "span 3, wrap");
		loadAndRestorePosition(100, 100, 350, 250);
	}

	public synchronized void setStatus(final String status) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (status != null) {
					statusLabel_.setText(status);
				}
			}
		});
	}

	public void setSubtractBGProcessor(SubtractBackgroundProcessor subtractProc) {
		subtractProcessor_ = subtractProc;
	}

	public void setOverlayArduinoProcessor(OverlayArduinoProcessor arduinoProc) {
		arduinoProcessor_ = arduinoProc;
		arduinoProcessor_.setListener(this);
	}

	private int currentDigitalIn_ = 0;
	@Override
	public void ValueChanged(ArduinoInputEvent e) {
		currentDigitalIn_ = e.getDigitalValue();
	}

	@Override
	public void IsRisingAt0() {
		this.setStatus(String.format("Signal Input0 rising to HIGH (%d)",currentDigitalIn_));
	}

	@Override
	public void IsFallingAt0() {
		this.setStatus(String.format("Signal Input0 falling to LOW (%d)",currentDigitalIn_));
	}

	@Override
	public void IsRisingAt1() {
		this.setStatus(String.format("Signal Input1 rising to HIGH (%d)",currentDigitalIn_));
	}

	@Override
	public void IsFallingAt1() {
		this.setStatus(String.format("Signal Input1 falling to LOW",currentDigitalIn_));
	}
}
