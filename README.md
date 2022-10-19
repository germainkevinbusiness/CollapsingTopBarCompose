[![](https://jitpack.io/v/germainkevinbusiness/CollapsingTopBarCompose.svg)](https://jitpack.io/#germainkevinbusiness/CollapsingTopBarCompose)

# CollapsingTopBarCompose

A Jetpack Compose TopBar that collapses or expands based on detected scroll events or by calling:
```CollapsingTopBarScrollBehavior.collapse()``` or ```CollapsingTopBarScrollBehavior.expand()```
extension methods.

<table>
  <tr>
    <td>Collapse/Expand due to user scroll while displaying centered expanded title & subtitle</td>
    <td>Collapse/Expand due to user scroll while displaying only the title on the left side</td>
    <td>Collapse/Expand while the mainAction icon animates</td>
  </tr>
  <tr>
    <td valign="top"><img src="https://user-images.githubusercontent.com/83923717/170046931-3f9cf06e-9476-4ea1-a932-34d3197a47df.gif" alt="Demonstration 1" width="100%" height="auto"/></td>
    <td valign="top"><img src="https://user-images.githubusercontent.com/83923717/170043487-5e78724b-bd66-4617-b703-624281d49c2a.gif" alt="Demonstration 2" width="100%" height="auto"/></td>
    <td valign="top"><img src="https://user-images.githubusercontent.com/83923717/196091667-c8a48a32-aa66-4e71-afc7-7bbd247d5ee3.gif" alt="Demonstration 3" width="100%" height="auto"/></td>
  </tr>
 </table> 

# How to get this library in your android app

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
    implementation "com.github.germainkevinbusiness:CollapsingTopBarCompose:1.1.2"
}
```

# How to use this library

The below example is a basic example, for a more elaborate example check out
the [sample app](https://github.com/germainkevinbusiness/CollapsingTopBarCompose/blob/master/app/src/main/java/com/germainkevin/collapsingtopbarcompose/MainActivity.kt)
.

In order to use a ```CollapsingTopBar```, you first need to create
a ```CollapsingTopBarScrollBehavior```.

```kotlin
val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
    isAlwaysCollapsed = false,
    isExpandedWhenFirstDisplayed = true,
    centeredTitleWhenCollapsed = false,
    centeredTitleAndSubtitle = true,
    collapsedTopBarHeight = 56.dp,
    expandedTopBarMaxHeight = 156.dp,
    userLazyListState = null
)
```

To know when scrolling occurs inside your Layout, so the ```CollapsingTopBar``` can collapse or
expand, add the ```scrollBehavior.nestedScrollConnection``` inside your Layout's
```Modifier.nestedScroll``` :

```kotlin
Scaffold(
    modifier = Modifier
        .nestedScroll(scrollBehavior.nestedScrollConnection),
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
) {}
```

So a complete example could look like:

```kotlin
private val contacts = listOf(
    "Alejandro Balde", "Barella Nicolo", "Cristiano Ronaldo", "David Beckham",
    "Federico Valverde", "Granit Xhaka", "Harry Kane", "Lionel Andres Messi",
)
val scrollBehavior = rememberCollapsingTopBarScrollBehavior(
    isAlwaysCollapsed = false,
    isExpandedWhenFirstDisplayed = true,
    centeredTitleWhenCollapsed = false,
    centeredTitleAndSubtitle = true,
    collapsedTopBarHeight = 56.dp,
    expandedTopBarMaxHeight = 156.dp,
    userLazyListState = null
)
//val isMoving = scrollBehavior.isMoving
val isCollapsed = scrollBehavior.isCollapsed
val isExpanded = scrollBehavior.isExpanded
val window = this@Activity.window
Column(
    modifier = Modifier
        .fillMaxSize()
        .nestedScroll(scrollBehavior.nestedScrollConnection),
) {
    CollapsingTopBar(
        scrollBehavior = scrollBehavior,
        title = TitleText,
        expandedTitle = ExpandedTitleText,
        subtitle = { SubtitleText(contacts) },
        navigationIcon = { NavigationIcon() },
        mainAction = {
            IconButton(onClick = {}) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Main Action Icon",
                )
            }
        },
        actions = { MoreMenuIcons(isCollapsed, isExpanded) },
        colors = CollapsingTopBarDefaults.colors(
            backgroundColorWhenCollapsingOrExpanding =
            MaterialTheme.colorScheme.onPrimaryContainer,
            onBackgroundColorChange = {
                window.statusBarColor = it.toArgb()
            },
        ),
    )
    LazyColumn(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        items(contacts) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { },
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = it,
                    fontSize = 16.sp,
                )
            }
        }
    }
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
