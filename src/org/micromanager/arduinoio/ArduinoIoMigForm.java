package org.micromanager.arduinoio;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.micromanager.api.ScriptInterface;
import org.micromanager.utils.MMDialog;
import org.micromanager.utils.ReportingUtils;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ArduinoIoMigForm extends MMDialog implements ArduinoInputListener {
	private final ScriptInterface gui_;
	@SuppressWarnings("unused")
	private final mmcorej.CMMCore mmc_;
	private ArduinoPoller poller_ = null;
	//private final JButton btnStartStop_;
	private final JLabel lblTitleStatus_;
	private final JLabel lblStatus_;
	private final JRadioButton radioInput0_;
	private final JRadioButton radioInput1_;
	private final JLabel lblMessage_;
	private final JCheckBox chkOutput8_;
	private final JCheckBox chkOutput13_;
	private ArrayList<ArduinoInputListener> listeners_ = new ArrayList<ArduinoInputListener>();
	private static ArduinoIoMigForm instance_;
	private final Font arialSmallFont_;
	private static int currentDigitalInput_ = 0;

	public static ArduinoIoMigForm getInstance(ScriptInterface gui) {
		if (instance_ == null) {
			instance_ = new ArduinoIoMigForm(gui);
		}
		return instance_;
	}

	private ArduinoIoMigForm(ScriptInterface gui) {
		gui_ = gui;
		gui_.addMMBackgroundListener(this);
		mmc_ = gui.getMMCore();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				dispose();
			}
		});

		arialSmallFont_ = new Font("Arial", Font.PLAIN, 12);
		this.setLayout(new MigLayout("flowx, fill, insets 2"));
		this.setTitle(ArduinoIO.menuName);

		// Start/Stop polling
//		btnStartStop_ = new JButton("Start/Stop");
//		btnStartStop_.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent evt) {
//				if (poller_.isRunning()) {
//					poller_.requestStop();
//				}else {
//					poller_.startThread();
//				}
//			}
//		});
//		add(btnStartStop_,"wrap");

		// Status title
		lblTitleStatus_ = new JLabel("Status:");
		lblTitleStatus_.setFont(arialSmallFont_);
		// Status label
		lblStatus_ = new JLabel(" ");
		lblStatus_.setFont(arialSmallFont_);
		add(lblTitleStatus_);
		add(lblStatus_, "wrap");

		// Input signals
		radioInput0_ = new JRadioButton("Input0");
		radioInput0_.setEnabled(false);
		radioInput1_ = new JRadioButton("Input1");
		radioInput1_.setEnabled(false);
		add(radioInput0_);
		add(radioInput1_, "wrap");

		// message label
		lblMessage_ = new JLabel(" ");
		add(lblMessage_, "span 3, wrap");

		// Output signals
		chkOutput8_ = new JCheckBox("Output8");
		chkOutput8_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				updateDigitalOut();
			}
		});
		chkOutput13_ = new JCheckBox("Output13");
		chkOutput13_.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				updateDigitalOut();
			}
		});
		add(chkOutput8_);
		add(chkOutput13_, "wrap");

		setVisible(true);

		// Setup ArduinoPoller and listener
		try {
			if (poller_ == null) {
				poller_ = ArduinoPoller.getInstance(gui_);
				poller_.addListener(this);
			}
		} catch (Exception ex) {
			ReportingUtils.logError(ex);
			lblStatus_.setText("Could not get Arduino poller.");
		}
	}

	public synchronized void setStatus(final String status) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (status != null) {
					lblMessage_.setText(status);
				}
			}
		});
	}

	private synchronized void updateDigitalOut() {
		int digitalValue = 0;
		if (chkOutput8_.isSelected()) {
			digitalValue += 1;
		}
		if (chkOutput13_.isSelected()) {
			digitalValue += 32;
		}
		if (poller_ != null) {
			poller_.setDigitalOut(digitalValue);
		}
	}

	public int getCurrentDigitalInput() {
		return currentDigitalInput_;
	}

	@Override
	public void ValueChanged(ArduinoInputEvent e) {
		for (ArduinoInputListener l : listeners_) {
			l.ValueChanged(e);
		}
		radioInput0_.setSelected(e.isHighAt0());
		radioInput1_.setSelected(e.isHighAt1());
		currentDigitalInput_ = e.getDigitalValue();
	}

	@Override
	public void IsRisingAt0() {
		for (ArduinoInputListener l : listeners_) {
			l.IsRisingAt0();
		}
		this.setStatus(String.format("Signal Input0 rising to HIGH (%d)", currentDigitalInput_));
	}

	@Override
	public void IsFallingAt0() {
		for (ArduinoInputListener l : listeners_) {
			l.IsFallingAt0();
		}
		this.setStatus(String.format("Signal Input0 falling to LOW (%d)", currentDigitalInput_));
	}

	@Override
	public void IsRisingAt1() {
		for (ArduinoInputListener l : listeners_) {
			l.IsRisingAt1();
		}
		this.setStatus(String.format("Signal Input1 rising to HIGH (%d)", currentDigitalInput_));
	}

	@Override
	public void IsFallingAt1() {
		for (ArduinoInputListener l : listeners_) {
			l.IsFallingAt1();
		}
		this.setStatus(String.format("Signal Input1 falling to LOW", currentDigitalInput_));
	}

	@Override
	public void dispose() {
		super.dispose();
		poller_.clearListeners();
		poller_.requestStop();
	}

	public void addListener(ArduinoInputListener l) {
		listeners_.add(l);
	}

	public void removeListener(ArduinoInputListener l) {
		listeners_.remove(listeners_.indexOf(l));
	}

	public void clearListeners() {
		listeners_.clear();
	}

}
