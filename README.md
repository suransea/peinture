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
        id = 1
        textSize = 120
        textColor = '#333333'
        Constraint {
            width = 'wrap'
            height = 'wrap'
            top = 60
            leftToLeft = 'parent'
            rightToRight = 'parent'
            topToTop = 'parent'
        }
    }
    Image {
        src = 'https://w.wallhaven.cc/full/6k/wallhaven-6k3oox.jpg'
        scaleType = 'fit_xy'
        Constraint {
            width = 'parent'
            height = 'wrap'
            top = 60
            topToBottom = 1
        }
    }
}

/*
 * There can only be one top-level declaration 
 */
Composite {
    Custom {
        color = '#F6F6F6'
        Constraint {
            width = 'parent'
            height = 'wrap'
        }
    }
    Constraint {
        width = 800
        height = 'wrap'
    }
}
```

The usage:

```kotlin
val drawer = Drawer(context)
val bitmap = drawer.drawBitmap(dsl)
```

you can also get the view with:

```kotlin
val drawer = Drawer(context, imageLoader = ImageLoaders.glade)
val view = drawer.drawView(dsl)
```

The image loader is "ImageLoaders.gladeBlocking" by default. 
A blocking image loader required to output a bitmap, 
but not to output a view to be displayed. 
You can also customize the image loader.


The result:

![example](https://i.loli.net/2020/09/14/RucOkQEdPUgeBIT.png)

## Widgets

### Common
```
id           // integer
color        // background color, ex: '#FFFFFF'
alpha        // float in [0, 1], 0 is transparent
constraint   // declaration
padding      // declaration
transform    // declaration
shape        // 'rectangle' or 'oval'
borderColor  // ex: '#333333'
borderWidth  // size
borderLength // size
borderSpace  // size
cornerRadii  // ex: [('10dp', '10dp'), ('10dp', '10dp'), ('10dp', '10dp'), ('10dp', '10dp')]
cornerRadius // size
gradient     // declaration
```

#### Declaration

##### Constraint
```
width                 // 'parent', 'wrap' or size, unit: dp, sp, pt, px, ex: '10dp'
height 
top                   // size
left
bottom
right
topToTop              // 'parent' or id
topToBottom       
leftToLeft        
leftToRight       
rightToRight      
rightToLeft       
bottomToBottom   
baselineToBaseline     
widthToHeight         // ratio
heightToWidth         
```

##### Padding
```
top       // size
bottom    
left      
right     
```

##### Transform 
```
pivot        // scale and rotation pivot, ex: ('200dp', 0)
scroll       // (x, y)
translation  // (x, y, z)
scale        // (x, y)
rotation     // (x, y, z)
```

##### Gradient
```
colors      // array, at least 2 items
type        // 'linear'
               'radial'
               'sweep'
orientation // 'bl_tr'
               'bt'
               'br_tl'
               'lr'
               'rl'
               'tl_br'
               'tb'
               'tr_bl'
radius      // radius for type "radial"
center      // center point for type "sweep", ex: (0.5, 0.5)
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
maxLines   // integer
```

### Image
```
src        // image url, also as the main argument
scaleType  // string: 'matrix'
                      'fit_xy'
                      'fit_start'
                      'fit_center'
                      'fit_end'
                      'center'
                      'center_crop'
                      'center_inside'
```

### Clip
A container to clip the inner widget to the specific shape.

### View, Empty
No specific arguments.

## License

[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0)
