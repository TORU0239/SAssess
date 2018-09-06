# SAssess
Assignment to SMOVE.  
Implementation of simple application with two APIs provided from SMOVE.  

Author: Toru Wonyoung Choi

## Basic Information for project
1. Implemented with Kotlin fully
2. Minimum SDK version is 21 (Lollipop)

### Used Framework or Library  
1. MVP Pattern (But for simple view, just used boilerplate code)
2. ConstraintLayout
3. Retrofit
4. GSON
5. Stetho

### Checking Booking Availability and Booking a car
1. When launching app, app detects user's current location.
2. If an user is out of Singapore (like author), current location is set to Marina Bay Sands automatically.
3. With booking API, pin all locations on Google Map, based on date users want.
4. Users can take date they want from picker.
5. When clicking each pin, app shows availability and drop-off point per each pin.
6. When clicking Drop Off Point, app calls and initializes another activity with information from user.
7. Users can choose Pick-Up Point, Drop-Off Point, and Date, and send a request to book.
8. Because not given any API for booking, request is a kind of dummy.
