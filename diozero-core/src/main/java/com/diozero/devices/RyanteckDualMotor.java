package com.diozero.devices;

import com.diozero.api.RuntimeIOException;
import com.diozero.devices.motor.DualMotor;
import com.diozero.devices.motor.PwmMotor;

/**
 * RTK MCB Robot. Generic robot controller with pre-configured GPIO connections.
 */
public class RyanteckDualMotor extends DualMotor {
	public RyanteckDualMotor() throws RuntimeIOException {
		super(new PwmMotor(17, 18), new PwmMotor(22, 23));
	}
}
