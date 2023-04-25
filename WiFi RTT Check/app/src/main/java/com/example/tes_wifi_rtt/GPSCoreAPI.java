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
 * Sort routers based on RSSI
 *  -If the RSSI is equal then sort by distance
 * Store router X and Y position on object creation
 *  -Lookup table?
 * Gets and sets for all variables
 */

package com.example.tes_wifi_rtt;

/* Imports */
import java.lang.String;

/** \class GPSCoreAPI
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
    private Device device;      /*!< Object containg information for a single device to record on the GPS. */

    /* Sub-Classes */
    /** \class Router
     *  \brief This class contains data relevant to a single router.
     *
     *  Each router object stores relevant data for a single router
     *  that is needed for algorithms and interfaces. Methods for
     *  obtaining data are provided. The class is private to the core
     *  GPSCoreAPI clas in order to abstract information and processes.
     */
    private class Router {
        /* Public Variables */
        /* Private Variables */
        private int x;          /*!< X coordinate of router on cartesian grid. */
        private int y;          /*!< Y coordinate of router on cartesian grid.  */
        private int dist;       /*!< Distance from the router in millimeters(mm). */
        private String MACAddr; /*!< Human readable string of the MAC address of the router. */
        private int rssi;       /*!< Signal strength of the router; typical range: -55 to -90. */

        /* Constructor(s) */

        public Router() {
            this.x = 0;
            this.y = 0;
            this.dist = 0;
            this.MACAddr = null;
            this.rssi = 0;
        }

        public Router(int x, int y, int dist, String MACAddr, int rssi) {
            this.x = x;
            this.y = y;
            this.dist = dist;
            this.MACAddr = MACAddr;
            this.rssi = rssi;
        }

        /* Gets */

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getDist() {
            return this.dist;
        }

        public String getMACAddr() {
            return this.MACAddr;
        }

        public int getRSSI() {
            return this.rssi;
        }

        /* Sets */

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setDist(int dist) {
            this.dist = dist;
        }

        public void setMACAddr(String MACAddr) {
            this.MACAddr = MACAddr;
        }

        public void setRSSI(int rssi) {
            this.rssi = rssi;
        }

        /* Methods */

        /** \fn public boolean equals(Object o)
         *  \brief This function compares the input object to this
         *  object and outputs whether the objects are equal.
         *
         *  \param[in] o An Object to compare this object to
         *  \return Returns true if the objects are equal, else
         *  returns false.
         */
        public boolean equals(Object o) {
            return (this == o);
        }
    }

    /** \class Device
     *  \brief This class contains information relevant to a single device that
     *  will be used in the GPS.
     *
     *  Any relevant information that is needed by the GPS algorithms and interfaces
     *  is stored inside this class. It is designed for scalability to make adding
     *  additional devices to the GPS easier while abstracting out unecessary information.
     */
    private class Device {
        /* Public Variables */
        /* Private Variables */
        private int x;
        private int y;
        private Router routerList[];

        /* Constructor(s) */

        public Device() {
            this.x = 0;
            this.y = 0;
            this.routerList = null;
        }

        public Device(int x, int y, Router[] routerList) {
            this.x = x;
            this.y =y;
            this.routerList = routerList;
        }

        /* Gets */

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public Router[] getRouterList() {
            return this.routerList;
        }

        /* Sets */

        public void setX(int x) {
            this.x = x;
        }

        public void setY(int y) {
            this.y = y;
        }

        public void setRouterList(Router[] routerList) {
            this.routerList = routerList;
        }

        /* Methods */

        /** \fn public boolean equals(Object o)
         *  \brief This function compares the input object to this
         *  object and outputs whether the objects are equal.
         *
         *  \param[in] o An Object to compare this object to
         *  \return Returns true if the objects are equal, else returns
         *  false.
         */
        public boolean equals(Object o) {
            return (this == o);
        }
    }

    /* Constructor(s) */

    public GPSCoreAPI() { this.device = new Device(); }

    public GPSCoreAPI(int dist, String MACAddr, int rssi) {
        // Create a new array of routers with one element
        Router[] router = {new Router(0, 0, dist, MACAddr, rssi)};
        // Create new Device object with input router data
        this.device = new Device(0, 0, router);
    }

    /* Gets */

    public int[] getDevicePos() {
        int[] devicePos = {this.device.getX(), this.device.getY()};
        return devicePos;
    }

    public int[] getRouterPos(int index) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if router list is empty
        if (routerList != null) {
            // Check if input value is a valid array index
            if (index < routerList.length) {
                // Get indexed router's positional data
                int[] routerPos = {routerList[index].getX(), routerList[index].getY(), routerList[index].getDist()};
                // Return array containing positional data
                return routerPos;
            }
        }

        // Input array index is invalid or router list is empty so return null
        return null; // Exceptions?
    }

    public int[] getRouterPos(String MACAddr) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if router list is empty
        if (routerList != null) {
            // Create empty array to hold positional data
            int[] routerPos = null;
            // Iterate through the list of routers
            for (int i = 0; i < routerList.length; i++) {
                // Compare current router's MAC address to the input MAC address
                if (routerList[i].getMACAddr().equals(MACAddr)) {
                    // MAC address was found so create empty array
                    routerPos = new int[3];
                    // Store positional data from router into the array
                    routerPos[0] = routerList[i].getX();
                    routerPos[1] = routerList[i].getY();
                    routerPos[2] = routerList[i].getDist();
                    // Exit loop
                    break;
                }
            }

            return routerPos;
        }

        // Router list was empty so return null
        return null;
    }

    public int[][] getAllRouterPos() {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if the current router list is a valid list
        if (routerList != null) {
            // Create two dimensional array to store positional data for each listed router
            int[][] routerListPos = new int[3][routerList.length];
            // Iterate through the router list and store the positional data
            for (int i = 0; i < routerList.length; i++) {
                routerListPos[0][i] = routerList[i].getX();
                routerListPos[1][i] = routerList[i].getY();
                routerListPos[2][i] = routerList[i].getDist();
            }

            // Return the array with populated data
            return routerListPos;
        }

        // Router list was empty so return null
        return null; // Exceptions?
    }

    public int getRouterX(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getX();
            }
        }

        return 0;
    }

    public int getRouterY(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getY();
            }
        }

        return 0;
    }

    public int getRouterDist(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getDist();
            }
        }

        return 0;
    }

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

    public String getRouterMACAddr(int i) {
        Router[] routerList = this.device.getRouterList();
        if (routerList != null) {
            if (i < routerList.length) {
                return routerList[i].getMACAddr();
            }
        }

        return null;
    }

    public int getRouterRssi(String MACAddr) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Check if router list is empty
        if (routerList != null)
        {
            // Create empty array to hold positional data
            int routerRssi = 0;
            // Iterate through the list of routers
            for (int i = 0; i < routerList.length; i++) {
                // Compare current router's MAC address to the input MAC address
                if (routerList[i].getMACAddr().equals(MACAddr)) {
                    routerRssi = routerList[i].getRSSI();
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

    public void setDevicePos(int[] devicePos) {
        if (devicePos[0] > 0)
            this.device.setX(devicePos[0]);
        if (devicePos[1] > 0)
            this.device.setY(devicePos[1]);
    }

    public void setDevicePos(int x, int y) {
        if (x > 0)
            this.device.setX(x);
        if (y > 0)
            this.device.setY(y);
    }

    public void setRouterPos(int x, int y, int index) {
        // Get current list of routers
        Router[] routerList = this.device.getRouterList();
        // If input index is valid then set X and Y position
        if (index < routerList.length) {
            routerList[index].setX(x);
            routerList[index].setY(y);
        }
    }
    public void setRouterPos(int x, int y, String MACAddr) {
        // Get current router list
        Router[] routerList = this.device.getRouterList();

        // Find input MAC ID in list of routers
        for (int i = 0; i < routerList.length; i++) {
            // If MAC ID is found then set X and Y position
            if (routerList[i].getMACAddr().equals(MACAddr)) {
                routerList[i].setX(x);
                routerList[i].setY(x);
                break;
            }
        }
    }

    /* Methods */

    /** \fn void appendRouterList(int dist, String MACAddr, int rssi)
     *  \brief This function converts the input data into a router object
     *  and inserts it into the list of routers. When instered, the routers
     *  are sorted based on RSSI and then distance.
     *
     *  \param[in] dist An integer containing the distance in mm from the device
     *  \param[in] MACAddr String object containing the router's MAC ID
     *  \param[in] rssi An integer containing the signal strength of the router
     *  \return None
     */
    public void appendRouterList(int dist, String MACAddr, int rssi) {
        boolean isAdded = false;
        boolean isNew = true;
        // Get current router list
        Router[] routerList = this.device.getRouterList();
        // Create empty router list
        Router[] newRouterList = null;
        // Check if current router list is empty
        if (routerList != null) {
            // Iterate through router list to see if input MAC address already exists
            for (int i = 0; i < routerList.length; i++) {
                // If MAC address is found then delete current object
                if (routerList[i].getMACAddr().equals(MACAddr)) {
                    for(int k = i; k < routerList.length - 1; k++){
                        routerList[k] = routerList[k + 1];
                    }
                    routerList[routerList.length - 1] = null;
                    isNew = false;
                    break;
                }
            }
            // Create new router list with original size + 1
            if(isNew)
                newRouterList = new Router[routerList.length + 1];
            else
                newRouterList = new Router[routerList.length];

            // Copies over past data
            for (int i = 0; i < routerList.length; i++){
                newRouterList[i] = routerList[i];
            }
            newRouterList[newRouterList.length - 1] = new Router(0, 0, dist, MACAddr, rssi); // Adds to end of array

            for (int i = 0; i < newRouterList.length; i++) {
                for (int j = i; j < newRouterList.length - 1; j++) {
                    if (newRouterList[i].getRSSI() < newRouterList[i + 1].getRSSI()) {
                        Router temp = newRouterList[i];
                        newRouterList[i] = newRouterList[i + 1];
                        newRouterList[i + 1] = temp;
                    }
                    else if(newRouterList[i].getRSSI() == newRouterList[i + 1].getRSSI()){
                        if (newRouterList[i].getDist() < newRouterList[i + 1].getDist()) {
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
            newRouterList[0] = new Router(0, 0, dist, MACAddr, rssi);
        }
        this.device.setRouterList(newRouterList);
    }

    /** \fn boolean equals(Object o)
     *  \brief This function compares the input object to this
     *  object and outputs whether the objects are equal.
     *
     *  \param[in] o An Object to compare this object to
     *  \return Returns true if the objects are equal, else
     *  returns false.
     */
    public boolean equals(Object o) {
        return (this == o);
    }
}