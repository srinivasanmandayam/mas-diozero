package com.diozero.internal.spi;

/*
 * #%L
 * Organisation: diozero
 * Project:      Device I/O Zero - Core
 * Filename:     DeviceFactoryInterface.java  
 * 
 * This file is part of the diozero project. More information about this project
 * can be found at http://www.diozero.com/
 * %%
 * Copyright (C) 2016 - 2021 diozero
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.Closeable;

import com.diozero.api.DeviceInterface;
import com.diozero.api.PinInfo;
import com.diozero.api.RuntimeIOException;
import com.diozero.sbc.BoardPinInfo;

public interface DeviceFactoryInterface extends Closeable {
	/**
	 * Lifecycle method to start this device factory
	 */
	default void start() {
		// Do nothing
	}

	/**
	 * Close this device factory including all devices that have been provisioned by
	 * it.
	 */
	@Override
	void close() throws RuntimeIOException;

	/**
	 * Check if this device factory is closed.
	 * 
	 * @return true if this device factory is closed
	 */
	boolean isClosed();

	/**
	 * Reopen this device factory.
	 */
	void reopen();

	/**
	 * Check if the device with the given unique key is opened
	 * 
	 * @param key the unique key of the device
	 * @return true if the device is opened
	 */
	boolean isDeviceOpened(String key);

	/**
	 * diozero internal method to notify the {@link AbstractDeviceFactory} that a
	 * device has been opened. Enables diozero to perform cleanup operations, for
	 * example closing a device factory closes all devices provisionined by that
	 * device factory.
	 */
	void deviceOpened(DeviceInterface device);

	/**
	 * diozero internal method to notify the {@link AbstractDeviceFactory} that a
	 * device has been closed.
	 */
	void deviceClosed(DeviceInterface device);

	/**
	 * Get the name of this device factory
	 * 
	 * @return the name of this device factory
	 */
	String getName();

	/**
	 * Get information about pins that can be provisioned by this device factory.
	 * 
	 * @return
	 */
	BoardPinInfo getBoardPinInfo();

	/**
	 * diozero internal method to generate a unique key for the specified pin. Used
	 * for maintaining the state of devices provisioned by this device factory.
	 * 
	 * @param pinInfo the pin to create the key for
	 * @return a key that is unique to this pin
	 */
	String createPinKey(PinInfo pinInfo);

	/**
	 * diozero internal method to generate a unique key for the I2C device at the
	 * specified address attached to the specified I2C bus controller.
	 * 
	 * @param controller the I2C bus controller number
	 * @param address    the I2C device address
	 * @return a unique I2C key
	 */
	String createI2CKey(int controller, int address);

	/**
	 * diozero internal method to generate a unique key for the SPI device attached
	 * to the specified SPI controller and chip select.
	 * 
	 * @param controller the SPI controller number
	 * @param chipSelect the SPI chip select number
	 * @return a unique SPI key
	 */
	String createSpiKey(int controller, int chipSelect);

	/**
	 * diozero internal method to generate a unique key for the specified serial
	 * device.
	 * 
	 * @param deviceFile the serial device filename
	 * @return a unique serial key
	 */
	String createSerialKey(String deviceFile);

	/**
	 * Get the already provisioned device for the specified key
	 * 
	 * @param key the unique device key
	 * @return the device otherwise null if not found
	 */
	<T extends DeviceInterface> T getDevice(String key);
}
