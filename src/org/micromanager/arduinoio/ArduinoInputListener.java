package org.micromanager.arduinoio;
/**
 * Interface for Arduino-Input digital value events
 * @author iobataya
 */
public interface ArduinoInputListener {
	void ValueChanged(ArduinoInputEvent e);
	void IsRisingAt0();
	void IsFallingAt0();
	void IsRisingAt1();
	void IsFallingAt1();
}
