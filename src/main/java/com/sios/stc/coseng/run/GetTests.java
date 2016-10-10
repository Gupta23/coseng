/*
 * Concurrent Selenium TestNG (COSENG)
 * Copyright (c) 2013-2016 SIOS Technology Corp.  All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sios.stc.coseng.run;

import java.io.File;
/*
 * The TestNG reports directory and the temporary resources directory must be
* writable.
* 
* If executing COSENG tests locally, each defined test's platform browser's
* WebDriver must exist and be executable.
* 
* If executing COSENG tests via "grid" the hub's URL must be valid. WebDriver
* executables are irrelevant when executing a test via "grid".
* 
* To override the defaults a valid {@value #COSENG_CONFIGS_NODE} JSON file must
* exist within the src/main/resources hierarchy and define some or all of the
* Node fields. Suggest placing the file in
* src/main/resources/coseng/configs/{@value #COSENG_CONFIGS_NODE}.
*/
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.sios.stc.coseng.run.Browsers.Browser;
import com.sios.stc.coseng.run.Locations.Location;
import com.sios.stc.coseng.util.Resource;

/**
 * The Class GetTests for deserializing Node and Tests JSON configuration
 * resources and validating the parameters.
 *
 * @see com.sios.stc.coseng.run.Node
 * @see com.sios.stc.coseng.run.Test
 * @see com.sios.stc.coseng.run.Tests
 * @see com.sios.stc.coseng.run.Validate#tests(Node, Tests)
 * 
 * @since 2.0
 * @version.coseng
 */
class GetTests {
    private static final Logger log   = LogManager.getLogger(Coseng.class.getName());
    private static Node         node  = null;
    private static Tests        tests = null;

    /**
     * With the given Node and Tests files deserialize the JSON into the
     * corresponding parameter classes.
     *
     * @param jsonNode
     *            the node json parameters
     * @param jsonTests
     *            the tests json parameters
     * @return the tests
     * @throws CosengException
     *             the coseng exception for CosengException, IOException,
     *             JsoonIOException and JsonSyntaxException
     * @see com.sios.stc.coseng.run.Node
     * @see com.sios.stc.coseng.run.Test
     * @see com.sios.stc.coseng.run.Tests
     * @see com.sios.stc.coseng.run.Validate#tests(Node, Tests)
     * @since 2.0
     * @version.coseng
     */
    protected static Tests with(File jsonNode, File jsonTests) throws CosengException {
        Gson gson;
        Reader jsonReader;
        // Read the node configuration file first; feeds into
        // Validate.tests()
        if (jsonNode != null) {
            File jsonNodeFile = Resource.get(jsonNode);
            try {
                gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                jsonReader = new FileReader(jsonNodeFile);
                node = gson.fromJson(jsonReader, Node.class);
            } catch (IOException | JsonIOException | JsonSyntaxException
                    | IllegalArgumentException e) {
                throw new CosengException("Exception reading Node JSON [" + jsonNode + "]", e);
            }
        } else {
            /* Doesn't have to exist; set defaults if absent; see below */
            log.warn("Node JSON not provided; using defaults");
            node = new Node();
        }

        // Read the tests configuration file after getting node
        // configuration
        try {
            File jsonTestsFile;
            jsonTestsFile = Resource.get(jsonTests);
            // Deserialize any case of Platform
            JsonDeserializer<Platform> platformTypeDeserializer = new JsonDeserializer<Platform>() {
                public Platform deserialize(JsonElement json, Type typeOfT,
                        JsonDeserializationContext context) throws JsonParseException {
                    return Platform.valueOf(json.getAsString().toUpperCase());
                }
            };
            // Deserialize any case of Browser
            JsonDeserializer<Browser> browserTypeDeserializer = new JsonDeserializer<Browser>() {
                public Browser deserialize(JsonElement json, Type typeOfT,
                        JsonDeserializationContext context) throws JsonParseException {
                    return Browser.valueOf(json.getAsString().toUpperCase());
                }
            };
            // Deserialize any case of Location
            JsonDeserializer<Location> locationTypeDeserializer = new JsonDeserializer<Location>() {
                public Location deserialize(JsonElement json, Type typeOfT,
                        JsonDeserializationContext context) throws JsonParseException {
                    return Location.valueOf(json.getAsString().toUpperCase());
                }
            };

            // Read the COSENG Tests JSON configuration file
            gson = new GsonBuilder().registerTypeAdapter(Platform.class, platformTypeDeserializer)
                    .registerTypeAdapter(Browser.class, browserTypeDeserializer)
                    .registerTypeAdapter(Location.class, locationTypeDeserializer)
                    .excludeFieldsWithoutExposeAnnotation().create();
            jsonReader = new FileReader(jsonTestsFile);
            tests = gson.fromJson(jsonReader, Tests.class);
            // validate the tests
            Validate.tests(node, tests);
        } catch (CosengException | IOException | JsonIOException | JsonSyntaxException
                | IllegalArgumentException e) {
            throw new CosengException("Exception reading Tests JSON [" + jsonTests + "]", e);
        }
        return tests;
    }

    /**
     * Configuration composed of the Node and Tests.
     *
     * @return the string
     * @see com.sios.stc.coseng.run.Node
     * @see com.sios.stc.coseng.run.Test
     * @see com.sios.stc.coseng.run.Tests
     * @since 2.0
     * @version.coseng
     */
    protected static String configuration() {
        List<String> configs = new ArrayList<String>();
        configs.add("Node & Tests Configuration");
        configs.add("Node: " + node.toString());
        for (Test test : tests.getAll()) {
            configs.add("Test: " + test.toString());
        }
        return StringUtils.join(configs, System.lineSeparator());
    }
}
