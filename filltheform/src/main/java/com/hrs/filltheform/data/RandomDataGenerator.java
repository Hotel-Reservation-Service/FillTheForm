/*
 * Copyright (C) 2015 HRS GmbH
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
package com.hrs.filltheform.data;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;

/**
 * RandomDataGenerator defines constants that can be used in the configuration file.
 * These constants indicate which type of random data is needed for which id.
 * The following library is used for generating random content: https://github.com/mdeanda/lorem
 */
class RandomDataGenerator {

    private static final String FIRST_NAME = "random_first_name";
    private static final String FIRST_NAME_MALE = "random_first_name_male";
    private static final String FIRST_NAME_FEMALE = "random_first_name_female";
    private static final String LAST_NAME = "random_last_name";

    private static final String NAME = "random_name";
    private static final String NAME_MALE = "random_name_male";
    private static final String NAME_FEMALE = "random_name_female";

    private static final String EMAIL = "random_email";
    private static final String EMAIL_LOCAL_PART = "random_email_local_part";
    private static final String CITY = "random_city";
    private static final String COUNTRY = "random_country";
    private static final String PHONE = "random_phone";
    private static final String STATE_ABBREVIATION = "random_state_abbreviation";
    private static final String STATE = "random_state";
    private static final String ZIP_CODE = "random_zip_code";

    private static final String WORD = "random_word";
    private static final String TEXT = "random_text";
    private static final String PARAGRAPH = "random_paragraph";

    private Lorem lorem;

    public RandomDataGenerator() {
        this.lorem = LoremIpsum.getInstance();
    }

    public boolean isRandomVariableKey(String variableKey) {
        switch (variableKey) {
            case FIRST_NAME:
            case FIRST_NAME_MALE:
            case FIRST_NAME_FEMALE:
            case LAST_NAME:
            case NAME:
            case NAME_MALE:
            case NAME_FEMALE:
            case EMAIL:
            case EMAIL_LOCAL_PART:
            case CITY:
            case COUNTRY:
            case PHONE:
            case STATE_ABBREVIATION:
            case STATE:
            case ZIP_CODE:
            case WORD:
            case TEXT:
            case PARAGRAPH:
                return true;
            default:
                return false;
        }
    }

    public String getRandomContent(String key) {
        switch (key) {
            case FIRST_NAME:
                return lorem.getFirstName();
            case FIRST_NAME_MALE:
                return lorem.getFirstNameMale();
            case FIRST_NAME_FEMALE:
                return lorem.getFirstNameFemale();
            case LAST_NAME:
                return lorem.getLastName();
            case NAME:
                return lorem.getName();
            case NAME_MALE:
                return lorem.getNameMale();
            case NAME_FEMALE:
                return lorem.getNameFemale();
            case EMAIL:
                return lorem.getEmail();
            case EMAIL_LOCAL_PART:
                return lorem.getEmail().replaceAll("@.*", "");
            case CITY:
                return lorem.getCity();
            case COUNTRY:
                return lorem.getCountry();
            case PHONE:
                return lorem.getPhone();
            case STATE_ABBREVIATION:
                return lorem.getStateAbbr();
            case STATE:
                return lorem.getStateFull();
            case ZIP_CODE:
                return lorem.getZipCode();
            case WORD:
                return lorem.getWords(1);
            case TEXT:
                return lorem.getWords(1, 20);
            case PARAGRAPH:
                return lorem.getParagraphs(1, 20);
            default:
                return null;
        }
    }

}
