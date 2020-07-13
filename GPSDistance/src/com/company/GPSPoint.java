package com.company;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

class GPSPoint {

    double lat; //Radians, converted in Constructor
    double lon;

    double latitude; //Degrees
    double longitude;

    int index = -1; //Array index - -1 default means not set

    GPSPoint nearest;
    Integer nearestDist = 0;

    PriorityQueue<GPSPointPair> closestPts;

    boolean visited = false;

    int visitedCount = 0;

    private static final int RAD = 6371; //average radius of Earth

    /*  Values for Earth from WGS-84 standards:
     *   https://en.wikipedia.org/wiki/World_Geodetic_System#WGS84
     *  */
    private static final double a = 6378137.0; //equatorial radius of Earth
    private static final double f = (1 / 298.257223563); //flattening of Earth
    private static final double b = 6356752.314245;

    /*  For Vincenty */
    private static final int MAX_RUNS = 200; //Times to iterate
    private static final double AP_LIMIT = 3.0; //Min. diff. allowed for antipodean pts

    public GPSPoint(double latitude, double longitude) {
        this.lat = Math.toRadians(latitude);
        this.lon = Math.toRadians(longitude);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GPSPoint(double latitude, double longitude, int index) {
        this(latitude, longitude);
        this.index = index;
    }

    public static int findDistance(GPSPoint x, GPSPoint y) {
        return (int) ( findDistanceHaversine(x, y) );
    }

    public static double getDistance( GPSPoint g1, GPSPoint g2){
        return findDistanceHaversine(g1,g2);
    }


    public static double findDistanceHaversine(GPSPoint x, GPSPoint y) {

        double latDiff = (y.lat - x.lat);
        double lonDiff = (y.lon - x.lon);

        double r = RAD;

        double dist = 2 * r * Math.asin(
                Math.sqrt(
                        Math.pow(Math.sin(latDiff / 2), 2) +
                                Math.cos(x.lat) * Math.cos(y.lat) *
                                        Math.pow(Math.sin(lonDiff / 2), 2)
                )
        );

        return dist;
    }

    public static double findDistanceLambert(GPSPoint x, GPSPoint y) {

        /* beta1, beta2 - reduced latitudes of x and y
         * -converted with
         *   ```tan beta = (1 - f)tan lat```
         *  -> (inverted with arctangent because ```tan (arctan x) = x```)
         * */
        double beta1 = Math.atan((1 - f) * Math.tan(x.lat));
        double beta2 = Math.atan((1 - f) * Math.tan(y.lat));

        /* The angle between the two points
         *  calculated as angle = distance / radius
         *   -> distance returned by haversine method
         * */
        double sigma = findDistanceHaversine(x, y) / RAD;

        double P = (beta1 + beta2) / 2;
        double Q = (beta2 - beta1) / 2;

        double X = (sigma - Math.sin(sigma)) *
                (
                        (Math.pow(Math.sin(P), 2) * Math.pow(Math.cos(Q), 2)) /
                                (Math.pow(Math.cos(sigma / 2), 2))
                );

        double Y = (sigma + Math.sin(sigma)) *
                (
                        (Math.pow(Math.cos(P), 2) * Math.pow(Math.sin(Q), 2)) /
                                (Math.pow(Math.sin(sigma / 2), 2))
                );

        double dist = a * (sigma - (f / 2) * (X + Y));

        return dist / 1000.0;
    }

    /*  Based on Wikipedia
     *   https://en.wikipedia.org/wiki/Vincenty%27s_formulae
     *   Inverse problem returns distance and azimuths,
     *   -azimuths not required here so not calculated
     * */
    public static double findDistanceVincenty(GPSPoint x, GPSPoint y) {
//
//        double xAntipodeLat = x.latitude * -1.0;
//        double xAntipodeLon = (180.0 - Math.abs(x.longitude)) * -1.0;

        /* If Antipodean points, try another method, as Vincenty
         *   may fail to converge
         * */
//        if( y.latitude - xAntipodeLat < AP_LIMIT &&
//                y.longitude - xAntipodeLon < AP_LIMIT ) {
//            return findDistanceLambert(x, y);
//        }

        double accuracy = 1E-12; //This level == std. error of 0.06mm

        /*  phi == latitude */
        double phi1 = x.lat;
        double phi2 = y.lat;

        /*  Reduced latitude calculation - account for "flattening" of Earth
         *   vs a perfect sphere
         *  */
        double U1 = Math.atan( (1 - f) * Math.tan(phi1) );
        double U2 = Math.atan( (1 - f) * Math.tan(phi2) );

        /*  Calculating sin & cos here because used for
         *   iterative converging lambda equations and also
         *   outside that code block for distance
         * */
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);

        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
//
        double L1 = x.lon;
        double L2 = y.lon;

        /*  Longitude difference */
        double L = L2 - L1;

        /* Longitude difference if perfect sphere shape */
        double lambda = L;
        double lastLambda;

        double alpha1, alpha2;  //UNUSED Forward azimuths pts x & y
        double alpha;           //UNUSED Geodesic equatorial fwd. azimuth
        double s;               //Ellipsoidal distance between x & y
        double sigma;           //Ang. separation between x & y
        double sigma1;          //Ang. sep. point & equator
        double sigmaM;          //Ang. sep. midpoint of line & equator

        double cosSqAlpha;
        double sinSigma;
        double cos2sigmaM;
        double cosSigma;


        /* Inverse Problem */
        int count = 0; //To limit iterations in edge cases

        /* Iterative function */
        do {

            double sinLambda = Math.sin(lambda);
            double cosLambda = Math.cos(lambda);

            sinSigma = Math.sqrt(
                    Math.pow(
                            ( cosU2 * sinLambda )
                            , 2 )
                            +
                            Math.pow(
                                    (
                                            cosU1 * sinU2
                                                    - sinU1 * cosU2 * cosLambda
                                    )
                                    , 2)
            );

            if (sinSigma == 0) {
                return findDistanceLambert(x, y);
            }

            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;

            sigma = Math.atan2(sinSigma, cosSigma);

            double sinAlpha = (cosU1 * cosU2 * sinLambda) / sinSigma;
            cosSqAlpha = 1 -  sinAlpha * sinAlpha;

            cos2sigmaM = cosSigma - (2 * sinU1 * sinU2) / cosSqAlpha;

            double C = (f / 16) * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));

            lastLambda = lambda;
            lambda = L + (1 - C) * f * sinAlpha *
                    (
                            sigma + C * sinSigma *
                                    (cos2sigmaM + C * cosSigma *
                                            (-1 + 2 * Math.pow(cos2sigmaM, 2))

                                    )
                    );

            count++;
        } while (Math.abs(lambda - lastLambda) > accuracy && count < MAX_RUNS);

        if(count == MAX_RUNS) { //Failure to converge
            return findDistanceLambert(x, y);
        }

        /*  After convergence is met */
        double uSq = cosSqAlpha * (
                ( Math.pow(a, 2) - Math.pow(b, 2)) /
                        Math.pow(b, 2)
        );

        double A = 1 + (uSq / 16384) * (
                4096 + uSq *
                        (
                                -768 + uSq * (320 - 175 * uSq)
                        )
        );

        double B = (uSq / 1024) * (
                256 + uSq *
                        (
                                -128 + uSq * (74 - 47 * uSq)
                        )
        );

        double deltaSigma = B * sinSigma * (
                cos2sigmaM + (1.0/ 4) * B *
                        (
                                cosSigma *
                                        (
                                                -1 + 2 * Math.pow(cos2sigmaM , 2)
                                        )
                                        - B / 6 *
                                        cos2sigmaM *
                                        (
                                                -3 + 4 * Math.pow(sinSigma, 2)
                                        ) *
                                        (
                                                -3 + 4 * Math.pow(cos2sigmaM, 2)
                                        )
                        )
        );

        s = b * A * (sigma - deltaSigma);

        return s / 1000.0;
    }

    /*  Find the nearest neighbour to this point that is at least 100km away */
    public void setNearest(GPSPoint[] allGPSPoints, int[][] distMatrix) {
        int minDist = Integer.MAX_VALUE;  // Arbitrary max value for checks.
                                            // Can't set this to the first array value without checking that the distance
                                            // is not -1 (designated num. for itself) or under 100,
                                            // which might require **another** loop, so this is easier.

        closestPts = new PriorityQueue<>();

        /*  Loop through the pre-made 2d array of distances from this point, to all other locations */
        for(int i=0; i<distMatrix.length; i++) {

            int d = distMatrix[index][i];

//            if(d < 100 || d == 0) {
//                continue;
//            }

            /*  Add only:
            *       *  distances over 100
            *       *  not skipped distances
            *           --  skipped distances are ones that are already visited,
            *               or the point itself, etc. (This should be safely covered,
            *               as own pt. distance is set to -1, but just in case)
            *  -- NB Function should be called one by one on the current point
            *     as it's visited, so the skipped places are dynamically
            *     updated
            * */
            if(d >=100 && !allGPSPoints[i].visited) {

                /*  Add the point pair to a priority queue, in order of distance */
                GPSPointPair nextPair = new GPSPointPair(this, allGPSPoints[i], d);
                closestPts.add(nextPair);

                if (d < minDist) {

//                System.out.println("\t\t" + d + " is less than " + minDist);
                    minDist = d;

                    nearest = allGPSPoints[i];
                    nearestDist = d;

                }
//            boolean overMin = (d >= 100);
//            System.out.println( allGPSPoints[i] + " " + d + " " + overMin);
            }

        }
        if(nearest == null) {
//            nearest = allGPSPoints[ (int)(Math.random() * allGPSPoints.length) ];
//            System.out.println("===\n\tfinding closest unvisited over 100 failed for " + index + "\n===");

            nearest = findClosestVisited(allGPSPoints, distMatrix);

        }
//        return nearest;
    }

    public GPSPoint findClosestVisited(GPSPoint[] allGPSPoints, int[][]distMatrix) {

        GPSPoint closest = null;
        int closestDist = Integer.MAX_VALUE;

        for(int i=0; i<distMatrix[index].length; i++) {
            if(index == i) {
                continue; //skip self
            }
            int checkDist = distMatrix[index][i];

            ArrayList<GPSPoint> candidates = new ArrayList<>();

            if(checkDist >= 100 && checkDist <= 300 && allGPSPoints[i].visitedCount <= 1 ) {

                candidates.add(allGPSPoints[i]);

                if(checkDist < closestDist) {
                    closest = allGPSPoints[i];
                    closestDist = distMatrix[index][i];
                }
            }
        }

        if(closest == null) { //handle errors by randomising choice - hopefully not needed

//            System.out.println("***\n\tfinding closest visited over 100 failed for " + index + "\n***");

            int randomIndex = (int) Math.floor(Math.random() * allGPSPoints.length);

            while(distMatrix[index][randomIndex] < 100) {
                randomIndex = (int) Math.floor(Math.random() * allGPSPoints.length);
            }

            closest = allGPSPoints[randomIndex];
        }

        return closest;

    }
    public static double findStringArrayDistance(String[] locs, GPSPoint[] allGPSPoints ) {

        int[] strToInt = new int[locs.length];

        for(int i=0; i<locs.length; i++) {
            strToInt[i] = Integer.parseInt( locs[i] );
        }

        return findIntArrayDistance(strToInt, allGPSPoints);
    }

    public static double findIntArrayDistance(int[] locs, GPSPoint[] allGPSPoints) {
        double checkDistance = 0;

        for (int i = 0; i < locs.length - 1; i++) {
            int locA = locs[i];
            int locB = locs[i + 1];

            checkDistance += getDistance(allGPSPoints[locA], allGPSPoints[locB]);

        }

        if (locs.length % 2 == 1) {
            int locA = locs[locs.length - 2];
            int locB = locs[locs.length - 1];
            checkDistance += getDistance(allGPSPoints[locA], allGPSPoints[locB]);
        }

        return checkDistance;
    }

    @Override
    public String toString(){
        return "Pt. #" + index + ": (" + latitude + ", " + longitude + ")";
    }






}

/*  Pair of GPSPoint objects, so that they can be easily compared for
*   SortedSet, PriorityQueue etc.
* */
class GPSPointPair implements Comparable<GPSPointPair> {

    GPSPoint origin;
    GPSPoint destination;

    int distance;

    GPSPointPair(GPSPoint origin, GPSPoint destination) {
        this.origin = origin;
        this.destination = destination;

        distance = GPSPoint.findDistance(origin, destination);
    }

    /* Constructor for pre-calculated distance */
    GPSPointPair(GPSPoint origin, GPSPoint destination, int distance) {
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
    }

    @Override
    public int compareTo(GPSPointPair other) {

        return  (this.distance - other.distance);

    }

    @Override
    public String toString() {
        return destination.toString() + ", " + distance;
    }

}


