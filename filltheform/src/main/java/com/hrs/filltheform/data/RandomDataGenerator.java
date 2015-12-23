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
public class RandomDataGenerator {

    private static final String FIRST_NAME = "random_first_name";
    private static final String FIRST_NAME_MALE = "random_first_name_male";
    private static final String FIRST_NAME_FEMALE = "random_first_name_female";
    private static final String LAST_NAME = "random_last_name";

    private static final String NAME = "random_name";
    private static final String NAME_MALE = "random_name_male";
    private static final String NAME_FEMALE = "random_name_female";

    private static final String EMAIL = "random_email";
    private static final String CITY = "random_city";
    private static final String COUNTRY = "random_country";
    private static final String PHONE = "random_phone";
    private static final String STATE_ABBREVIATION = "random_state_abbreviation";
    private static final String STATE = "random_state";
    private static final String ZIP_CODE = "random_zip_code";

    private static final String WORD = "random_word";
    private static final String TEXT = "random_text";
    private static final String PARAGRAPH = "random_paragraph";

    private static Lorem lorem;

    public static void init() {
        if (lorem == null) {
            lorem = LoremIpsum.getInstance();
        }
    }

    public static boolean isRandomKey(String key) {
        switch (key) {
            case FIRST_NAME:
            case FIRST_NAME_MALE:
            case FIRST_NAME_FEMALE:
            case LAST_NAME:
            case NAME:
            case NAME_MALE:
            case NAME_FEMALE:
            case EMAIL:
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

    public static String getRandomContent(String key) {
        if (lorem == null) {
            lorem = LoremIpsum.getInstance();
        }

        String randomContent;

        switch (key) {
            case FIRST_NAME:
                randomContent = lorem.getFirstName();
                break;
            case FIRST_NAME_MALE:
                randomContent = lorem.getFirstNameMale();
                break;
            case FIRST_NAME_FEMALE:
                randomContent = lorem.getFirstNameFemale();
                break;
            case LAST_NAME:
                randomContent = lorem.getLastName();
                break;
            case NAME:
                randomContent = lorem.getName();
                break;
            case NAME_MALE:
                randomContent = lorem.getNameMale();
                break;
            case NAME_FEMALE:
                randomContent = lorem.getNameFemale();
                break;
            case EMAIL:
                randomContent = lorem.getEmail();
                break;
            case CITY:
                randomContent = lorem.getCity();
                break;
            case COUNTRY:
                randomContent = lorem.getCountry();
                break;
            case PHONE:
                randomContent = lorem.getPhone();
                break;
            case STATE_ABBREVIATION:
                randomContent = lorem.getStateAbbr();
                break;
            case STATE:
                randomContent = lorem.getStateFull();
                break;
            case ZIP_CODE:
                randomContent = lorem.getZipCode();
                break;
            case WORD:
                randomContent = lorem.getWords(1);
                break;
            case TEXT:
                randomContent = lorem.getWords(1, 20);
                break;
            case PARAGRAPH:
                randomContent = lorem.getParagraphs(1, 20);
                break;
            default:
                randomContent = lorem.getWords(1);
                break;
        }

        return randomContent;
    }

}
