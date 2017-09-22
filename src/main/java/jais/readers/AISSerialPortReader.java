/*
 * Copyright 2016 Jonathan Machen <jon.machen@robotaccomplice.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jais.readers;

import jais.exceptions.AISPacketException;
import jais.handlers.AISHandler;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AISSerialPortReader extends AISReaderBase implements SerialPortEventListener {

    private final static Logger LOG = LogManager.getLogger( AISSerialPortReader.class );

    private final static long LOOP_INTERVAL_MS = 1000;

    // connection settings for AIS-RX unit
    private static final int DEFAULT_BAUD_RATE = SerialPort.BAUDRATE_38400;
    private static final int DEFAULT_DATA_BITS = SerialPort.DATABITS_8;
    private static final int DEFAULT_STOP_BITS = SerialPort.STOPBITS_1;
    private static final int DEFAULT_PARITY = SerialPort.PARITY_NONE;

    private final StringBuffer _sb = new StringBuffer();
    private SerialPort _port;
    private final String _portName;
    private final int _baud;
    private final int _dataBits;
    private final int _stopBits;
    private final int _parity;

    /**
     *
     * @param portName
     */
    public AISSerialPortReader( String portName ) {
        this( portName, DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS,
                DEFAULT_PARITY, portName );
    }

    /**
     *
     * @param portName
     * @param source
     */
    public AISSerialPortReader( String portName, String source ) {
        this( portName, DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS,
                DEFAULT_PARITY, source );
    }

    /**
     *
     * @param portName
     * @param handler
     */
    public AISSerialPortReader( String portName, AISHandler handler ) {
        this( portName, DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS,
                DEFAULT_PARITY, handler, portName );
    }

    /**
     *
     * @param portName
     * @param handler
     * @param source
     */
    public AISSerialPortReader( String portName, AISHandler handler, String source ) {
        this( portName, DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS,
                DEFAULT_PARITY, handler, source );
    }

    /**
     *
     * @param portName
     * @param baud
     * @param dataBits
     * @param stopBits
     * @param parity
     */
    public AISSerialPortReader( String portName, int baud, int dataBits,
            int stopBits, int parity ) {
        this( portName, baud, dataBits, stopBits, parity, portName );
    }

    /**
     *
     * @param portName
     * @param baud
     * @param dataBits
     * @param stopBits
     * @param parity
     * @param source
     */
    public AISSerialPortReader( String portName, int baud, int dataBits,
            int stopBits, int parity, String source ) {
        _portName = portName;
        _baud = baud;
        _dataBits = dataBits;
        _stopBits = stopBits;
        _parity = parity;
        _source = source;
        if( LOG.isDebugEnabled() ) LOG.debug( "{} - AISSerialPortReader invoked with: {}, {}, {}, {}, {}",
                source, portName, baud, dataBits, stopBits, parity );
    }

    /**
     *
     * @param portName
     * @param baud
     * @param dataBits
     * @param stopBits
     * @param parity
     * @param handler
     */
    public AISSerialPortReader( String portName, int baud, int dataBits,
            int stopBits, int parity, AISHandler handler ) {
        super( handler, portName );
        _portName = portName;
        _baud = baud;
        _dataBits = dataBits;
        _stopBits = stopBits;
        _parity = parity;
        if( LOG.isDebugEnabled() ) LOG.debug( "{} - AISSerialPortReader invoked with: {}, {}, {}, {}, {}",
                portName, portName, baud, dataBits, stopBits, parity );
    }

    /**
     *
     * @param portName
     * @param baud
     * @param dataBits
     * @param stopBits
     * @param parity
     * @param handler
     * @param source
     */
    public AISSerialPortReader( String portName, int baud, int dataBits,
            int stopBits, int parity, AISHandler handler, String source ) {
        super( handler, source );
        _portName = portName;
        _baud = baud;
        _dataBits = dataBits;
        _stopBits = stopBits;
        _parity = parity;
        if( LOG.isDebugEnabled() ) LOG.debug( "{} - AISSerialPortReader invoked with: {}, {}, {}, {}, {}",
                source, portName, baud, dataBits, stopBits, parity );
    }

    /**
     *
     * @param serialPortEvent
     */
    @Override
    public synchronized void serialEvent( SerialPortEvent serialPortEvent ) {

        if( LOG.isTraceEnabled() ) LOG.trace( "{} - New SerialPortEvent: ", _portName );
        switch( serialPortEvent.getEventType() ) {
            case SerialPortEvent.BREAK:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - BREAK received.", _portName );
                break;
            case SerialPortEvent.CTS:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - CTS received.", _portName );
                break;
            case SerialPortEvent.DSR:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - DSR received.", _portName );
                break;
            case SerialPortEvent.ERR:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - ERR received.", _portName );
                break;
            case SerialPortEvent.RING:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - RING received.", _portName );
                break;
            case SerialPortEvent.RLSD:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - RSLD received.", _portName );
                break;
            case SerialPortEvent.RXCHAR:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - RXCHAR received.", _portName );
                try {
                    byte[] bytes = _port.readBytes();
                    if( bytes != null ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "{} - Read {} from serial port.", _portName, bytes.length );
                        
                        for( byte b : bytes ) {
                            if( b == '\n' || b == '\r' ) {
                                String line = _sb.toString();
                                _sb.delete( 0, line.length() );
                                
                                try {
                                    if( line.length() > 0 ) {
                                        super.processPacketString( line );
                                    }
                                } catch( AISPacketException ipe ) {
                                    if( LOG.isDebugEnabled() ) LOG.debug( "{} - Invalid Packet \"{}\": {}", _portName, line, ipe.getMessage(), ipe );
                                }
                            } else {
                                _sb.append( ( char ) b );
                            }
                        }
                    }
                } catch( SerialPortException spe ) {
                    LOG.error( "{} - ERROR reading port!  {}", _portName, spe.getMessage(), spe );
                } catch( NullPointerException npe ) {
                    if( LOG.isDebugEnabled() ) LOG.debug( "{} - NullPointerException: {}", _portName, npe.getMessage(), npe );
                }
                break;
            case SerialPortEvent.RXFLAG:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - RXFLAG received.", _portName );
                break;
            case SerialPortEvent.TXEMPTY:
                if( LOG.isTraceEnabled() ) LOG.trace( "{} - TXEMPTY received.", _portName );
                break;
        }
    }

    /**
     *
     * @throws jais.readers.AISReaderException
     */
    @Override
    public void read() throws AISReaderException {
        try {
            _port = new SerialPort( _portName );
            if( _port.openPort() ) {
                if( LOG.isDebugEnabled() ) {
                    LOG.debug( "{} - Successfully opened port", _portName );
                    LOG.debug( "{} - Initializing serial port with ({}, {}, {}, {})...", _portName, _baud, _dataBits, _stopBits, _parity );
                }

                _port.setParams( _baud, _dataBits, _stopBits, _parity );
            } else {
                LOG.fatal( "{} - Failed to open port for an unknown reason.", _portName );
            }
            
            LOG.fatal( "{} - Adding event listener to serial port..." );
            _port.addEventListener( this );

            while( super._shouldRun && _port.isOpened() ) {
                try {
                    Thread.sleep( LOOP_INTERVAL_MS );
                } catch( InterruptedException ie ) {
                    if( LOG.isDebugEnabled() ) LOG.debug( "Thread interrupted: " + ie.getMessage() );
                }
            }
            
            if( _shouldRun && !_port.isOpened() ) {
                LOG.fatal( "Port was closed (presumably by the OS).  Shutting down." );
            }
        } catch( SerialPortException spe ) {
            LOG.fatal( "There was a problem reading from the serial port: {}", spe.getMessage(), spe );
            if( LOG.isTraceEnabled() ) LOG.trace( "StackTrace: ", spe );
        } finally {
            try {
                if( _port != null ) {
                    _port.closePort();
                }
            } catch( SerialPortException spe ) {
                // ignore at this point
            }
        }
    }
}
