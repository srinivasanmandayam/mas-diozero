package com.diozero.internal.provider.firmata;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.Pin.Mode;
import org.firmata4j.firmata.FirmataDevice;
import org.pmw.tinylog.Logger;

import com.diozero.api.*;
import com.diozero.internal.spi.*;
import com.diozero.util.BoardInfo;
import com.diozero.util.RuntimeIOException;

public class FirmataDeviceFactory extends BaseNativeDeviceFactory {
	public static final String DEVICE_NAME = "Firmata";
	
	private IODevice ioDevice;
	
	public FirmataDeviceFactory() {
		String port_name = System.getProperty("FIRMATA_PORT");
		if (port_name == null) {
			port_name = System.getenv("FIRMATA_PORT");
		}
		if (port_name == null) {
			throw new IllegalArgumentException("Error, FIRMATA_PORT not set");
		}
		ioDevice = new FirmataDevice(port_name);
		
		try {
			ioDevice.start();
			Logger.info("Waiting for Firmata device '" + port_name + "' to initialise");
			ioDevice.ensureInitializationIsDone();
			Logger.info("Firmata device '" + port_name + "' successfully initialised");
		} catch (IOException | InterruptedException e) {
			throw new RuntimeIOException(e);
		}
		
		initialiseBoardInfo();
	}
	
	@Override
	public void shutdown() {
		Logger.info("shutdown()");
		super.shutdown();
		if (ioDevice != null) {
			try { ioDevice.stop(); } catch (Exception e) {}
		}
	}
	
	IODevice getIoDevice() {
		return ioDevice;
	}

	@Override
	public String getName() {
		return DEVICE_NAME;
	}
	
	@Override
	public void initialiseBoardInfo() {
		boardInfo = new FirmataBoardInfo(ioDevice);
	}
	
	@Override
	public int getPwmFrequency(int gpio) {
		// Note that the base frequency for pins 3, 9, 10, and 11 is 31250 Hz and for pins 5 and 6 is 62500 Hz
		switch (gpio) {
		case 3:
		case 9:
		case 10:
		case 11:
			return 31250;
		case 5:
		case 6:
			return 62500;
		}
		return -1;
	}
	
	@Override
	public void setPwmFrequency(int gpio, int frequency) {
		// Ignore
		Logger.warn("Not implemented");
	}
	
	@Override
	public GpioDigitalInputDeviceInterface createDigitalInputDevice(String key, PinInfo pinInfo, GpioPullUpDown pud,
			GpioEventTrigger trigger) throws RuntimeIOException {
		return new FirmataDigitalInputDevice(this, key, pinInfo.getDeviceNumber(), pud, trigger);
	}
	
	@Override
	public GpioDigitalOutputDeviceInterface createDigitalOutputDevice(String key, PinInfo pinInfo, boolean initialValue)
			throws RuntimeIOException {
		return new FirmataDigitalOutputDevice(this, key, pinInfo.getDeviceNumber(), initialValue);
	}

	@Override
	public GpioDigitalInputOutputDeviceInterface createDigitalInputOutputDevice(
			String key, PinInfo pinInfo, DeviceMode mode)
			throws RuntimeIOException {
		return new FirmataDigitalInputOutputDevice(this, key, pinInfo.getDeviceNumber(), mode);
	}
	
	@Override
	public PwmOutputDeviceInterface createPwmOutputDevice(String key, PinInfo pinInfo, float initialValue)
			throws RuntimeIOException {
		return new FirmataPwmOutputDevice(this, key, pinInfo.getDeviceNumber(), initialValue);
	}
	
	@Override
	public AnalogInputDeviceInterface createAnalogInputDevice(String key, PinInfo pinInfo)
			throws RuntimeIOException {
		return new FirmataAnalogInputDevice(this, key, pinInfo.getDeviceNumber());
	}
	
	@Override
	public AnalogOutputDeviceInterface createAnalogOutputDevice(String key, PinInfo pinInfo)
			throws RuntimeIOException {
		throw new UnsupportedOperationException("Analog output not supported by device factory '"
				+ getClass().getSimpleName() + "' on device '" + boardInfo.getName() + "'");
	}
	
	@Override
	protected SpiDeviceInterface createSpiDevice(String key, int controller, int chipSelect, int frequency,
			SpiClockMode spiClockMode, boolean lsbFirst) throws RuntimeIOException {
		throw new UnsupportedOperationException("SPI is not supported by device factory '"
				+ getClass().getSimpleName() + "' on device '" + boardInfo.getName() + "'");
	}

	@Override
	protected I2CDeviceInterface createI2CDevice(String key, int controller, int address, int addressSize,
			int clockFrequency) throws RuntimeIOException {
		return new FirmataI2CDevice(this, key, controller, address, addressSize, clockFrequency);
	}
	
	public static class FirmataBoardInfo extends BoardInfo {
		public FirmataBoardInfo(IODevice ioDevice) {
			super("Firmata", "Unknown", -1, "firmata");
			
			for (Pin pin : ioDevice.getPins()) {
				int pin_number = pin.getIndex();
				addGpioPinInfo(pin_number, pin_number, convertModes(pin.getSupportedModes()));
			}
		}

		private static Set<DeviceMode> convertModes(Set<Mode> firmataModes) {
			Set<DeviceMode> modes = new HashSet<>();
			
			for (Mode firmata_mode : firmataModes) {
				switch (firmata_mode) {
				case INPUT:
					modes.add(DeviceMode.DIGITAL_INPUT);
					break;
				case OUTPUT:
					modes.add(DeviceMode.DIGITAL_OUTPUT);
					break;
				case ANALOG:
					modes.add(DeviceMode.ANALOG_INPUT);
					break;
				case PWM:
					modes.add(DeviceMode.PWM_OUTPUT);
					break;
				default:
					// Ignore
				}
			}
			
			return modes;
		}
	}
}
