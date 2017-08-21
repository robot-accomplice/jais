/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.enums;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author vermi
 */
public enum Talkers {
    
    AB( "Independent AIS Base Station" ),
    AD( "Dependent AIS Base Station" ),
    AG( "Heading Track Controller (General)" ),
    AP( "Heading Track Controller (Magnetic)" ),
    AI( "Mobile Class A or B AIS Station" ),
    AN( "AIS Aid to Navigation Station" ),
    AR( "AIS Receiving Station" ),
    AS( "AIS Station (ITU_R M1371 - Limited Base Station)" ),
    AT( "AIS Transmitting Station" ),
    AX( "AIS Simplex Repeater Station" ),
    BI( "Bilge Systems" ),
    BN( "Bridge Navigational Watch Alarm System" ),
    
    // Communiciations
    CD( "Communication - Digital Selective Calling (DSC)" ),
    CR( "Communication - Data Receiver" ),
    CS( "Communication - Satellite" ),
    CT( "Communication - Radio-Telephone (MF/HF)" ),
    CV( "Communication - Radio-Telephone (VHF)" ),
    CX( "Communication - Scanning Receiver" ),
    
    DF( "Direction Finder" ),
    DU( "Duplex Repeater Station" ),
    EC( "Electronic Chart System (ECS)" ),
    EI( "Electronic Chart Display & Information System (ECDIS)" ),
    EP( "Emergency Position Indicating Beacon (EPIRB)" ),
    ER( "Engine Room Monitoring Systems" ),
    FD( "Fire Door Controller/Monitoring Point" ),
    FE( "Fire Extinguisher System" ),
    FR( "Fire Detection Point" ),
    FS( "Fire Sprinkler System" ),
    
    GA( "Galileo" ),
    GL( "GLONASS Receiver" ),
    GN( "Global Navigation Satellite System (GNSS)" ),
    GP( "GPS" ),
    
    // Heading Sensors
    HC( "Heading - Compass, Magnetic" ),
    HE( "Heading - Gyro, North Seeking" ),
    HF( "Heading - Fluxgate" ),
    HN( "Heading - Gyro, Non-North Seeking"),
    
    HD( "Hull Door Controller/Monitoring Panel" ),
    HS( "Hull Stress Monitoring" ),
    II( "Integrated Instrumentation" ),
    IN( "Integrated Navigation" ),
    LC( "Loran C" ),
    MX( "Multiplexer" ),
    NL( "Navigation Light Controller" ),
    P( "Proprietary Code" ),
    RA( "Radar and/or Radar Plotting" ),
    RC( "Propulsion Machinery Including Remote Control" ),
    SA( "Physical Shore AIS Station" ),
    SD( "Sounder, depth" ),
    SG( "Steering Gear/Steering Engine" ),
    SN( "Electronic Positioning System, other/general" ),
    SS( "Sounder, scanning" ),
    TI( "Turn Rate Indicator" ),
    UP( "Microprocessor Controller" ),
    U0( "User configured talker identifier (0)"),
    U1( "User configured talker identifier (1)"),
    U2( "User configured talker identifier (2)"),
    U3( "User configured talker identifier (3)"),
    U4( "User configured talker identifier (4)"),
    U5( "User configured talker identifier (5)"),
    U6( "User configured talker identifier (6)"),
    U7( "User configured talker identifier (7)"),
    U8( "User configured talker identifier (8)"),
    U9( "User configured talker identifier (9)"),
    
    // Velocity Sensors
    VD( "Velocity - Doppler, other/general" ),
    VM( "Velocity - Speed Log, Water, Magnetic" ),
    VW( "Velocity - Speed Log, Water, Mechanical" ),
    
    VR( "Voyage Data Recorder" ),
    WD( "Watertight Door Controller/Monitoring Panel" ),
    WI( "Weather Instruments" ),
    WL( "Water Level Detection Systems" ),
    YX( "Transducer" ),
    
    // Timekeepers, Time/Date
    ZA( "Atomics Clock" ),
    ZC( "Chronometer" ),
    ZQ( "Quartz" ),
    ZV( "Radio Update" )
    ;
    
    public  final String description;
    public final static Logger LOG = LogManager.getLogger( Talkers.class );
    
    /**
     * 
     * @param description 
     */
    Talkers( String description ) {
        this.description = description;
    }
    
    /**
     * 
     * @param code
     * @return 
     */
    public static boolean isValid( String code ) {
        if( LOG.isDebugEnabled() ) LOG.debug( "Checking validity of talker with code: \"{}\"", code );
        return code != null;
    }
}
