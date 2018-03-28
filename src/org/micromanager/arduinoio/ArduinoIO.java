package org.micromanager.arduinoio;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.micromanager.MMStudio;
import org.micromanager.api.MMPlugin;
import org.micromanager.api.ScriptInterface;
import org.micromanager.utils.ReportingUtils;

import mmcorej.CMMCore;

/**
 * Polls and sets Digital-Input and Digital-Output of Arduino.
 * @author iobataya
 *
 */
@SuppressWarnings("unused")
public class ArduinoIO implements MMPlugin {
	public static final String menuName = "Arduino I/O";
	public static final String tooltipDescription = "Displays a frame to control triggering of acq by external TTL signal";
	private ScriptInterface gui_;
	private static MMStudio mmStudio_;
	private CMMCore core_;
	private ArduinoIoMigForm acqform_;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			mmStudio_ = new MMStudio(false);
			ArduinoIO arduino = new ArduinoIO();
			arduino.setApp(mmStudio_);
			arduino.show();
		} catch (Exception e) {
			ReportingUtils.showError(e, "A java error has caused Micro-Manager to exit.");
		}
	}

	public ArduinoPoller getPoller() {
		try {
			return ArduinoPoller.getInstance(mmStudio_);
		} catch (Exception ex) {
			ReportingUtils.logError(ex);
		}
		return null;
	}

	@Override
	public String getDescription() {
		return tooltipDescription;
	}

	@Override
	public String getInfo() {
		return tooltipDescription;
	}

	@Override
	public String getVersion() {
		return "1.0";
	}

	@Override
	public String getCopyright() {
		return "JPK Instruments AG, 2018";
	}

	@Override
	public void dispose() {
	}

	@Override
	public void setApp(ScriptInterface app) {
		gui_ = app;
		core_ = gui_.getMMCore();
	}

	@Override
	public void show() {
		acqform_ = ArduinoIoMigForm.getInstance(mmStudio_);
	}

}
