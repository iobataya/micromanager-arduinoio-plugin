package org.micromanager.arduinoio;

public interface ArduinoInputListener {
	void ValueChanged(ArduinoInputEvent e);
	void IsRisingAt0();
	void IsFallingAt0();
	void IsRisingAt1();
	void IsFallingAt1();
}
