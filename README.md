[![](https://jitpack.io/v/germainkevinbusiness/CollapsingTopBarCompose.svg)](https://jitpack.io/#germainkevinbusiness/CollapsingTopBarCompose)
# CollapsingTopBarCompose
A top bar that expands or collapses based on the scrolling of a content
<img src="https://user-images.githubusercontent.com/83923717/170029697-b6f0c0b2-bb1d-4fce-ba96-677620227753.gif" width="50%" height="auto"/>
<img src="https://user-images.githubusercontent.com/83923717/170036886-f340d845-b5f8-475d-93ea-709652aa6ad6.gif" width="50%" height="auto"/>


# How to get this library in your android app

**Step 1.** Add the jitpack repository to the ``repositories { }``  function, inside
your ``project build.gradle`` or your ``settings.gradle`` like so:

```groovy
repositories {
    google()
    mavenCentral()
    // Place the jitpack repository inside this, like so:
    maven { url 'https://jitpack.io' }
}
```

**Step 2.** Add the dependency in your ``` module build.gradle ``` file, like so:

```groovy
dependencies {
    implementation 'com.github.germainkevinbusiness:CollapsingTopBarCompose:1.0.0-alpha05'
}
```

**That's it!**

## License

Licenced under the MIT Licence

```
Copyright (c) 2022 Kevin Germain

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would
like to change.
