Simple AR Browser Android library written in Kotlin which represents single 
Camera activity with custom AR overlay. 

So if you're working with some geolocation data and looking for additional/optional 
functionality to provide the projection environment for your app's content, 
you may check this one.

*Import notes in progress*

After the import all you have to do to make it work is:
- extending your own activity from `ARActivity` class: it should be stand-alone
activity because no custom xml layouts or views can be attached to the lib)
- transforming your data into collection of `ARMarker`s
or extending your app's data class from `ARMarker`.