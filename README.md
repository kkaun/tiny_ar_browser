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
- extending `ARActivity` class with your own standalone activity: be aware of the fact that 
basic activity has no parent xml layouts; you can attach custom xml 
layouts or views to UI implementation though, but in most cases you could add 
custom elements programmatically if needed;
- transforming your data into collection of `ARMarker`s
or extending your app's data class from `ARMarker`.

## Features

Basically UI overlay has few additional features, such as:

- AR markers themselves: you can set bitmap icon and hide/edit text 
of each marker, set on click listeners etc;
- Radius change bar AKA Zoom bar: changes radius of cached/requested data display;
- Radar: displays cached geo points on improvised radar overlay;
- Menu: manages visibility of UI elements if they're not disabled 
and closes activity if back press is not working standard way.

To change UI elements appearance call one or more methods listed below 
inside overriden `onCreate()` or business methods: 

| Method signature                                               | Description | Default args / Boundaries |
|----------------------------------------------------------------|---|---|
| `setMenuEnabled(enabled: Boolean)`                             | Enable/disable menu button  | `true`  |
| `setZoomProgress(progress: Int)`                               | Set Zoom bar progress (in km) | `1-5`  |
| `setMaxZoom(max: Int)`                                         | Set Zoom bar's max value (in km) | `2-100`  |
| `setMenuSwitchRadarTitle(title: String)`                       | Set menu Radar item title  | Any `String` from your resources  |
| `setMenuSwitchZoomBarTitle(title: String)`                     | Set menu Zoom bar item title  | Any `String` from your resources  |
| `setMenuExitActivityTitle(title: String)`                      | Set menu Exit item title  | Any `String` from your resources  |
| `useRadar(use: Boolean)`                                       | Use Radar at all?  | `true`  |
| `useZoombar(use: Boolean)`                                     | Use Zoom bar at all?  | `true`  |
| `showRadar(show: Boolean)`                                     | Show Radar?  | `true`  |
| `showZoombar(show: Boolean)`                                   | Show Zoom bar?  | `true`  |
| `set<*>BodyRadius(radius: Int)`                                | Replace `<*>` with `Radar` or `MarkerText` to change body radius of corresponding element | `50-200`  |
| `set<*>BodyColor(alpha: Int, red: Int, green: Int, blue: Int)` | Replace `<*>` with `Radar` or `MarkerText` to change body color of corresponding element  | `0-255` for each (ARGB)  |
| `set<*>TextColor(red: Int, green: Int, blue: Int)`             | Replace `<*>` with `Radar` or `MarkerText` to change text color of corresponding element  | `0-255` for each (RGB)  |
| `set<*>LineColor(red: Int, green: Int, blue: Int)`             | Replace `<*>` with `Radar` or `MarkerText` to change lines color of corresponding element | `0-255` for each (RGB)  |
