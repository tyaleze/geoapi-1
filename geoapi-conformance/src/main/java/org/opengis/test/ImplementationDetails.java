/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2011 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.opengis.test;

import java.io.IOException;
import java.util.Properties;
import org.opengis.util.Factory;


/**
 * Provides optional information about the implementation being tested. Implementors can
 * provide instance of this interface in their test packages, and declare their instance
 * in the {@code META-INF/services/org.opengis.test.ImplementationDetails} file. Before
 * to run any {@link TestCase}, the GeoAPI {@link TestSuite} will iterate over every
 * {@code ImplementationDetails} found on the classpath and checks whatever a particular
 * factory can be tested, and which tests should be disabled (if any).
 * <p>
 * If no instance of {@code ImplementationDetails} is registered, then GeoAPI assumes that
 * every factories found on the classpath shall be tested and all tests are enabled. This
 * is equivalent to using a {@code ImplementationDetails} instance where:
 * <p>
 * <ul>
 *   <li>{@link #filter(Class, Factory)} returns unconditionally {@code true}</li>
 *   <li>{@link #configuration(Factory[])} returns unconditionally {@code null}</li>
 * </ul>
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public interface ImplementationDetails {
    /**
     * Returns {@code true} if the given factory can be tested. Implementors shall return
     * {@code false} only when they really want to exclude a particular factory. For every
     * unknown factory, this method should return {@code true}.
     * <p>
     * If more than one {@code ImplementationDetails} is found on the classpath, then the
     * given factory will be tested only if all {@code ImplementationDetails.filter(...)}
     * calls returned {@code true}.
     *
     * @param  <T>      The compile-time type of the {@code category} argument.
     * @param  category The factory interface ({@link org.opengis.util.NameFactory},
     *                  {@link org.opengis.referencing.crs.CRSFactory}, <i>etc.</i>).
     * @param  factory  The factory instance.
     * @return {@code false} if the given factory should be excluded from the tests,
     *         or {@code true} otherwise.
     */
    <T extends Factory> boolean filter(Class<T> category, T factory);

    /**
     * Returns the set of tests that should be disabled, or {@code null} if none.
     * If non-null, then the returned map can assign the value {@code "false"} to
     * any of the following keys:
     * <p>
     * <ul>
     *   <li>{@link org.opengis.test.referencing.TransformTestCase#isDoubleToDoubleSupported   isDoubleToDoubleSupported}</li>
     *   <li>{@link org.opengis.test.referencing.TransformTestCase#isFloatToFloatSupported     isFloatToFloatSupported}</li>
     *   <li>{@link org.opengis.test.referencing.TransformTestCase#isDoubleToFloatSupported    isDoubleToFloatSupported}</li>
     *   <li>{@link org.opengis.test.referencing.TransformTestCase#isFloatToDoubleSupported    isFloatToDoubleSupported}</li>
     *   <li>{@link org.opengis.test.referencing.TransformTestCase#isOverlappingArraySupported isOverlappingArraySupported}</li>
     *   <li>{@link org.opengis.test.referencing.TransformTestCase#isInverseTransformSupported isInverseTransformSupported}</li>
     *   <li>{@link org.opengis.test.referencing.TransformTestCase#isDerivativeSupported       isDerivativeSupported}</li>
     * </ul>
     * <p>
     * If more than one {@code ImplementationDetails} is found on the classpath, then the above
     * tests are enabled only if none of the {@code ImplementationDetails.configuration(...)}
     * calls assigned {@code "false"} to the corresponding key.
     * <p>
     * This method may be invoked often, so implementors are advised to cache their properties map.
     *
     * @param  factories The factories to be tested.
     * @return The collection of tests to disable for the given factories, or {@code null} if none.
     * @throws IOException If the implementation tried to {@linkplain Properties#load(java.io.Reader)
     *         load} the properties from a file and that operation failed.
     */
    Properties configuration(Factory... factories) throws IOException;
}
