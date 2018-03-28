Simple AR Browser Android library written in Kotlin which represents single 
Camera activity with custom AR overlay.



After the import all you need to do to make it work is:
- extending your own activity from `ARActivity`
- transforming your data into collection of `ARMarker`
or extend your app's data class from `ARMarker`.