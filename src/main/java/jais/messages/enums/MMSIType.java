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

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum MMSIType {
    
    UNKNOWN( -99 ),
    ALBANIA( 201 ),
    ANDORRA( 202 ),
    AUSTRIA( 203 ),
    AZORES( 204 ),
    BELGIUM( 205 ),
    BELARUS( 206 ),
    BULGARIA( 207 ),
    VATICAN_CITY_STATE( 208 ),
    CYPRUS( 209, 210, 212 ),
    GERMANY( 211, 218 ),
    GEORGIA( 213 ),
    MOLDOVA( 214 ),
    MALTA( 215, 248, 249, 256 ),
    ARMENIA( 216 ),
    DENMARK( 219, 220 ),
    SPAIN( 224, 225 ),
    FRANCE( 226, 227, 228 ),
    FINLAND( 230 ),
    FAROE_ISLANDS( 231 ),
    UNITED_KINGDOM_OF_GREAT_BRITAIN_AND_NORTHERN_IRELAND( 232, 233, 234, 235 ),
    GIBRALTAR( 236 ),
    GREECE( 237 ),
    MOROCCO( 242 ),
    HUNGARY( 243 ),
    NETHERLANDS( 244, 245, 246 ),
    ITALY( 247 ),
    IRELAND( 250 ),
    ICELAND( 251 ),
    LIECHTENSTEIN( 252 ),
    LUXEMBOURG( 253 ),
    MONACO( 254 ),
    MADEIRA( 255 ),
    NORWAY( 257, 258, 259 ),
    POLAND( 261 ),
    MONTENEGRO( 262 ),
    PORTUGAL( 263 ),
    ROMANIA( 264 ),
    SWEDEN( 265, 266 ),
    SLOVAK_REPUBLIC( 267 ),
    SAN_MARINO( 268 ),
    SWITZERLAND( 269 ),
    CZECH_REPUBLIC( 270 ),
    TURKEY( 271 ),
    UKRAINE( 272 ),
    RUSSIAN_FEDERATION( 273 ),
    THE_FORMER_YUGOSLAV_REPUBLIC_OF_MACEDONIA( 274 ),
    LATVIA( 275 ),
    ESTONIA( 276 ),
    LITHUANIA( 277 ),
    SLOVENIA( 278 ),
    SERBIA( 279 ),
    ANGUILLA( 301 ),
    ALASKA( 303 ),
    ANTIGUA_AND_BARBUDA( 304, 305 ),
    NETHERLANDS_ANTILLES( 306 ),
    ARUBA( 307 ),
    BAHAMAS( 308, 309, 311 ),
    BERMUDA( 310 ),
    BELIZE( 312 ),
    BARBADOS( 314 ),
    CANADA( 316 ),
    CAYMAN_ISLANDS( 319 ),
    COSTA_RICA( 321 ),
    CUBA( 323 ),
    DOMINICA( 325 ),
    DOMINICAN_REPUBLIC( 327 ),
    GUADELOUPE( 329 ),
    GRENADA( 330 ),
    GREENLAND( 331 ),
    GUATEMALA( 332 ),
    HONDURAS( 334 ),
    HAITI( 336 ),
    UNITED_STATES_OF_AMERICA( 338, 366, 367, 368, 369 ),
    JAMAICA( 339 ),
    SAINT_KITTS_AND_NEVIS( 341 ),
    SAINT_LUCIA( 343 ),
    MEXICO( 345 ),
    MARTINIQUE( 347 ),
    MONTSERRAT( 348 ),
    NICARAGUA( 350 ),
    PANAMA( 351, 352, 353, 354, 351, 352, 353, 354 ),
    UNASSIGNED( 355, 356, 357 ),
    PUERTO_RICO( 358 ),
    EL_SALVADOR( 359 ),
    SAINT_PIERRE_AND_MIQUELON( 361 ),
    TRINIDAD_AND_TOBAGO( 362 ),
    TURKS_AND_CAICOS_ISLANDS( 364 ),
    SAINT_VINCENT_AND_THE_GRENADINES( 375, 376, 377 ),
    BRITISH_VIRGIN_ISLANDS( 378 ),
    UNITED_STATES_VIRGIN_ISLANDS( 379 ),
    AFGHANISTAN( 401 ),
    SAUDI_ARABIA( 403 ),
    BANGLADESH( 405 ),
    BAHRAIN( 408 ),
    BHUTAN( 410 ),
    CHINA( 412, 413 ),
    TAIWAN( 416 ),
    SRI_LANKA( 417 ),
    INDIA( 419 ),
    IRAN( 422 ),
    AZERBAIJANI_REPUBLIC( 423 ),
    IRAQ( 425 ),
    ISRAEL( 428 ),
    JAPAN( 431, 432 ),
    TURKMENISTAN( 434 ),
    KAZAKHSTAN( 436 ),
    UZBEKISTAN( 437 ),
    JORDAN( 438 ),
    KOREA( 440, 441 ),
    PALESTINE( 443 ),
    DEMOCRATIC_PEOPLES_REPUBLIC_OF_KOREA( 445 ),
    KUWAIT( 447 ),
    LEBANON( 450 ),
    KYRGYZ_REPUBLIC( 451 ),
    MACAO( 453 ),
    MALDIVES( 455 ),
    MONGOLIA( 457 ),
    NEPAL( 459 ),
    OMAN( 461 ),
    QATAR( 466 ),
    SYRIAN_ARAB_REPUBLIC( 468 ),
    UNITED_ARAB_EMIRATES( 470 ),
    YEMEN( 473, 475 ),
    HONG_KONG( 477 ),
    BOSNIA_AND_HERZEGOVINA( 478 ),
    ADELIE_LAND( 501 ),
    AUSTRALIA( 503 ),
    MYANMAR( 506 ),
    BRUNEI_DARUSSALAM( 508 ),
    MICRONESIA( 510 ),
    PALAU( 511 ),
    NEW_ZEALAND( 512 ),
    CAMBODIA( 514, 515 ),
    CHRISTMAS_ISLAND( 516 ),
    COOK_ISLANDS( 518 ),
    FIJI( 520 ),
    COCOS_ISLANDS( 523 ),
    INDONESIA( 525 ),
    KIRIBATI( 529 ),
    LAO_PEOPLES_DEMOCRATIC_REPUBLIC( 531 ),
    MALAYSIA( 533 ),
    NORTHERN_MARIANA_ISLANDS( 536 ),
    MARSHALL_ISLANDS( 538 ),
    NEW_CALEDONIA( 540 ),
    NIUE( 542 ),
    NAURU( 544 ),
    FRENCH_POLYNESIA( 546 ),
    PHILIPPINES( 548 ),
    PAPUA_NEW_GUINEA( 553 ),
    PITCAIRN_ISLAND( 555 ),
    SOLOMON_ISLANDS( 557 ),
    AMERICAN_SAMOA( 559 ),
    SAMOA( 561 ),
    SINGAPORE( 563, 564, 565 ),
    THAILAND( 567 ),
    TONGA( 570 ),
    TUVALU( 572 ),
    VIET_NAM( 574 ),
    VANUATU( 576 ),
    WALLIS_AND_FUTUNA_ISLANDS( 578 ),
    SOUTH_AFRICA( 601 ),
    ANGOLA( 603 ),
    ALGERIA( 605 ),
    SAINT_PAUL_AND_AMSTERDAM_ISLANDS( 607 ),
    ASCENSION_ISLAND( 608 ),
    BURUNUD( 609 ),
    BENIN( 610 ),
    BOTSWANA( 611 ),
    CENTRAL_AFRICAN_REPUBLIC( 612 ),
    CAMEROON( 613 ),
    CONGO( 615 ),
    COMOROS( 616 ),
    CAPE_VERDE( 617 ),
    CROZET_ARCHIPELAGO( 618 ),
    COTE_D_IVOIRE( 619 ),
    DJIBOUTI( 621 ),
    EGYPT( 622 ),
    ETHIOPIA( 624 ),
    ERITREA( 625 ),
    GABONESE_REPUBLIC( 626 ),
    GHANA( 627 ),
    GAMBIA( 629 ),
    GUINEA_BISSAU( 630 ),
    EQUATORIAL_GUINEA( 631 ),
    GUINEA( 632 ),
    BURKINA_FASO( 633 ),
    KENYA( 634 ),
    KERGUELEN_ISLANDS( 635 ),
    LIBERIA( 636, 637 ),
    SOCIALIST_PEOPLES_LIBYAN_ARAB_JAMAHIRIYA( 642 ),
    LESOTHO( 644 ),
    MAURITIUS( 645 ),
    MADAGASCAR( 647 ),
    MALI( 649 ),
    MOZAMBIQUE( 650 ),
    MAURITANIA( 654 ),
    MALAWI( 655 ),
    NIGER( 656 ),
    NIGERIA( 657 ),
    NAMIBIA( 659 ),
    REUNION( 660 ),
    RWANDA( 661 ),
    SUDAN( 662 ),
    SENEGAL( 663 ),
    SEYCHELLES( 664 ),
    SAINT_HELENA( 665 ),
    SOMALI_DEMOCRATIC_REPUBLIC( 666 ),
    SIERRA_LEONE( 667 ),
    SAO_TOME_AND_PRINCIPE( 668 ),
    SWAZILAND( 669 ),
    CHAD( 670 ),
    TOGOLESE_REPUBLIC( 671 ),
    TUNISIA( 672 ),
    TANZANIA( 674, 677 ),
    UGUNDA( 675 ),
    DEMOCRATIC_REPUBLIC_OF_THE_CONGO( 676 ),
    ZAMBIA( 678 ),
    ZIMBABWE( 679 ),
    ARGENTINE_REPUBLIC( 701 ),
    BRAZIL( 710 ),
    BOLIVIA( 720 ),
    CHILE( 725 ),
    COLUMBIA( 730 ),
    ECUADOR( 735 ),
    FALKLAND_ISLANDS( 740 ),
    GUIANA( 745 ),
    GUYANA( 750 ),
    PARAGUAY( 755 ),
    PERU( 760 ),
    SURINAME( 765 ),
    URUGUAY( 770 ),
    VENEZUELA( 775 )
    ;
    
    private final int [] _codes;
    
    /**
     * 
     * @param codes
     */
    MMSIType( int... codes ) {
        _codes = codes;
    }
    
    /**
     * 
     * @return 
     */
    public int [] getCodes() {
        return _codes;
    }
    
    /**
     * 
     * @param regionCode
     * @return 
     */
    public static MMSIType forCode( int regionCode ) {
        for( MMSIType type : MMSIType.values() ) {
            for( int code : type.getCodes() ) {
                if( code == regionCode ) return type;
            }
        }        
        
        return UNKNOWN;
    }
    
    /**
     * 
     * @param mmsi
     * @return 
     */
    public static MMSIType forMMSI( int mmsi ) {
        if( isValidMMSI( mmsi ) ) {
            String regionString = Integer.toString( mmsi ).substring( 0, 3 );
            int regionCode = Integer.parseInt( regionString ); // grab the first three digits of the MMSI
            return forCode( regionCode );
        }
        
        return UNKNOWN;        
    }
    
    /**
     * 
     * @param mmsi
     * @return 
     */
    public static boolean isValidMMSI( int mmsi ) {
        return ( mmsi > 19999999 && mmsi < 800000000 );
    }
}
