/*$************************************************************************************************
 **
 ** $Id$
 **
 ** $URL$
 **
 ** Copyright (C) 2003-2005 Open GIS Consortium, Inc.
 ** All Rights Reserved. http://www.opengis.org/legal/
 **
 *************************************************************************************************/
package org.opengis.geometry.coordinate;

// Annotations
import org.opengis.annotation.UML;
import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Takes a standard geometric construction and places it in geographic space.
 * This interface defines a transformation from a constructive parameter space
 * to the coordinate space of the coordinate reference system being used. Parameter
 * spaces in formulae are given as (<var>u</var>, <var>v</var>) in 2D and
 * (<var>u</var>, <var>v</var>, <var>w</var>) in 3D. Coordinate reference systems
 * positions are given in formulae by either (<var>x</var>, <var>y</var>) in 2D,
 * or (<var>x</var>, <var>y</var>, <var>z</var>) in 3D. 
 *
 * @version <A HREF="http://www.opengis.org/docs/01-101.pdf">Abstract specification 5</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 2.0
 */
@UML(identifier="GM_Placement", specification=ISO_19107)
public interface Placement {
    /**
     * Return the dimension of the input parameter space.
     */
    @UML(identifier="inDimension", obligation=MANDATORY, specification=ISO_19107)
    public int getInDimension();

    /**
     * Return the dimension of the output coordinate reference system.
     * Normally, {@code outDimension} (the dimension of the coordinate reference system)
     * is larger than {@code inDimension}. If this is not the case, the transformation is
     * probably singular, and may be replaceable by a simpler one from a smaller dimension
     * parameter space.
     */
    @UML(identifier="outDimension", obligation=MANDATORY, specification=ISO_19107)
    public int getOutDimension();

    /**
     * Maps the parameter coordinate points to the coordinate points in the output Cartesian space.
     *
     * @param in Input coordinate points. The length of this vector must be equals to {@link #getInDimension inDimension}.
     * @return The output coordinate points. The length of this vector is equals to {@link #getOutDimension outDimension}.
     */
    @UML(identifier="transform", obligation=MANDATORY, specification=ISO_19107)
    public double[] transform (double[] in);
}