/*
 * Copyright 2016-2019 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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

package jais.messages.binaryaddressed;

import jais.AISSentence;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.CargoCode;
import jais.messages.enums.CargoUnitCode;
import jais.messages.enums.FieldMap;
import jais.messages.enums.MarpolAnnexIIType;
import jais.messages.enums.MarpolAnnexIType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class IMO289DangerousCargoIndication extends BinaryAddressedMessageBase {

    private static final int CARGO_ENTRY_SIZE_BITS = 17;

    private CargoUnitCode cargoUnit;
    private int amount;
    private final List<DangerousCargoEntry> cargos = new ArrayList<>();

    /**
     *
     * @param source the name of the source for this message
     * @param sentences the AIS Sentences from which this message was composed
     */
    public IMO289DangerousCargoIndication(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.DANGEROUS_CARGO_INDICATION, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        cargoUnit = CargoUnitCode.getForCode(AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289DangerousCargoIndicationFieldMap.CARGO_UNIT.getStartBit(),
                IMO289DangerousCargoIndicationFieldMap.CARGO_UNIT.getEndBit()));
        amount = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289DangerousCargoIndicationFieldMap.AMOUNT.getStartBit(),
                IMO289DangerousCargoIndicationFieldMap.AMOUNT.getEndBit());

        cargos.clear();
        int entryCount = maxCargoEntriesAvailable();
        for (int i = 0; i < entryCount; i++) {
            int startBit = IMO289DangerousCargoIndicationFieldMap.CARGOS.getStartBit() + (i * CARGO_ENTRY_SIZE_BITS);
            CargoCode code = CargoCode.getForCode(AISMessageDecoder.decodeUnsignedInt(bits, startBit, startBit + 3));
            int value = AISMessageDecoder.decodeUnsignedInt(bits, startBit + 4, startBit + 16);

            cargos.add(new DangerousCargoEntry(
                    code,
                    value,
                    decodeCargoDescription(code, value)));
        }
    }

    /**
     *
     * @return immutable view of the cargo entries carried in the message
     */
    public List<DangerousCargoEntry> getCargos() {
        return Collections.unmodifiableList(cargos);
    }

    private int maxCargoEntriesAvailable() {
        int availableBits = bits.length() - IMO289DangerousCargoIndicationFieldMap.CARGOS.getStartBit();
        if (availableBits <= 0) {
            return 0;
        }
        return availableBits / CARGO_ENTRY_SIZE_BITS;
    }

    private static String decodeCargoDescription(CargoCode code, int value) {
        if (code == null) {
            return null;
        }

        return switch (code) {
            case NOT_AVAILABLE -> code.getDescription();
            case IMDG_CODE -> "IMDG code " + value;
            case IGC_CODE -> "UN number " + value;
            case BC_CODE -> decodeBcCode(value);
            case MARPOL_ANNEX_I -> {
                MarpolAnnexIType type = MarpolAnnexIType.getForCode(value >> 9);
                yield type != null ? type.getDescription() : null;
            }
            case MARPOL_ANNEX_II -> {
                MarpolAnnexIIType type = MarpolAnnexIIType.getForCode(value >> 10);
                yield type != null ? type.getDescription() : null;
            }
            case REGIONAL -> "Regional code " + value;
        };
    }

    private static String decodeBcCode(int value) {
        int bcClass = (value >> 10) & 0x7;
        int imdgClass = (value >> 3) & 0x7F;
        return "BC class " + bcClass + ", IMDG class " + imdgClass;
    }

    /**
     *
     */
    @Getter
    public static class DangerousCargoEntry {
        private final CargoCode code;
        private final int rawValue;
        private final String description;

        DangerousCargoEntry(CargoCode code, int rawValue, String description) {
            this.code = code;
            this.rawValue = rawValue;
            this.description = description;
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289DangerousCargoIndicationFieldMap implements FieldMap {

        CARGO_UNIT(88, 89),
        AMOUNT(90, 99),
        CARGOS(100, -1);

        private final int startBit;
        private final int endBit;

        IMO289DangerousCargoIndicationFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }

        @Override
        public int getStartBit() {
            return startBit;
        }

        @Override
        public int getEndBit() {
            return endBit;
        }
    }
}
