# SAssess
Assignment to SMOVE.  
Implementation of simple application with two APIs provided from SMOVE.  

Author: Toru Wonyoung Choi

## Basic Information for project
1. Implemented with Kotlin
2. Minimum SDK version is 21 (Lollipop)

### Used Framework or Library  
1. Android Architecture Component
2. DataBinding
3. Retrofit
4. GSON
5. Stetho

### Checking Booking Availability and Booking a car
1. When launching app, app detects user's current location.
2. If an user is out of Singapore (like author), current location is set to City Hall automatically.
3. With booking API, pin all locations on Google Map, based on date users want.
4. Users can take date they want from picker.
5. When clicking each pin, app shows availability and drop-off point per each pin in another activity.
6. Drop-Off point must be sorted in order of distance from user. 
7. In order to sort Latitude and Longitude information, app needs to calculate distance between point and user, by specific formula.
8. TBD about obtaining address of drop-off poin using geocode api of Google.
9. Users can choose Pick-Up Point, Drop-Off Point, and Date, and send a request to book.
10. Because not given any API for booking, request is a kind of dummy.
