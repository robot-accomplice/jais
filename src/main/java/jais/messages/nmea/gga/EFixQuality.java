package jais.messages.nmea.gga;

public enum EFixQuality {
    INVALID(),
    GNSS(),
    DGPS(),
    PPS(),
    REAL_TIME_KINEMATIC(),
    ESTIMATED(), // (dead reckoning) (2.3 feature)
    MANUAL_INPUT_MODE(),
    SIMULATION_MODE();
}
