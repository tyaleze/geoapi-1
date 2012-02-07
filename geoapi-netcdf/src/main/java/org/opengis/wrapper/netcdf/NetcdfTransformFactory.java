/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    This file is hereby placed into the Public Domain.
 *    This means anyone is free to do whatever they wish with this file.
 *
 *    The NetCDF wrappers are provided as code examples, in the hope to facilitate
 *    GeoAPI implementations backed by other libraries. Implementors can take this
 *    source code and use it for any purpose, commercial or non-commercial, copyrighted
 *    or open-source, with no legal obligation to acknowledge the borrowing/copying
 *    in any way.
 */
package org.opengis.wrapper.netcdf;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Collections;

import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.*;


/**
 * Creates {@link NetcdfProjection} instances from NetCDF, OGC or EPSG parameters.
 *
 * @author  Martin Desruisseaux (Geomatys)
 * @version 3.1
 * @since   3.1
 */
public class NetcdfTransformFactory implements MathTransformFactory {
    /**
     * The list of parameters and aliases for each projection.
     */
    private final Map<String, ProjectionProvider<?>> providers;

    /**
     * A copy of the {@link #providers} values.
     */
    private final Set<OperationMethod> methods;

    /**
     * Creates a new factory.
     */
    public NetcdfTransformFactory() {
        providers = new LinkedHashMap<String, ProjectionProvider<?>>();
        final Map<Alias,Alias> existings = new HashMap<Alias,Alias>();
        add(new ProjectionProvider.Albers              (existings));
        add(new ProjectionProvider.Flat                (existings));
        add(new ProjectionProvider.LambertAzimuthal    (existings));
        add(new ProjectionProvider.LambertConic1SP     (existings));
        add(new ProjectionProvider.PlateCarree         (existings));
        add(new ProjectionProvider.Mercator2SP         (existings));
        add(new ProjectionProvider.Ortho               (existings));
        add(new ProjectionProvider.RotatedSouth        (existings));
        add(new ProjectionProvider.RotatedNorth        (existings));
        add(new ProjectionProvider.ObliqueStereographic(existings));
        add(new ProjectionProvider.Transverse          (existings));
        add(new ProjectionProvider.UTM                 (existings));
        add(new ProjectionProvider.Perspective         (existings));
        methods = Collections.unmodifiableSet(new LinkedHashSet<OperationMethod>(providers.values()));
    }

    /**
     * Adds the given provider to the list of registered providers.
     * This method ensure that there is no duplicated values.
     */
    private void add(final ProjectionProvider<?> provider) {
        provider.name.addTo(providers, provider);
    }

    /**
     * Returns the provider of this factory.
     */
    @Override
    public Citation getVendor() {
        return SimpleCitation.NETCDF;
    }

    /**
     * Returns a set of available methods for {@linkplain MathTransform math transforms}.
     *
     * @param  type The type of operations, or {@code null} if unspecified.
     * @return All {@linkplain MathTransform math transform} methods available in this factory.
     */
    @Override
    public Set<OperationMethod> getAvailableMethods(final Class<? extends SingleOperation> type) {
        return (type == null || type.isAssignableFrom(Projection.class)) ? methods : Collections.<OperationMethod>emptySet();
    }

    /**
     * Not yet implemented.
     */
    @Override
    public OperationMethod getLastMethodUsed() {
        return null;
    }

    /**
     * Returns the default parameter values for a math transform using the given method.
     * The {@code method} argument is the name of any operation method returned by
     * <code>{@link #getAvailableMethods getAvailableMethods}({@linkplain CoordinateOperation}.class)</code>.
     *
     * @param  method The case insensitive name of the method to search for.
     * @return The default parameter values.
     * @throws NoSuchIdentifierException if there is no transform registered for the specified method.
     */
    @Override
    public ParameterValueGroup getDefaultParameters(final String method) throws NoSuchIdentifierException {
        final ProjectionProvider<?> provider = providers.get(method);
        if (provider != null) {
            return provider.createValue();
        }
        throw new NoSuchIdentifierException("Projection \"" + method + "\" not found.", method);
    }

    /**
     * Not yet implemented.
     */
    @Override
    public MathTransform createBaseToDerived(final CoordinateReferenceSystem baseCRS,
                                             final ParameterValueGroup parameters,
                                             final CoordinateSystem derivedCS)
            throws FactoryException
    {
        throw new FactoryException("Not supported yet.");
    }

    /**
     * Creates a transform from a group of parameters. All cartographic projections
     * created through this method will have the following properties:
     * <p>
     * <UL>
     *   <LI>Converts from (<var>longitude</var>,<var>latitude</var>) coordinates to (<var>x</var>,<var>y</var>).</LI>
     *   <LI>All angles are assumed to be degrees, and all distances are assumed to be meters.</LI>
     *   <LI>The domain shall be a subset of {[-180,180)&times;(-90,90)}.</LI>
     * </UL>
     *
     * @param  parameters The parameter values.
     * @return The parameterized transform.
     * @throws FactoryException if the object creation failed. This exception is thrown
     *         if some required parameter has not been supplied, or has illegal value.
     */
    @Override
    public MathTransform createParameterizedTransform(final ParameterValueGroup parameters)
            throws FactoryException
    {
        final String method = parameters.getDescriptor().getName().getCode();
        final ProjectionProvider<?> provider = providers.get(method);
        if (provider != null) try {
            return new NetcdfProjection(provider.createProjection(parameters), provider, null, null);
        } catch (ParameterNotFoundException e) {
            throw new FactoryException("Illegal parameters for the \"" + method +
                    "\" projection: " + e.getLocalizedMessage(), e);
        }
        throw new NoSuchIdentifierException("Projection \"" + method + "\" not found.", method);
    }

    /**
     * Not yet implemented.
     */
    @Override
    public MathTransform createAffineTransform(final Matrix matrix) throws FactoryException {
        throw new FactoryException("Not supported yet.");
    }

    /**
     * Not yet implemented.
     */
    @Override
    public MathTransform createConcatenatedTransform(final MathTransform transform1,
                                                     final MathTransform transform2)
            throws FactoryException
    {
        throw new FactoryException("Not supported yet.");
    }

    /**
     * Not yet implemented.
     */
    @Override
    public MathTransform createPassThroughTransform(final int firstAffectedOrdinate,
                                                    final MathTransform subTransform,
                                                    final int numTrailingOrdinates)
            throws FactoryException
    {
        throw new FactoryException("Not supported yet.");
    }

    /**
     * Not yet implemented.
     */
    @Override
    public MathTransform createFromXML(final String xml) throws FactoryException {
        throw new FactoryException("Not supported yet.");
    }

    /**
     * Not yet implemented.
     */
    @Override
    public MathTransform createFromWKT(final String wkt) throws FactoryException {
        throw new FactoryException("Not supported yet.");
    }
}