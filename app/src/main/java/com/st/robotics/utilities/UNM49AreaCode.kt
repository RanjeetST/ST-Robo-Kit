package com.st.robotics.utilities

val isoToM49 = mapOf(
    "AF" to "4", // Afghanistan
    "AL" to "8", // Albania
    "DZ" to "12", // Algeria
    "AS" to "16", // American Samoa
    "AD" to "20", // Andorra
    "AO" to "24", // Angola
    "AI" to "660", // Anguilla
    "AQ" to "10", // Antarctica
    "AG" to "28", // Antigua and Barbuda
    "AR" to "32", // Argentina
    "AM" to "51", // Armenia
    "AW" to "533", // Aruba
    "AU" to "36", // Australia
    "AT" to "40", // Austria
    "AZ" to "31", // Azerbaijan
    "BS" to "44", // Bahamas
    "BH" to "48", // Bahrain
    "BD" to "50", // Bangladesh
    "BB" to "52", // Barbados
    "BY" to "112", // Belarus
    "BE" to "56", // Belgium
    "BZ" to "84", // Belize
    "BJ" to "204", // Benin
    "BM" to "60", // Bermuda
    "BT" to "64", // Bhutan
    "BO" to "68", // Bolivia
    "BA" to "70", // Bosnia and Herzegovina
    "BW" to "72", // Botswana
    "BR" to "76", // Brazil
    "IO" to "86", // British Indian Ocean Territory
    "VG" to "92", // British Virgin Islands
    "BN" to "96", // Brunei
    "BG" to "100", // Bulgaria
    "BF" to "854", // Burkina Faso
    "BI" to "108", // Burundi
    "KH" to "116", // Cambodia
    "CM" to "120", // Cameroon
    "CA" to "124", // Canada
    "CV" to "132", // Cape Verde
    "KY" to "136", // Cayman Islands
    "CF" to "140", // Central African Republic
    "TD" to "148", // Chad
    "CL" to "152", // Chile
    "CN" to "156", // China
    "CX" to "162", // Christmas Island
    "CC" to "166", // Cocos (Keeling) Islands
    "CO" to "170", // Colombia
    "KM" to "174", // Comoros
    "CG" to "178", // Congo
    "CD" to "180", // Democratic Republic of the Congo
    "CK" to "184", // Cook Islands
    "CR" to "188", // Costa Rica
    "CI" to "384", // Ivory Coast (Côte d'Ivoire)
    "HR" to "191", // Croatia
    "CU" to "192", // Cuba
    "CW" to "531", // Curacao
    "CY" to "196", // Cyprus
    "CZ" to "203", // Czech Republic
    "DK" to "208", // Denmark
    "DJ" to "262", // Djibouti
    "DM" to "212", // Dominica
    "DO" to "214", // Dominican Republic
    "EC" to "218", // Ecuador
    "EG" to "818", // Egypt
    "SV" to "222", // El Salvador
    "GQ" to "226", // Equatorial Guinea
    "ER" to "232", // Eritrea
    "EE" to "233", // Estonia
    "ET" to "231", // Ethiopia
    "FK" to "238", // Falkland Islands
    "FO" to "234", // Faroe Islands
    "FJ" to "242", // Fiji
    "FI" to "246", // Finland
    "FR" to "250", // France
    "GF" to "254", // French Guiana
    "PF" to "258", // French Polynesia
    "TF" to "260", // French Southern and Antarctic Lands
    "GA" to "266", // Gabon
    "GM" to "270", // Gambia
    "GE" to "268", // Georgia
    "DE" to "276", // Germany
    "GH" to "288", // Ghana
    "GI" to "292", // Gibraltar
    "GR" to "300", // Greece
    "GL" to "304", // Greenland
    "GD" to "308", // Grenada
    "GP" to "312", // Guadeloupe
    "GU" to "316", // Guam
    "GT" to "320", // Guatemala
    "GG" to "831", // Guernsey
    "GN" to "324", // Guinea
    "GW" to "624", // Guinea-Bissau
    "GY" to "328", // Guyana
    "HT" to "332", // Haiti
    "HM" to "334", // Heard Island and McDonald Islands
    "HN" to "340", // Honduras
    "HK" to "344", // Hong Kong
    "HU" to "348", // Hungary
    "IS" to "352", // Iceland
    "IN" to "356", // India
    "ID" to "360", // Indonesia
    "IR" to "364", // Iran
    "IQ" to "368", // Iraq
    "IE" to "372", // Ireland
    "IL" to "376", // Israel
    "IT" to "380", // Italy
    "JM" to "388", // Jamaica
    "JP" to "392", // Japan
    "JE" to "832", // Jersey
    "JO" to "400", // Jordan
    "KZ" to "398", // Kazakhstan
    "KE" to "404", // Kenya
    "KI" to "296", // Kiribati
    "KP" to "408", // North Korea
    "KR" to "410", // South Korea
    "KW" to "414", // Kuwait
    "KG" to "417", // Kyrgyzstan
    "LA" to "418", // Laos
    "LV" to "428", // Latvia
    "LB" to "422", // Lebanon
    "LS" to "426", // Lesotho
    "LR" to "430", // Liberia
    "LY" to "434", // Libya
    "LI" to "438", // Liechtenstein
    "LT" to "440", // Lithuania
    "LU" to "442", // Luxembourg
    "MO" to "446", // Macau
    "MK" to "807", // North Macedonia
    "MG" to "450", // Madagascar
    "MW" to "454", // Malawi
    "MY" to "458", // Malaysia
    "MV" to "462", // Maldives
    "ML" to "466", // Mali
    "MT" to "470", // Malta
    "MH" to "474", // Marshall Islands
    "MQ" to "478", // Martinique
    "MR" to "480", // Mauritania
    "MU" to "484", // Mauritius
    "YT" to "175", // Mayotte
    "MX" to "484", // Mexico
    "FM" to "583", // Micronesia
    "MD" to "498", // Moldova
    "MC" to "492", // Monaco
    "MN" to "496", // Mongolia
    "ME" to "499", // Montenegro
    "MS" to "500", // Montserrat
    "MA" to "504", // Morocco
    "MZ" to "508", // Mozambique
    "MM" to "104", // Myanmar
    "NA" to "516", // Namibia
    "NR" to "520", // Nauru
    "NP" to "524", // Nepal
    "NL" to "528", // Netherlands
    "NC" to "540", // New Caledonia
    "NZ" to "554", // New Zealand
    "NI" to "558", // Nicaragua
    "NE" to "562", // Niger
    "NG" to "566", // Nigeria
    "NU" to "570", // Niue
    "NF" to "574", // Norfolk Island
    "MP" to "580", // Northern Mariana Islands
    "NO" to "578", // Norway
    "OM" to "512", // Oman
    "PK" to "586", // Pakistan
    "PW" to "585", // Palau
    "PA" to "591", // Panama
    "PG" to "598", // Papua New Guinea
    "PY" to "600", // Paraguay
    "PE" to "604", // Peru
    "PH" to "608", // Philippines
    "PN" to "612", // Pitcairn Islands
    "PL" to "616", // Poland
    "PT" to "620", // Portugal
    "PR" to "630", // Puerto Rico
    "QA" to "634", // Qatar
    "RE" to "638", // Réunion
    "RO" to "642", // Romania
    "RU" to "643", // Russia
    "RW" to "646", // Rwanda
    "BL" to "652", // Saint Barthélemy
    "SH" to "654", // Saint Helena
    "KN" to "659", // Saint Kitts and Nevis
    "LC" to "662", // Saint Lucia
    "MF" to "663", // Saint Martin
    "PM" to "666", // Saint Pierre and Miquelon
    "VC" to "670", // Saint Vincent and the Grenadines
    "WS" to "882", // Samoa
    "SM" to "674", // San Marino
    "ST" to "678", // São Tomé and Príncipe
    "SA" to "682", // Saudi Arabia
    "SN" to "686", // Senegal
    "RS" to "688", // Serbia
    "SC" to "690", // Seychelles
    "SL" to "694", // Sierra Leone
    "SG" to "702", // Singapore
    "SX" to "534", // Sint Maarten
    "SK" to "703", // Slovakia
    "SI" to "705", // Slovenia
    "SB" to "90", // Solomon Islands
    "SO" to "706", // Somalia
    "ZA" to "710", // South Africa
    "GS" to "239", // South Georgia and the South Sandwich Islands
    "KR" to "410", // South Korea
    "SS" to "728", // South Sudan
    "ES" to "724", // Spain
    "LK" to "144", // Sri Lanka
    "SD" to "736", // Sudan
    "SR" to "740", // Suriname
    "SJ" to "744", // Svalbard and Jan Mayen
    "SZ" to "748", // Eswatini (Swaziland)
    "SE" to "752", // Sweden
    "CH" to "756", // Switzerland
    "SY" to "760", // Syria
    "TW" to "158", // Taiwan
    "TJ" to "762", // Tajikistan
    "TZ" to "834", // Tanzania
    "TH" to "764", // Thailand
    "TL" to "626", // Timor-Leste
    "TG" to "768", // Togo
    "TK" to "772", // Tokelau
    "TO" to "776", // Tonga
    "TT" to "780", // Trinidad and Tobago
    "TN" to "788", // Tunisia
    "TR" to "792", // Turkey
    "TM" to "795", // Turkmenistan
    "TC" to "796", // Turks and Caicos Islands
    "TV" to "798", // Tuvalu
    "UG" to "800", // Uganda
    "UA" to "804", // Ukraine
    "AE" to "784", // United Arab Emirates
    "GB" to "826", // United Kingdom
    "US" to "840", // United States
    "UY" to "858", // Uruguay
    "UZ" to "860", // Uzbekistan
    "VU" to "548", // Vanuatu
    "VA" to "336", // Vatican City
    "VE" to "862", // Venezuela
    "VN" to "704", // Vietnam
    "WF" to "876", // Wallis and Futuna
    "EH" to "732", // Western Sahara
    "YE" to "887", // Yemen
    "ZM" to "894", // Zambia
    "ZW" to "716"  // Zimbabwe
)