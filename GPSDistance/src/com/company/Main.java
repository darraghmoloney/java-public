package com.company;


import java.util.PriorityQueue;

public class Main {

    private static final int NUM_RUNS = 500; //too high seems to cause memory leak ??

    public static void main(String[] args) {


        FileIO file = new FileIO();

        //Retrieve the best result found so far incl. distance & pts visited
        String[] bestFile = file.load("src/com/company/best.txt");

        String[] bestStr = bestFile[0].split(" ");

        //For comparison - if better distance found, file will be overwritten
        int bestDist = Integer.parseInt(bestStr[0]);



        int numPts = PlacesList.latLongs.length;
        GPSPoint[] allGPSPoints = new GPSPoint[numPts]; //Array of all pts in special Object
        int index = 0;

        for (String s : PlacesList.latLongs) {
            String[] split = s.split("\\s+");
            double lat = Double.parseDouble(split[0]);
            double lon = Double.parseDouble(split[1]);

            allGPSPoints[index] = new GPSPoint(lat, lon, index); //Store the index loc. of this Point for arrays, etc

            index++;
        }

        /*  Generate 2d matrix of distances between each point
         * */
        int[][] distMatrix = new int[numPts][numPts];


        PriorityQueue<Integer> locsQueue = new PriorityQueue<>();


        for(int i=0; i<numPts; i++) {

            for(int j=0; j<numPts; j++) {

                /*  If it's the same point, set the distance to -1,
                 *   to mark this as a location to be ignored
                 * */
                if(i == j) {
                    distMatrix[i][j] = -1;
                } else {
                    int distance = (int) GPSPoint.getDistance(allGPSPoints[i], allGPSPoints[j]);
                    distMatrix[i][j] = distance;
                    distMatrix[j][i] = distance;
                }

            }

        }

/*  Monte Carlo slight random variation in distances, but close to nearest neighbour */
        System.out.println("...checking...");

    final int[][] startDistMatrix = new int[distMatrix.length][distMatrix[0].length];

    for(int i=0; i<distMatrix.length; i++) {
        for(int j=0; j<distMatrix[i].length; j++) {
            startDistMatrix[i][j] = distMatrix[i][j];
        }
    }

    String bestPlacesStr = bestStr[1];

    String monteCarloBest= "";
    int monteCarloLowestDist = Integer.MAX_VALUE;

    for(int count=0; count<NUM_RUNS; count++) {
    //NEAREST NEIGHBOUR & Monte Carlo Random Variation
    //////////////////////////////////

        //reset so random extra distances are removed on the next run
        for(int i=0; i<distMatrix.length; i++) {
            for(int j=0; j<distMatrix[i].length; j++) {
                distMatrix[i][j] = startDistMatrix[i][j];
            }
        }


        for (int i = 0; i < numPts; i++) {




            for (int j = 0; j < numPts; j++) {



                        if (distMatrix[i][j] > 100
                                && distMatrix[i][j] < 1000)
                        {

                            if (Math.random() > 0.85) {
                                /*add a random factor of 0-200km */
                                int randExtraDistance = (int) Math.floor(Math.random() * (250) );

                                distMatrix[i][j] += randExtraDistance;
                            }
                        }



            }

        }
            int placesToVisit = allGPSPoints.length;

            GPSPoint currentPlace = allGPSPoints[0];
            String visitedPlacesStr = "0,";



            double planeSpeed = 800;


            int visitedCount = 0;

            boolean[] visitedPts = new boolean[allGPSPoints.length];

            /*  Basic nearest neighbour algorithm */
            while (visitedCount < placesToVisit) {


                if (!visitedPts[currentPlace.index]) {
                    visitedPts[currentPlace.index] = true;

                }

                locsQueue.add(currentPlace.index);

                currentPlace.setNearest(allGPSPoints, distMatrix);
                int distance = currentPlace.nearestDist;

                if (distance < 100) {

                    GPSPoint next = currentPlace.findClosestVisited(allGPSPoints, distMatrix);
                    distance = GPSPoint.findDistance(currentPlace, next);

                    currentPlace = next;


                } else {
                    currentPlace = currentPlace.nearest;


                }



                if (!currentPlace.visited) {
                    currentPlace.visited = true;
                    visitedCount++;
                }


                currentPlace.visitedCount++; //prevent loops between two close pts if revisit needed due to randomness

                try {
                    visitedPlacesStr += currentPlace.index + ",";
                }
                catch (Exception e) {
                    e.printStackTrace();
                }



            }


            //If we have finished we need to add in the return trip home

                int returnToStartDist = distMatrix[0][currentPlace.index];
                currentPlace = allGPSPoints[0];


                visitedCount++;

                visitedPlacesStr += "0";



            String[] locs = visitedPlacesStr.split(","); //Re-calculate the distances

            double checkDistance = 0;
            for (int i = 0; i < locs.length - 1; i++) {
                int locA = Integer.parseInt(locs[i]);
                int locB = Integer.parseInt(locs[i + 1]);
    //            System.out.println(locA + "," + locB);
                checkDistance += GPSPoint.getDistance(allGPSPoints[locA], allGPSPoints[locB]);


                if(checkDistance > bestDist) {
                    break;
                }


            }



            if (locs.length % 2 == 1) {
                int locA = Integer.parseInt(locs[locs.length - 2]);
                int locB = Integer.parseInt(locs[locs.length - 1]);
                checkDistance += GPSPoint.getDistance(allGPSPoints[locA], allGPSPoints[locB]);
            }
            System.out.println((int) checkDistance);

            //Save the results to check the best, given the slight randomization in the nearest neighbour
            String fileString = (int) checkDistance + " {" + visitedPlacesStr + "},";


            String[] res = fileString.split(" ");

            if(checkDistance < monteCarloLowestDist) {

                monteCarloLowestDist = (int) checkDistance;
                monteCarloBest = visitedPlacesStr;

            }


            if (checkDistance < bestDist) {
                bestDist = (int) checkDistance;
                String[] newBestForSave = {
                        fileString
                };

                bestPlacesStr = visitedPlacesStr;

                file.save("src/com/company/best.txt", newBestForSave);
                System.out.println("NEW BEST---");
                System.out.println(fileString);


            }

            //Reset visited status for each GPS point, for new run
            for (GPSPoint g : allGPSPoints) {
                g.visited = false;
                g.visitedCount = 0;
            }

        }

        System.out.println("...finished basic checks...");
        System.out.println("-/- 2OPT START -\\-");

        //Having found the best result for this round, try 2-opt
        //to untangle cross-overs pairs iteratively.
        //Don't want to change first & last places in the list, so care needed
        String[] bestPlacesList = monteCarloBest.substring(2, monteCarloBest.length()-2).split(",");
        int[] bestPlacesIndices = new int[bestPlacesList.length];
        for(int i=0; i<bestPlacesList.length; i++) {

            bestPlacesIndices[i] = Integer.parseInt(bestPlacesList[i]);
        }

        //repeated runs - so successful swaps can potentially trigger more improvements in other places
        for(int count=0; count < 100; count++) {
            for (int i = 1; i < bestPlacesIndices.length - 2; i++) {



                //Swap pairs & check the distance

                if (distMatrix[bestPlacesIndices[i]][bestPlacesIndices[(i + 1)]] < 100
                    || distMatrix[ bestPlacesIndices[(i+1)] ][ bestPlacesIndices[i] ] < 100
                        || distMatrix[ bestPlacesIndices[i-1] ][ bestPlacesIndices[i+1] ] < 100
                        || distMatrix[ bestPlacesIndices[i] ][ bestPlacesIndices[i+2] ] < 100
                ) {

                    continue;
                }
                else {

                    int temp = bestPlacesIndices[i];
                    bestPlacesIndices[i] = bestPlacesIndices[i + 1];
                    bestPlacesIndices[i + 1] = temp;

                    double newDist = GPSPoint.findIntArrayDistance(bestPlacesIndices, allGPSPoints);


                    if (newDist < bestDist && GPSPoint.getDistance(allGPSPoints[i], allGPSPoints[i + 1]) > 100) {
                        bestDist = (int) newDist;

                        String visitedPlacesStr = "";
                        for (int num : bestPlacesIndices) {
                            visitedPlacesStr += num + ",";
                        }

                        //remove extra comma
                        visitedPlacesStr = visitedPlacesStr.substring(0, visitedPlacesStr.length() - 1);


                        String fileString = (int) newDist + " {" + visitedPlacesStr + "},";

                        String[] newBestForSave = {
                                fileString
                        };

                        bestPlacesStr = visitedPlacesStr;

                        file.save("src/com/company/best.txt", newBestForSave);
                        System.out.println("NEW BEST---");
                        System.out.println(fileString);


                    } else {
                        //Swap back
                        temp = bestPlacesIndices[i];
                        bestPlacesIndices[i] = bestPlacesIndices[i + 1];
                        bestPlacesIndices[i + 1] = temp;
                    }


                }
            }
        }

        System.out.println("-/- 2OPT finished -\\-");


        //Check pairs of values, too
        System.out.println("-\\- 2OPT PAIRS start -/-");
//        System.out.println(bestPlacesStr.substring(3, bestPlacesStr.length()-2));
        String[] bestPlacesAll = bestPlacesStr.substring(1, bestPlacesStr.length()-2).split(",");
        int[] bestPlacesAllInt = new int[bestPlacesAll.length];
        for(int i=0; i<bestPlacesAll.length; i++) {

            bestPlacesAllInt[i] = Integer.parseInt(bestPlacesAll[i]);
        }

        //repeated runs - so successful swaps can potentially trigger more improvements in other places
        for(int count=0; count < 100; count++) {
            for (int i = 1; i < bestPlacesAllInt.length - 6; i++) {

                //i-1 // i i+1 | i+2 i+3 // i+4

                //Swap pairs & check the distance



                    int tempA = bestPlacesAllInt[i];
                    int tempB = bestPlacesAllInt[i+1];

                    bestPlacesAllInt[i] = bestPlacesAllInt[i + 2];
                    bestPlacesAllInt[i + 2] = tempA;

                    bestPlacesAllInt[i+1] = bestPlacesAllInt[i+3];
                    bestPlacesAllInt[i+3] = tempB;

                    if( distMatrix[bestPlacesAllInt[i-1]][bestPlacesAllInt[i]] < 100 ||
                        distMatrix[bestPlacesAllInt[i+1]][bestPlacesAllInt[i+2]] < 100 ||
                            distMatrix[bestPlacesAllInt[i+3]][bestPlacesAllInt[i+4]] < 100
                    ) {
                       //reversing.
                        tempA = bestPlacesAllInt[i];
                        tempB = bestPlacesAllInt[i+1];

                        bestPlacesAllInt[i] = bestPlacesAllInt[i + 2];
                        bestPlacesAllInt[i + 2] = tempA;

                        bestPlacesAllInt[i+1] = bestPlacesAllInt[i+3];
                        bestPlacesAllInt[i+3] = tempB;

                        continue;

                    }

                    double newDist = GPSPoint.findIntArrayDistance(bestPlacesAllInt, allGPSPoints);


                    if (newDist < bestDist && GPSPoint.getDistance(allGPSPoints[i], allGPSPoints[i + 1]) > 100) {
                        bestDist = (int) newDist;

                        String visitedPlacesStr = "";
                        for (int num : bestPlacesAllInt) {
                            visitedPlacesStr += num + ",";
                        }

                        //remove extra comma
                        visitedPlacesStr = visitedPlacesStr.substring(0, visitedPlacesStr.length() - 1);


                        String fileString = (int) newDist + " {" + visitedPlacesStr + "},";

                        String[] newBestForSave = {
                                fileString
                        };

                        bestPlacesStr = visitedPlacesStr;

                        file.save("src/com/company/best.txt", newBestForSave);
                        System.out.println("NEW BEST---");
                        System.out.println(fileString);


                    } else {

                        //reversing if distance is not better.
                        tempA = bestPlacesAllInt[i];
                        tempB = bestPlacesAllInt[i+1];

                        bestPlacesAllInt[i] = bestPlacesAllInt[i + 2];
                        bestPlacesAllInt[i + 2] = tempA;

                        bestPlacesAllInt[i+1] = bestPlacesAllInt[i+3];
                        bestPlacesAllInt[i+3] = tempB;
                    }


                }
            }

        System.out.println("-\\- 2OPT PAIRS finished -/-");

        }




        //try random swaps
//        for (int i = 1; i < bestPlacesIndices.length - 2; i++) {
//
//            int randSwapIndex = (int) Math.floor(Math.random() * ( bestPlacesIndices.length  ) );
//
//            while(randSwapIndex == 0 || randSwapIndex == bestPlacesIndices.length-1) {
//                randSwapIndex = (int) Math.floor(Math.random() * ( bestPlacesIndices.length ) );
//            }
//
//            int temp = bestPlacesIndices[i];
//            bestPlacesIndices[i] = bestPlacesIndices[randSwapIndex];
//            bestPlacesIndices[randSwapIndex] = temp;
//
//            double newDist = GPSPoint.findIntArrayDistance(bestPlacesIndices, allGPSPoints);
//
//
//            if (newDist < bestDist && GPSPoint.getDistance(allGPSPoints[i], allGPSPoints[ bestPlacesIndices[randSwapIndex] ]) > 100) {
//                bestDist = (int) newDist;
//
//                String visitedPlacesStr = "";
//                for (int num : bestPlacesIndices) {
//                    visitedPlacesStr += num + ",";
//                }
//
//                //remove extra comma
//                visitedPlacesStr = visitedPlacesStr.substring(0, visitedPlacesStr.length() - 1);
//
//
//                String fileString = (int) newDist + " {" + visitedPlacesStr + "},";
//
//                String[] newBestForSave = {
//                        fileString
//                };
//
//                bestPlacesStr = visitedPlacesStr;
//
//                file.save("src/com/company/best.txt", newBestForSave);
//                System.out.println("NEW BEST---");
//                System.out.println(fileString);
//
//
//            } else {
//                //Swap back
//                temp = bestPlacesIndices[i];
//                bestPlacesIndices[i] = bestPlacesIndices[randSwapIndex];
//                bestPlacesIndices[randSwapIndex] = temp;
//            }
//
//
//
//
//        }



//    }



}