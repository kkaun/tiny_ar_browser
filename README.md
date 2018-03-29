# Tiny AR Browser

Simple AR Browser Android library written in Kotlin which represents single 
Camera activity with AR overlay. 

If you're working with some geolocation data and looking for additional/optional 
functionality to provide the projection environment for your app's content, 
you may check this one.

## Using it

Current version can be imported to your project via JitPack:

root `build.gradle`:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

local `build.gradle`:

	dependencies {
	        compile 'com.github.kkaun:tiny_ar_browser:master-SNAPSHOT'
	}

After the import all you have to do to make it work is:
- extending your own standalone activity from `ARActivity` class (no custom xml 
layouts or views should be attached to UI implementation; in most cases you can add 
custom elements programmatically if needed);
- transforming your data into collection of `ARMarker`s
or extending your app's data class from `ARMarker`.

## Features

In progress
