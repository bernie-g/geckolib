![geckkolib](https://i.softwarelocker.net/geckolib.png)

Geckolib is an entity animation loader for Java Edition, which lets you export bedrock animations and play them automatically.

## Installation
[![curseforge](http://cf.way2muchnoise.eu/versions/geckolib.svg)](https://www.curseforge.com/minecraft/mc-mods/geckolib)

We use jitpack for publishing as it provides easy access to releases and allows for javadocs in the jars (unlike curseforge).

For 1.15.2:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation fg.deobf('com.github.bernie-g:geckolib:1.15-1.0.2')
}
```

For 1.15.2 Fabric:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation fg.deobf('com.github.bernie-g:geckolib:fabric-1.15-1.0.0')
}
```

For 1.12.2:
```gradle
minecraft {
    useDepAts = true
}

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.bernie-g:geckolib:1.0.0-1.12'
}
```
## Documentation
GeckoLib provides detailed documentation in the form of a [wiki](https://github.com/bernie-g/geckolib/wiki) and javadocs. GeckoLib is currently available for 1.12 and 1.15.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.



## License
[GPL 3.0](https://choosealicense.com/licenses/gpl-3.0/)
