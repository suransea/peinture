package top.srsea.peinture.draw

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

val vl = """
// Use keyword "let" to declare a custom view
let Custom = Composite {
    Text('Hello') {
        width = 'wrap'
        height = 'wrap'
        id = 1
        textSize = '12dp'
        textColor = '#333333'
        underLine = true
        deleteLine = true
        textStyle = 'italic'
        Margin {
            top = '10dp'
        }
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tt = 'parent'
        }
    }
    Card {
        cardRadius = '360dp'
        width = 'wrap'
        height = 'wrap'
        color = '#00FFFFFF'
        Image {
            width = '300dp'
            height = '300dp'
            src = 'https://w.wallhaven.cc/full/6k/wallhaven-6k3oox.jpg'
            scaleType = 'center_crop'
        }
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
    color = '#F6F6F6'
    Custom {
        id = 2
        width = 'match'
        height = 'wrap'
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tt = 'parent'
        }
        Transform {
            alpha = 0.5
        }
    }
    Composite {
        width = '300dp'
        height = '60dp'
        Shape {
            width = '200dp'
            height = '400dp'
            shape = 'rectangle'
            fillColor = '#ffd566'
            Constraint {
                ll = 'parent'
                tt = 'parent'
            }
            Transform {
                pivot = ('200dp', 0)
                rotation = (0, 0, 10)
            }
        }
        Constraint {
            ll = 'parent'
            rr = 'parent'
            tb = 2
        }
    }
}
""".trimIndent()

@RunWith(AndroidJUnit4::class)
class DrawTest {
    @Test
    fun testDraw() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val drawer = Drawer(appContext)
        val bitmap = drawer.drawBitmap(vl, Bitmap.Config.RGB_565)
        println(bitmap)
    }

    @Test
    fun testDrawView() {
        Handler(Looper.getMainLooper()).post {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            val drawer = Drawer(appContext, imageLoader = GladeImageLoader)
            val view = drawer.drawView(vl)
            println(view)
        }
    }
}
