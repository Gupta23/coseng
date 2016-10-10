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
package com.sios.stc.coseng.tests.demo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.sios.stc.coseng.run.Coseng;
import com.sios.stc.coseng.run.CosengException;
import com.sios.stc.coseng.run.CosengRunner;
import com.sios.stc.coseng.run.WebElement;

public class Ask extends CosengRunner {
    private static final String ELEMENT_SEARCHFORM = "search-box";
    private static final String URL                = "http://www.ask.com";
    private static final Logger log                = LogManager.getLogger(Coseng.class.getName());

    @Test(description = "Verify connect to Ask and search")
    public void connect1() throws CosengException {
        Assert.assertTrue(hasWebDriver(), "No WebDriver");
        WebDriver webDriver = getWebDriver();
        log.debug("Test [{}], WebDriver [{}]", getTest().getName(), webDriver.hashCode());
        WebElement searchForm = newWebElement(By.id(ELEMENT_SEARCHFORM));
        webDriver.get(URL);
        searchForm.find();
        Assert.assertTrue(searchForm.isDisplayed());
    }

    @Test(description = "Verify connect to Ask and search")
    public void connect2() throws CosengException {
        Assert.assertTrue(hasWebDriver(), "No WebDriver");
        WebDriver webDriver = getWebDriver();
        log.debug("Test [{}], WebDriver [{}]", getTest().getName(), webDriver.hashCode());
        WebElement searchForm = newWebElement(By.id(ELEMENT_SEARCHFORM));
        webDriver.get(URL);
        searchForm.find();
        Assert.assertTrue(searchForm.isDisplayed());
    }
}
