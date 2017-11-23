package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

// A bus route drawer
public class BusRouteDrawer extends MapViewOverlay {
    /** overlay used to display bus route legend text on a layer above the map */
    private BusRouteLegendOverlay busRouteLegendOverlay;
    /** overlays used to plot bus routes */
    private List<Polyline> busRouteOverlays;

    /**
     * Constructor
     * @param context   the application context
     * @param mapView   the map view
     */
    public BusRouteDrawer(Context context, MapView mapView) {
        super(context, mapView);
        busRouteLegendOverlay = createBusRouteLegendOverlay();
        busRouteOverlays = new ArrayList<>();
    }

    /**
     * Plot each visible segment of each route pattern of each route going through the selected stop.
     */
    public void plotRoutes(int zoomLevel) {
        updateVisibleArea();
        busRouteOverlays.clear();
        Stop selectedStops = StopManager.getInstance().getSelected();
        busRouteLegendOverlay.clear();

        if(selectedStops != null) {
            Set<Route> routes = selectedStops.getRoutes();

            for(Route route : routes) {
                busRouteLegendOverlay.add(route.getNumber());
                List<RoutePattern> routePattern = route.getPatterns();

                for(RoutePattern p : routePattern) {
                    List<LatLon> locn = p.getPath();

                    for(int i = 0; i < locn.size()-1; i++){
                        Polyline line = new Polyline(mapView.getContext());
                        line.setColor(busRouteLegendOverlay.getColor(route.getNumber()));
                        line.setWidth(getLineWidth(zoomLevel));
                        List<GeoPoint> points = new ArrayList<>();

                        if(Geometry.rectangleIntersectsLine(northWest, southEast, locn.get(i), locn.get(i + 1))) {
                            points.add(new GeoPoint(locn.get(i).getLatitude(), locn.get(i).getLongitude()));
                            points.add(new GeoPoint(locn.get(i+ 1).getLatitude(), locn.get(i + 1).getLongitude()));
                            line.setPoints(points);
                            busRouteOverlays.add(line);
                        }
                    }
                }
            }
        }
        //TODO: complete the implementation of this method (Task 7)
    }

    public List<Polyline> getBusRouteOverlays() {
        return Collections.unmodifiableList(busRouteOverlays);
    }

    public BusRouteLegendOverlay getBusRouteLegendOverlay() {
        return busRouteLegendOverlay;
    }


    /**
     * Create text overlay to display bus route colours
     */
    private BusRouteLegendOverlay createBusRouteLegendOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);
        return new BusRouteLegendOverlay(rp, BusesAreUs.dpiFactor());
    }

    /**
     * Get width of line used to plot bus route based on zoom level
     * @param zoomLevel   the zoom level of the map
     * @return            width of line used to plot bus route
     */
    private float getLineWidth(int zoomLevel) {
        if(zoomLevel > 14)
            return 7.0f * BusesAreUs.dpiFactor();
        else if(zoomLevel > 10)
            return 5.0f * BusesAreUs.dpiFactor();
        else
            return 2.0f * BusesAreUs.dpiFactor();
    }
}
