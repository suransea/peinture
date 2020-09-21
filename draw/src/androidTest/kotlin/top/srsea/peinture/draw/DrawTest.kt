package top.srsea.peinture.draw

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
""".trimIndent()

@RunWith(AndroidJUnit4::class)
class DrawTest {
    @Test
    fun testDraw() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val drawer = Drawer(appContext)
        val bitmap = drawer.drawBitmap(vl)
        println(bitmap)
    }

    @Test
    fun testDrawView() {
        Handler(Looper.getMainLooper()).post {
            val appContext = InstrumentationRegistry.getInstrumentation().targetContext
            val drawer = Drawer(appContext, imageLoader = ImageLoaders.glade)
            val view = drawer.drawView(vl)
            println(view)
        }
    }
}
