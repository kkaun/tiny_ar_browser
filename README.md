## *Tiny AR Browser*

Simple AR Browser Android library written in Kotlin which represents single 
Camera activity with AR and fully customizable UI overlays. 

If you're working with some geolocation data and looking for additional/optional 
functionality to provide the projection environment for your app's content, 
you may check this one.

## Using it

Current version can be imported via JitPack:

Root `build.gradle`:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Local `build.gradle`:

	dependencies {
	        compile 'com.github.kkaun:tiny_ar_browser:master-SNAPSHOT'
	}

After the import all you have to do to make it work is:
- extending `ARActivity` class with your own standalone activity (no custom xml 
layouts or views should be attached to UI implementation; in most cases you can add 
custom elements programmatically if needed);
- transforming your data into collection of `ARMarker`s
or extending your app's data class from `ARMarker`.

## Features

Basically UI overlay has few additional features, such as:

- AR markers themselves: you can set bitmap icon and hide/edit text 
of each marker, set on click listeners etc;
- Radius change bar: changes radius of cached/requested data display;
- Radar: displays cached geo points on improvised radar overlay;
- Menu: manages visibility of UI elements if they're not disabled 
and closes activity if back press is not working standard way.

To change UI elements appearance call one or more listed methods listed below 
in Activity `onCreate()` method: 

| Method signature                                               | Description  |
|----------------------------------------------------------------|---|
| setMenuEnabled(enabled: Boolean)                               |   |
| setZoomProgress(progress: Int)                                 |   |
| setMaxZoom(max: Int)                                           |   |
| setMenuSwitchRadarTitle(title: String)                         |   |
| setMenuSwitchZoomBarTitle(title: String)                       |   |
| setMenuExitActivityTitle(title: String)                        |   |
| useRadar(use: Boolean)                                         |   |
| useZoombar(use: Boolean)                                       |   |
| showRadar(show: Boolean)                                       |   |
| showZoombar(show: Boolean)                                     |   |
| set<*>BodyRadius(radius: Int)                                  |   |
| set<*>BodyColor(alpha: Int, red: Int, green: Int, blue: Int)   |   |
| set<*>TextColor(red: Int, green: Int, blue: Int)               |   |
| set<*>LineColor(red: Int, green: Int, blue: Int)               |   |
