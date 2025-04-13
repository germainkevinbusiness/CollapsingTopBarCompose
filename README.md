[![](https://jitpack.io/v/germainkevinbusiness/CollapsingTopBarCompose.svg)](https://jitpack.io/#germainkevinbusiness/CollapsingTopBarCompose)

# CollapsingTopBarCompose

A Jetpack Compose TopBar that collapses or expands based on detected scroll events or by calling:
```CollapsingTopBarScrollBehavior.collapse()``` or ```CollapsingTopBarScrollBehavior.expand()```
extension methods.

<table>
  <tr>
    <td>Collapse/Expand due to user scroll and/or by calling the methods: collapse() & expand()</td>
    <td>Collapse/Expand due to user scroll while displaying only the title on the left side</td>
    <td>Expand only when a user has reached the top of a LazyColumn</td>
  </tr>
  <tr>
    <td vertical-align="top"><img src="https://user-images.githubusercontent.com/83923717/196091667-c8a48a32-aa66-4e71-afc7-7bbd247d5ee3.gif" alt="Demonstration 1" width="100%" height="auto"/></td>
    <td vertical-align="top"><img src="https://user-images.githubusercontent.com/83923717/170043487-5e78724b-bd66-4617-b703-624281d49c2a.gif" alt="Demonstration 2" width="100%" height="auto"/></td>
     <td vertical-align="top"><img src="https://user-images.githubusercontent.com/83923717/196796410-ba5ccac7-1e25-4222-aad5-7aa149014a85.gif" alt="Demonstration 3" width="100%" height="auto"/></td>
  </tr>
 </table>

# How to add this library to your android app with a 'libs.versions.toml' file in 3 steps
**Step 1.** Add the jitpack repository to the ``repositories { }``  function, inside 
your ``settings.gradle.kts`` like so:
```kotlin
    repositories { 
        google()
        mavenCentral()
        // Place the jitpack repository inside this, like so:
        maven(url = "https://jitpack.io")
    }
```

**Step 2.** Go to your ``` libs.versions.toml ``` file, and add:
```toml
# In here add this
[versions]
collapsingTopBarCompose = "1.2.6"

# In here add this
[libraries]
collapsing-top-bar-compose = { module = "com.github.germainkevinbusiness:CollapsingTopBarCompose", version.ref = "collapsingTopBarCompose" }
```

**Step 3.** Add the dependency in your ``` module build.gradle.kts ``` file, like so:
```kotlin
dependencies {
    implementation(libs.collapsing.top.bar.compose)
}
```

# How to add this library to your android app (with gradle files in Groovy language & without a 'libs.versions.toml' file) in 2 steps
**Step 1.** Add the jitpack repository to the ``repositories { }``  function, inside
your ``project build.gradle`` or inside your ``settings.gradle`` like so:

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
    implementation "com.github.germainkevinbusiness:CollapsingTopBarCompose:1.2.6"
}
```

# How to use this library

1 - In order to use a ```CollapsingTopBar```, you first need to create a ```CollapsingTopBarScrollBehavior```.

```kotlin
val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
    isAlwaysCollapsed = false,
    isExpandedWhenFirstDisplayed = true,
    centeredTitleWhenCollapsed = false,
    centeredTitleAndSubtitle = true,
    collapsedTopBarHeight = 56.dp,
    expandedTopBarMaxHeight = 156.dp,
    scrollableState = null
)
```

2-  In order for the ```CollapsingTopBar``` to collapse or expand when a vertical scrolling is occuring inside your Layout, you need to add the ```scrollBehavior.nestedScrollConnection``` inside your Layout's ```Modifier.nestedScroll``` :

```kotlin
 Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CollapsingTopBar(
                scrollBehavior = scrollBehavior,
                title = { Text(text = "Contacts") },
                subtitle = { Text(text = "17 contacts") },
                mainAction = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = "Main Action Icon",
                        )
                    }
                },
            )
        },
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {}
        
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {}
    }
```

So a complete example could look like:

```kotlin

Column(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
    CollapsingTopBar(
        scrollBehavior = scrollBehavior,
        title = { TitleText },
        expandedTitle = { ExpandedTitleText },
        subtitle = { SubtitleText },
        navigationIcon = { NavigationIcon },
        mainAction = { MainActionIconButton },
        actions = { MoreMenuIcons },
    )
    LazyColumn {
        items(contactsList) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { },
            ) {
                Text( modifier = Modifier.padding(16.dp), text = it )
            }
        }
    }
}
```

The above example is when you want the ```CollapsingTopBar``` to collapse or expand on any detected vertical scroll. <br> But what if 
for example, you want your ```CollapsingTopBar``` to only expand when a user is at the top of your LazyColumn or LazyVerticalGrid? 
For that, you need to pass a ```ScrollableState``` such as a  ```LazyListState``` for a LazyColumn  or a ```LazyGridState``` for a LazyVerticalGrid, inside your  ```CollapsingTopBarScrollBehavior```:

```kotlin
val scrollableState = rememberLazyListState()
val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
    isAlwaysCollapsed = false,
    isExpandedWhenFirstDisplayed = true,
    centeredTitleWhenCollapsed = false,
    centeredTitleAndSubtitle = true,
    collapsedTopBarHeight = 56.dp,
    expandedTopBarMaxHeight = 156.dp,
    scrollableState = scrollableState
)
```

Then you pass that same ```ScrollableState``` to your LazyColumn, like so:
```kotlin

Column(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
    CollapsingTopBar(
        scrollBehavior = scrollBehavior,
        title = { TitleText },
        expandedTitle = { ExpandedTitleText },
        subtitle = { SubtitleText },
        navigationIcon = { NavigationIcon },
        mainAction = { MainActionIconButton },
        actions = { MoreMenuIcons },
    )
    LazyColumn(state = scrollableState) {
        items(contactsList) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { },
            ) {
                Text( modifier = Modifier.padding(16.dp), text = it )
            }
        }
    }
}
```

The above examples are basic examples, to know & learn more about the ```CollapsingTopBar```, check out
the [sample app](https://github.com/germainkevinbusiness/CollapsingTopBarCompose/blob/master/app/src/main/java/com/germainkevin/collapsingtopbarcompose/MainActivity.kt)
.


## License

Licenced under the MIT Licence

```
Copyright (c) 2025 Kevin Germain

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

Author & support
-----------------
This project was created by [Germain Kevin](https://realgermainkevin.web.app/).

If this project help you reduce time to develop, you can give me a cup of coffee :) 

[![paypal](https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif)](https://www.paypal.me/KevinGermainBusiness)
