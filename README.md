# TheFloowChallenge

This simple app follows the guidelines given for the said technical challenge.

There are two main components:

1. MainActivity
Is where all the action comes into place. 
It acts as the controller of the layout and thus initializes the map and keeps track of button press actions, current journey and drawing.

2. RecyclerView
The list of the journeys is guided by the adapter for the displacement of the list, and then by the MainActivity via the implementation of the interface MapActivity.

The usage of the FusedLocationProviderClient provides a light and precise tracking that stays safe even when the app is on background.
