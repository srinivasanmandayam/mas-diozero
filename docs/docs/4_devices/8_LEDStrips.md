---
parent: Devices
nav_order: 8
permalink: /devices/ledstrips.html
redirect_from:
  - /en/latest/LEDStrips/index.html
  - /en/stable/LEDStrips/index.html
---

# LED Strips

## WS2811B / WS2812B / Adafruit NeoPixel
{: #ws281x }

Provides support for [WS2811B / WS2812B aka Adafriut NeoPixel LEDs](https://learn.adafruit.com/adafruit-neopixel-uberguide)
via a JNI wrapper around the [rpi_ws281x C library](https://github.com/jgarff/rpi_ws281x)
as well as a native Java implementation that uses SPI.
Also controls APA102 LED strips using SPI.

{: .note-title }
> Colour Coding
>
> All colours are represented as 24bit RGB values.

The [README](https://github.com/jgarff/rpi_ws281x#spi) on jgarff's GitHub project provides some
good tips for if you are having issues controlling LED strips, in particular the [SPI section](https://github.com/jgarff/rpi_ws281x#spi).
