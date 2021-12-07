package api;
/**
 * This interface represents a geo location <x,y,z>, (aka Point3D data).
 *
 */
public interface GeoLocation {
    double x();
    double y();
    double z();
    double distance(GeoLocation g);
}
