/** \file GPSCoreAPI.java
 *  \brief This file contains all methods and sub-classes for interfacing
 *  with the GPS algorithms
 *
 *  This file contains the core structure of the GPS API. All relevant
 *  methods, sub-classes, and data structures are contained in the main
 *  GPSCoreAPI class. Methods for converting data between the separate
 *  algorithms are also contained in this file. All separate algorithms
 *  and APIs must interface directly with the main class in this file
 *  for getting, setting, converting or sending information between
 *  interfaces.
 *
 *  \author Brenden Newman
 *  \date 18 Apr 2023
 */

/** TODO:
 * Error check necessary variables.
 * Exceptions?
 */

package com.example.tes_wifi_rtt;

/* Imports */
import java.lang.String;
import java.util.HashMap;
import java.util.Arrays;
import android.widget.*;
import android.view.*;
import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;


/**
 *  \brief This class contains methods and sub-classes for general GPS
 *  data storage and conversion.
 *
 *  Any important data structures are stored inside this class. Methods
 *  for obtaining, setting, and converting data for going between APIs
 *  and algorithms are contained in this class. Communication methods
 *  are also contained inside this class for interfacing with outside
 *  interfaces.
 */
public class GPSCoreAPI {
    /* Public Variables */
    /* Private Variables */
    private final Device device;                        /*!< Object containing information for a single device to record on the GPS. */
    private final HashMap<String, float[]>lookupTable;    /*!< HashMap containing a list of MAC Addresses and their corresponding X and Y coordinates */

    /* Sub-Classes */
    /**
     *  \brief This class contains data relevant to a single router.
     *
     *  Each router object stores relevant data for a single router
     *  that is needed for algorithms and interfaces. Methods for
     *  obtaining data are provided. The class is private to the core
     *  GPSCoreAPI clas in order to abstract information and processes.
     */
    private static class Router {
        /* Public Variables */
        /* Private Variables */
        private float x;          /*!< X coordinate of router on cartesian grid. */
        private float y;          /*!< Y coordinate of router on cartesian grid.  */
        private float[] dist = new float[10];     /*!< Distance from the router in meters(m). */
        private String MACAddr; /*!< Human readable string of the MAC address of the router. */
        private int rssi;       /*!< Signal strength of the router; typical range: -55 to -90. */

        /* Constructor(s) */

        /**
         *  \brief Empty constructor. Initializes all variables as null.
         *
         *  \param None.
         */
        public Router() {
            this.x = 0;
            this.y = 0;
            this.dist = new float[10];
            this.MACAddr = null;
            this.rssi = 0;
        }

        /**
         *  \brief Constructor for the class Router. Initializes all variables with the input parameters.
         *
         *  \param x A floating integer containing the X position of the router on a cartesian grid.
         *  \param y A floating integer containing the Y position of the router ona cartesian grid.
         *  \param dist A floating integer containing the distance in meters(m) from a device.
         *  \param MACAddr A String object of the router's MAC Address.
         *  \param rssi An integer containing the signal strength of the router to a device.
         */
        public Router(float x, float y, float dist, String MACAddr, int rssi) {
            this.x = x;
            this.y = y;
            for (int i = this.dist.length - 1; i > 0; i--) {
                this.dist[i] = this.dist[i - 1];
            }
            this.dist[0] = dist/1000f;
            this.MACAddr = MACAddr;
            this.rssi = rssi;
        }

        /* Gets */

        /**
        *  \brief Gets the current X position on a cartesian grid of the router.
        *
        *  \param None.
        *  \return A floating integer representing the X position on a cartesian grid.
        */
        public float getX() {
            return this.x;
        }

        /**
        *  \brief Gets the current Y position on a cartesian grid of the router.
        *
        *  \param None.
        *  \return A floating integer representing the Y position on a cartesian grid.
        */
        public float getY() {
            return this.y;
        }

        /**
        *  \brief Gets the current distance in meters(m) from a device.
        *
        *  \param None.
        *  \return A floating integer representing the distance from the parent device in meters(m).
        */
        public float getDist() {
            return this.dist[0];
        }

        /**
         *  \brief Gets the router's current MAC Address.
         *
         *  \param None.
         *  \return A String object containing the MAC Address of the router.
         */
        public String getMACAddr() {
            return this.MACAddr;
        }

        /**
         *  \brief Gets the current signal strength to the parent device. Represented as a negative
         *         number.
         *
         *  \param None.
         *  \return An integer representing the signal strength to the parent device.
         */
        public int getRSSI() {
            return this.rssi;
        }

        /* Sets */

        /**
         *  \brief Sets the router's X position on a cartesian grid.
         *
         *  \param x A floating integer containing the X position on a cartesian grid.
         *  \return None.
         */
        public void setX(float x) {
            this.x = x;
        }

        /**
         *  \brief Sets the router's Y position on a cartesian grid.
         *
         *  \param y A floating integer containing the Y position on a cartesian grid.
         *  \return None.
         */
        public void setY(float y) {
            this.y = y;
        }

        /**
         *  \brief Sets the router's distance from the parent device in meters(m).
         *
         *  \param dist A floating integer containing the distance in meters(m) from the parent device.
         *  \return None.
         */
        public void setDist(float dist) {
            for (int i = this.dist.length - 1; i > 0; i--) {
                this.dist[i] = this.dist[i - 1];
            }
            this.dist[0] = dist/1000f;
        }

        /**
         *  \brief Sets the router's MAC Address.
         *
         *  \param MACAddr A String object containing the MAC Address.
         *  \return None.
         */
        public void setMACAddr(String MACAddr) {
            this.MACAddr = MACAddr;
        }

        /**
         *  \brief Sets the router's signal strength from the parent device. Input should always be
         *         a number below, or equal to zero, i.e. negative.
         *
         *  \param rssi An integer containing the signal strength from the parent device.
         *  \return None.
         */
        public void setRSSI(int rssi) {
            this.rssi = rssi;
        }

        /* Methods */

        /**
         *  \brief This function compares the input object to this
         *  object and outputs whether the objects are equal.
         *
         *  \param o An Object to compare this object to
         *  \return Returns true if the objects or MAC Addresses are equal.
         */
        public boolean equals(Object o) {
            if (this == o)
                return true;
            else if (o instanceof Router) {
                Router temp = (Router)o;
                return (this.getMACAddr().equals(temp.getMACAddr()));
            }
            return false;
        }
    }

    /**
     *  \brief This class contains information relevant to a single device that
     *  will be used in the GPS.
     *
     *  Any relevant information that is needed by the GPS algorithms and interfaces
     *  is stored inside this class. It is designed for scalability to make adding
     *  additional devices to the GPS easier while abstracting out unnecessary information.
     */
    private static class Device {
        /* Public Variables */
        /* Private Variables */
        private float[] x = new float[10];                  /*!< The X position on a cartesian grid. */
        private float[] y = new float[10];                  /*!< The Y position on a cartesian grid. */
        private Router[] routerList;    /*!< Contains a list of routers used for triangulating the parent devices position. Sorted by RSSI then distance. */

        /* Constructor(s) */

        /**
         *  \brief Empty constructor. Initializes all variables to null.
         *
         *  \param None.
         */
        public Device() {
            this.x = new float[10];
            this.y = new float[10];
            this.routerList = null;
        }

        /**
         *  \brief Constructor for the class Device. Initializes all variables with the input data.
         *
         *  \param x A floating integer containing the X position on a cartesian grid.
         *  \param y A floating integer containing the Y position on a cartesian grid.
         *  \param routerList A list of routers used for triangulating the parent devices position.
         */
        public Device(float x, float y, Router[] routerList) {
            for (int i = this.x.length - 1; i > 0; i--) {
                this.x[i] = this.x[i - 1];
            }
            this.x[0] = x;
            for (int i = this.y.length - 1; i > 0; i--) {
                this.y[i] = this.y[i - 1];
            }
            this.y[0] = y;
            this.routerList = routerList;
        }

        /* Gets */

        /**
         * \brief Gets the devices current X position on the map.
         *
         * \param None
         * \return An floating integer containing the X position of the device.
         */
        public float getX() {
            return this.x[0];
        }

        /**
         * \brief Gets the current Y position of the device.
         *
         * \param None.
         * \return A floating integer containing the Y position of the device.
         */
        public float getY() {
            return this.y[0];
        }

        /**
         * \brief Gets the current list of routers the device is communicating with.
         *
         * \param None.
         * \return An array of Router objects containing data from RTT communications.
         */
        public Router[] getRouterList() {
            return this.routerList;
        }

        /* Sets */

        /**
         * \brief Sets the devices current X position to be used on the map.
         *
         * \param x A floating integer containing the X position to be stored.
         * \return None.
         */
        public void setX(float x) {
            for (int i = this.x.length - 1; i > 0; i--) {
                this.x[i] = this.x[i - 1];
            }
            this.x[0] = x;
        }

        /**
         * \brief Sets the devices current Y position to be used on the map.
         *
         * \param y A floating integer containing the Y position to be stored.
         * \return None.
         */
        public void setY(float y) {
            for (int i = this.y.length - 1; i > 0; i--) {
                this.y[i] = this.y[i - 1];
            }
            this.y[0] = y;
        }

        /**
         * \brief Sets the list of routers to a new list to be used for triangulation.
         *
         * \param routerList An array of Router objects to be stored as the new list.
         * \return None.
         */
        public void setRouterList(Router[] routerList) {
            this.routerList = routerList;
        }

        /* Methods */

        /**
         *  \brief This function compares the input object to this
         *  object and outputs whether the objects are equal.
         *
         *  \param o An Object to compare this object to
         *  \return Returns true if the objects are equal, else returns
         *  false.
         */
        public boolean equals(Object o) {
            if (this == o)
                return true;
            else if (o instanceof Device) {
                Device temp = (Device)o;
                return ((this.getX() == temp.getX()) & (this.getY() == temp.getY()) & (Arrays.equals(this.getRouterList(), temp.getRouterList())));
            }
            return false;
        }
    }

    /* Constructor(s) */

    /**
     * \brief Empty constructor. Initializes all variables with an empty object. Formats the
     *        router lookup table by MAC Address for obtaining a pre-set position.
     *
     * \param None.
     */
    public GPSCoreAPI() {
        this.device = new Device();
        this.lookupTable = new HashMap<>();
        lookupTable.put("70:3a:cb:6e:ce:85", new float[] {17.78f, 18.22f});
        lookupTable.put("70:3a:cb:29:4b:3a", new float[] {10.16f, 18.22f});
        lookupTable.put("d8:6c:63:d6:5f:aa", new float[] {17.78f, 24.30f});
    }

    /**
     * \brief Constructor for the GPSCoreAPI class. Initializes all variables with the input data
     *        and creates a list of routers with one object. Formats the router lookup table by
     *        MAC Address for obtaining a pre-set position.
     *
     * \param dist A floating integer containing the distance between a router and device.
     * \param MACAddr A String object containing the MAC address of the router.
     * \rssi An integer containing the signal strength of the router.
     */
    public GPSCoreAPI(float dist, String MACAddr, int rssi) {
        // Create a new array of routers with one element
        Router[] router = {new Router(0, 0, dist, MACAddr, rssi)};
        // Create new Device object with input router data
        this.device = new Device(0, 0, router);
        this.lookupTable = new HashMap<>();
        lookupTable.put("70:3a:cb:6e:ce:85", new float[] {17.78f, 18.22f});
        lookupTable.put("70:3a:cb:29:4b:3a", new float[] {10.16f, 18.22f});
        lookupTable.put("d8:6c:63:d6:5f:aa", new float[] {17.78f, 24.30f});
    }

    /* Gets */

    /**
     * \brief Gets the devices position and returns it as an floating integer array. Index 0 contains the X
     *        position and index 1 contains the Y position.
     *
     * \param None.
     * \return A floating integer array containing the positional data of the device.
     */
    public float[] getDevicePos() {
        return new float[] {this.device.getX(), this.device.getY()};
    }

    /**
     * \brief Gets a specific routers positional data using an input index of the array list.
     *        Index 0 of the array contains the distance, index 1 contains the X position, and
     *        index 2 contains the Y position.
     *
     * \param index An integer containing the index of the router in the router array list.
     * \return A floating integer array containing the positional data of the router.
     */
    public float[] getRouterPos(int index) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if router list is empty
        if (routerList != null) {
            // Check if input value is a valid array index
            if (index < routerList.length) {
                // Return array containing positional data
                return new float[] {routerList[index].getDist(), routerList[index].getX(), routerList[index].getY()};
            }
        }

        // Input array index is invalid or router list is empty so return null
        return null; // Exceptions?
    }

    /**
     * \brief Gets a specific routers positional data using an input MAC Address. It searches the
     *        router list for the MAC Address and returns the positional data in the following
     *        format: {Distance, X, Y}. Returns null if the MAC Address was not found in the list.
     *
     * \param MACAddr A String object containing the MAC Address of the router to search for.
     * \return A floating integer array containing the positional data of the specified router.
     */
    public float[] getRouterPos(String MACAddr) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if router list is empty
        if (routerList != null) {
            // Create empty array to hold positional data
            float[] routerPos = null;
            // Iterate through the list of routers
            for (Router router : routerList) {
                // Compare current router's MAC address to the input MAC address
                if (router.getMACAddr().equals(MACAddr)) {
                    // MAC address was found so create empty array
                    routerPos = new float[3];
                    // Store positional data from router into the array
                    routerPos[0] = router.getDist();
                    routerPos[1] = router.getY();
                    routerPos[2] = router.getX();
                    // Exit loop
                    break;
                }
            }

            return routerPos;
        }

        // Router list was empty so return null
        return null;
    }

    /**
     * \brief Gets the positional data of all routers in the list. Returns the data as a double
     *        array using the following format: {Distance, X, Y}{Router Index}.
     *
     * \param None.
     * \return A double sized array containing the positional data of every router in the list.
     */
    public double[][] getAllRouterPos() {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if the current router list is a valid list
        if (routerList != null) {
            // Create two dimensional array to store positional data for each listed router
            double[][] routerListPos = new double[3][routerList.length];
            // Iterate through the router list and store the positional data
            for (int i = 0; i < routerList.length; i++) {
                routerListPos[i][0] = routerList[i].getDist();
                routerListPos[i][1] = routerList[i].getX();
                routerListPos[i][2] = routerList[i].getY();
            }

            // Return the array with populated data
            return routerListPos;
        }

        // Router list was empty so return null
        return null; // Exceptions?
    }

    /**
     * \brief Gets a specific routers X position using an index value in the router array list.
     *
     * \param i An integer containing the index of the router in the array list.
     * \return A floating integer containing the specified routers X position.
     */
    public float getRouterX(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getX();
            }
        }

        return 0;
    }

    /**
     * \brief Gets a specific routers Y position using an index value in the router array list.
     *
     * \param i An integer containing the index of the router in the array list.
     * \return A floating integer containing the specified routers Y position.
     */
    public float getRouterY(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getY();
            }
        }

        return 0;
    }

    /**
     * \brief Gets a specific routers distance using an index value in the router array list.
     *
     * \param i An integer containing the index of the router in the array list.
     * \return A floating integer containing the specified routers distance.
     */
    public float getRouterDist(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getDist();
            }
        }

        return 0;
    }

    /**
     * \brief Gets a specific routers signal strength using an index value in the router array list.
     *
     * \param i An integer containing the index of the router in the array list.
     * \return An integer containing the specified routers signal strength.
     */
    public int getRouterRssi(int i) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if router list is empty
        if (routerList != null)
        {
            // Check if input value is a valid array index
            if (i < routerList.length) {
                return routerList[i].getRSSI();
            }
        }

        return 0;
    }

    /**
     * \brief Gets a specific routers MAC Address using an index value in the router array list.
     *
     * \param i An integer containing the index of the router in the array list.
     * \return A String object containing the specified routers MAC Address.
     */
    public String getRouterMACAddr(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getMACAddr();
            }
        }

        return null;
    }

    /**
     * \brief Gets a specific routers signal strength using a MAC Address.
     *
     * \param MACAddr A String object containing the MAC Address of the router to search for.
     * \return An integer containing the signal strength of the specified router.
     */
    public int getRouterRssi(String MACAddr) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if router list is empty
        if (routerList != null)
        {
            // Create empty array to hold positional data
            int routerRssi = 0;
            // Iterate through the list of routers
            for (Router router : routerList) {
                // Compare current router's MAC address to the input MAC address
                if (router.getMACAddr().equals(MACAddr)) {
                    routerRssi = router.getRSSI();
                    // Exit loop
                    break;
                }
            }

            return routerRssi;
        }

        // Router list was empty so return null
        return 0;
    }

    /* Sets */

    /**
     * \brief Sets the devices positional data using the given inputs. Inputs should follow the
     *        given format: {X, Y}.
     *
     * \param devicePos An integer array containing the positional data to store.
     * \return None.
     */
    public void setDevicePos(int[] devicePos) {
        if (devicePos[0] > 0)
            this.device.setX(devicePos[0]);
        if (devicePos[1] > 0)
            this.device.setY(devicePos[1]);
    }

    /**
     * \brief Sets the devices positional data using the given inputs.
     *
     * \param x A floating integer containing the X position to store.
     * \param y A floating integer containing the Y position to store.
     * \return None.
     */
    public void setDevicePos(float x, float y) {
        if (x > 0)
            this.device.setX(x);
        if (y > 0)
            this.device.setY(y);
    }

    /**
     * \brief Sets a specific router's positional data using an index in the router array list.
     *
     * \param x A floating integer containing the X position to store.
     * \param y A floating integer containing the Y position to store.
     * \param index An integer containing the index of the router to store the data to in the array list.
     * \return None.
     */
    public void setRouterPos(float x, float y, int index) {
        // Get current list of routers
        Router[] routerList = this.device.getRouterList();
        // If input index is valid then set X and Y position
        if (index < routerList.length) {
            routerList[index].setX(x);
            routerList[index].setY(y);
        }
    }

    /**
     * \brief Sets a specific routes positional data using the MAC Address of the router.
     *
     * \param x A floating integer containing the X position to store.
     * \param y A floating integer containing the Y position to store.
     * \param MACAddr A String object containing the MAC Address of the router to store to.
     * \return None.
     */
    public void setRouterPos(float x, float y, String MACAddr) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();

        // Find input MAC ID in list of routers
        for (Router router : routerList) {
            // If MAC ID is found then set X and Y position
            if (router.getMACAddr().equals(MACAddr)) {
                router.setX(x);
                router.setY(y);
                break;
            }
        }
    }

    /* Methods */

    /**
     *  \brief This function converts the input data into a router object
     *  and inserts it into the list of routers. When inserted, the routers
     *  are sorted based on RSSI and then distance.
     *
     *  \param dist An integer containing the distance in mm from the device.
     *  \param MACAddr String object containing the router's MAC ID.
     *  \param rssi An integer containing the signal strength of the router.
     *  \return None.
     */
    public void appendRouterList(float dist, String MACAddr, int rssi, Context context) {
        boolean isNew = true;
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Create empty router list
        Router[] newRouterList;
        // Check if current router list is empty
        if (routerList != null) {
            // Iterate through router list to see if input MAC address already exists
            for (int i = 0; i < routerList.length; i++) {
                // If MAC address is found then delete current object and shift remaining objects
                if (routerList[i].getMACAddr().equals(MACAddr)) {
                    for(int k = i; k < routerList.length - 1; k++){
                        routerList[k] = routerList[k + 1];
                    }
                    routerList[routerList.length - 1] = null;
                    isNew = false;
                    break;
                }
            }
            // If a new object is being inserted then create new router list with size + 1
            if(isNew) {
                newRouterList = new Router[routerList.length + 1];

            }
            // Else current object is just being updated so make new list of the same size
            else {
                newRouterList = new Router[routerList.length];
            }

            // Copies over past data
            System.arraycopy(routerList, 0, newRouterList, 0, routerList.length);

            // Search for the input MAC Address in the lookup table
            float[] routerPos = lookupTable.get(MACAddr);
            // If the MAC Address was found in the lookup table then create object with the defined positional data
            if (routerPos != null)
                newRouterList[newRouterList.length - 1] = new Router(routerPos[0], routerPos[1], dist, MACAddr, rssi);
            // Else MAC Address was not found in the lookup table so create object with no positional data
            else
                newRouterList[newRouterList.length - 1] = new Router(0, 0, dist, MACAddr, rssi);

            // Shift the router objects to sort the list by RSSI then by distance
            for (int i = 0; i < newRouterList.length; i++) {
                for (int j = i; j < newRouterList.length - 1; j++) {
                    if (newRouterList[i].getRSSI() < newRouterList[i + 1].getRSSI()) {
                        Router temp = newRouterList[i];
                        newRouterList[i] = newRouterList[i + 1];
                        newRouterList[i + 1] = temp;
                    }
                    else if(newRouterList[i].getRSSI() == newRouterList[i + 1].getRSSI()){
                        if (newRouterList[i].getDist() > newRouterList[i + 1].getDist()) {
                            Router temp = newRouterList[i];
                            newRouterList[i] = newRouterList[i + 1];
                            newRouterList[i + 1] = temp;
                        }
                    }
                }
            }
        } else {
            // Router list is empty so create a new list
            newRouterList = new Router[1];

            // Search for the input MAC Address in the lookup table
            float[] routerPos = lookupTable.get(MACAddr);
            // If the MAC Address was found in the lookup table then create object with the defined positional data
            if (routerPos != null)
                newRouterList[0] = new Router(routerPos[0], routerPos[1], dist, MACAddr, rssi);
            // Else MAC Address was not found in the lookup table so create object with no positional data
            else
                newRouterList[0] = new Router(0, 0, dist, MACAddr, rssi);
        }
        this.device.setRouterList(newRouterList);
    }




    /**
     *  \brief This function compares the input object to this object and outputs whether the
     *         objects are equal.
     *
     *  \param o An Object to compare this object to.
     *  \return Returns true if the objects are equal.
     */
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (o instanceof GPSCoreAPI) {
            GPSCoreAPI temp = (GPSCoreAPI)o;
            return (this.device == temp.device);
        }
        return false;
    }


    // Format for variables passed to method (distance, xVal, yVal)
    public double[] calculatePosition() {
        double[][] testVal = getAllRouterPos();

        double tempA, tempB, tempC, tempD, tempE, tempF, outX = 0, outY = 0;
        double[] output = {0, 0};
        if(testVal != null) {
            // Check if there are 3 routers
            if (testVal.length < 3) {
                return output;
            }
            // Evaluation for temporary variables using the first and second equations
            // A = -2*x1 + 2*x2
            tempA = (-2*testVal[0][1])+(2*testVal[1][1]);
            // B = -2*y1 + 2*y2
            tempB = (-2*testVal[0][2])+(2*testVal[1][2]);
            // C = (r1)^2 - (r2)^2 - (x1)^2 + (x2)^2 - (y1)^2 + (y2)^2
            tempC = (testVal[0][0]*testVal[0][0])-(testVal[1][0]*testVal[1][0])-(testVal[0][1]*testVal[0][1])+(testVal[1][1]*testVal[1][1])-(testVal[0][2]*testVal[0][2])+(testVal[1][2]*testVal[1][2]);
            // Evaluation for temporary variables using the second and third equations
            // D = -2*x2 + 2*x3
            tempD = (-2*testVal[1][1])+(2*testVal[2][1]);
            // E = -2*y2 + 2*y3
            tempE = (-2*testVal[1][2])+(2*testVal[2][2]);
            // F = (r2)^2 - (r3)^2 - (x2)^2 + (x3)^2 - (y2)^2 + (y3)^2
            tempF = (testVal[1][0]*testVal[1][0])-(testVal[2][0]*testVal[2][0])-(testVal[1][1]*testVal[1][1])+(testVal[2][1]*testVal[2][1])-(testVal[1][2]*testVal[1][2])+(testVal[2][2]*testVal[2][2]);
            // If denominator does not equal 0, output can be calculated
            if (!((tempA == 0 || tempE == 0) && (tempB == 0 || tempD == 0)) && ((tempA*tempE) != (tempB*tempD))) {
                // Evaluation temporary variables to find 2-D coordinates
                // X = (CE-FB)/(EA-BD)
                outX = ((tempC*tempE)-(tempF*tempB))/((tempE*tempA)-(tempB*tempD));
                // Y = (CD-AF)/(BD-AE)
                outY = ((tempC*tempD)-(tempA*tempF))/((tempB*tempD)-(tempA*tempE));
            }
            output[0] = outX;
            output[1] = outY;
        }

        return output;
    }
}