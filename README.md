# Peinture

[![](https://jitpack.io/v/suransea/peinture.svg)](https://jitpack.io/#suransea/peinture)

A DSL drawing toolkit.

## Gradle

Step 1. Add the JitPack repository to your build file

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

```
Step 2. Add the dependency

```groovy
dependencies {
    implementation 'com.github.suransea:peinture:x.y.z'
}
```

## Example

The DSL:

```javascript
// Use keyword "let" to declare a custom view 
let Custom = Composite {
    Text('Hello') {
        width = 'wrap'
        height = 'wrap'
        id = 1
        textSize = '12dp'
        textColor = '#333333'
        Margin {
            top = '10dp'
        }
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tt = 'parent'
        }
    }
    Image {
        width = 'match'
        height = 'wrap'
        src = 'https://w.wallhaven.cc/full/6k/wallhaven-6k3oox.jpg'
        scaleType = 'fit_xy'
        Margin {
            top = '10dp'
        }
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tb = 1
        }
    }
}

/*
 * There can only be one top-level declaration 
 */
Composite {
    // size = '300dp'  // width = height = '300dp'
    width = '300dp'
    height = 'wrap'
    Custom {
        color = '#F6F6F6'
        width = 'match'
        height = 'wrap'
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tt = 'parent'
            bb = 'parent'
        }
    }
}
```

The usage:

```kotlin
val drawer = Drawer(context)
val bitmap = drawer.drawBitmap(dsl, Bitmap.Config.RGB_565)
```

you can also get the view with:

```kotlin
val drawer = Drawer(context, imageLoader = GladeImageLoader)
val view = drawer.drawView(dsl)
```

The image loader is "GladeBlockingImageLoader" by default. 
A blocking image loader required to output a bitmap, 
but not to output a view to be displayed. 
You can also customize the image loader.


The result:

![example](https://i.loli.net/2020/08/29/kAa5TbwxcfjuQEJ.png)

## Widgets

### Common
```
id          // integer
width       // size, unit: dp, sp, pt, px, ex: '10dp'
height      // size
color       // background color, ex: '#FFFFFF'
constraint  // declaration
padding     // declaration
margin      // declaration
```

#### Declaration

##### Constraint
```
ll        // id or 'parent', left to left
lr        // left to right
tt        // top to top
tb        // top to bottom
rr        // right to right
rl        // right to left
bb        // bottom to bottom
bt        // bottom to top
```

##### Padding
```
top       // size
bottom    
left      
right     
```

##### Margin
```
top       // size
bottom    
left      
right     
```

### Text
```
text       // the main argument
textSize   // ex: '16sp'
textColor  // ex: '#333333'
textStyle  // string: 'bold'
                      'monospace'
                      'sans_serif'
                      'serif'
                      'italic'
                      'bold_italic'
                      'normal'
deleteLine // boolean
underLine  // boolean
```

### Image
```
src        // image url, also as the main argument
scyleType  // string: 'matrix'
                      'fit_xy'
                      'fit_start'
                      'fit_center'
                      'fit_end'
                      'center'
                      'center_crop'
                      'center_inside'
```

### Empty
No specific arguments.

## License

[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)
