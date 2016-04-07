/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
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
import org.apache.logging.log4j.*;

/**
 *
 * @author Jonathan Machen
 */
public class AISSerialPortReader extends AISReaderBase implements SerialPortEventListener {

    private final static Logger LOG = LogManager
            .getLogger( AISSerialPortReader.class );

    private final static long LOOP_INTERVAL_MS = 1000;

    // connection settings for AIS-RX unit
    private static final int DEFAULT_BAUD_RATE = 38400;
    private static final int DEFAULT_DATA_BITS = 8;
    private static final int DEFAULT_STOP_BITS = 1;
    private static final int DEFAULT_PARITY = 0;

    private final StringBuffer _sb = new StringBuffer();
    private SerialPort _port;
    private String _portName;
    private int _baud;
    private int _dataBits;
    private int _stopBits;
    private int _parity;

    /**
     *
     * @param portName
     */
    public AISSerialPortReader( String portName ) {
        this( portName, DEFAULT_BAUD_RATE, DEFAULT_DATA_BITS, DEFAULT_STOP_BITS,
                DEFAULT_PARITY );
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
                DEFAULT_PARITY, handler );
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
        _portName = portName;
        _baud = baud;
        _dataBits = dataBits;
        _stopBits = stopBits;
        _parity = parity;
        LOG.debug( "AISSerialPortReader invoked with: " + portName + ", " + baud + ", " + dataBits
                + ", " + stopBits + ", " + parity );
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
        LOG.debug( "AISSerialPortReader invoked with: " + portName + ", " + baud + ", " + dataBits
                + ", " + stopBits + ", " + parity );
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
        super( handler );
        _portName = portName;
        _baud = baud;
        _dataBits = dataBits;
        _stopBits = stopBits;
        _parity = parity;
        LOG.debug( "invoked with: " + portName + ", " + baud + ", " + dataBits
                + ", " + stopBits + ", " + parity );
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
        super( handler );
        _portName = portName;
        _baud = baud;
        _dataBits = dataBits;
        _stopBits = stopBits;
        _parity = parity;
        _source = source;
        LOG.debug( "invoked with: " + portName + ", " + baud + ", " + dataBits
                + ", " + stopBits + ", " + parity + ", " + source );
    }

    /**
     *
     * @param serialPortEvent
     */
    @Override
    public synchronized void serialEvent( SerialPortEvent serialPortEvent ) {

        LOG.trace( "New SerialPortEvent: " );
        switch( serialPortEvent.getEventType() ) {
            case SerialPortEvent.BREAK:
                LOG.trace( "BREAK received." );
                break;
            case SerialPortEvent.CTS:
                LOG.trace( "CTS received." );
                break;
            case SerialPortEvent.DSR:
                LOG.trace( "DSR received." );
                break;
            case SerialPortEvent.ERR:
                LOG.trace( "ERR received." );
                break;
            case SerialPortEvent.RING:
                LOG.trace( "RING received." );
                break;
            case SerialPortEvent.RLSD:
                LOG.trace( "RSLD received." );
                break;
            case SerialPortEvent.RXCHAR:
                LOG.trace( "RXCHAR received." );
                try {
                    byte[] bytes = _port.readBytes();
                    if( bytes != null ) {
                        for( byte b : bytes ) {
                            if( b == '\n' || b == '\r' ) {
                                try {
                                    if( _sb.length() > 0 ) {
                                        super.processPacketString( _sb.toString() );
                                    }
                                } catch( AISPacketException ipe ) {
                                    LOG.debug( "Invalid Packet: " + ipe.getMessage(), ipe );
                                } finally {
                                    _sb.delete( 0, _sb.length() );
                                }
                            } else {
                                _sb.append( ( char ) b );
                            }
                        }
                    }
                } catch( SerialPortException spe ) {
                    LOG.error( "ERROR reading port!" + spe.getMessage(), spe );
                } catch( NullPointerException npe ) {
                    LOG.debug( "NullPointerException: " + npe.getMessage(), npe );
                }
                break;
            case SerialPortEvent.RXFLAG:
                LOG.trace( "RXFLAG received." );
                break;
            case SerialPortEvent.TXEMPTY:
                LOG.trace( "TXEMPTY received." );
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

            LOG.debug( "*** Port Initialization ***************************************** " );
            LOG.debug( " Port opened: " + _port.openPort() );
            LOG.debug( " Params set : " + _port.setParams( _baud, _dataBits, _stopBits, _parity ) );
            LOG.debug( "***************************************************************** " );

            _port.addEventListener( this );

            while( super._shouldRun && _port.isOpened() ) {
                try {
                    Thread.sleep( LOOP_INTERVAL_MS );
                } catch( InterruptedException ie ) {
                    LOG.debug( "Thread interrupted: " + ie.getMessage() );
                }
            }
            try {
                _port.closePort();
                _port = null;
            } catch( SerialPortException spe ) {
            }
        } catch( SerialPortException spe ) {
            throw new RuntimeException( "There was a problem reading from the serial port: "
                    + spe.getMessage(), spe );
        }
    }
}
